import { useCallback, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { brandApi, categoryApi, errorMessage, productApi } from '../services/api'
import { useRequest } from '../services/useRequest'

const manage = { manage: true }

export default function AdminProductForm() {
  const { id } = useParams()
  const load = useCallback(async () => {
    const [brands, categories, product] = await Promise.all([
      brandApi.list(), categoryApi.list(manage), id ? productApi.get(id, manage) : null,
    ])
    return { brands, categories, product }
  }, [id])
  const request = useRequest(load)

  return (
    <>
      <Link to="/admin/products" className="inline-flex items-center gap-2 text-sm text-muted"><ArrowLeft size={15} /> Back to products</Link>
      <h1 className="mt-5 font-display text-4xl">{id ? 'Edit product' : 'Add product'}</h1>
      <RequestState {...request} />
      {!request.loading && request.data && <ProductFields key={id || 'new'} {...request.data} />}
    </>
  )
}

function ProductFields({ product, brands, categories }) {
  const { catalogue } = useShop()
  const navigate = useNavigate()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function submit(event) {
    event.preventDefault()
    const fields = Object.fromEntries(new FormData(event.currentTarget))
    const { brandId, categoryId, ...details } = fields
    const body = {
      ...details, brand: { id: Number(brandId) }, category: { id: Number(categoryId) },
      price: Number(details.price), stock: Number(details.stock), featured: fields.featured === 'on',
    }
    setBusy(true)
    setError('')
    try {
      if (product) await productApi.update(product.id, body)
      else await productApi.create(body)
      catalogue.reload()
      navigate('/admin/products')
    } catch (failure) {
      setError(errorMessage(failure))
    } finally {
      setBusy(false)
    }
  }

  if (!brands.length || !categories.length) {
    return <p className="mt-6 text-sm text-muted">Add a brand and category in <Link className="font-semibold text-wine underline" to="/admin/catalogue">Brands and categories</Link> before creating a product.</p>
  }

  return (
    <form onSubmit={submit} className="mt-8 max-w-3xl rounded-2xl border border-line bg-white p-6 md:p-8">
      <fieldset disabled={busy} className="grid gap-5 md:grid-cols-2">
        <label className="text-sm">Product name<input name="name" required maxLength={150} defaultValue={product?.name} className="field mt-2" /></label>
        <label className="text-sm">SKU<input name="sku" required maxLength={50} defaultValue={product?.sku} className="field mt-2" /></label>
        <label className="text-sm">Brand<select name="brandId" required defaultValue={product?.brand?.id || ''} className="field mt-2">
          <option value="" disabled>Choose a brand</option>{brands.map(brand => <option key={brand.id} value={brand.id}>{brand.name}</option>)}
        </select></label>
        <label className="text-sm">Category<select name="categoryId" required defaultValue={product?.category?.id || ''} className="field mt-2">
          <option value="" disabled>Choose a category</option>{categories.map(category => <option key={category.id} value={category.id}>{category.name}{!category.active && ' (hidden)'}</option>)}
        </select></label>
        <label className="text-sm">Price (USD)<input name="price" type="number" required min="0" max="9999999999.99" step="0.01" defaultValue={product?.price} className="field mt-2" /></label>
        <label className="text-sm">Stock quantity<input name="stock" type="number" required min="0" max="2147483647" step="1" defaultValue={product?.stock} className="field mt-2" /></label>
        <label className="text-sm">Status<select name="productStatus" defaultValue={product?.productStatus || 'ACTIVE'} className="field mt-2"><option value="ACTIVE">Active</option><option value="DRAFT">Draft</option><option value="ARCHIVED">Archived</option></select></label>
        <label className="text-sm">Image URL<input name="imageUrl" maxLength={2048} defaultValue={product?.imageUrl} className="field mt-2" /></label>
        <label className="text-sm md:col-span-2">Description<textarea name="description" defaultValue={product?.description} className="field mt-2 min-h-28" /></label>
        <label className="flex items-center gap-2 text-sm md:col-span-2"><input type="checkbox" name="featured" defaultChecked={product?.featured} /> Feature on the home page</label>
      </fieldset>
      <RequestState error={error} />
      <div className="mt-7 flex gap-3 border-t border-line pt-6"><button disabled={busy} className="btn-primary"><Save size={16} />{busy ? 'Saving…' : 'Save product'}</button><Link to="/admin/products" className="btn-outline">Cancel</Link></div>
    </form>
  )
}

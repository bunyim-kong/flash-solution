import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import { useShop } from '../context/ShopContext'

const empty = {
  sku: '', name: '', brandId: '', categoryId: '', price: '', stock: '',
  status: 'Active', featured: false, imageUrl: '', description: ''
}

const apiMessage = (error) =>
  error.response?.data?.message || 'The product could not be saved. Check that the backend is running.'

export default function AdminProductForm() {
  const { id } = useParams()
  const editing = Boolean(id)
  const { products, brands, categories, catalogLoading, saveProduct } = useShop()
  const navigate = useNavigate()
  const [form, setForm] = useState(empty)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!editing) return
    const found = products.find(product => product.id === id)
    if (found) {
      setForm({
        sku: found.sku || '', name: found.name, brandId: String(found.brandId),
        categoryId: String(found.categoryId), price: found.price, stock: found.stock,
        status: found.status, featured: found.featured, imageUrl: found.imageUrl,
        description: found.description
      })
    }
  }, [id, editing, products])

  useEffect(() => {
    if (editing) return
    setForm(current => ({
      ...current,
      brandId: current.brandId || String(brands[0]?.id || ''),
      categoryId: current.categoryId || String(categories[0]?.id || '')
    }))
  }, [editing, brands, categories])

  const change = event => {
    const { name, value, type, checked } = event.target
    setForm(current => ({ ...current, [name]: type === 'checkbox' ? checked : value }))
  }

  const submit = async event => {
    event.preventDefault()
    setError('')
    if (!form.sku.trim() || !form.name.trim() || !form.brandId || !form.categoryId || form.price === '' || form.stock === '') {
      setError('Please complete SKU, name, brand, category, price and stock.')
      return
    }

    const payload = {
      sku: form.sku.trim(),
      name: form.name.trim(),
      brand: { id: Number(form.brandId) },
      category: { id: Number(form.categoryId) },
      price: Number(form.price),
      stock: Number(form.stock),
      productStatus: form.status.toUpperCase(),
      featured: Boolean(form.featured),
      imageUrl: form.imageUrl.trim() || null,
      description: form.description.trim() || null
    }

    setSaving(true)
    try {
      await saveProduct(payload, editing ? id : null)
      navigate('/admin/products')
    } catch (requestError) {
      setError(apiMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return <>
    <Link to="/admin/products" className="inline-flex items-center gap-2 text-sm text-muted"><ArrowLeft size={15}/> Back to products</Link>
    <div className="mt-5"><p className="eyebrow text-rose">{editing ? 'Catalogue update' : 'New catalogue item'}</p><h1 className="mt-2 font-display text-4xl">{editing ? 'Edit product' : 'Add product'}</h1><p className="mt-2 text-sm text-muted">Saved products are written to the Spring Boot API and MySQL.</p></div>
    <form onSubmit={submit} className="mt-8 max-w-3xl rounded-2xl border border-line bg-white p-6 md:p-8">
      <div className="grid gap-5 md:grid-cols-2">
        <label className="text-xs font-semibold">SKU *<input className="field mt-2" name="sku" value={form.sku} onChange={change} required placeholder="e.g. P-1011"/></label>
        <label className="text-xs font-semibold">Product name *<input className="field mt-2" name="name" value={form.name} onChange={change} required placeholder="e.g. Daily Hydrating Serum"/></label>
        <label className="text-xs font-semibold">Brand *<select className="field mt-2" name="brandId" value={form.brandId} onChange={change} required disabled={catalogLoading}><option value="">Select a brand</option>{brands.map(brand => <option key={brand.id} value={brand.id}>{brand.name}</option>)}</select></label>
        <label className="text-xs font-semibold">Category *<select className="field mt-2" name="categoryId" value={form.categoryId} onChange={change} required disabled={catalogLoading}><option value="">Select a category</option>{categories.filter(category => category.active !== false).map(category => <option key={category.id} value={category.id}>{category.name}</option>)}</select></label>
        <label className="text-xs font-semibold">Price (USD) *<input className="field mt-2" type="number" min="0" step="0.01" name="price" value={form.price} onChange={change} required placeholder="0.00"/></label>
        <label className="text-xs font-semibold">Stock quantity *<input className="field mt-2" type="number" min="0" name="stock" value={form.stock} onChange={change} required placeholder="0"/></label>
        <label className="text-xs font-semibold">Status<select className="field mt-2" name="status" value={form.status} onChange={change}><option>Active</option><option>Draft</option><option>Archived</option></select></label>
        <label className="text-xs font-semibold md:col-span-2">Image URL<input className="field mt-2" name="imageUrl" value={form.imageUrl} onChange={change} placeholder="/images/serum.svg or https://example.com/product.jpg"/></label>
        <label className="text-xs font-semibold md:col-span-2">Description<textarea className="field mt-2 min-h-28" name="description" value={form.description} onChange={change} placeholder="Describe the product..."/></label>
        <label className="flex items-center gap-2 text-sm md:col-span-2"><input type="checkbox" name="featured" checked={Boolean(form.featured)} onChange={change}/> Feature this product on the home page</label>
      </div>
      {error && <p className="mt-4 rounded-xl bg-red-50 p-3 text-sm text-rose">{error}</p>}
      {!catalogLoading && (!brands.length || !categories.length) && <p className="mt-4 rounded-xl bg-cream p-3 text-sm text-muted">Create at least one brand and category in the backend before creating a product.</p>}
      <div className="mt-7 flex flex-wrap gap-3 border-t border-line pt-6"><button disabled={saving || catalogLoading} className="btn-primary disabled:cursor-not-allowed disabled:opacity-50"><Save size={16}/> {saving ? 'Saving…' : 'Save product'}</button><Link to="/admin/products" className="btn-outline">Cancel</Link></div>
    </form>
  </>
}

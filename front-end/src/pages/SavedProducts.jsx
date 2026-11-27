import { Link } from 'react-router-dom'
import ProductCard from '../components/ProductCard'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'

export default function SavedProducts() {
  const { catalogue, products, savedProductIds, cartLoading, cartError, reloadCart } = useShop()
  const saved = products.filter(product => savedProductIds.includes(product.id))
  const loading = cartLoading || catalogue.loading
  const error = cartError || catalogue.error

  return (
    <main className="container-page min-h-[55vh] py-12">
      <h1 className="font-display text-4xl">Saved products</h1>
      <RequestState loading={loading} error={error} reload={cartError ? reloadCart : catalogue.reload} />
      {!loading && !error && !saved.length && <p className="mt-6 text-sm text-muted">Use the heart on any product to save it here.</p>}
      <div className="mt-8 grid grid-cols-2 gap-5 md:grid-cols-4">{saved.map(product => <ProductCard key={product.id} product={product} />)}</div>
      <Link to="/shop" className="btn-outline mt-6">Browse products</Link>
    </main>
  )
}

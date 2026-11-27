import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { errorMessage } from '../services/api'

export default function Checkout() {
  const { account, cart, cartLoading, cartBusy, cartError, reloadCart, placeOrder } = useShop()
  const [order, setOrder] = useState(null)
  const [error, setError] = useState('')
  const requestId = useRef(null)
  const total = cart.reduce((sum, product) => sum + product.price * product.quantity, 0)

  async function submit(event) {
    event.preventDefault()
    setError('')
    const fields = Object.fromEntries(new FormData(event.currentTarget))
    try {
      requestId.current ||= crypto.randomUUID()
      const reviewedItems = cart.map(({ id, quantity, price }) => ({ productId: id, quantity, unitPrice: price }))
      const result = await placeOrder({
        ...fields, requestId: requestId.current, paymentMethod: 'Cash on delivery', reviewedItems,
      })
      if (result) setOrder(result)
    } catch (failure) {
      setError(errorMessage(failure))
    }
  }

  if (order) return (
    <main className="container-page py-24 text-center">
      <h1 className="font-display text-4xl">Order placed</h1>
      <p className="mt-4 text-muted">Order #{order.id} · ${Number(order.totalAmount).toFixed(2)} · Cash on delivery</p>
      <Link to="/orders" className="btn-primary mt-8">View your orders</Link>
    </main>
  )
  if (cartLoading || (cartError && !cart.length)) return (
    <main className="container-page py-12"><h1 className="font-display text-4xl">Checkout</h1><RequestState loading={cartLoading} error={cartError} reload={reloadCart} /></main>
  )
  if (!cart.length) return <main className="container-page py-24 text-center"><h1 className="font-display text-3xl">Your bag is empty</h1><RequestState error={error} /><Link to="/shop" className="btn-primary mt-6">Shop now</Link></main>
  if (!account) return <main className="container-page py-24 text-center"><h1 className="font-display text-3xl">Sign in to check out</h1><Link to="/login" state={{ next: '/checkout' }} className="btn-primary mt-6">Sign in</Link></main>

  return (
    <main className="container-page max-w-3xl py-12">
      <h1 className="font-display text-4xl">Checkout</h1>
      <form onSubmit={submit} className="mt-8 grid gap-8 md:grid-cols-[1fr_260px]">
        <fieldset disabled={cartBusy} className="space-y-4">
          <legend className="mb-3 font-semibold">Contact and delivery</legend>
          <label className="block text-sm">Full name<input name="customerName" autoComplete="name" defaultValue={account.name} required maxLength={150} className="field mt-2" /></label>
          <label className="block text-sm">Email address<input name="customerEmail" type="email" autoComplete="email" defaultValue={account.email} required maxLength={255} className="field mt-2" /></label>
          <label className="block text-sm">Phone number<input name="phoneNumber" type="tel" autoComplete="tel" required maxLength={32} className="field mt-2" /></label>
          <label className="block text-sm">Delivery address<input name="address" autoComplete="street-address" required maxLength={170} className="field mt-2" /></label>
          <label className="block text-sm">City<input name="city" autoComplete="address-level2" required maxLength={80} className="field mt-2" /></label>
          <p className="text-sm text-muted">Pay by cash on delivery.</p>
          <RequestState error={cartError || error} reload={cartError ? reloadCart : undefined} />
          {error && <Link to="/cart" className="block text-sm font-semibold text-wine">Review your bag</Link>}
          <button className="btn-primary w-full">{cartBusy ? 'Please wait…' : 'Place order'}</button>
        </fieldset>
        <aside className="h-fit rounded-2xl bg-cream p-5">
          <h2 className="font-display text-xl">Summary</h2>
          {cart.map(product => <div key={product.id} className="mt-3 flex justify-between gap-3 text-xs"><span>{product.name} × {product.quantity}</span><span>${(product.price * product.quantity).toFixed(2)}</span></div>)}
          <div className="mt-5 flex justify-between border-t border-line pt-4 font-semibold"><span>Total</span><span>${total.toFixed(2)}</span></div>
        </aside>
      </form>
    </main>
  )
}

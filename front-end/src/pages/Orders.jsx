import { useState } from 'react'
import { Link } from 'react-router-dom'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { authApi, errorMessage, orderApi } from '../services/api'
import { useRequest } from '../services/useRequest'

function OrderList({ admin }) {
  const request = useRequest(admin ? orderApi.all : orderApi.mine)
  const orders = request.data || []
  return (
    <>
      <RequestState {...request} />
      {!request.loading && !request.error && orders.length === 0 && (
        <p className="mt-8 rounded-2xl border border-line p-8 text-sm text-muted">{admin ? 'No orders have been placed.' : 'You haven’t placed an order yet.'}</p>
      )}
      <ul className="mt-8 space-y-4">
        {orders.map(order => (
          <li key={order.id} className="rounded-2xl border border-line bg-white p-5 sm:p-6">
            <div className="flex flex-wrap justify-between gap-2">
              <h2 className="font-semibold">Order #{order.id}</h2>
              <p className="text-sm text-wine">{order.status} · ${Number(order.totalAmount).toFixed(2)}</p>
            </div>
            {admin && <p className="mt-2 break-words text-sm text-muted">{order.customerName} · {order.customerEmail} · {order.phoneNumber}</p>}
            <p className="mt-2 text-sm text-muted">{order.address} · {order.paymentMethod}</p>
            <ul className="mt-4 divide-y divide-line text-sm">
              {order.items.map((item, index) => <li key={`${item.sku}-${index}`} className="flex justify-between gap-3 py-2"><span>{item.name} × {item.quantity}</span><span>${Number(item.lineTotal).toFixed(2)}</span></li>)}
            </ul>
          </li>
        ))}
      </ul>
    </>
  )
}

export function AdminOrders() {
  return <><h1 className="font-display text-4xl">Orders</h1><OrderList admin /></>
}

export default function Orders() {
  const { account, accountLoading, setAccount } = useShop()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  async function signOut() {
    setBusy(true)
    try {
      await authApi.logout()
      setAccount(null)
      setError('')
    } catch (failure) {
      setError(errorMessage(failure))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="container-page min-h-[55vh] py-16">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="font-display text-4xl">Order history</h1>
        {account && <div className="flex flex-wrap gap-3"><Link to="/saved" className="btn-outline">Saved products</Link><button disabled={busy} onClick={signOut} className="btn-outline">Sign out</button></div>}
      </div>
      <RequestState loading={accountLoading} error={error} />
      {account && <OrderList key={account.id} />}
      {!accountLoading && !account && <Link to="/login" state={{ next: '/orders' }} className="btn-primary mt-8">Sign in</Link>}
    </main>
  )
}

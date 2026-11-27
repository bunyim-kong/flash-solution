import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { authApi, errorMessage } from '../services/api'

export default function Auth({ register = false }) {
  const { account, accountLoading, setAccount } = useShop()
  const location = useLocation()
  const navigate = useNavigate()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const next = location.state?.next || '/orders'
  const home = user => location.state?.next ? next : user.role.toUpperCase() === 'ADMIN' ? '/admin' : '/orders'

  async function submit(event) {
    event.preventDefault()
    setBusy(true)
    setError('')
    const details = Object.fromEntries(new FormData(event.currentTarget))
    try {
      const user = await (register ? authApi.register(details) : authApi.login(details))
      setAccount(user)
      navigate(home(user), { replace: true })
    } catch (failure) {
      setError(errorMessage(failure))
    } finally {
      setBusy(false)
    }
  }

  if (account) return <Navigate to={home(account)} replace />

  return (
    <main className="container-page flex min-h-[65vh] items-center justify-center py-14">
      <form onSubmit={submit} className="w-full max-w-md rounded-3xl border border-line p-8 shadow-soft">
        <p className="eyebrow text-center text-rose">Lumière account</p>
        <h1 className="mt-3 text-center font-display text-4xl">{register ? 'Create your account' : 'Welcome back'}</h1>
        <fieldset disabled={busy || accountLoading} className="mt-7 space-y-4">
          {register && <label className="block text-sm">Full name<input name="name" autoComplete="name" required maxLength={150} className="field mt-2" /></label>}
          <label className="block text-sm">Email address<input name="email" type="email" autoComplete="email" required maxLength={255} className="field mt-2" /></label>
          <label className="block text-sm">Password<input name="password" type="password" autoComplete={register ? 'new-password' : 'current-password'} minLength={register ? 8 : undefined} required className="field mt-2" /></label>
          <button className="btn-primary w-full">{busy ? 'Please wait…' : register ? 'Create account' : 'Sign in'}</button>
        </fieldset>
        <RequestState error={error} loading={accountLoading} />
        <p className="mt-5 text-center text-xs text-muted">
          <Link className="font-semibold text-rose" state={location.state} to={register ? '/login' : '/register'}>{register ? 'Already have an account? Sign in' : 'New to Lumière? Create an account'}</Link>
        </p>
      </form>
    </main>
  )
}

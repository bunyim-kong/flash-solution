import { useState } from 'react'
import { Ban, CheckCircle2, Trash2, UserPlus, UserRound } from 'lucide-react'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { errorMessage, userApi } from '../services/api'
import { useRequest } from '../services/useRequest'

export default function AdminUsers() {
  const { account } = useShop()
  const request = useRequest(userApi.list)
  const [query, setQuery] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const users = request.data || []
  const filtered = users.filter(user => `${user.name} ${user.email} ${user.role}`.toLowerCase().includes(query.trim().toLowerCase()))

  async function change(operation) {
    setBusy(true)
    setError('')
    try {
      await operation()
      request.reload()
      return true
    } catch (failure) {
      setError(errorMessage(failure))
      return false
    } finally {
      setBusy(false)
    }
  }

  async function addUser(event) {
    event.preventDefault()
    const fields = Object.fromEntries(new FormData(event.currentTarget))
    if (await change(() => userApi.create(fields))) setShowForm(false)
  }

  function update(user, changes) {
    return change(() => userApi.update(user.id, { ...user, ...changes }))
  }

  function remove(user) {
    if (window.confirm(`Remove ${user.name}'s account?`)) change(() => userApi.remove(user.id))
  }

  return (
    <>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <h1 className="font-display text-4xl text-wine">User management</h1>
        <button onClick={() => setShowForm(value => !value)} className="btn-primary"><UserPlus size={16} /> Add user</button>
      </div>

      {showForm && (
        <form onSubmit={addUser} className="mt-6 rounded-2xl border border-line bg-white p-6">
          <h2 className="font-display text-2xl text-wine">Add an account</h2>
          <fieldset disabled={busy} className="mt-5 grid gap-4 sm:grid-cols-2">
            <label className="text-sm">Full name<input name="name" required maxLength={150} className="field mt-2" /></label>
            <label className="text-sm">Email address<input name="email" type="email" required maxLength={255} className="field mt-2" /></label>
            <label className="text-sm">Initial password<input name="password" type="password" autoComplete="new-password" required minLength={8} className="field mt-2" /></label>
            <label className="text-sm">Role<select name="role" className="field mt-2"><option value="CUSTOMER">Customer</option><option value="ADMIN">Admin</option></select></label>
            <div className="flex flex-wrap gap-3"><button className="btn-primary">Save account</button><button type="button" onClick={() => setShowForm(false)} className="btn-outline">Cancel</button></div>
          </fieldset>
        </form>
      )}

      <RequestState {...request} />
      <RequestState error={error} />
      <label className="mt-6 block max-w-xl text-sm">Search accounts<input className="field mt-2" placeholder="Name, email or role" value={query} onChange={event => setQuery(event.target.value)} /></label>
      <div className="mt-5 divide-y divide-line rounded-2xl border border-line bg-white">
        {filtered.map(user => (
          <div key={user.id} className="grid gap-3 p-4 sm:grid-cols-[minmax(0,2fr)_minmax(0,1fr)_auto] sm:items-center">
            <div className="flex min-w-0 items-center gap-3">
              <UserRound size={18} className="shrink-0 text-rose" />
              <div className="min-w-0"><p className="font-semibold">{user.name}</p><p className="break-all text-xs text-muted">{user.email} · {user.active ? 'Active' : 'Suspended'}</p></div>
            </div>
            <label className="text-xs text-muted">Role<select aria-label={`Role for ${user.name}`} disabled={busy || request.loading || account.id === user.id} className="field mt-1" value={user.role.toUpperCase()} onChange={event => update(user, { role: event.target.value })}><option value="CUSTOMER">Customer</option><option value="ADMIN">Admin</option></select></label>
            {account.id !== user.id && <div className="flex gap-2">
              <button disabled={busy || request.loading} onClick={() => update(user, { active: !user.active })} aria-label={`${user.active ? 'Suspend' : 'Activate'} ${user.name}`} className="grid h-11 w-11 place-items-center rounded-xl bg-cream text-wine">{user.active ? <Ban size={17} /> : <CheckCircle2 size={17} />}</button>
              <button disabled={busy || request.loading} onClick={() => remove(user)} aria-label={`Remove ${user.name}`} className="grid h-11 w-11 place-items-center rounded-xl bg-cream text-rose"><Trash2 size={17} /></button>
            </div>}
          </div>
        ))}
        {!request.loading && !request.error && !filtered.length && <p className="p-8 text-center text-sm text-muted">No matching accounts.</p>}
      </div>
    </>
  )
}

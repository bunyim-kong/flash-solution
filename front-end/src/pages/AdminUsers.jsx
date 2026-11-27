import { useEffect, useMemo, useState } from 'react'
import { UserPlus, Search, UserRound, Trash2 } from 'lucide-react'
import { userApi } from '../services/api'

const emptyForm = { name: '', email: '', password: '', role: 'Admin' }
const apiMessage = error => error.response?.data?.message || 'The request could not be completed.'

export default function AdminUsers() {
  const [users, setUsers] = useState([])
  const [query, setQuery] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)

  const loadUsers = async () => {
    setLoading(true)
    setMessage('')
    try {
      setUsers(await userApi.getAll())
    } catch (error) {
      setMessage(apiMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadUsers() }, [])

  const filtered = useMemo(() => users.filter(user =>
    `${user.name} ${user.email} ${user.role}`.toLowerCase().includes(query.toLowerCase())
  ), [users, query])

  const addUser = async event => {
    event.preventDefault()
    setMessage('')
    try {
      const saved = await userApi.create({
        name: form.name.trim(), email: form.email.trim(),
        password: form.password, role: form.role
      })
      setUsers(current => [saved, ...current])
      setForm(emptyForm)
      setShowForm(false)
      setMessage('Account saved to the database.')
    } catch (error) {
      setMessage(apiMessage(error))
    }
  }

  const updateRole = async (user, role) => {
    setMessage('')
    try {
      const saved = await userApi.update(user.id, { ...user, role })
      setUsers(current => current.map(item => item.id === user.id ? saved : item))
    } catch (error) {
      setMessage(apiMessage(error))
    }
  }

  const removeUser = async user => {
    if (!window.confirm(`Remove ${user.name} from the database?`)) return
    setMessage('')
    try {
      await userApi.remove(user.id)
      setUsers(current => current.filter(item => item.id !== user.id))
    } catch (error) {
      setMessage(apiMessage(error))
    }
  }

  return <div>
    <div className="flex flex-wrap items-end justify-between gap-4">
      <div><p className="eyebrow text-sage">Store access</p><h1 className="mt-2 font-display text-4xl text-wine">User management</h1><p className="mt-2 text-sm text-muted">Accounts on this page are loaded from the Spring Boot users API.</p></div>
      <button onClick={() => { setShowForm(value => !value); setMessage('') }} className="btn-primary"><UserPlus size={16}/> Add user</button>
    </div>

    <div className="mt-7 grid gap-4 sm:grid-cols-2">
      <div className="rounded-2xl border border-line bg-white p-5"><p className="text-xs text-muted">Total accounts</p><p className="mt-2 font-display text-3xl text-wine">{users.length}</p></div>
      <div className="rounded-2xl border border-line bg-white p-5"><p className="text-xs text-muted">Administrators</p><p className="mt-2 font-display text-3xl text-wine">{users.filter(user => user.role === 'Admin').length}</p></div>
    </div>

    {showForm && <form onSubmit={addUser} className="mt-6 rounded-2xl border border-line bg-white p-6">
      <div className="flex items-center justify-between"><div><h2 className="font-display text-2xl text-wine">Add an account</h2><p className="mt-1 text-xs text-muted">The password is hashed by the backend before MySQL stores it.</p></div><button type="button" onClick={() => setShowForm(false)} className="text-sm text-muted">Close</button></div>
      <div className="mt-5 grid gap-4 md:grid-cols-2">
        <label className="text-xs font-semibold text-ink">Full name<input required className="field mt-2 text-sm" value={form.name} onChange={event => setForm({ ...form, name: event.target.value })} placeholder="e.g. Alex Morgan"/></label>
        <label className="text-xs font-semibold text-ink">Email address<input required type="email" className="field mt-2 text-sm" value={form.email} onChange={event => setForm({ ...form, email: event.target.value })} placeholder="name@example.com"/></label>
        <label className="text-xs font-semibold text-ink">Password<input required minLength="6" type="password" className="field mt-2 text-sm" value={form.password} onChange={event => setForm({ ...form, password: event.target.value })} placeholder="At least 6 characters"/></label>
        <label className="text-xs font-semibold text-ink">Role<select className="field mt-2 text-sm" value={form.role} onChange={event => setForm({ ...form, role: event.target.value })}><option value="Admin">Admin</option><option value="Customer">Customer</option></select></label>
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

    <div className="mt-7"><label className="relative block w-full max-w-xl"><Search size={18} className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"/><input className="field !h-12 !min-h-12 !rounded-xl !pl-11 !pr-4 text-sm leading-normal" placeholder="Search name, email or role" value={query} onChange={event => setQuery(event.target.value)}/></label></div>
    <div className="mt-5 overflow-hidden rounded-2xl border border-line bg-white">
      <div className="hidden grid-cols-[minmax(180px,1.4fr)_minmax(190px,1.5fr)_minmax(125px,1fr)_80px] items-center gap-4 bg-cream px-5 py-4 text-[11px] font-bold uppercase tracking-wider text-muted md:grid"><span>User</span><span>Email</span><span>Role</span><span className="text-center">Action</span></div>
      {filtered.map(user => <div key={user.id} className="grid gap-4 border-t border-line p-4 md:grid-cols-[minmax(180px,1.4fr)_minmax(190px,1.5fr)_minmax(125px,1fr)_80px] md:items-center md:gap-4 md:px-5 md:py-5">
        <div className="flex items-center gap-3"><span className="grid h-10 w-10 place-items-center rounded-full bg-blush text-rose"><UserRound size={18}/></span><div><p className="text-sm font-semibold text-ink">{user.name}</p><p className="text-[11px] text-muted">Account #{user.id}</p></div></div>
        <span className="break-all text-xs text-muted">{user.email}</span>
        <label className="text-[10px] text-muted md:text-xs">Role<select aria-label={`Role for ${user.name}`} className="field mt-1 py-2 text-xs" value={user.role} onChange={event => updateRole(user, event.target.value)}><option>Customer</option><option>Admin</option></select></label>
        <div className="flex justify-start md:justify-center"><button onClick={() => removeUser(user)} title="Remove account" className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-cream text-rose transition hover:bg-blush"><Trash2 size={17}/></button></div>
      </div>)}
      {loading && <p className="p-10 text-center text-sm text-muted">Loading users from the backend…</p>}
      {!loading && filtered.length === 0 && <p className="p-10 text-center text-sm text-muted">No matching accounts.</p>}
    </div>
    <p className="mt-4 rounded-xl border border-[#E8DED4] bg-[#F5F3E9] p-4 text-xs leading-6 text-muted"><strong className="text-wine">Security note:</strong> CRUD is connected, but these admin routes are not protected yet. Add Spring Security authentication and backend role authorization before production.</p>
  </div>
}

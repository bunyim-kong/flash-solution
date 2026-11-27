import { useCallback, useState } from 'react'
import RequestState from '../components/RequestState'
import { useShop } from '../context/ShopContext'
import { brandApi, categoryApi, errorMessage } from '../services/api'
import { useRequest } from '../services/useRequest'

function CatalogueSection({ label, api, category = false }) {
  const { catalogue } = useShop()
  const load = useCallback(() => api.list({ manage: true }), [api])
  const request = useRequest(load)
  const [editing, setEditing] = useState(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function change(operation) {
    setBusy(true)
    setError('')
    try {
      await operation()
      setEditing(null)
      request.reload()
      catalogue.reload()
    } catch (failure) {
      setError(errorMessage(failure))
    } finally {
      setBusy(false)
    }
  }

  function submit(event) {
    event.preventDefault()
    const fields = Object.fromEntries(new FormData(event.currentTarget))
    if (category) fields.active = fields.active === 'on'
    change(() => editing.id ? api.update(editing.id, fields) : api.create(fields))
  }

  function remove(entry) {
    if (window.confirm(`Delete ${entry.name}?`)) change(() => api.remove(entry.id))
  }

  return (
    <section className="rounded-2xl border border-line bg-white p-5">
      <div className="flex items-center justify-between gap-3">
        <h2 className="font-display text-3xl">{category ? 'Categories' : 'Brands'}</h2>
        <button disabled={busy} onClick={() => setEditing({})} className="btn-outline">Add {label}</button>
      </div>
      <RequestState {...request} />
      <RequestState error={error} />
      {editing && (
        <form key={editing.id || 'new'} onSubmit={submit} className="my-5 rounded-xl bg-cream p-4">
          <fieldset disabled={busy} className="space-y-3">
            <label className="block text-sm">Name<input name="name" defaultValue={editing.name} required maxLength={100} className="field mt-2" /></label>
            <label className="block text-sm">Slug<input name="slug" defaultValue={editing.slug} required maxLength={100} placeholder="e.g. skin-care" className="field mt-2" /></label>
            {category && <>
              <label className="block text-sm">Description<textarea name="description" defaultValue={editing.description} maxLength={255} className="field mt-2" /></label>
              <label className="flex items-center gap-2 text-sm"><input name="active" type="checkbox" defaultChecked={editing.active ?? true} /> Visible in store</label>
            </>}
            <div className="flex gap-2"><button className="btn-primary">Save {label}</button><button type="button" className="btn-outline" onClick={() => setEditing(null)}>Cancel</button></div>
          </fieldset>
        </form>
      )}
      {!request.loading && !request.error && !request.data?.length && <p className="mt-5 text-sm text-muted">{category ? 'No categories yet.' : 'No brands yet.'}</p>}
      <ul className="mt-5 divide-y divide-line">
        {request.data?.map(entry => (
          <li key={entry.id} className="flex flex-wrap items-center justify-between gap-2 py-3">
            <div><p className="font-medium">{entry.name}</p><p className="text-xs text-muted">{entry.slug}{entry.active === false && ' · Hidden'}</p></div>
            <div className="flex gap-3 text-sm"><button disabled={busy} onClick={() => setEditing(entry)} className="text-wine">Edit</button><button disabled={busy} onClick={() => remove(entry)} className="text-rose">Delete</button></div>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default function AdminCatalogue() {
  return <><h1 className="font-display text-4xl">Brands and categories</h1><div className="mt-8 grid gap-6 xl:grid-cols-2"><CatalogueSection label="brand" api={brandApi} /><CatalogueSection label="category" api={categoryApi} category /></div></>
}

export default function RequestState({ loading, error, reload }) {
  if (error) return (
    <div className="my-5 rounded-xl border border-line bg-cream p-4">
      <p role="alert" className="text-sm text-wine">{error}</p>
      {reload && <button type="button" className="btn-outline mt-3" onClick={reload}>Try again</button>}
    </div>
  )
  if (loading) return <p className="my-5 text-sm text-muted" role="status">Loading…</p>
  return null
}

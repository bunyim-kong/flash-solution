import { useMemo, useState } from 'react'
import { Search, SlidersHorizontal } from 'lucide-react'
import { useSearchParams } from 'react-router-dom'
import { useShop } from '../context/ShopContext'
import RequestState from '../components/RequestState'
import ProductCard from '../components/ProductCard'

export default function Shop() {
  const { products, brands, categories, catalogue } = useShop()
  const [params, setParams] = useSearchParams()
  const [query, setQuery] = useState('')
  const [sort, setSort] = useState('featured')
  const brand = params.get('brand') || 'All'
  const active = params.get('category') || 'All'
  const selectFilter = (key, value) => {
    const next = new URLSearchParams(params)
    if (value === 'All') next.delete(key)
    else next.set(key, value)
    setParams(next)
  }
  const filtered = useMemo(() => {
    let list = products.filter(p => p.status === 'Active' &&
      (active === 'All' || p.category === active) &&
      (brand === 'All' || p.brand === brand) &&
      `${p.name} ${p.brand}`.toLowerCase().includes(query.toLowerCase()))
    if (sort === 'featured') list.sort((a, b) => Number(b.featured) - Number(a.featured))
    if (sort === 'low') list.sort((a,b) => a.price-b.price)
    if (sort === 'high') list.sort((a,b) => b.price-a.price)
    if (sort === 'name') list.sort((a,b) => a.name.localeCompare(b.name))
    return list
  }, [products, active, brand, query, sort])
  return <main className="container-page py-12">
    <div className="text-center"><p className="eyebrow text-rose">The collection</p><h1 className="mt-3 font-display text-5xl">Shop beauty brands</h1><p className="mt-3 text-sm text-muted">Explore skincare, makeup and fragrance from a range of beauty labels.</p></div>
    <div className="mt-9 rounded-2xl bg-cream p-5"><p className="eyebrow text-muted">Shop by brand</p><div className="mt-3 flex flex-wrap gap-2">{brands.map(brand => brand.name).map(b=><button key={b} onClick={()=>selectFilter('brand', b)} className={`rounded-full border px-4 py-2 text-xs ${brand===b?'border-ink bg-ink text-white':'border-line bg-white text-ink/75 hover:border-rose'}`}>{b}</button>)}<button onClick={()=>selectFilter('brand', 'All')} className={`rounded-full border px-4 py-2 text-xs ${brand==='All'?'border-ink bg-ink text-white':'border-line bg-white text-ink/75'}`}>All brands</button></div></div>
    <div className="mt-8 flex flex-col justify-between gap-4 border-b border-line pb-5 md:flex-row md:items-center"><div className="flex flex-wrap gap-2">{['All', ...categories.map(category => category.name)].map(c=><button key={c} onClick={()=>selectFilter('category', c)} className={`rounded-full px-4 py-2 text-xs ${active===c?'bg-ink text-white':'bg-cream text-ink/70'}`}>{c}</button>)}</div><div className="grid w-full min-w-0 grid-cols-[minmax(0,1fr)_148px] gap-3 md:w-[430px] md:flex-none"><label className="relative block min-w-0"><Search size={17} strokeWidth={1.8} className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"/><input type="search" className="h-12 w-full min-w-0 rounded-xl border border-line bg-white pl-11 pr-4 text-sm text-ink outline-none transition placeholder:text-muted/70 hover:border-rose/60 focus:border-rose focus:ring-4 focus:ring-rose/10" placeholder="Search products or brands" value={query} onChange={e=>setQuery(e.target.value)}/></label><select aria-label="Sort products" className="h-12 w-full min-w-0 rounded-xl border border-line bg-white px-3 text-sm text-ink outline-none transition hover:border-rose/60 focus:border-rose focus:ring-4 focus:ring-rose/10" value={sort} onChange={e=>setSort(e.target.value)}><option value="featured">Featured</option><option value="low">Price: low to high</option><option value="high">Price: high to low</option><option value="name">Name</option></select></div></div>
    <div className="mt-7 flex items-center justify-between text-xs text-muted"><span>{filtered.length} products {brand!=='All'?`· ${brand}`:''}</span><span className="flex items-center gap-1"><SlidersHorizontal size={14}/> Refine your edit</span></div>
    <div className="mt-6 grid grid-cols-2 gap-x-5 gap-y-10 md:grid-cols-4">{filtered.map(p=><ProductCard key={p.id} product={p}/>)}</div>
    <RequestState {...catalogue} />
    {!catalogue.loading && !catalogue.error && filtered.length===0&&<p className="py-20 text-center text-muted">No products found. Try another brand or category.</p>}
  </main>
}

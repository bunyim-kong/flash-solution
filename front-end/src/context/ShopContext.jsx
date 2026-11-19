import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { brandApi, categoryApi, productApi } from '../services/api'

const ShopContext = createContext(null)

const titleCaseStatus = (status = 'DRAFT') =>
  status.charAt(0) + status.slice(1).toLowerCase()

const normalizeProduct = (product) => ({
  id: String(product.id),
  sku: product.sku,
  name: product.name,
  brand: product.brand?.name || '',
  brandId: product.brand?.id || '',
  category: product.category?.name || '',
  categoryId: product.category?.id || '',
  price: Number(product.price),
  stock: Number(product.stock),
  status: titleCaseStatus(product.productStatus),
  featured: Boolean(product.featured),
  imageUrl: product.imageUrl || '/images/cream.svg',
  description: product.description || '',
  createdAt: product.createdAt,
  updatedAt: product.updatedAt
})

export function ShopProvider({ children }) {
  const [products, setProducts] = useState([])
  const [brands, setBrands] = useState([])
  const [categories, setCategories] = useState([])
  const [catalogLoading, setCatalogLoading] = useState(true)
  const [catalogError, setCatalogError] = useState('')
  const [cart, setCart] = useState(() => {
    try { return JSON.parse(localStorage.getItem('lumiere-cart')) || [] }
    catch { return [] }
  })

  const loadCatalog = useCallback(async () => {
    setCatalogLoading(true)
    setCatalogError('')
    try {
      const [productData, brandData, categoryData] = await Promise.all([
        productApi.getAll(),
        brandApi.getAll(),
        categoryApi.getAll()
      ])
      setProducts(productData.map(normalizeProduct))
      setBrands(brandData)
      setCategories(categoryData)
    } catch (error) {
      setCatalogError(error.response?.data?.message || 'Could not load the catalogue from the backend.')
    } finally {
      setCatalogLoading(false)
    }
  }, [])

  useEffect(() => { loadCatalog() }, [loadCatalog])
  useEffect(() => localStorage.setItem('lumiere-cart', JSON.stringify(cart)), [cart])
  const addToCart = (product) => setCart(current => {
    const found = current.find(item => item.id === product.id)
    return found ? current.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item) : [...current, { ...product, quantity: 1 }]
  })
  const updateQuantity = (id, quantity) => setCart(current => quantity <= 0 ? current.filter(item => item.id !== id) : current.map(item => item.id === id ? { ...item, quantity } : item))
  const removeFromCart = (id) => setCart(current => current.filter(item => item.id !== id))
  const saveProduct = async (productData, id = null) => {
    const saved = id
      ? await productApi.update(id, productData)
      : await productApi.create(productData)
    const normalized = normalizeProduct(saved)
    setProducts(current => id
      ? current.map(item => item.id === String(id) ? normalized : item)
      : [normalized, ...current])
    return normalized
  }

  const deleteProduct = async (id) => {
    await productApi.remove(id)
    setProducts(current => current.filter(item => item.id !== String(id)))
    setCart(current => current.filter(item => item.id !== String(id)))
  }

  const value = useMemo(() => ({
    products,
    brands,
    categories,
    catalogLoading,
    catalogError,
    reloadCatalog: loadCatalog,
    cart,
    addToCart,
    updateQuantity,
    removeFromCart,
    saveProduct,
    deleteProduct
  }), [products, brands, categories, catalogLoading, catalogError, loadCatalog, cart])
  return <ShopContext.Provider value={value}>{children}</ShopContext.Provider>
}
export const useShop = () => useContext(ShopContext)

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState
} from 'react'

import { brandApi, categoryApi, productApi } from '../services/api'

const ShopContext = createContext(null)

function formatStatus(status = 'DRAFT') {
  return status.charAt(0) + status.slice(1).toLowerCase()
}

function normalizeProduct(product) {
  return {
    id: String(product.id),
    sku: product.sku,
    name: product.name,
    brand: product.brand?.name || '',
    brandId: product.brand?.id || '',
    category: product.category?.name || '',
    categoryId: product.category?.id || '',
    price: Number(product.price),
    stock: Number(product.stock),
    status: formatStatus(product.productStatus),
    featured: Boolean(product.featured),
    imageUrl: product.imageUrl || '/images/cream.svg',
    description: product.description || '',
    createdAt: product.createdAt,
    updatedAt: product.updatedAt
  }
}

function readSavedCart() {
  try {
    return JSON.parse(localStorage.getItem('lumiere-cart')) || []
  } catch {
    return []
  }
}

function getApiErrorMessage(error) {
  return (
    error.response?.data?.message ||
    'Could not load the catalogue from the backend.'
  )
}

export function ShopProvider({ children }) {
  const [products, setProducts] = useState([])
  const [brands, setBrands] = useState([])
  const [categories, setCategories] = useState([])
  const [catalogLoading, setCatalogLoading] = useState(true)
  const [catalogError, setCatalogError] = useState('')
  const [cart, setCart] = useState(readSavedCart)

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
      setCatalogError(getApiErrorMessage(error))
    } finally {
      setCatalogLoading(false)
    }
  }, [])

  useEffect(() => {
    loadCatalog()
  }, [loadCatalog])

  useEffect(() => {
    localStorage.setItem('lumiere-cart', JSON.stringify(cart))
  }, [cart])

  function addToCart(product) {
    setCart(currentCart => {
      const existingItem = currentCart.find(item => item.id === product.id)

      if (existingItem) {
        return currentCart.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      }

      return [...currentCart, { ...product, quantity: 1 }]
    })
  }

  function updateQuantity(id, quantity) {
    setCart(currentCart => {
      if (quantity <= 0) {
        return currentCart.filter(item => item.id !== id)
      }

      return currentCart.map(item =>
        item.id === id ? { ...item, quantity } : item
      )
    })
  }

  function removeFromCart(id) {
    setCart(currentCart => currentCart.filter(item => item.id !== id))
  }

  async function saveProduct(productData, id = null) {
    const savedProduct = id
      ? await productApi.update(id, productData)
      : await productApi.create(productData)

    const normalizedProduct = normalizeProduct(savedProduct)

    setProducts(currentProducts => {
      if (!id) {
        return [normalizedProduct, ...currentProducts]
      }

      return currentProducts.map(product =>
        product.id === String(id) ? normalizedProduct : product
      )
    })

    return normalizedProduct
  }

  async function deleteProduct(id) {
    await productApi.remove(id)

    setProducts(currentProducts =>
      currentProducts.filter(product => product.id !== String(id))
    )

    setCart(currentCart =>
      currentCart.filter(item => item.id !== String(id))
    )
  }

  const contextValue = {
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
  }

  return (
    <ShopContext.Provider value={contextValue}>
      {children}
    </ShopContext.Provider>
  )
}

export function useShop() {
  return useContext(ShopContext)
}

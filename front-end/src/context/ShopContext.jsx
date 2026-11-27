import { createContext, useContext, useEffect, useRef, useState } from 'react'
import { authApi, brandApi, cartApi, categoryApi, errorMessage, orderApi, productApi } from '../services/api'
import { useRequest } from '../services/useRequest'

const ShopContext = createContext(null)
const emptyCart = { items: [], savedProductIds: [] }

export function displayProduct(product) {
  return {
    ...product,
    brand: product.brand?.name || '',
    category: product.category?.name || '',
    status: { ACTIVE: 'Active', DRAFT: 'Draft', ARCHIVED: 'Archived' }[product.productStatus],
  }
}

async function loadCatalogue() {
  const [products, brands, categories] = await Promise.all([
    productApi.list(), brandApi.list(), categoryApi.list(),
  ])
  return { products: products.map(displayProduct), brands, categories }
}

export function ShopProvider({ children }) {
  const catalogue = useRequest(loadCatalogue)
  const [selection, setSelection] = useState(emptyCart)
  const [account, updateAccount] = useState(null)
  const [accountLoading, setAccountLoading] = useState(true)
  const [cartLoading, setCartLoading] = useState(true)
  const [cartError, setCartError] = useState('')
  const [cartBusy, setCartBusy] = useState(false)
  const cartPending = useRef(false)

  useEffect(() => {
    let current = true
    authApi.me().then(
      user => { if (current) updateAccount(user) },
      () => { if (current) updateAccount(null) },
    ).finally(() => { if (current) setAccountLoading(false) })
    return () => { current = false }
  }, [])

  useEffect(() => {
    let current = true
    setCartLoading(true)
    cartApi.get().then(
      result => { if (current) setSelection(result) },
      error => { if (current) setCartError(errorMessage(error)) },
    ).finally(() => { if (current) setCartLoading(false) })
    return () => { current = false }
  }, [account])

  async function changeCart(operation, shouldThrow = false) {
    if (cartPending.current) return
    cartPending.current = true
    setCartBusy(true)
    setCartError('')
    try {
      return await operation()
    } catch (error) {
      if (shouldThrow) throw error
      setCartError(errorMessage(error))
    } finally {
      cartPending.current = false
      setCartBusy(false)
    }
  }

  function setAccount(user) {
    setSelection(emptyCart)
    setCartError('')
    setCartLoading(true)
    updateAccount(user)
  }

  const updateCart = operation => changeCart(async () => setSelection(await operation()))

  async function placeOrder(details) {
    return changeCart(async () => {
      const order = await orderApi.create(details)
      setSelection(current => ({ ...current, items: [] }))
      catalogue.reload()
      return order
    }, true)
  }

  const value = {
    catalogue, products: catalogue.data?.products || [],
    brands: catalogue.data?.brands || [], categories: catalogue.data?.categories || [],
    account, accountLoading, setAccount,
    cart: selection.items.map(({ product, quantity }) => ({ ...displayProduct(product), quantity })),
    savedProductIds: selection.savedProductIds,
    cartBusy: cartBusy || cartLoading || accountLoading,
    cartLoading, cartError,
    reloadCart: () => updateCart(cartApi.get),
    addToCart: product => updateCart(() => cartApi.add(product.id)),
    updateQuantity: (id, quantity) => updateCart(() => cartApi.update(id, quantity)),
    removeFromCart: id => updateCart(() => cartApi.remove(id)),
    saveProduct: (id, saved) => updateCart(() => cartApi.save(id, saved)),
    placeOrder,
  }
  return <ShopContext.Provider value={value}>{children}</ShopContext.Provider>
}

export const useShop = () => useContext(ShopContext)

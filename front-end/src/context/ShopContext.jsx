import { createContext, useContext, useCallback, useEffect, useRef, useState } from 'react'
import { authApi, brandApi, cartApi, categoryApi, errorMessage, orderApi, productApi, sessionExpiredEvent } from '../services/api'
import { useRequest } from '../services/useRequest'

const ShopContext = createContext(null)
const emptyCart = { items: [], savedProductIds: [] }

export function displayProduct(product) {
  return {
    ...product,
    brand: product.brand?.name || '',
    category: product.category?.name || '',
    imageUrl: product.imageUrl || '/images/cream.svg',
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
  const sessionRevision = useRef(0)
  const currentAccount = useRef(null)

  const setAccount = useCallback(user => {
    if (user === currentAccount.current) return
    currentAccount.current = user
    sessionRevision.current += 1
    setSelection(emptyCart)
    setCartError('')
    setCartLoading(true)
    updateAccount(user)
  }, [])

  useEffect(() => {
    const clearExpiredSession = () => setAccount(null)
    window.addEventListener(sessionExpiredEvent, clearExpiredSession)
    return () => window.removeEventListener(sessionExpiredEvent, clearExpiredSession)
  }, [setAccount])

  useEffect(() => {
    let current = true
    authApi.me().then(
      user => { if (current) setAccount(user) },
      () => { if (current) setAccount(null) },
    ).finally(() => { if (current) setAccountLoading(false) })
    return () => { current = false }
  }, [setAccount])

  useEffect(() => {
    let current = true
    const revision = sessionRevision.current
    setCartLoading(true)
    cartApi.get().then(
      result => { if (current && revision === sessionRevision.current) setSelection(result) },
      error => { if (current && revision === sessionRevision.current) setCartError(errorMessage(error)) },
    ).finally(() => { if (current && revision === sessionRevision.current) setCartLoading(false) })
    return () => { current = false }
  }, [account])

  async function changeCart(operation, shouldThrow = false) {
    if (cartPending.current) return
    cartPending.current = true
    setCartBusy(true)
    setCartError('')
    const revision = sessionRevision.current
    try {
      return await operation()
    } catch (error) {
      if (shouldThrow) throw error
      if (revision === sessionRevision.current) setCartError(errorMessage(error))
    } finally {
      cartPending.current = false
      setCartBusy(false)
    }
  }

  const updateCart = operation => changeCart(async () => {
    const revision = sessionRevision.current
    const result = await operation()
    if (revision === sessionRevision.current) setSelection(result)
  })

  async function placeOrder(details) {
    return changeCart(async () => {
      const revision = sessionRevision.current
      try {
        const order = await orderApi.create(details)
        if (revision === sessionRevision.current) {
          setSelection(current => ({ ...current, items: [] }))
          catalogue.reload()
        }
        return order
      } catch (error) {
        if (error.response?.status === 409 && revision === sessionRevision.current) {
          try {
            const updatedCart = await cartApi.get()
            if (revision === sessionRevision.current) setSelection(updatedCart)
          } catch {
            if (revision === sessionRevision.current) setCartError('Could not refresh your bag. Try again.')
          }
        }
        throw error
      }
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

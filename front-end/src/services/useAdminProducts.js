import { displayProduct } from '../context/ShopContext'
import { productApi } from './api'
import { useRequest } from './useRequest'

async function loadProducts() {
  const products = await productApi.list({ manage: true })
  return products.map(displayProduct)
}

export function useAdminProducts() {
  return useRequest(loadProducts)
}

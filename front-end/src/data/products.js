import products from './products.json'

// Keep one product-data source while preserving the existing named export.
export const initialProducts = products

export const categories = [
  'All',
  'Skincare',
  'Makeup',
  'Haircare',
  'Body',
  'Fragrance'
]

export const brands = [
  'All',
  ...new Set(initialProducts.map((product) => product.brand))
]

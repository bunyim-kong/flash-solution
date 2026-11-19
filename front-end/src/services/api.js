import axios from 'axios'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// Used by the storefront and admin product pages.
export const productApi = {
  async getAll() {
    const response = await apiClient.get('/products')
    return response.data
  },

  async create(product) {
    const response = await apiClient.post('/products', product)
    return response.data
  },

  async update(id, product) {
    const response = await apiClient.put(`/products/${id}`, product)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/products/${id}`)
  }
}

// Used by the shop filters and product-form select options.
export const brandApi = {
  async getAll() {
    const response = await apiClient.get('/brands')
    return response.data
  }
}

export const categoryApi = {
  async getAll() {
    const response = await apiClient.get('/categories')
    return response.data
  }
}

// Used by the admin user-management page.
export const userApi = {
  async getAll() {
    const response = await apiClient.get('/users')
    return response.data
  },

  async create(user) {
    const response = await apiClient.post('/users', user)
    return response.data
  },

  async update(id, user) {
    const response = await apiClient.put(`/users/${id}`, user)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/users/${id}`)
  }
}

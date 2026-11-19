import axios from 'axios'

// Base URL of the Java Spring Boot REST API.
// Change VITE_API_BASE_URL in the .env file when the backend is ready.
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// Create one Axios instance so all API requests share the same configuration.
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})

// Axios returns the response body inside response.data.
// async/await makes asynchronous HTTP requests easier to read.
export const productApi = {
  // GET /api/products
  async getAll() {
    try {
      const response = await apiClient.get('/products')
      return response.data
    } catch (error) {
      console.error('Unable to load products:', error)
      throw error
    }
  },

  // GET /api/products/{id}
  async getById(id) {
    try {
      const response = await apiClient.get(`/products/${id}`)
      return response.data
    } catch (error) {
      console.error(`Unable to load product ${id}:`, error)
      throw error
    }
  },

  // POST /api/products
  async create(productData) {
    try {
      const response = await apiClient.post('/products', productData)
      return response.data
    } catch (error) {
      console.error('Unable to create product:', error)
      throw error
    }
  },

  // PUT /api/products/{id}
  async update(id, productData) {
    try {
      const response = await apiClient.put(`/products/${id}`, productData)
      return response.data
    } catch (error) {
      console.error(`Unable to update product ${id}:`, error)
      throw error
    }
  },

  // DELETE /api/products/{id}
  async remove(id) {
    try {
      const response = await apiClient.delete(`/products/${id}`)
      return response.data
    } catch (error) {
      console.error(`Unable to delete product ${id}:`, error)
      throw error
    }
  }
}

export const brandApi = {
  // GET /api/brands
  async getAll() {
    try {
      const response = await apiClient.get('/brands')
      return response.data
    } catch (error) {
      console.error('Unable to load brands:', error)
      throw error
    }
  },

  async getById(id) {
    const response = await apiClient.get(`/brands/${id}`)
    return response.data
  },

  async create(brandData) {
    const response = await apiClient.post('/brands', brandData)
    return response.data
  },

  async update(id, brandData) {
    const response = await apiClient.put(`/brands/${id}`, brandData)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/brands/${id}`)
  }
}

export const categoryApi = {
  async getAll() {
    const response = await apiClient.get('/categories')
    return response.data
  },

  async getById(id) {
    const response = await apiClient.get(`/categories/${id}`)
    return response.data
  },

  async create(categoryData) {
    const response = await apiClient.post('/categories', categoryData)
    return response.data
  },

  async update(id, categoryData) {
    const response = await apiClient.put(`/categories/${id}`, categoryData)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/categories/${id}`)
  }
}

export const userApi = {
  async getAll() {
    const response = await apiClient.get('/users')
    return response.data
  },

  async getById(id) {
    const response = await apiClient.get(`/users/${id}`)
    return response.data
  },

  async create(userData) {
    const response = await apiClient.post('/users', userData)
    return response.data
  },

  async update(id, userData) {
    const response = await apiClient.put(`/users/${id}`, userData)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/users/${id}`)
  }
}

export const authApi = {
  // POST /api/auth/login
  async login(credentials) {
    try {
      const response = await apiClient.post('/auth/login', credentials)
      return response.data
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  },

  // POST /api/auth/register
  async register(accountData) {
    try {
      const response = await apiClient.post('/auth/register', accountData)
      return response.data
    } catch (error) {
      console.error('Registration failed:', error)
      throw error
    }
  }
}

export const customerOrderApi = {
  async getAll() {
    const response = await apiClient.get('/customer-orders')
    return response.data
  },

  async getById(id) {
    const response = await apiClient.get(`/customer-orders/${id}`)
    return response.data
  },

  async create(userId, orderData) {
    const response = await apiClient.post('/customer-orders', orderData, {
      params: { userId }
    })
    return response.data
  },

  async update(id, orderData) {
    const response = await apiClient.put(`/customer-orders/${id}`, orderData)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/customer-orders/${id}`)
  }
}

export const orderItemApi = {
  async getAll() {
    const response = await apiClient.get('/order-items')
    return response.data
  },

  async getById(id) {
    const response = await apiClient.get(`/order-items/${id}`)
    return response.data
  },

  async create(orderItemData) {
    const response = await apiClient.post('/order-items', orderItemData)
    return response.data
  },

  async update(id, orderItemData) {
    const response = await apiClient.put(`/order-items/${id}`, orderItemData)
    return response.data
  },

  async remove(id) {
    await apiClient.delete(`/order-items/${id}`)
  }
}

export default apiClient

import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

api.interceptors.request.use(config => {
  if (['post', 'put', 'patch', 'delete'].includes(config.method)) {
    config.headers.set('X-Lumiere-Request', '1')
  }
  return config
})

export const sessionExpiredEvent = 'lumiere:session-expired'

api.interceptors.response.use(undefined, error => {
  if (error.response?.status === 401 && !error.config?.url?.startsWith('/auth/')) {
    window.dispatchEvent(new Event(sessionExpiredEvent))
  }
  return Promise.reject(error)
})

export const errorMessage = error =>
  error.response?.data?.message || (error.response ? 'The request could not be completed.' : 'Could not reach the server. Try again.')

export function resource(path) {
  return {
    list: async params => (await api.get(path, { params })).data,
    get: async (id, params) => (await api.get(`${path}/${id}`, { params })).data,
    create: async body => (await api.post(path, body)).data,
    update: async (id, body) => (await api.put(`${path}/${id}`, body)).data,
    remove: async id => { await api.delete(`${path}/${id}`) },
  }
}

export const productApi = resource('/products')
export const brandApi = resource('/brands')
export const categoryApi = resource('/categories')
export const userApi = resource('/users')

export const authApi = {
  me: async () => (await api.get('/auth/me')).data,
  login: async body => (await api.post('/auth/login', body)).data,
  register: async body => (await api.post('/auth/register', body)).data,
  logout: async () => { await api.post('/auth/logout') },
}

export const cartApi = {
  get: async () => (await api.get('/cart')).data,
  add: async id => (await api.post(`/cart/items/${id}`)).data,
  update: async (id, quantity) => (await api.put(`/cart/items/${id}`, { quantity })).data,
  remove: async id => (await api.delete(`/cart/items/${id}`)).data,
  save: async (id, saved) => (await api.put(`/cart/saved/${id}`, { saved })).data,
}

export const orderApi = {
  create: async body => (await api.post('/orders', body)).data,
  mine: async () => (await api.get('/orders/my')).data,
  all: async () => (await api.get('/admin/orders')).data,
}

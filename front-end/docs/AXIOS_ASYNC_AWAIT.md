# Axios and async/await – Brief 15

The React front end uses **Axios** to send HTTP requests to the **Java Spring Boot REST API**.

## 1. Install Axios

```bash
npm install axios
```

## 2. Example: retrieve products

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

export async function getProducts() {
  const response = await api.get('/products')
  return response.data
}
```

## 3. What do `async` and `await` mean?

- `async` marks a function as asynchronous. It allows the function to use `await` and returns a Promise.
- `await` pauses that function until the Promise settles, so the next line can use the result.
- `api.get('/products')` sends an HTTP GET request to the Spring Boot API.
- `response.data` contains the response body returned by the API.

## 4. Example: use the service in a React component

```jsx
import { useEffect, useState } from 'react'
import { productApi } from '../services/api'

export default function ProductList() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadProducts() {
      try {
        const data = await productApi.getAll()
        setProducts(data)
      } catch (error) {
        setError('Could not load products. Please try again.')
      }
    }

    loadProducts()
  }, [])

  if (error) return <p>{error}</p>

  return (
    <ul>
      {products.map(product => (
        <li key={product.id}>{product.name}</li>
      ))}
    </ul>
  )
}
```

## Important for the current project

The storefront now loads products, brands, and categories through this Axios service, and the admin product and user pages use real backend CRUD. Start Spring Boot before the frontend. The catalogue deliberately starts empty and shows an error if the API cannot be reached; it does not silently replace backend data with mock products. The cart is the only shop state that remains in browser local storage.

# Lumière Frontend and Backend Data Integration

## Purpose

This document explains how the React frontend fetches data from the Spring Boot backend. It follows the beginner-friendly request flow:

```text
React page -> ShopContext or page function -> Axios service -> Spring Boot API -> MySQL
```

The frontend fetch cleanup did not modify backend source code.

## Start the applications

Start the backend:

```bash
cd back-end
./mvnw spring-boot:run
```

Start the frontend in another terminal:

```bash
cd front-end
npm install
npm run dev
```

Axios uses this API URL by default:

```text
http://localhost:8080/api
```

To use another URL, create `front-end/.env`:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

## Clean frontend data structure

### `src/services/api.js`

This file contains one shared Axios client and only the requests currently used by the interface.

```javascript
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
})
```

The service exports four API groups:

- `productApi`: list, create, update, and delete products.
- `brandApi`: list brands.
- `categoryApi`: list categories.
- `userApi`: list, create, update, and delete users.

Unused methods were removed. The frontend no longer declares API calls for authentication, customer orders, order items, brand management, or category management because no current page uses those calls.

### `src/context/ShopContext.jsx`

The context is responsible for shared catalogue data. When the app starts, it loads products, brands, and categories together:

```javascript
const [productData, brandData, categoryData] = await Promise.all([
  productApi.getAll(),
  brandApi.getAll(),
  categoryApi.getAll()
])
```

`Promise.all` starts the three independent GET requests together. After they finish, the context updates the React state:

```javascript
setProducts(productData.map(normalizeProduct))
setBrands(brandData)
setCategories(categoryData)
```

The context also provides loading, error, retry, product create/update, and product delete behavior.

The cart still uses `localStorage` because the backend has no cart table. Product catalogue data does not use `localStorage` or mock fallback data.

## Backend tables and frontend usage

### Brands table

| Database column | Backend field | Frontend use |
|---|---|---|
| `id` | `id` | Product relationship and option key |
| `name` | `name` | Shop filter and product-form label |
| `slug` | `slug` | Backend URL-friendly identifier |

Frontend request:

```text
GET /api/brands
```

Fetch location: `ShopContext.jsx` calls `brandApi.getAll()` when the app starts.

Used by:

- `Shop.jsx` for brand filter buttons.
- `AdminProductForm.jsx` for the brand selector.

The frontend currently does not create, edit, or delete brands.

### Categories table

| Database column | Backend field | Frontend use |
|---|---|---|
| `id` | `id` | Product relationship and option key |
| `name` | `name` | Shop filter and product-form label |
| `slug` | `slug` | Backend URL-friendly identifier |
| `description` | `description` | Available category description |
| `active` | `active` | Hides inactive category choices |

Frontend request:

```text
GET /api/categories
```

Fetch location: `ShopContext.jsx` calls `categoryApi.getAll()` when the app starts.

Used by:

- `Shop.jsx` for active category filters.
- `AdminProductForm.jsx` for the category selector.

The frontend currently does not create, edit, or delete categories.

### Products table

| Database column | Backend field | Frontend use |
|---|---|---|
| `id` | `id` | Product detail and admin edit route |
| `sku` | `sku` | Catalogue reference |
| `name` | `name` | Product title |
| `brand_id` | `brand` | Nested brand relationship |
| `category_id` | `category` | Nested category relationship |
| `price` | `price` | Product and cart price |
| `description` | `description` | Product details |
| `image_url` | `imageUrl` | Product image |
| `stock` | `stock` | Availability and admin stock count |
| `product_status` | `productStatus` | `ACTIVE`, `DRAFT`, or `ARCHIVED` |
| `featured` | `featured` | Featured product label |
| `created_at` | `createdAt` | Created timestamp |
| `updated_at` | `updatedAt` | Updated timestamp |

Frontend requests:

```text
GET    /api/products
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Fetch location: `ShopContext.jsx` calls `productApi.getAll()` when the app starts.

Used by:

- `Home.jsx`: featured product section.
- `Shop.jsx`: catalogue, filtering, search, and sorting.
- `ProductDetail.jsx`: finds the selected product by database ID.
- `AdminDashboard.jsx`: product totals, stock value, and recent catalogue items.
- `AdminProducts.jsx`: product list and delete action.
- `AdminProductForm.jsx`: create and update actions.
- `Cart.jsx`: receives a fetched product when it is added to the cart.

Spring Boot returns nested brand and category objects and an uppercase status. `normalizeProduct` converts the response to the shape already used by the design:

```text
brand.name                 -> brand
brand.id                   -> brandId
category.name              -> category
category.id                -> categoryId
productStatus "ACTIVE"     -> status "Active"
numeric id                 -> route-safe string id
price                      -> JavaScript Number
```

Create and update requests send relationship IDs:

```json
{
  "sku": "P-1011",
  "name": "Daily Hydrating Serum",
  "brand": { "id": 1 },
  "category": { "id": 1 },
  "price": 29.99,
  "stock": 12,
  "productStatus": "ACTIVE",
  "featured": false,
  "imageUrl": "/images/serum.svg",
  "description": "A daily serum."
}
```

### Users table

| Database column | Backend field | Frontend use |
|---|---|---|
| `id` | `id` | Account key |
| `name` | `name` | Account display name |
| `email` | `email` | Account email |
| `password` | write-only password | Sent only when creating a user |
| `role` | `role` | Admin or Customer selection |

Frontend requests:

```text
GET    /api/users
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

Fetch location: `AdminUsers.jsx` calls `userApi.getAll()` when the page opens.

Write locations:

- `addUser` calls `userApi.create()`.
- `updateRole` calls `userApi.update()`.
- `removeUser` calls `userApi.remove()`.

Authentication is not implemented. These CRUD requests do not create a working admin login session.

### Customer order table

| Database column | Backend field |
|---|---|
| `id` | `id` |
| `order_number` | `orderNumber` |
| `user_id` | `user` |
| `status` | `status` |
| `total_amount` | `totalAmount` |
| `customer_name` | `customerName` |
| `customer_email` | `customerEmail` |
| `phone_number` | `phoneNumber` |
| `address` | `address` |
| `map_link` | `mapLink` |
| `payment_method` | `paymentMethod` |

Current frontend status: not fetched.

Reason: the UI needs authenticated “my orders” behavior. Fetching every order from the public list endpoint would expose other customers' records.

### Order items table

| Database column | Backend field |
|---|---|
| `id` | `id` |
| `order_id` | `customerOrder` |
| `product_id` | `product` |
| `quantity` | `quantity` |
| `product_sku` | `productSku` |
| `product_name` | `productName` |
| `unit_price` | `unitPrice` |
| `line_total` | `lineTotal` |

Current frontend status: not fetched.

Reason: checkout should create an order and all order items in one backend transaction. The current frontend keeps checkout as a demo instead of risking a partially created order.

## Loading and error handling

The catalogue begins with empty arrays:

```javascript
const [products, setProducts] = useState([])
const [brands, setBrands] = useState([])
const [categories, setCategories] = useState([])
```

While fetching, pages use `catalogLoading`. If a request fails, `catalogError` stores the backend message or a friendly fallback. `reloadCatalog` lets the user try again.

Admin create, update, and delete functions wait for the Axios request to succeed before changing the displayed data.

## Removed frontend code

The cleanup removed:

- `src/data/products.js`, because no current page uses static catalogue data.
- Unused `getById` API methods.
- Unused brand and category write methods.
- Unused authentication methods for endpoints that do not exist.
- Unused customer-order and order-item API methods.
- Repeated `try/catch` blocks inside every Axios method. Errors now pass to the page or context that can display them meaningfully.

## Current source files

```text
front-end/src/services/api.js
front-end/src/context/ShopContext.jsx
front-end/src/pages/Home.jsx
front-end/src/pages/Shop.jsx
front-end/src/pages/ProductDetail.jsx
front-end/src/pages/AdminDashboard.jsx
front-end/src/pages/AdminProducts.jsx
front-end/src/pages/AdminProductForm.jsx
front-end/src/pages/AdminUsers.jsx
```


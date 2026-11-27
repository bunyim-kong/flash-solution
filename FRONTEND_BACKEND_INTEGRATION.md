# Lumière frontend and backend integration

React sends requests through the Axios client in `front-end/src/services/api.js`. Vite proxies `/api` to Spring Boot, which reads and writes MySQL data. The same-origin URL lets the browser send its session cookie.

## Local setup

Follow the [backend setup](back-end/README.md#first-run) for MySQL, the first administrator, and the optional sample catalogue. Start each app from its own directory.

| Directory | Command |
| --- | --- |
| `back-end` | `./mvnw spring-boot:run` |
| `front-end` | `npm install`, then `npm run dev` |

Use `VITE_API_BASE_URL=/api` and set `API_PROXY_TARGET` if Spring runs somewhere other than `http://localhost:8080`. See the [frontend setup](front-end/README.md) for the production proxy configuration.

## Requests by feature

| Feature | Spring endpoints |
| --- | --- |
| Browse the catalogue | `GET /api/products`, `GET /api/products/{id}`, `GET /api/brands`, `GET /api/categories` |
| Account session | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`, `POST /api/auth/logout` |
| Cart and saved products | `GET /api/cart`, `POST/PUT/DELETE /api/cart/items/{productId}`, `PUT /api/cart/saved/{productId}` |
| Checkout and history | `POST /api/orders`, `GET /api/orders/my` |
| Admin catalogue | Product, brand, and category create, update, and delete endpoints. The product and category lists support `?manage=true` for administrators. |
| Admin accounts and orders | `/api/users` and `GET /api/admin/orders` |

API writes use the shared `X-Lumiere-Request: 1` header. Spring authorizes the session on protected requests. Checkout verifies the items and prices reviewed in the browser before using the current server prices and stock to record a cash-on-delivery order. The request ID supports safe retries.

## Data used by the screens

| Data | Where it is used |
| --- | --- |
| Brand and category IDs, names, and slugs | Product relationships, shop filters, and selectors in the admin product form. The public category list shows active categories. |
| Product ID, SKU, name, description, image, price, and stock | Product details, cards, cart rows, and the admin catalogue. The management view includes draft and archived products. |
| Product status and featured flag | The public catalogue includes active products in active categories. The home page shows products marked as featured. |
| User ID, name, email, role, and account status | The session, sign-in status, and user management. Passwords are accepted on account creation or password updates and are excluded from API responses. |
| Cart quantity by product ID and saved product IDs | Guest selections live in the session. Account selections persist in MySQL and are available after signing in on another device. |
| Order reference, customer and delivery details, status, and total | Checkout confirmation, customer order history, and the protected admin order list. |
| Order item SKU, product name, quantity, unit price, and line total | Order history uses the values saved at checkout, including after a product is edited or archived. |

Product writes send brand and category relationships as objects containing their database IDs, for example `"brand": { "id": 1 }`. The response includes the related records, so the storefront can display their names.

## Verification

Run `npm run build` in the frontend and `./mvnw test` in the backend. The backend test profile uses H2. The older PDF in this directory documents an earlier integration.

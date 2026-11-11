# Lumière Beauty Shop Backend

## MVP Scope Decision

**Project:** Flash Solution cosmetics shop backend API  
**Database:** PostgreSQL  
**Decision date:** 3 November 2026

## 1. Scope decision

The team will build a Spring Boot REST API using PostgreSQL for the Lumière Beauty cosmetics shop.

The MVP will support:

- Public product browsing
- Brands and categories
- Product and inventory management
- Customer registration and login
- Customer and administrator roles
- Customer checkout and order creation
- Customer order history
- Administrator order viewing
- Validation, error handling, testing, API documentation, and front-end integration

This scope is realistic for the 13-day project and matches the existing front-end screens and assessment requirements.

## 2. Features included

### Customer storefront

- View active products
- View product details
- Search products
- Filter by brand and category
- Sort products by price or name
- Add products to the shopping cart
- Submit an order
- View personal order history

### Authentication and authorization

- Register a customer account
- Log in with email and password
- Hash passwords securely
- Use authenticated sessions or JWT access tokens
- Support `CUSTOMER` and `ADMIN` roles
- Protect administrator endpoints on the backend
- Prevent customers from accessing administrator functions

### Administrator functions

- Create products
- View products
- Update products
- Delete or deactivate products
- Manage price, description, image URL, status, featured flag, and stock
- View customer orders
- View basic user accounts and roles if time allows

### Inventory

Inventory will be kept simple for the MVP. Stock will be stored on the product record as `stock_quantity`.

When an order is successfully created:

1. The backend checks that every product exists.
2. The backend checks that enough stock is available.
3. The backend calculates the total using database product prices.
4. The backend creates the order and order items.
5. The backend reduces the product stock inside a database transaction.

## 3. Main API endpoints

### Products

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products              ADMIN
PUT    /api/products/{id}         ADMIN
DELETE /api/products/{id}         ADMIN
```

Supported catalogue filters:

```text
GET /api/products?brand=Dior&category=Makeup&search=lip
```

### Brands and categories

```text
GET /api/brands
GET /api/categories
```

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Orders

```text
POST /api/orders                CUSTOMER
GET  /api/orders/my             CUSTOMER
GET  /api/admin/orders          ADMIN
```

The order request should contain customer/delivery information and the selected product IDs with quantities. The backend must not trust prices or totals sent by the front end.

## 4. PostgreSQL database tables

The initial database design will contain these tables:

```text
users
brands
categories
products
orders
order_items
```

### Product fields

```text
id
sku
name
brand_id
category_id
price
description
image_url
stock_quantity
status
featured
created_at
updated_at
```

The existing sample identifiers such as `P-1001` should be stored as a unique `sku`. The database primary key should be a generated numeric ID or UUID.

## 5. Validation and error handling

The backend should validate:

- Required product name
- Positive or zero product price
- Positive or zero stock quantity
- Valid product status
- Unique user email
- Strong enough password
- Positive order quantities
- Existing product IDs
- Sufficient product stock

Suggested HTTP status codes:

```text
200 OK                 Successful read or update
201 Created            Successful creation
204 No Content         Successful deletion
400 Bad Request        Invalid request data
401 Unauthorized       Missing or invalid login
403 Forbidden          Insufficient permissions
404 Not Found          Resource does not exist
409 Conflict           Duplicate data or insufficient stock
500 Internal Server Error Unexpected server failure
```

Error responses should use one consistent JSON structure, for example:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "price": "Price must be greater than or equal to zero"
  },
  "timestamp": "2026-11-03T12:00:00Z"
}
```

## 6. Features excluded from the MVP

The following features are outside the first release:

- Online payment gateway
- Shipping-provider integration
- Discount and coupon system
- Product reviews and ratings
- Wishlist
- Email invitations
- Image upload service
- Persistent cart across multiple devices
- Detailed inventory movement history
- Advanced analytics and reporting

These can be recorded as future improvements if the core API is completed early.

## 7. MVP acceptance criteria

The MVP is complete when:

- A visitor can retrieve and filter active products from the API.
- An administrator can securely create, update, and delete products.
- Product stock is stored in PostgreSQL.
- A customer can register and log in.
- The backend enforces customer and administrator permissions.
- A customer can place an order with valid products and quantities.
- The backend rejects invalid orders and insufficient stock.
- Stock decreases after a successful order.
- A customer can view their own orders.
- An administrator can view all orders.
- The front end communicates with the backend instead of using product mock data.
- Postman tests cover successful and invalid requests.
- Swagger/OpenAPI documentation is available.
- The API is deployable and documented in the project README.

## 8. Team decision statement

> The team agrees to build a Spring Boot REST API using PostgreSQL for a cosmetics retail shop. The MVP will manage products, brands, categories, stock, users, roles, and orders. Customers can browse products, register, log in, place orders, and view their order history. Administrators can securely manage products, inventory, users, and orders. Payment, shipping integrations, reviews, discounts, and advanced analytics are excluded from the MVP.


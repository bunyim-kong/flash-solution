# Admin area

Sign in at `/admin` with an administrator account. See the [backend first-run setup](../../back-end/README.md#first-run) to create the first administrator.

The dashboard includes draft and archived products. Administrators can manage products, brands, categories, and accounts and view customer orders. Existing order items retain their product details when a product is archived. Suspending an account removes access while preserving its order history.

The browser uses a session cookie, and Spring enforces the administrator role on management endpoints. The frontend routes provide navigation and sign-in redirects.

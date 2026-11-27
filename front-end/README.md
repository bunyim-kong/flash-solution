# Lumière Frontend

React frontend for the Lumière cosmetics shop. It includes the customer storefront and admin pages.

## Technologies

- React 18
- Vite 6
- React Router 6
- Tailwind CSS 3
- Axios
- Lucide React
- Node.js and npm

## Requirements

- Node.js 18 or newer
- npm
- Lumière backend running on port `8080` by default

## Installation

From the project root, open the frontend directory and install the dependencies:

```bash
cd front-end
npm install
```

Create the local environment file:

```bash
cp .env.example .env
```

On Windows Command Prompt, use:

```bat
copy .env.example .env
```

The default API configuration uses the Vite proxy:

```env
VITE_API_BASE_URL=/api
API_PROXY_TARGET=http://localhost:8080
```

Set `API_PROXY_TARGET` if Spring runs at a different address. Use the relative `/api` path so the browser sends its session cookie to the same origin.

## Run Instructions

Start the development server:

```bash
npm run dev
```

Open the URL shown by Vite, normally `http://localhost:5173`.

## Production Build

Create and preview a production build:

```bash
npm run build
npm run preview
```

The generated production files are placed in `dist/`.

## Notes

For production hosting, serve `dist/` with an SPA fallback and forward `/api/*` to Spring on the same origin. Set `SESSION_COOKIE_SECURE=true` on the backend when serving over HTTPS.

The shop, cart, saved products, accounts, checkout, order history, and admin pages use Spring. Older browser demo data is separate from the server catalogue and accounts. See the [backend setup](../back-end/README.md) to bootstrap an administrator and optionally load the sample catalogue.

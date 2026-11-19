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
- Lumière backend running on port `8080` when using the API

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

The default API URL in `.env` is:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Change it if the backend runs at a different address.

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

The admin area is included for interface and API analysis. Authentication and authorization are not implemented; the routes are not protected. Add Spring Security and role-based authorization in the backend before using a real admin account.

## Backend integration
Products, brands, categories, and admin users are loaded from the Java Spring Boot REST API. Admin create, edit, and delete actions write through the API to MySQL. The shopping cart remains in browser local storage because the backend does not have a cart table.

The API service layer is in `src/services/api.js` and uses **Axios with async/await**. It uses `http://localhost:8080/api` by default; set `VITE_API_BASE_URL` in `.env` to use another URL. The original `src/data/products.js` file remains only as a reference for the initial catalogue and is not imported by the application. See `back-end/docs/FRONTEND_BACKEND_INTEGRATION.md` for the complete table, endpoint, mapping, and screen-by-screen fetch documentation.

## Teaching note
The product list and admin CRUD interface are designed to help Junior Backend Developers identify entities, fields, validation rules, API endpoints, HTTP methods, and expected responses. Sample data is illustrative and can be changed by the project team.


Store navigation includes Home, Shop all, About us and Contact. The storefront uses a warm ivory, espresso and champagne-gold palette. The contact page contains demo content; add verified business contact details before publishing.


## Visual identity — Champagne, Rosewood, Sage & Gold

The storefront and admin workspace share one coordinated palette:
- Pearl/champagne `#F8F4EE` for the main canvas and soft surfaces
- Rosewood `#805B50` for primary buttons and active navigation
- Deep wine `#6E3037` for high-emphasis details
- Botanical sage `#65745D` for secondary accents and reassurance icons
- Champagne gold `#C6A46A` for subtle borders and premium details
- Espresso `#302722` for readable headings and body text

The Contact page contains demo placeholders. Add verified business contact details before publishing.


## Version 8 — Complete storefront and admin visual refresh

This version applies the approved Lumière Beauty visual direction throughout the app:
- Champagne/pearl backgrounds and soft blush surfaces
- Rosewood primary buttons, active states and key actions
- Deep wine emphasis for headings and important navigation
- Sage secondary accents for botanical cues
- Champagne-gold borders and decorative details
- Refreshed responsive announcement bar, header, hero, category tiles, reassurance strip and footer
- Coordinated styling across shop, product, cart, checkout, account, About, Contact and admin screens

The existing local SVG product illustrations remain demo assets. Replace them with licensed product photography when preparing a production storefront.


## Contact and Admin User Management
- Contact email shown in the storefront: LumiereBeauty@gmail.com
- Customer support hours shown: 24/7
- Admin workspace includes a User Management page at `/admin/users` with backend list, create, role update, and delete actions.
- Passwords are hashed by the backend and omitted from API responses. Authentication and authorization are still not implemented; production use requires protected admin endpoints and login/session handling.


## Real product images
The demo catalogue now uses product packshot image URLs for the sample products (Dior Capture Youth Glow Booster, Rare Beauty Soft Pinch Liquid Blush, Fenty Beauty Pro Filt'r Foundation, Rouge Dior lipstick, The Ordinary Niacinamide serum, CeraVe Hydrating Facial Cleanser, Miss Dior Eau de Parfum, Beauty of Joseon Relief Sun, and Laneige Lip Sleeping Mask). Product cards, product details, admin thumbnails and homepage featured imagery use these catalogue images. Images are loaded from third-party brand/retailer image hosts, so an image host may change or block external access. For production, obtain permission or use image assets supplied/licensed by the brands or distributors and host them on your own server.


## V19 Sage & Pearl colour correction

The interface uses the Sage & Pearl palette consistently. Purple/lavender theme values from the previous version have been replaced with sage, olive, and warm pearl tones.

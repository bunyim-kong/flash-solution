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

The API client is located at `src/services/api.js`. Some storefront features still use sample data or browser local storage and are not fully connected to the backend.

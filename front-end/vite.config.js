import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const { API_PROXY_TARGET } = loadEnv(mode, process.cwd(), '')
  // Preserve the browser-facing host so Spring treats proxied requests as same-origin.
  const proxy = { '/api': { target: API_PROXY_TARGET || 'http://localhost:8080' } }
  return { plugins: [react()], server: { proxy }, preview: { proxy } }
})

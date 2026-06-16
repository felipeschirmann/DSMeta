/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const isTest = mode === 'test' || !!process.env.VITEST;
  
  return {
    plugins: [
      !isTest && react()
    ].filter(Boolean) as any,
    test: {
      globals: true,
      environment: 'jsdom',
      setupFiles: './src/setupTests.ts',
      css: false,
      coverage: {
        provider: 'v8',
        reporter: ['text', 'lcov'],
        exclude: ['node_modules/', 'src/setupTests.ts', 'src/main.tsx', 'src/vite-env.d.ts', '**/*.css', '**/*.svg', '**/*.png']
      }
    }
  }
})

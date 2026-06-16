import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import { PluginOption } from 'vite'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const isTest = mode === 'test' || !!process.env.VITEST;
  
  return {
    plugins: [
      !isTest && react()
    ].filter(Boolean) as PluginOption[],
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

/// <reference types="vitest/config" />
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// The controller to proxy /v2 to in `npm run dev`, including its context path
const tank = new URL(process.env.TANK_URL ?? 'http://localhost:8080/tank');

export default defineConfig(({ command }) => ({
  // Built asset URLs are relative and resolve against <base href>, which the controller sets to the
  // WAR's context path, so one build works under /tank or at the root
  base: command === 'build' ? './' : '/app/',
  plugins: [react()],
  build: {
    // Packaged into the tank-web-react jar, which Tomcat and Spring serve as /app/**
    outDir: 'target/classes/META-INF/resources/app',
    emptyOutDir: true,
    // esbuild writes CSS as ASCII (its default charset), keeping escapes such as primeicons'
    // content: "\e963". lightningcss, Vite's default, turns them into raw UTF-8 glyphs, which garble
    // whenever a browser decodes the stylesheet with another charset. Escaping inside the minifier
    // also keeps the content hash in the file name honest.
    cssMinify: 'esbuild',
    rolldownOptions: {
      output: {
        // The core libraries every page needs change less often than the app, so they get their own
        // long-cached chunk; the rest (PrimeReact included) splits along the lazily loaded routes
        codeSplitting: {
          groups: [
            {
              name: 'vendor',
              test: /node_modules[\\/](react|react-dom|scheduler|react-router|@tanstack|openapi-fetch)[\\/]/,
            },
          ],
        },
      },
    },
  },
  server: {
    proxy: {
      '/v2': {
        target: tank.origin,
        changeOrigin: true,
        secure: false,
        rewrite: (path) => tank.pathname.replace(/\/$/, '') + path,
        // The session and XSRF-TOKEN cookies are scoped to the context path; the dev server is at /
        cookiePathRewrite: { [tank.pathname.replace(/\/$/, '') || '/']: '/' },
      },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['src/test/setup.ts'],
  },
}));

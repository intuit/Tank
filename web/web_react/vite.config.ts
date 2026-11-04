/// <reference types="vitest/config" />
import react from '@vitejs/plugin-react';
import { defineConfig, type Plugin } from 'vite';

// The controller to proxy /v2 to in `npm run dev`, including its context path
const tank = new URL(process.env.TANK_URL ?? 'http://localhost:8080/tank');

// lara-light-blue hardcodes Tailwind's bright blue-500 (#3b82f6) and its shades, so every
// component that uses it (checkboxes, toggles, paginator, selection, focus rings) is remapped onto
// a softer scale built around PrimeFaces redmond's blues, which the JSF UI uses
const REDMOND_PALETTE: Record<string, string> = {
  '#3b82f6': '#4a8bc2', // primary
  '#2563eb': '#3f76a5', // primary hover
  '#1d4ed8': '#2e6e9e', // highlight text, primary active (redmond's ui-state-default text)
  '#bfdbfe': '#c5dbec', // focus ring (redmond's ui-state-default border)
  '#9dc1fb': '#9fc9ea', // button focus ring
  '#eff6ff': '#e8f2fb', // highlight background
  '59, 130, 246': '74, 139, 194', // rgba() tints of the primary
  '219, 234, 254': '223, 239, 252', // rgba() tint of blue-100
  '#f5f9ff': '#f6f9fc', // --primary-50 … --primary-900
  '#d0e1fd': '#dbe8f3',
  '#abc9fb': '#b7d1e7',
  '#85b2f9': '#92b9da',
  '#609af8': '#6ea2ce',
  '#326fd1': '#3f76a5',
  '#295bac': '#346188',
  '#204887': '#294c6b',
  '#183462': '#1e384e',
};

function redmondPalette(): Plugin {
  const pattern = new RegExp(Object.keys(REDMOND_PALETTE).join('|'), 'gi');
  return {
    name: 'tank-redmond-palette',
    enforce: 'pre',
    transform(code, id) {
      if (!/lara-light-blue[\\/]theme\.css$/.test(id.replace(/\?.*/, ''))) return null;
      return { code: code.replace(pattern, (color) => REDMOND_PALETTE[color.toLowerCase()] ?? color), map: null };
    },
  };
}

export default defineConfig(({ command }) => ({
  // Built asset URLs are relative and resolve against <base href>, which the controller sets to the
  // WAR's context path, so one build works under /tank or at the root
  base: command === 'build' ? './' : '/app/',
  plugins: [react(), redmondPalette()],
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
    // above the 5 s findBy wait in setup.ts, so a slow find fails with its own message
    testTimeout: 15000,
  },
}));

import createClient, { type Middleware } from 'openapi-fetch';
import type { components, paths } from './schema';

export type { components, paths };
export type Schemas = components['schemas'];

/** Body of every /v2 error response (GenericExceptionHandler → SimpleErrorResponseBody). */
export interface TankApiError {
  message: string;
  debugInfo?: string;
}

/** Names fixed by rest-mvc's CsrfTokens. */
export const CSRF_COOKIE = 'XSRF-TOKEN';
export const CSRF_HEADER = 'X-XSRF-TOKEN';

const SAFE_METHODS = new Set(['GET', 'HEAD', 'OPTIONS', 'TRACE']);

export function readCookie(name: string, cookies: string = document.cookie): string | undefined {
  for (const part of cookies.split(';')) {
    const eq = part.indexOf('=');
    if (eq > 0 && part.slice(0, eq).trim() === name) {
      return decodeURIComponent(part.slice(eq + 1).trim());
    }
  }
  return undefined;
}

/**
 * The servlet context path the SPA is served under, e.g. "/tank" for /tank/app/projects.
 * Empty when the SPA runs at the root (the Vite dev server).
 */
export function contextPath(pathname: string = window.location.pathname): string {
  const i = pathname.indexOf('/app/');
  return i >= 0 ? pathname.slice(0, i) : '';
}

/** Echoes the XSRF-TOKEN cookie in the X-XSRF-TOKEN header on state-changing requests. */
export function csrfMiddleware(getCookie: (name: string) => string | undefined = readCookie): Middleware {
  return {
    onRequest({ request }) {
      if (!SAFE_METHODS.has(request.method.toUpperCase())) {
        const token = getCookie(CSRF_COOKIE);
        if (token) {
          request.headers.set(CSRF_HEADER, token);
        }
      }
      return request;
    },
  };
}

/** Calls the handler when the session is missing or expired, so the app can send the user to login. */
export function unauthorizedMiddleware(onUnauthorized: (response: Response) => void): Middleware {
  return {
    onResponse({ response }) {
      if (response.status === 401) {
        onUnauthorized(response);
      }
      return response;
    },
  };
}

export interface TankClientOptions {
  /** Prefix for /v2 paths; defaults to the context path the SPA is served under. */
  baseUrl?: string;
  onUnauthorized?: (response: Response) => void;
  fetch?: typeof globalThis.fetch;
}

/**
 * Typed client for the Tank /v2 API. Authenticates with the session cookie set by POST /v2/auth/login
 * (or SSO), so requests must stay same-origin.
 */
export function createTankClient(options: TankClientOptions = {}) {
  const client = createClient<paths>({
    baseUrl: options.baseUrl ?? contextPath(),
    credentials: 'same-origin',
    fetch: options.fetch,
  });
  client.use(csrfMiddleware());
  if (options.onUnauthorized) {
    client.use(unauthorizedMiddleware(options.onUnauthorized));
  }
  return client;
}

export type TankClient = ReturnType<typeof createTankClient>;

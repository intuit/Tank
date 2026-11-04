import type { ErrorResponse } from './client';

/** Thrown by query functions so TanStack Query treats a non-2xx response as an error. */
export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

/** Longest plain-text error body shown as is; anything longer is likely a page, not a message */
const MAX_TEXT_MESSAGE = 300;

/**
 * The message in an error body: an ErrorResponse's `message`, or a plain-text body from the
 * framework handlers ("Incorrect request body"). An HTML error page from a proxy or Tomcat is
 * not a message.
 */
export function errorMessage(error: ErrorResponse | string | unknown): string | undefined {
  if (typeof error === 'string') {
    const text = error.trim();
    return text && text.length <= MAX_TEXT_MESSAGE && !text.startsWith('<') ? text : undefined;
  }
  if (error && typeof error === 'object' && 'message' in error && typeof error.message === 'string') {
    return error.message.trim() || undefined;
  }
  return undefined;
}

/** The server's message for a failed call, or a generic one for its status. */
export function apiErrorMessage(error: ErrorResponse | string | unknown, response: Response, action: string): string {
  const message = errorMessage(error);
  if (message) {
    return message;
  }
  switch (response.status) {
    case 403:
      return `You don't have permission to ${action}`;
    case 404:
      return `Couldn't ${action}: it no longer exists`;
    default:
      return `Couldn't ${action} (error ${response.status})`;
  }
}

export function toApiError(error: ErrorResponse | string | unknown, response: Response, action: string): ApiError {
  return new ApiError(apiErrorMessage(error, response, action), response.status);
}

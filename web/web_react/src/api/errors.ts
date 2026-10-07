import type { TankApiError } from './client';

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

/** The server's message for a failed call, or a generic one for its status. */
export function apiErrorMessage(error: unknown, response: Response, action: string): string {
  const message = (error as Partial<TankApiError> | undefined)?.message;
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

export function toApiError(error: unknown, response: Response, action: string): ApiError {
  return new ApiError(apiErrorMessage(error, response, action), response.status);
}

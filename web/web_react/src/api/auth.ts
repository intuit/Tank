import apiClient from './client';
import type { AuthUser, LoginRequest } from '../types/auth';

export const authApi = {
  login: (req: LoginRequest) =>
    apiClient.post<AuthUser>('/v2/auth/token', req),
};

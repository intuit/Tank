import apiClient from './client';
import type { UserTO, UserRequest } from '../types/user';

export const usersApi = {
  getAll: () => apiClient.get<UserTO[]>('/v2/users'),
  getById: (id: number) => apiClient.get<UserTO>(`/v2/users/${id}`),
  getGroups: () => apiClient.get<string[]>('/v2/users/groups'),
  create: (request: UserRequest) => apiClient.post<UserTO>('/v2/users', request),
  update: (id: number, request: UserRequest) => apiClient.put<UserTO>(`/v2/users/${id}`, request),
  delete: (id: number) => apiClient.delete(`/v2/users/${id}`),
  generateToken: (id: number) => apiClient.post<{ apiToken: string }>(`/v2/users/${id}/token`),
  deleteToken: (id: number) => apiClient.delete(`/v2/users/${id}/token`),
};

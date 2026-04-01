import apiClient from './client';
import type { FilterContainer, FilterTO, FilterGroupContainer, FilterGroupTO } from '../types/filter';

export const filtersApi = {
  getAll: () => apiClient.get<FilterContainer>('/v2/filters'),
  getById: (id: number) => apiClient.get<FilterTO>(`/v2/filters/${id}`),
  delete: (id: number) => apiClient.delete(`/v2/filters/${id}`),

  getGroups: () => apiClient.get<FilterGroupContainer>('/v2/filters/groups'),
  getGroupById: (id: number) => apiClient.get<FilterGroupTO>(`/v2/filters/groups/${id}`),
  deleteGroup: (id: number) => apiClient.delete(`/v2/filters/groups/${id}`),
  // TODO: backend endpoint POST /v2/filters/groups needs to be created
  createGroup: (group: { name: string; productName?: string }) =>
    apiClient.post<FilterGroupTO>('/v2/filters/groups', group),
};

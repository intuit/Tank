import apiClient from './client';
import type { FilterContainer, FilterTO, FilterGroupContainer, FilterGroupTO } from '../types/filter';

export const filtersApi = {
  getAll: () => apiClient.get<FilterContainer>('/v2/filters'),
  getById: (id: number) => apiClient.get<FilterTO>(`/v2/filters/${id}`),
  create: (filter: Omit<FilterTO, 'id'>) => apiClient.post<{ filterId: number }>('/v2/filters', filter),
  update: (id: number, filter: FilterTO) => apiClient.put<FilterTO>(`/v2/filters/${id}`, filter),
  delete: (id: number) => apiClient.delete(`/v2/filters/${id}`),

  getGroups: () => apiClient.get<FilterGroupContainer>('/v2/filters/groups'),
  getGroupById: (id: number) => apiClient.get<FilterGroupTO>(`/v2/filters/groups/${id}`),
  createGroup: (group: { name: string; productName?: string }) =>
    apiClient.post<{ filterGroupId: number }>('/v2/filters/groups', group),
  deleteGroup: (id: number) => apiClient.delete(`/v2/filters/groups/${id}`),
};

import apiClient from './client';
import type { ProjectContainer, ProjectTO, AutomationRequest } from '../types/project';

export const projectsApi = {
  getAll: () => apiClient.get<ProjectContainer>('/v2/projects'),
  getNames: () => apiClient.get<Record<number, string>>('/v2/projects/names'),
  getById: (id: number) => apiClient.get<ProjectTO>(`/v2/projects/${id}`),
  create: (request: AutomationRequest) =>
    apiClient.post<Record<string, string>>('/v2/projects', request),
  update: (id: number, request: AutomationRequest) =>
    apiClient.put<Record<string, string>>(`/v2/projects/${id}`, request),
  delete: (id: number) => apiClient.delete(`/v2/projects/${id}`),
  download: (id: number) =>
    apiClient.get(`/v2/projects/download/${id}`, { responseType: 'blob' }),
};

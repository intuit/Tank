import apiClient from './client';
import type { ProjectContainer, ProjectTO } from '../types/project';

export const projectsApi = {
  getAll: () => apiClient.get<ProjectContainer>('/v2/projects'),
  getNames: () => apiClient.get<Record<number, string>>('/v2/projects/names'),
  getById: (id: number) => apiClient.get<ProjectTO>(`/v2/projects/${id}`),
  delete: (id: number) => apiClient.delete(`/v2/projects/${id}`),
  download: (id: number) =>
    apiClient.get(`/v2/projects/download/${id}`, { responseType: 'blob' }),
};

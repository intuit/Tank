import apiClient from './client';
import type { ScriptDescriptionContainer, ScriptDescription, ExternalScriptTO, ExternalScriptContainer } from '../types/script';

export const scriptsApi = {
  getAll: () => apiClient.get<ScriptDescriptionContainer>('/v2/scripts'),
  getNames: () => apiClient.get<Record<number, string>>('/v2/scripts/names'),
  getById: (id: number) => apiClient.get<ScriptDescription>(`/v2/scripts/${id}`),
  delete: (id: number) => apiClient.delete(`/v2/scripts/${id}`),
  update: (id: number, data: { productName?: string; comments?: string }) =>
    apiClient.put<ScriptDescription>(`/v2/scripts/${id}`, data),
  download: (id: number) =>
    apiClient.get(`/v2/scripts/download/${id}`, { responseType: 'blob' }),
  downloadHarness: (id: number) =>
    apiClient.get(`/v2/scripts/harness/download/${id}`, { responseType: 'blob' }),
  upload: (formData: FormData) =>
    apiClient.post<Record<string, string>>('/v2/scripts', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  applyFilters: (scriptId: number, filterIds: number[]) =>
    apiClient.post(`/v2/filters/apply-filters/${scriptId}`, { filterIds }),

  // External scripts
  getExternalAll: () => apiClient.get<ExternalScriptContainer>('/v2/scripts/external'),
  getExternalById: (id: number) => apiClient.get<ExternalScriptTO>(`/v2/scripts/external/${id}`),
  createExternal: (script: Partial<ExternalScriptTO>) =>
    apiClient.post<ExternalScriptTO>('/v2/scripts/external', script),
  deleteExternal: (id: number) => apiClient.delete(`/v2/scripts/external/${id}`),
};

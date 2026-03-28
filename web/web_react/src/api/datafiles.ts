import apiClient from './client';
import type { DataFileDescriptorContainer, DataFileDescriptor } from '../types/datafile';

export const datafilesApi = {
  getAll: () => apiClient.get<DataFileDescriptorContainer>('/v2/datafiles'),
  getNames: () => apiClient.get<Record<number, string>>('/v2/datafiles/names'),
  getById: (id: number) => apiClient.get<DataFileDescriptor>(`/v2/datafiles/${id}`),
  getContent: (id: number, offset?: number, lines?: number) =>
    apiClient.get<string>(`/v2/datafiles/content`, {
      params: { id, offset, lines },
      transformResponse: [(data) => data],
    }),
  download: (id: number) =>
    apiClient.get(`/v2/datafiles/download/${id}`, { responseType: 'blob' }),
  upload: (formData: FormData) =>
    apiClient.post<Record<string, string>>('/v2/datafiles/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  delete: (id: number) => apiClient.delete(`/v2/datafiles/${id}`),
};

import apiClient from './client';

export const logsApi = {
  getFile: (filename: string, from?: string) =>
    apiClient.get(`/v2/logs/${filename}`, {
      responseType: 'text',
      params: from ? { from } : undefined,
    }),
};

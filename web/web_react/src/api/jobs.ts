import apiClient from './client';
import type { JobContainer, JobTO, CloudVmStatusContainer } from '../types/job';

export const jobsApi = {
  getAll: () => apiClient.get<JobContainer>('/v2/jobs'),
  getById: (id: number) => apiClient.get<JobTO>(`/v2/jobs/${id}`),
  getByProject: (projectId: number) =>
    apiClient.get<JobContainer>(`/v2/jobs/project/${projectId}`),
  getStatus: (jobId: number) =>
    apiClient.get<string>(`/v2/jobs/status/${jobId}`, {
      transformResponse: [(data) => data],
    }),
  getAllStatuses: () => apiClient.get<Array<Record<string, string>>>('/v2/jobs/status'),
  getInstanceStatuses: (jobId: number) =>
    apiClient.get<CloudVmStatusContainer>(`/v2/jobs/instance-status/${jobId}`),
  start: (jobId: number) => apiClient.get(`/v2/jobs/start/${jobId}`),
  stop: (jobId: number) => apiClient.get(`/v2/jobs/stop/${jobId}`),
  pause: (jobId: number) => apiClient.get(`/v2/jobs/pause/${jobId}`),
  resume: (jobId: number) => apiClient.get(`/v2/jobs/resume/${jobId}`),
  kill: (jobId: number) => apiClient.get(`/v2/jobs/kill/${jobId}`),
};

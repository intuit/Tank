import apiClient from './client';
import type { CloudVmStatus } from '../types/job';

export const agentsApi = {
  getInstanceStatus: (instanceId: string) =>
    apiClient.get<CloudVmStatus>(`/v2/agent/instance/status/${instanceId}`),
  stopInstance: (instanceId: string) =>
    apiClient.get<string>(`/v2/agent/instance/stop/${instanceId}`),
  pauseInstance: (instanceId: string) =>
    apiClient.get<string>(`/v2/agent/instance/pause/${instanceId}`),
  resumeInstance: (instanceId: string) =>
    apiClient.get<string>(`/v2/agent/instance/resume/${instanceId}`),
  killInstance: (instanceId: string) =>
    apiClient.get<string>(`/v2/agent/instance/kill/${instanceId}`),
};

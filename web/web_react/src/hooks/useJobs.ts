import { useQuery } from '@tanstack/react-query';
import { jobsApi } from '../api/jobs';

export const jobKeys = {
  all: ['jobs'] as const,
  byId: (id: number) => ['jobs', id] as const,
  byProject: (projectId: number) => ['jobs', 'project', projectId] as const,
  status: (jobId: number) => ['jobs', 'status', jobId] as const,
  instanceStatuses: (jobId: number) => ['jobs', 'instance-status', jobId] as const,
  allStatuses: ['jobs', 'statuses'] as const,
};

export function useJobs() {
  return useQuery({
    queryKey: jobKeys.all,
    queryFn: () => jobsApi.getAll().then((r) => r.data.jobs ?? []),
  });
}

export function useJobsByProject(projectId: number) {
  return useQuery({
    queryKey: jobKeys.byProject(projectId),
    queryFn: () => jobsApi.getByProject(projectId).then((r) => r.data.jobs ?? []),
    enabled: !!projectId,
  });
}

export function useAllJobStatuses(pollingEnabled = false) {
  return useQuery({
    queryKey: jobKeys.allStatuses,
    queryFn: () => jobsApi.getAllStatuses().then((r) => r.data),
    refetchInterval: pollingEnabled ? 5000 : false,
  });
}

export function useJobInstanceStatuses(jobId: number, pollingEnabled = false) {
  return useQuery({
    queryKey: jobKeys.instanceStatuses(jobId),
    queryFn: () => jobsApi.getInstanceStatuses(jobId).then((r) => r.data.statuses ?? []),
    enabled: !!jobId,
    refetchInterval: pollingEnabled ? 5000 : false,
  });
}

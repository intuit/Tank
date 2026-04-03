import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { jobsApi } from '../api/jobs';
import type { CreateJobRequest } from '../types/job';

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

export function useJobById(jobId: number) {
  return useQuery({
    queryKey: jobKeys.byId(jobId),
    queryFn: () => jobsApi.getById(jobId).then((r) => r.data),
    enabled: !!jobId,
  });
}

export function useCreateJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateJobRequest) => jobsApi.create(request).then((r) => r.data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: jobKeys.all });
      queryClient.invalidateQueries({ queryKey: jobKeys.byProject(variables.projectId) });
    },
  });
}

function makeJobControlMutation(apiFn: (id: number) => Promise<unknown>) {
  return function useJobControl() {
    const queryClient = useQueryClient();
    return useMutation({
      mutationFn: apiFn,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: jobKeys.all });
        queryClient.invalidateQueries({ queryKey: jobKeys.allStatuses });
      },
    });
  };
}

export const useStartJob  = makeJobControlMutation((id) => jobsApi.start(id));
export const useStopJob   = makeJobControlMutation((id) => jobsApi.stop(id));
export const usePauseJob  = makeJobControlMutation((id) => jobsApi.pause(id));
export const useResumeJob = makeJobControlMutation((id) => jobsApi.resume(id));
export const useKillJob   = makeJobControlMutation((id) => jobsApi.kill(id));

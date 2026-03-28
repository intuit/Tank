import { useQuery } from '@tanstack/react-query';
import { projectsApi } from '../api/projects';

export const projectKeys = {
  all: ['projects'] as const,
  byId: (id: number) => ['projects', id] as const,
};

export function useProjects() {
  return useQuery({
    queryKey: projectKeys.all,
    queryFn: () => projectsApi.getAll().then((r) => r.data.projects ?? []),
  });
}

export function useProject(id: number) {
  return useQuery({
    queryKey: projectKeys.byId(id),
    queryFn: () => projectsApi.getById(id).then((r) => r.data),
    enabled: !!id,
  });
}

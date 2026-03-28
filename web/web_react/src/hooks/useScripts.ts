import { useQuery } from '@tanstack/react-query';
import { scriptsApi } from '../api/scripts';

export const scriptKeys = {
  all: ['scripts'] as const,
  byId: (id: number) => ['scripts', id] as const,
};

export function useScripts() {
  return useQuery({
    queryKey: scriptKeys.all,
    queryFn: () => scriptsApi.getAll().then((r) => r.data.scripts ?? []),
  });
}

export function useScript(id: number) {
  return useQuery({
    queryKey: scriptKeys.byId(id),
    queryFn: () => scriptsApi.getById(id).then((r) => r.data),
    enabled: !!id,
  });
}

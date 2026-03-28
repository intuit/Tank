import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
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

export function useUploadScript() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (formData: FormData) => scriptsApi.upload(formData),
    onSuccess: () => qc.invalidateQueries({ queryKey: scriptKeys.all }),
  });
}

export function useDeleteScript() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => scriptsApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: scriptKeys.all }),
  });
}

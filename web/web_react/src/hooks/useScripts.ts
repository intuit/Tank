import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { scriptsApi } from '../api/scripts';
import type { ExternalScriptTO } from '../types/script';

export const scriptKeys = {
  all: ['scripts'] as const,
  byId: (id: number) => ['scripts', id] as const,
  external: ['scripts', 'external'] as const,
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

export function useUpdateScript(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: { productName?: string; comments?: string }) => scriptsApi.update(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: scriptKeys.all });
      qc.invalidateQueries({ queryKey: scriptKeys.byId(id) });
    },
  });
}

export function useExternalScripts() {
  return useQuery({
    queryKey: scriptKeys.external,
    queryFn: () => scriptsApi.getExternalAll().then((r) => r.data.externalScripts ?? []),
  });
}

export function useCreateExternalScript() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (script: Partial<ExternalScriptTO>) => scriptsApi.createExternal(script),
    onSuccess: () => qc.invalidateQueries({ queryKey: scriptKeys.external }),
  });
}

export function useDeleteExternalScript() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => scriptsApi.deleteExternal(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: scriptKeys.external }),
  });
}

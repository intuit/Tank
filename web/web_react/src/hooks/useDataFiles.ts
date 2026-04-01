import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { datafilesApi } from '../api/datafiles';

export const datafileKeys = {
  all: ['datafiles'] as const,
  byId: (id: number) => ['datafiles', id] as const,
  content: (id: number, offset?: number, lines?: number) =>
    ['datafiles', 'content', id, offset, lines] as const,
};

export function useDataFiles() {
  return useQuery({
    queryKey: datafileKeys.all,
    queryFn: () => datafilesApi.getAll().then((r) => r.data.dataFiles ?? []),
  });
}

export function useDataFile(id: number) {
  return useQuery({
    queryKey: datafileKeys.byId(id),
    queryFn: () => datafilesApi.getById(id).then((r) => r.data),
    enabled: !!id,
  });
}

export function useDataFileContent(id: number, offset?: number, lines = 50) {
  return useQuery({
    queryKey: datafileKeys.content(id, offset, lines),
    queryFn: () => datafilesApi.getContent(id, offset, lines).then((r) => r.data),
    enabled: !!id,
  });
}

export function useUploadDataFile() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (formData: FormData) => datafilesApi.upload(formData),
    onSuccess: () => qc.invalidateQueries({ queryKey: datafileKeys.all }),
  });
}

export function useDeleteDataFile() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => datafilesApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: datafileKeys.all }),
  });
}

export function useUpdateDataFile(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: { comments?: string }) => datafilesApi.update(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: datafileKeys.all });
      qc.invalidateQueries({ queryKey: datafileKeys.byId(id) });
    },
  });
}

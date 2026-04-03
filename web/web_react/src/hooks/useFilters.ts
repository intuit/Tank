import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { filtersApi } from '../api/filters';
import type { FilterTO } from '../types/filter';

export const filterKeys = {
  all: ['filters'] as const,
  byId: (id: number) => ['filters', id] as const,
  groups: ['filters', 'groups'] as const,
  groupById: (id: number) => ['filters', 'groups', id] as const,
};

export function useFilters() {
  return useQuery({
    queryKey: filterKeys.all,
    queryFn: () => filtersApi.getAll().then((r) => r.data.filters ?? []),
  });
}

export function useFilter(id: number) {
  return useQuery({
    queryKey: filterKeys.byId(id),
    queryFn: () => filtersApi.getById(id).then((r) => r.data),
    enabled: id > 0,
  });
}

export function useFilterGroups() {
  return useQuery({
    queryKey: filterKeys.groups,
    queryFn: () => filtersApi.getGroups().then((r) => r.data.filterGroups ?? []),
  });
}

export function useCreateFilter() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (filter: Omit<FilterTO, 'id'>) => filtersApi.create(filter).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: filterKeys.all }),
  });
}

export function useUpdateFilter() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, filter }: { id: number; filter: FilterTO }) =>
      filtersApi.update(id, filter).then((r) => r.data),
    onSuccess: (_data, { id }) => {
      qc.invalidateQueries({ queryKey: filterKeys.all });
      qc.invalidateQueries({ queryKey: filterKeys.byId(id) });
    },
  });
}

export function useDeleteFilter() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => filtersApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: filterKeys.all }),
  });
}

export function useDeleteFilterGroup() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => filtersApi.deleteGroup(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: filterKeys.groups }),
  });
}

export function useCreateFilterGroup() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (group: { name: string; productName?: string }) =>
      filtersApi.createGroup(group).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: filterKeys.groups }),
  });
}

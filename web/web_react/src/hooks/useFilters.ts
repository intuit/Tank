import { useQuery } from '@tanstack/react-query';
import { filtersApi } from '../api/filters';

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

export function useFilterGroups() {
  return useQuery({
    queryKey: filterKeys.groups,
    queryFn: () => filtersApi.getGroups().then((r) => r.data.filterGroups ?? []),
  });
}

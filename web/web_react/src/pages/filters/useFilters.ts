import { useQuery } from '@tanstack/react-query';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { useSession } from '../../session';

export type Filter = Schemas['FilterTO'];
export type FilterGroup = Schemas['FilterGroupTO'];

/** Every filter and filter group; small enough to load whole (FilterBean, FilterGroupBean) */
export function useFilters() {
  const { client } = useSession();
  return useQuery({
    queryKey: ['filters', 'all'],
    queryFn: async ({ signal }) => {
      const [all, groups] = await Promise.all([
        client.GET('/v2/filters', { signal }),
        client.GET('/v2/filters/groups', { signal }),
      ]);
      if (!all.data || !groups.data) {
        throw toApiError(all.error ?? groups.error, all.data ? groups.response : all.response, 'load the filters');
      }
      return { filters: all.data.filters ?? [], groups: groups.data.filterGroups ?? [] };
    },
  });
}

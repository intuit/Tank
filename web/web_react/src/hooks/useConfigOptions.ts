import { useQuery } from '@tanstack/react-query';
import { toApiError } from '../api/errors';
import { useSession } from '../session';

/** Reference data for pickers (GET /v2/config/options), shared by every page */
export function useConfigOptions() {
  const { client } = useSession();
  return useQuery({
    queryKey: ['config', 'options'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/config/options', { signal });
      if (!data) {
        throw toApiError(error, response, 'load the form options');
      }
      return data;
    },
    staleTime: Infinity,
  });
}

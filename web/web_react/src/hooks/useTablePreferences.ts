import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Schemas } from '../api/client';
import { toApiError } from '../api/errors';
import { useNotify } from '../notify';
import { useSession } from '../session';

export type ColumnPreference = Schemas['ColumnPreference'];
type TablePreferences = Schemas['TablePreferences'];
type ColumnUpdate = Schemas['ColumnPreferenceUpdate'];

/** Table keys known to the server (TableColumnDefaults.Table) */
export type TableName = 'projects' | 'scripts' | 'scriptSteps' | 'datafiles' | 'jobs';

const PREFERENCES_KEY = ['me', 'preferences'] as const;

/**
 * The signed-in user's column visibility and widths for one table. Changes show at once and are saved
 * in the background; a failed save reverts them.
 */
export function useTablePreferences(table: TableName) {
  const { client } = useSession();
  const queryClient = useQueryClient();
  const notify = useNotify();

  const query = useQuery({
    queryKey: PREFERENCES_KEY,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/me/preferences', { signal });
      if (!data) {
        throw toApiError(error, response, 'load your table settings');
      }
      return data;
    },
    staleTime: Infinity,
  });

  const save = useMutation({
    mutationFn: async (updates: ColumnUpdate[]) => {
      const { data, error, response } = await client.PUT('/v2/me/preferences/tables/{table}', {
        params: { path: { table } },
        body: updates,
      });
      if (!data) {
        throw toApiError(error, response, 'save your table settings');
      }
      return data;
    },
    onMutate: async (updates) => {
      await queryClient.cancelQueries({ queryKey: PREFERENCES_KEY });
      const previous = queryClient.getQueryData<TablePreferences>(PREFERENCES_KEY);
      queryClient.setQueryData<TablePreferences>(PREFERENCES_KEY, (prefs) => applyUpdates(prefs, table, updates));
      return { previous };
    },
    onError: (error, _updates, context) => {
      queryClient.setQueryData(PREFERENCES_KEY, context?.previous);
      notify.error('Table settings not saved', error.message);
    },
    onSuccess: (prefs) => queryClient.setQueryData(PREFERENCES_KEY, prefs),
  });

  const columns: ColumnPreference[] = query.data?.tables?.[table] ?? [];
  return {
    columns,
    isLoading: query.isPending,
    error: query.error,
    /** Shows exactly these hideable columns */
    setVisible: (visible: string[]) => {
      const updates = columns
        .filter((c) => c.hideable && c.visible !== visible.includes(c.colName ?? ''))
        .map((c) => ({ colName: c.colName, visible: !c.visible }));
      if (updates.length) {
        save.mutate(updates);
      }
    },
    setWidth: (colName: string, size: number) => save.mutate([{ colName, size: Math.round(size) }]),
  };
}

function applyUpdates(prefs: TablePreferences | undefined, table: string, updates: ColumnUpdate[]): TablePreferences {
  const tables = { ...(prefs?.tables ?? {}) };
  tables[table] = (tables[table] ?? []).map((column) => {
    const update = updates.find((u) => u.colName === column.colName);
    return update
      ? { ...column, visible: update.visible ?? column.visible, size: update.size ?? column.size }
      : column;
  });
  return { ...prefs, tables };
}

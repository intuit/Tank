import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { produce, type Draft } from 'immer';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { ApiError } from '../api/errors';

export type Update<T> = (recipe: (draft: Draft<T>) => void) => void;

/**
 * Loads a document and holds unsaved edits to it (the JSF conversation beans). `save` sends the whole
 * document with the `modified` time it was loaded at; a 409 means someone saved it in between, and is
 * reported as `conflict` rather than as an error.
 */
export function useDocumentDraft<T extends object, P = never>({
  queryKey,
  enabled,
  load,
  store,
  validate,
  invalidate = [],
}: {
  queryKey: readonly unknown[];
  enabled: boolean;
  load: (signal: AbortSignal) => Promise<T>;
  store: (doc: T) => Promise<T>;
  validate: (doc: T) => P[];
  /** Other queries a save makes stale, such as the list the document appears in */
  invalidate?: (readonly unknown[])[];
}) {
  const queryClient = useQueryClient();

  const saved = useQuery({
    queryKey,
    enabled,
    queryFn: ({ signal }) => load(signal),
    // a refetch must not replace edits in progress; the draft resets only when asked to
    staleTime: Infinity,
  });

  const [draft, setDraft] = useState<T>();
  useEffect(() => {
    if (saved.data) {
      setDraft(saved.data);
    }
  }, [saved.data]);

  const update = useCallback<Update<T>>((recipe) => setDraft((current) => current && produce(current, recipe)), []);
  const dirty = useMemo(
    () => !!draft && !!saved.data && JSON.stringify(draft) !== JSON.stringify(saved.data),
    [draft, saved.data],
  );
  const problems = useMemo(() => (draft ? validate(draft) : []), [draft, validate]);

  const save = useMutation({
    mutationFn: store,
    onSuccess: (stored) => {
      queryClient.setQueryData(queryKey, stored);
      for (const key of invalidate) {
        void queryClient.invalidateQueries({ queryKey: key });
      }
    },
  });

  /** Throws away the edits and loads the document as it is stored now */
  const reload = useCallback(async () => {
    save.reset();
    const fresh = await saved.refetch();
    if (fresh.data) {
      setDraft(fresh.data);
    }
  }, [save, saved]);

  const revert = useCallback(() => {
    save.reset();
    setDraft(saved.data);
  }, [save, saved.data]);

  const conflict = save.error instanceof ApiError && save.error.status === 409;
  return {
    saved: saved.data,
    loadError: saved.error,
    isLoading: saved.isPending && enabled,
    draft,
    update,
    dirty,
    problems,
    save: () => draft && save.mutate(draft),
    saveAsync: (doc: T) => save.mutateAsync(doc),
    saving: save.isPending,
    saveError: conflict ? undefined : save.error,
    conflict,
    reload,
    revert,
  };
}

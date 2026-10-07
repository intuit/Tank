import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { produce, type Draft } from 'immer';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { ApiError, toApiError } from '../../../api/errors';
import { useSession } from '../../../session';
import { validateProject, type ProjectDetail } from './validation';

export type Update = (recipe: (draft: Draft<ProjectDetail>) => void) => void;

export function projectQueryKey(projectId: number) {
  return ['project', projectId] as const;
}

/**
 * Loads a project and holds the unsaved edits to it (the JSF ProjectBean conversation). Saving
 * sends the whole project with the `modified` time it was loaded at; the server answers 409 if
 * someone saved it in between.
 */
export function useProjectDraft(projectId: number) {
  const { client } = useSession();
  const queryClient = useQueryClient();

  const saved = useQuery({
    queryKey: projectQueryKey(projectId),
    enabled: Number.isInteger(projectId),
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/projects/{projectId}/full', {
        params: { path: { projectId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the project');
      }
      return data;
    },
    // a refetch must not replace edits in progress; the draft resets only when asked to
    staleTime: Infinity,
  });

  const [draft, setDraft] = useState<ProjectDetail>();
  useEffect(() => {
    if (saved.data) {
      setDraft(saved.data);
    }
  }, [saved.data]);

  const update = useCallback<Update>((recipe) => setDraft((current) => current && produce(current, recipe)), []);

  const dirty = useMemo(
    () => !!draft && !!saved.data && JSON.stringify(draft) !== JSON.stringify(saved.data),
    [draft, saved.data],
  );
  const problems = useMemo(() => (draft ? validateProject(draft) : []), [draft]);

  const save = useMutation({
    mutationFn: async (detail: ProjectDetail) => {
      const { data, error, response } = await client.PUT('/v2/projects/{projectId}/full', {
        params: { path: { projectId } },
        body: { ...detail, name: detail.name?.trim() },
      });
      if (!data) {
        throw toApiError(error, response, 'save the project');
      }
      return data;
    },
    onSuccess: (stored) => {
      queryClient.setQueryData(projectQueryKey(projectId), stored);
      void queryClient.invalidateQueries({ queryKey: ['projects'] });
    },
  });

  /** Throws away the edits and loads the project as it is stored now */
  const reload = useCallback(async () => {
    save.reset();
    await queryClient.invalidateQueries({ queryKey: projectQueryKey(projectId) });
    const fresh = await saved.refetch();
    if (fresh.data) {
      setDraft(fresh.data);
    }
  }, [projectId, queryClient, save, saved]);

  const revert = useCallback(() => {
    save.reset();
    setDraft(saved.data);
  }, [save, saved.data]);

  const conflict = save.error instanceof ApiError && save.error.status === 409;
  return {
    saved: saved.data,
    loadError: saved.error,
    isLoading: saved.isPending && Number.isInteger(projectId),
    draft,
    update,
    dirty,
    problems,
    save: () => draft && save.mutate(draft),
    saveAsync: (detail: ProjectDetail) => save.mutateAsync(detail),
    saving: save.isPending,
    saveError: conflict ? undefined : save.error,
    conflict,
    reload,
    revert,
  };
}

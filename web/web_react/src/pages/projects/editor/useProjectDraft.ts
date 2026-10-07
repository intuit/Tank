import type { Draft } from 'immer';
import { toApiError } from '../../../api/errors';
import { useDocumentDraft } from '../../../hooks/useDocumentDraft';
import { useSession } from '../../../session';
import { validateProject, type ProjectDetail } from './validation';

export type Update = (recipe: (draft: Draft<ProjectDetail>) => void) => void;

export function projectQueryKey(projectId: number) {
  return ['project', projectId] as const;
}

/** A project and the unsaved edits to it (the JSF ProjectBean conversation) */
export function useProjectDraft(projectId: number) {
  const { client } = useSession();
  return useDocumentDraft({
    queryKey: projectQueryKey(projectId),
    enabled: Number.isInteger(projectId),
    load: async (signal) => {
      const { data, error, response } = await client.GET('/v2/projects/{projectId}/full', {
        params: { path: { projectId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the project');
      }
      return data;
    },
    store: async (detail: ProjectDetail) => {
      const { data, error, response } = await client.PUT('/v2/projects/{projectId}/full', {
        params: { path: { projectId } },
        body: { ...detail, name: detail.name?.trim() },
      });
      if (!data) {
        throw toApiError(error, response, 'save the project');
      }
      return data;
    },
    validate: validateProject,
    invalidate: [['projects']],
  });
}

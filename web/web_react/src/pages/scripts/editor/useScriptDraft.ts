import type { Draft } from 'immer';
import { toApiError } from '../../../api/errors';
import { useDocumentDraft } from '../../../hooks/useDocumentDraft';
import { useSession } from '../../../session';
import { validateScript, type ScriptDocument } from './steps';

export type ScriptUpdate = (recipe: (draft: Draft<ScriptDocument>) => void) => void;

/** A script's header and steps, and the unsaved edits to them (the JSF ScriptEditor conversation) */
export function useScriptDraft(scriptId: number) {
  const { client } = useSession();
  return useDocumentDraft({
    queryKey: ['script', scriptId],
    enabled: Number.isInteger(scriptId),
    load: async (signal) => {
      const { data, error, response } = await client.GET('/v2/scripts/{scriptId}/steps', {
        params: { path: { scriptId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the script');
      }
      return data;
    },
    store: async (doc: ScriptDocument) => {
      const { data, error, response } = await client.PUT('/v2/scripts/{scriptId}/steps', {
        params: { path: { scriptId } },
        body: { ...doc, name: doc.name?.trim() },
      });
      if (!data) {
        throw toApiError(error, response, 'save the script');
      }
      return data;
    },
    validate: validateScript,
    invalidate: [['scripts']],
  });
}

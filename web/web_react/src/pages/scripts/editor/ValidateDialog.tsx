import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useState } from 'react';
import { toApiError } from '../../../api/errors';
import { useSession } from '../../../session';
import { formatDuration } from './search';
import type { ScriptStep } from './steps';

/**
 * Checks the draft as JSF's Validate dialog does (JobValidator): the expected time for a pass,
 * best-practice warnings, variables used but never set (and set but never used), and data files.
 */
export function ValidateDialog({ name, steps, onHide }: { name: string; steps: ScriptStep[]; onHide: () => void }) {
  const { client } = useSession();
  // the steps as they were when the dialog opened
  const [body] = useState(() => ({ name, steps }));
  const result = useQuery({
    queryKey: ['script-validate', body],
    gcTime: 0,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/validate', { body, signal });
      if (!data) throw toApiError(error, response, 'validate the script');
      return data;
    },
  });
  const v = result.data;

  return (
    <Dialog header="Validate script" visible onHide={onHide} className="wide-dialog" modal draggable={false}>
      {result.isPending && <ProgressSpinner className="loading" aria-label="Validating" />}
      {result.error && <Message severity="error" text={result.error.message} />}
      {v && (
        <div className="validation" role="status">
          <dl className="validation-summary">
            <dt>Expected time for one pass</dt>
            <dd>{formatDuration(v.durationMs ?? 0)}</dd>
            <dt>Data files</dt>
            <dd>{v.dataFiles?.length ? v.dataFiles.join(', ') : 'None'}</dd>
          </dl>
          {v.warnings?.length ? (
            <>
              <h3 className="form-section">
                {v.warnings.length} {v.warnings.length === 1 ? 'warning' : 'warnings'}
              </h3>
              <ul className="warning-list">
                {v.warnings.map((w) => (
                  <li key={w}>
                    <i className="pi pi-exclamation-triangle" aria-hidden /> {w}
                  </li>
                ))}
              </ul>
            </>
          ) : (
            <Message severity="success" text="No problems found." />
          )}
        </div>
      )}
      <div className="form-actions">
        <Button type="button" label="Done" onClick={onHide} />
      </div>
    </Dialog>
  );
}

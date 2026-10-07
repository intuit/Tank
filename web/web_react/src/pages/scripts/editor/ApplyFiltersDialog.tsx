import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { useState } from 'react';
import { toApiError } from '../../../api/errors';
import { useNotify } from '../../../notify';
import { useSession } from '../../../session';
import { FilterPicker } from '../FilterPicker';
import type { ScriptStep } from './steps';

/** Runs script filters over the draft's steps (ScriptEditor.reapplyFilters); the result is unsaved */
export function ApplyFiltersDialog({
  steps,
  onApplied,
  onHide,
}: {
  steps: ScriptStep[];
  onApplied: (steps: ScriptStep[]) => void;
  onHide: () => void;
}) {
  const { client } = useSession();
  const notify = useNotify();
  const [filterIds, setFilterIds] = useState<number[]>([]);
  const apply = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/apply-filters', { body: { filterIds, steps } });
      if (!data) throw toApiError(error, response, 'apply the filters');
      return data;
    },
    onSuccess: (data) => {
      const changed = data.changed ?? 0;
      if (changed) {
        onApplied(data.steps ?? []);
        notify.success(`Filters changed ${changed} ${changed === 1 ? 'step' : 'steps'}`, 'Save the script to keep the changes');
      } else {
        notify.success('The filters changed nothing');
      }
      onHide();
    },
  });

  return (
    <Dialog header="Apply filters" visible onHide={onHide} className="wide-dialog" modal draggable={false}>
      <div className="form-grid">
        <FilterPicker value={filterIds} onChange={setFilterIds} />
        <small className="field-help">Filters can remove and change steps. You can revert until you save.</small>
        {apply.error && <Message severity="error" text={apply.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button
            type="button"
            label={filterIds.length ? `Apply ${filterIds.length} ${filterIds.length === 1 ? 'filter' : 'filters'}` : 'Apply'}
            disabled={!filterIds.length}
            loading={apply.isPending}
            onClick={() => apply.mutate()}
          />
        </div>
      </div>
    </Dialog>
  );
}

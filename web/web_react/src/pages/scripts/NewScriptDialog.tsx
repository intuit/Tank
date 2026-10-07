import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { SelectButton } from 'primereact/selectbutton';
import { useRef, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router';
import { toApiError } from '../../api/errors';
import { useConfigOptions } from '../../hooks/useConfigOptions';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { SCRIPT_FILE_TYPES, uploadScript } from './scriptUpload';
import { focusOnShow } from '../../components/focusOnShow';

type Mode = 'blank' | 'recording';
const MODES = [
  { label: 'Blank script', value: 'blank' },
  { label: 'From a recording', value: 'recording' },
];

/**
 * Creates a script, blank or from a Tank Proxy recording with filters applied (ScriptCreationBean),
 * then opens it.
 */
export function NewScriptDialog({ onHide }: { onHide: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const options = useConfigOptions();
  const nameInput = useRef<HTMLInputElement>(null);
  const [mode, setMode] = useState<Mode>('blank');
  const [name, setName] = useState('');
  const [productName, setProductName] = useState<string>();
  const [comments, setComments] = useState('');
  const [file, setFile] = useState<File>();
  const [filterIds, setFilterIds] = useState<number[]>([]);
  const [groupIds, setGroupIds] = useState<number[]>([]);

  const filters = useQuery({
    queryKey: ['filters', 'all'],
    enabled: mode === 'recording',
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

  // as ScriptCreationBean does: a group selects (or clears) its filters; filters apply in list order
  const toggleGroup = (id: number, members: number[], on: boolean) => {
    setGroupIds((current) => (on ? [...current, id] : current.filter((g) => g !== id)));
    setFilterIds((current) => (on ? [...new Set([...current, ...members])] : current.filter((f) => !members.includes(f))));
  };
  const orderedFilterIds = (filters.data?.filters ?? []).map((f) => f.id!).filter((id) => filterIds.includes(id));

  const create = useMutation({
    mutationFn: async () => {
      if (mode === 'recording') {
        const result = await uploadScript(client, file!, { name: name.trim(), productName, filterIds: orderedFilterIds });
        if (!result.scriptId) {
          throw new Error(result.message ?? 'The recording was uploaded, but no script was created');
        }
        return result.scriptId;
      }
      const { data, error, response } = await client.POST('/v2/scripts/blank', {
        body: { name: name.trim(), productName, comments: comments.trim() || undefined },
      });
      if (!data?.id) {
        throw toApiError(error, response, 'create the script');
      }
      return data.id;
    },
    onSuccess: (id) => {
      notify.success('Script created', name.trim());
      void queryClient.invalidateQueries({ queryKey: ['scripts'] });
      onHide();
      void navigate(`/scripts/${id}`);
    },
  });

  const ready = !!name.trim() && (mode === 'blank' || !!file);
  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (ready) {
      create.mutate();
    }
  };

  return (
    <Dialog
      header="New script"
      visible
      onHide={onHide}
      className={mode === 'recording' ? 'wide-dialog' : 'form-dialog'}
      modal
      draggable={false}
      onShow={focusOnShow(nameInput)}
    >
      <form onSubmit={submit} className="form-grid">
        <SelectButton
          value={mode}
          options={MODES}
          onChange={(e) => e.value && setMode(e.value as Mode)}
          allowEmpty={false}
          aria-label="How to create the script"
        />
        <label htmlFor="script-name">Name</label>
        <InputText id="script-name" ref={nameInput} value={name} onChange={(e) => setName(e.target.value)} maxLength={255} required />
        <label htmlFor="script-product">Product</label>
        <Dropdown
          inputId="script-product"
          value={productName ?? null}
          options={options.data?.products ?? []}
          optionLabel="label"
          optionValue="value"
          onChange={(e) => setProductName((e.value as string | null) ?? undefined)}
          placeholder="None"
          showClear
        />
        {mode === 'blank' ? (
          <>
            <label htmlFor="script-comments">Comments</label>
            <InputTextarea id="script-comments" value={comments} onChange={(e) => setComments(e.target.value)} rows={2} autoResize />
          </>
        ) : (
          <>
            <label htmlFor="script-file">Recording</label>
            <input
              id="script-file"
              type="file"
              accept={SCRIPT_FILE_TYPES}
              onChange={(e) => setFile(e.target.files?.[0])}
              className="file-input"
            />
            <small className="field-help">The XML the Tank Proxy recorded (.xml, or gzipped .gz)</small>

            <h3 className="form-section">Filters to apply</h3>
            {filters.error && <Message severity="error" text={filters.error.message} />}
            {filters.isPending && <span className="field-help">Loading filters…</span>}
            {filters.data && (
              <div className="filter-picker">
                <fieldset>
                  <legend>Filter groups</legend>
                  {filters.data.groups.length === 0 && <span className="field-help">No filter groups</span>}
                  {filters.data.groups.map((g) => (
                    <div key={g.id} className="field-inline">
                      <Checkbox
                        inputId={`group-${g.id}`}
                        checked={groupIds.includes(g.id!)}
                        onChange={(e) => toggleGroup(g.id!, g.filterIds ?? [], !!e.checked)}
                      />
                      <label htmlFor={`group-${g.id}`}>
                        {g.name}
                        {g.productName && <span className="field-help"> · {g.productName}</span>}
                      </label>
                    </div>
                  ))}
                </fieldset>
                <fieldset>
                  <legend>Filters, applied in this order</legend>
                  {filters.data.filters.length === 0 && <span className="field-help">No filters</span>}
                  {filters.data.filters.map((f) => (
                    <div key={f.id} className="field-inline">
                      <Checkbox
                        inputId={`filter-${f.id}`}
                        checked={filterIds.includes(f.id!)}
                        onChange={(e) =>
                          setFilterIds((current) => (e.checked ? [...current, f.id!] : current.filter((x) => x !== f.id)))
                        }
                      />
                      <label htmlFor={`filter-${f.id}`}>{f.name}</label>
                    </div>
                  ))}
                </fieldset>
              </div>
            )}
          </>
        )}
        {create.error && <Message severity="error" text={create.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Create" loading={create.isPending} disabled={!ready} />
        </div>
      </form>
    </Dialog>
  );
}

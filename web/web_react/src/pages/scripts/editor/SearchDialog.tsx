import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { MultiSelect } from 'primereact/multiselect';
import { SelectButton } from 'primereact/selectbutton';
import { useRef, useState, type FormEvent } from 'react';
import { toApiError } from '../../../api/errors';
import { focusOnShow } from '../../../components/focusOnShow';
import { useNotify } from '../../../notify';
import { useSession } from '../../../session';
import { byStep, EVERYTHING, searchQuery, SECTION_GROUPS, sectionLabel, type StepMatches } from './search';
import type { ScriptStep } from './steps';

const MODES = [
  { label: 'Values', value: 'VALUE' },
  { label: 'Keys', value: 'KEY' },
];

/**
 * Finds steps by the parts chosen, and replaces what matched (ScriptSearchBean). Works on the draft:
 * replacing is an unsaved change like any other.
 */
export function SearchDialog({
  steps,
  readOnly,
  onReplaced,
  onHide,
}: {
  steps: ScriptStep[];
  readOnly: boolean;
  onReplaced: (steps: ScriptStep[]) => void;
  onHide: () => void;
}) {
  const { client } = useSession();
  const notify = useNotify();
  const input = useRef<HTMLInputElement>(null);
  const [text, setText] = useState('');
  const [anywhere, setAnywhere] = useState(true);
  const [sections, setSections] = useState<string[]>([]);
  const [checked, setChecked] = useState<StepMatches[]>([]);
  const [replacement, setReplacement] = useState('');
  const [mode, setMode] = useState('VALUE');
  /** The query and sections of the results shown, so replace changes what was found */
  const [searched, setSearched] = useState<{ query: string; sections: string[] }>();
  const looksIn = sections.length ? sections : [EVERYTHING];

  const search = useMutation({
    mutationFn: async (args: { query: string; sections: string[]; steps: ScriptStep[] }) => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/search', { body: args });
      if (!data) throw toApiError(error, response, 'search the steps');
      return byStep(data);
    },
    onSuccess: (_d, args) => {
      setSearched({ query: args.query, sections: args.sections });
      setChecked([]);
    },
  });

  const replace = useMutation({
    mutationFn: async (uuids: string[] | undefined) => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/replace', {
        body: { steps, ...searched!, replacement, mode, uuids },
      });
      if (!data) throw toApiError(error, response, 'replace');
      return data;
    },
    onSuccess: (data) => {
      const changed = data.changed ?? 0;
      notify.success(changed ? `Replaced in ${changed} ${changed === 1 ? 'step' : 'steps'}` : 'Nothing was replaced');
      if (changed) {
        onReplaced(data.steps ?? []);
      }
      // what's left to find, in the steps as they are now
      search.mutate({ ...searched!, steps: data.steps ?? steps });
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (text.trim()) {
      search.mutate({ query: searchQuery(text, anywhere), sections: looksIn, steps });
    }
  };
  const results = search.data;
  const error = search.error ?? replace.error;

  return (
    <Dialog
      header="Search steps"
      visible
      onHide={onHide}
      className="request-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(input)}
    >
      <form onSubmit={submit} className="search-form">
        <InputText
          ref={input}
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Find"
          aria-label="Find"
          className="search-text"
        />
        <MultiSelect
          value={sections}
          options={SECTION_GROUPS}
          optionGroupLabel="label"
          optionGroupChildren="items"
          optionLabel="label"
          optionValue="value"
          onChange={(e) => setSections(e.value as string[])}
          placeholder="Everywhere"
          maxSelectedLabels={2}
          selectedItemsLabel="{0} parts of steps"
          aria-label="Look in"
          display="comma"
          filter
          className="search-sections"
        />
        <Button type="submit" label="Search" icon="pi pi-search" loading={search.isPending} disabled={!text.trim()} />
        <div className="field-inline search-anywhere">
          <Checkbox inputId="search-anywhere" checked={anywhere} onChange={(e) => setAnywhere(!!e.checked)} />
          <label htmlFor="search-anywhere">Match anywhere in a value</label>
        </div>
        <small className="field-help search-help">
          {anywhere
            ? 'Ignores case. Use * for any text and ? for one character to match a pattern instead.'
            : 'Matches whole values, ignoring case. Use * for any text and ? for one character.'}
        </small>
      </form>

      {error && <Message severity="error" text={error.message} className="editor-message" />}
      {results && (
        <>
          <DataTable
            value={results}
            dataKey="uuid"
            selectionMode="checkbox"
            selection={checked}
            onSelectionChange={(e) => setChecked(e.value as StepMatches[])}
            emptyMessage="No steps match."
            size="small"
            scrollable
            scrollHeight="20rem"
            header={`${results.length} matching ${results.length === 1 ? 'step' : 'steps'}`}
            className="search-results"
          >
            {!readOnly && <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />}
            <Column header="Step" field="position" style={{ width: '4rem' }} />
            <Column header="Label" body={(r: StepMatches) => steps.find((s) => s.uuid === r.uuid)?.label} bodyClassName="ellipsis" />
            <Column
              header="Matches"
              body={(r: StepMatches) => (
                <ul className="match-list">
                  {r.matches.map((m, i) => (
                    <li key={i}>
                      <span className="step-type">{sectionLabel(m.section)}</span>
                      {m.key ? `${m.key} = ${m.value ?? ''}` : m.value}
                    </li>
                  ))}
                </ul>
              )}
            />
          </DataTable>

          {!readOnly && results.length > 0 && (
            <div className="search-form replace-form">
              <InputText
                value={replacement}
                onChange={(e) => setReplacement(e.target.value)}
                placeholder="Replace with"
                aria-label="Replace with"
                className="search-text"
              />
              <SelectButton
                value={mode}
                options={MODES}
                onChange={(e) => e.value && setMode(e.value as string)}
                allowEmpty={false}
                aria-label="Replace"
              />
              <Button
                type="button"
                label={checked.length ? `Replace in ${checked.length} checked` : `Replace in all ${results.length}`}
                icon="pi pi-sync"
                outlined
                loading={replace.isPending}
                onClick={() => replace.mutate(checked.length ? checked.map((c) => c.uuid) : undefined)}
              />
              <small className="field-help search-help">
                Each match is replaced whole.{' '}
                {mode === 'KEY'
                  ? 'Keys renames the header, query string, cookie or post data entry that matched; other parts get the new value.'
                  : 'For a key that matched, its value is replaced.'}
              </small>
            </div>
          )}
        </>
      )}

      <div className="form-actions">
        <Button type="button" label="Done" onClick={onHide} />
      </div>
    </Dialog>
  );
}

import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { MultiSelect } from 'primereact/multiselect';
import { SelectButton } from 'primereact/selectbutton';
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { toApiError } from '../../../api/errors';
import { focusOnShow } from '../../../components/focusOnShow';
import { useNotify } from '../../../notify';
import { useSession } from '../../../session';
import { byStep, EVERYTHING, replacedMatch, searchQuery, SECTION_GROUPS, sectionLabel, type StepMatches } from './search';
import type { ScriptStep } from './steps';

/** Matches listed per step; a recorded request can match in dozens of headers */
const MATCHES_SHOWN = 5;

const MODES = [
  { label: 'Values', value: 'VALUE' },
  { label: 'Keys', value: 'KEY' },
];

/**
 * Finds steps by the parts chosen, and replaces what matched (ScriptSearchBean): in the checked steps or
 * all of them, or one step at a time with Previous and Next, previewing the step's change. Works on the
 * draft: replacing is an unsaved change like any other.
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
  /** The step being stepped through, an index into the results */
  const [current, setCurrent] = useState(0);
  const [nextX, setNextX] = useState<number | null>(5);
  /** After a replace, the step to carry on from: the first match after this position */
  const resumeAfter = useRef<number>(undefined);
  /** The query and sections of the results shown, so replace changes what was found */
  const [searched, setSearched] = useState<{ query: string; sections: string[] }>();
  const looksIn = sections.length ? sections : [EVERYTHING];

  const search = useMutation({
    mutationFn: async (args: { query: string; sections: string[]; steps: ScriptStep[] }) => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/search', { body: args });
      if (!data) throw toApiError(error, response, 'search the steps');
      return byStep(data);
    },
    onSuccess: (found, args) => {
      setSearched({ query: args.query, sections: args.sections });
      setChecked([]);
      const after = resumeAfter.current;
      resumeAfter.current = undefined;
      // like ScriptSearchBean.next(), wrapping to the first match
      const next = after === undefined ? 0 : found.findIndex((r) => r.position > after);
      setCurrent(Math.max(0, next));
    },
  });

  const replace = useMutation({
    mutationFn: async ({ uuids }: { uuids?: string[]; resumeAfter?: number }) => {
      const { data, error, response } = await client.POST('/v2/scripts/steps/replace', {
        body: { steps, ...searched!, replacement, mode, uuids },
      });
      if (!data) throw toApiError(error, response, 'replace');
      return data;
    },
    onSuccess: (data, args) => {
      resumeAfter.current = args.resumeAfter;
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
  const byUuid = new Map(steps.map((s) => [s.uuid, s]));
  const error = search.error ?? replace.error;
  const at = results?.length ? Math.min(current, results.length - 1) : -1;
  const currentStep = at >= 0 ? results![at] : undefined;
  const move = (by: number) => results?.length && setCurrent((at + by + results.length) % results.length);
  /** Replaces in the current step and the next count - 1 steps (ScriptSearchBean.replaceNextX) */
  const replaceFromCurrent = (count: number) => {
    const targets = results!.slice(at, at + count);
    replace.mutate({ uuids: targets.map((t) => t.uuid), resumeAfter: targets[targets.length - 1]!.position });
  };

  // keep the current step in view as Previous and Next move through a long list
  const table = useRef<DataTable<StepMatches[]>>(null);
  useEffect(() => {
    table.current?.getElement()?.querySelector('.search-current')?.scrollIntoView?.({ block: 'nearest' });
  }, [at, results]);

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
            ? 'Ignores case. Use * for any text to match a pattern instead.'
            : 'Matches whole values, ignoring case. Use * for any text.'}
        </small>
      </form>

      {error && <Message severity="error" text={error.message} className="editor-message" />}
      {results && (
        <>
          <DataTable
            ref={table}
            value={results}
            rowClassName={(r: StepMatches) => (r.uuid === currentStep?.uuid ? 'search-current p-highlight' : '')}
            dataKey="uuid"
            selectionMode="checkbox"
            selection={checked}
            onSelectionChange={(e) => setChecked(e.value as StepMatches[])}
            emptyMessage="No steps match."
            size="small"
            scrollable
            scrollHeight="20rem"
            header={
              <div className="search-results-header">
                <span>{`${results.length} matching ${results.length === 1 ? 'step' : 'steps'}`}</span>
                {results.length > 0 && (
                  <span className="search-stepper">
                    <Button type="button" icon="pi pi-chevron-up" text rounded size="small" aria-label="Previous match" onClick={() => move(-1)} />
                    <span aria-live="polite">
                      Step {currentStep?.position} ({at + 1} of {results.length})
                    </span>
                    <Button type="button" icon="pi pi-chevron-down" text rounded size="small" aria-label="Next match" onClick={() => move(1)} />
                  </span>
                )}
              </div>
            }
            // fixed, so a long recorded URL can't push the matches out of view
            tableStyle={{ tableLayout: 'fixed', width: '100%' }}
            className="search-results"
          >
            {!readOnly && <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />}
            <Column
              header="Step"
              body={(r: StepMatches) => (
                <Button
                  type="button"
                  link
                  label={String(r.position)}
                  className="search-goto"
                  onClick={() => setCurrent(results.indexOf(r))}
                  aria-label={`Go to step ${r.position}`}
                />
              )}
              style={{ width: '4rem' }}
            />
            <Column
              header="Label"
              body={(r: StepMatches) => {
                const label = byUuid.get(r.uuid)?.label;
                return <span title={label}>{label}</span>;
              }}
              style={{ width: '40%' }}
              bodyClassName="ellipsis"
            />
            <Column
              header="Matches"
              body={(r: StepMatches) => (
                <ul className="match-list">
                  {r.matches.slice(0, MATCHES_SHOWN).map((m, i) => (
                    <li key={i} title={m.key ? `${m.key} = ${m.value ?? ''}` : m.value}>
                      <span className="step-type">{sectionLabel(m.section)}</span>
                      {m.key ? `${m.key} = ${m.value ?? ''}` : m.value}
                    </li>
                  ))}
                  {r.matches.length > MATCHES_SHOWN && (
                    <li className="field-help">and {r.matches.length - MATCHES_SHOWN} more</li>
                  )}
                </ul>
              )}
            />
          </DataTable>

          {currentStep && (
            <table className="kv-table kv-table-wide search-preview" aria-label="Before and after">
              <caption>
                {readOnly ? 'Matches in' : 'Replacing in'} step {currentStep.position}: {byUuid.get(currentStep.uuid)?.label}
              </caption>
              <thead>
                <tr>
                  <th>Part</th>
                  <th>Before</th>
                  {!readOnly && <th>After</th>}
                </tr>
              </thead>
              <tbody>
                {currentStep.matches.map((m, i) => {
                  const after = replacedMatch(m, replacement, mode);
                  return (
                    <tr key={i}>
                      <td>{sectionLabel(m.section)}</td>
                      <td className="ellipsis" title={showMatch(m)}>
                        {showMatch(m)}
                      </td>
                      {!readOnly && (
                        <td className="ellipsis" title={showMatch(after)}>
                          {showMatch(after)}
                        </td>
                      )}
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}

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
                label={`Replace in step ${currentStep?.position}`}
                icon="pi pi-arrow-right"
                loading={replace.isPending}
                onClick={() => replaceFromCurrent(1)}
              />
              <span className="field-inline">
                <Button
                  type="button"
                  label="Replace next"
                  outlined
                  disabled={!nextX || replace.isPending}
                  onClick={() => replaceFromCurrent(nextX!)}
                />
                <InputNumber
                  value={nextX}
                  onValueChange={(e) => setNextX(e.value ?? null)}
                  min={1}
                  max={results.length}
                  showButtons={false}
                  inputStyle={{ width: '4rem' }}
                  aria-label="Steps to replace in"
                />
                <span className="field-help">steps</span>
              </span>
              <Button
                type="button"
                label={checked.length ? `Replace in ${checked.length} checked` : `Replace in all ${results.length}`}
                icon="pi pi-sync"
                outlined
                loading={replace.isPending}
                onClick={() => replace.mutate({ uuids: checked.length ? checked.map((c) => c.uuid) : undefined })}
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

function showMatch(m: { key?: string; value?: string }): string {
  return m.key ? `${m.key} = ${m.value ?? ''}` : (m.value ?? '');
}

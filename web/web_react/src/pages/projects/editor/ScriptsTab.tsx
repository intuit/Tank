import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { TabPanel, TabView } from 'primereact/tabview';
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { contextPath, type Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { useSession } from '../../../session';
import { Field } from '../../../components/Field';
import type { Update } from './useProjectDraft';
import { testPlanPercentageWarning, type ProjectDetail } from './validation';
import { focusOnShow } from '../../../components/focusOnShow';

type ScriptGroup = Schemas['ScriptGroupDetail'];
type ScriptRef = Schemas['ScriptRef'];

/** Test plans, their script groups and the scripts in each group (WorkloadScripts) */
export function ScriptsTab({ detail, update, readOnly }: { detail: ProjectDetail; update: Update; readOnly: boolean }) {
  const plans = detail.testPlans ?? [];
  const [active, setActive] = useState(0);
  const [addingPlan, setAddingPlan] = useState(false);
  /** The group being edited, or a new one to insert at `index` */
  const [editing, setEditing] = useState<{ planIndex: number; groupIndex?: number; insertAt?: number }>();
  const planIndex = Math.min(active, Math.max(plans.length - 1, 0));
  const warning = testPlanPercentageWarning(detail);

  const removePlan = (index: number) =>
    confirmDialog({
      header: 'Delete test plan',
      message: `Delete test plan "${plans[index]?.name}" and its script groups?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => {
        update((d) => void d.testPlans!.splice(index, 1));
        setActive(Math.max(0, index - 1));
      },
    });

  return (
    <div>
      <div className="tab-toolbar">
        {!readOnly && <Button label="Add test plan" icon="pi pi-plus" outlined onClick={() => setAddingPlan(true)} />}
        <a
          className="p-button p-button-text plain-link"
          href={`${contextPath()}/v2/projects/download/${detail.id}`}
          title="Download the harness XML of the saved project"
        >
          <i className="pi pi-download" aria-hidden />
          &nbsp;Harness XML
        </a>
      </div>
      {warning && <Message severity="warn" text={warning} className="tab-message" />}

      <TabView activeIndex={planIndex} onTabChange={(e) => setActive(e.index)} scrollable>
        {plans.map((plan, index) => (
          <TabPanel key={index} header={`${plan.name || 'Unnamed'} (${plan.userPercentage ?? 0}%)`}>
            <div className="form-columns">
              <Field label="Name" htmlFor={`plan-name-${index}`}>
                <InputText
                  id={`plan-name-${index}`}
                  value={plan.name ?? ''}
                  onChange={(e) => update((d) => void (d.testPlans![index]!.name = e.target.value))}
                  disabled={readOnly}
                />
              </Field>
              <Field label="User percentage" htmlFor={`plan-percent-${index}`}>
                <InputNumber
                  inputId={`plan-percent-${index}`}
                  value={plan.userPercentage ?? 0}
                  onValueChange={(e) => update((d) => void (d.testPlans![index]!.userPercentage = e.value ?? 0))}
                  min={0}
                  max={100}
                  suffix="%"
                  disabled={readOnly}
                />
              </Field>
            </div>

            <div className="tab-toolbar">
              {!readOnly && (
                <Button
                  label="Add script group"
                  icon="pi pi-plus"
                  outlined
                  size="small"
                  onClick={() => setEditing({ planIndex: index, insertAt: plan.scriptGroups?.length ?? 0 })}
                />
              )}
              {!readOnly && plans.length > 1 && (
                <Button
                  label="Delete test plan"
                  icon="pi pi-trash"
                  severity="danger"
                  text
                  size="small"
                  onClick={() => removePlan(index)}
                />
              )}
            </div>

            <DataTable
              value={plan.scriptGroups ?? []}
              reorderableRows={!readOnly}
              onRowReorder={(e) =>
                update((d) => void (d.testPlans![index]!.scriptGroups = e.value as ScriptGroup[]))
              }
              emptyMessage="No script groups yet. Add one to choose the scripts this test plan runs."
              size="small"
            >
              {!readOnly && <Column rowReorder style={{ width: '3rem' }} />}
              <Column
                header="Script group"
                body={(group: ScriptGroup, { rowIndex }) => (
                  <Button
                    label={group.name}
                    link
                    className="link-cell"
                    onClick={() => setEditing({ planIndex: index, groupIndex: rowIndex })}
                  />
                )}
              />
              <Column
                header="Scripts"
                body={(group: ScriptGroup) =>
                  (group.scripts ?? []).map((s) => s.scriptName).join(', ') || <em>none</em>
                }
                bodyClassName="ellipsis"
              />
              <Column header="Loop" field="loop" style={{ width: '6rem' }} />
              {!readOnly && (
                <Column
                  style={{ width: '9rem' }}
                  body={(group: ScriptGroup, { rowIndex }) => (
                    <div className="row-actions">
                      <Button
                        icon="pi pi-pencil"
                        rounded
                        text
                        aria-label={`Edit ${group.name}`}
                        onClick={() => setEditing({ planIndex: index, groupIndex: rowIndex })}
                      />
                      <Button
                        icon="pi pi-plus-circle"
                        rounded
                        text
                        aria-label={`Insert a group before ${group.name}`}
                        tooltip="Insert a group before this one"
                        tooltipOptions={{ position: 'top' }}
                        onClick={() => setEditing({ planIndex: index, insertAt: rowIndex })}
                      />
                      <Button
                        icon="pi pi-trash"
                        rounded
                        text
                        severity="danger"
                        aria-label={`Delete ${group.name}`}
                        onClick={() => update((d) => void d.testPlans![index]!.scriptGroups!.splice(rowIndex, 1))}
                      />
                    </div>
                  )}
                />
              )}
            </DataTable>
          </TabPanel>
        ))}
      </TabView>

      {addingPlan && (
        <TestPlanDialog
          suggestedPercentage={Math.max(0, 100 - plans.reduce((sum, p) => sum + (p.userPercentage ?? 0), 0))}
          onHide={() => setAddingPlan(false)}
          onAdd={(name, userPercentage) => {
            update((d) => void (d.testPlans ??= []).push({ name, userPercentage, scriptGroups: [] }));
            setActive(plans.length);
            setAddingPlan(false);
          }}
        />
      )}
      {editing && (
        <ScriptGroupDialog
          group={
            editing.groupIndex !== undefined
              ? plans[editing.planIndex]?.scriptGroups?.[editing.groupIndex]
              : { name: '', loop: 1, scripts: [] }
          }
          isNew={editing.groupIndex === undefined}
          readOnly={readOnly}
          onHide={() => setEditing(undefined)}
          onSave={(group) => {
            update((d) => {
              const groups = (d.testPlans![editing.planIndex]!.scriptGroups ??= []);
              if (editing.groupIndex !== undefined) {
                groups[editing.groupIndex] = group;
              } else {
                groups.splice(editing.insertAt ?? groups.length, 0, group);
              }
            });
            setEditing(undefined);
          }}
        />
      )}
    </div>
  );
}

function TestPlanDialog({
  suggestedPercentage,
  onHide,
  onAdd,
}: {
  suggestedPercentage: number;
  onHide: () => void;
  onAdd: (name: string, percentage: number) => void;
}) {
  const [name, setName] = useState('');
  const [percentage, setPercentage] = useState(suggestedPercentage);
  const nameInput = useRef<HTMLInputElement>(null);
  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (name.trim()) {
      onAdd(name.trim(), percentage);
    }
  };
  return (
    <Dialog
      header="Add test plan"
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(nameInput)}
    >
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="new-plan-name">Name</label>
        <InputText id="new-plan-name" ref={nameInput} value={name} onChange={(e) => setName(e.target.value)} required />
        <label htmlFor="new-plan-percent">User percentage</label>
        <InputNumber
          inputId="new-plan-percent"
          value={percentage}
          onValueChange={(e) => setPercentage(e.value ?? 0)}
          min={0}
          max={100}
          suffix="%"
        />
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Add" disabled={!name.trim()} />
        </div>
      </form>
    </Dialog>
  );
}

/** Edits a copy of a script group; nothing changes in the project until Done */
function ScriptGroupDialog({
  group,
  isNew,
  readOnly,
  onHide,
  onSave,
}: {
  group: ScriptGroup | undefined;
  isNew: boolean;
  readOnly: boolean;
  onHide: () => void;
  onSave: (group: ScriptGroup) => void;
}) {
  const [name, setName] = useState(group?.name ?? '');
  const [loop, setLoop] = useState(group?.loop ?? 1);
  const [scripts, setScripts] = useState<ScriptRef[]>(group?.scripts ?? []);
  const nameInput = useRef<HTMLInputElement>(null);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (name.trim()) {
      onSave({ name: name.trim(), loop, scripts });
    }
  };
  const move = (from: number, to: number) =>
    setScripts((current) => {
      const next = [...current];
      const [item] = next.splice(from, 1);
      next.splice(to, 0, item!);
      return next;
    });

  return (
    <Dialog
      header={isNew ? 'Add script group' : `Script group ${group?.name ?? ''}`}
      visible
      onHide={onHide}
      className="wide-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(nameInput)}
    >
      <form onSubmit={submit}>
        <div className="form-columns">
          <Field label="Name" htmlFor="group-name">
            <InputText
              id="group-name"
              ref={nameInput}
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              disabled={readOnly}
            />
          </Field>
          <Field label="Loop" htmlFor="group-loop" help="How many times the whole group runs">
            <InputNumber
              inputId="group-loop"
              value={loop}
              onValueChange={(e) => setLoop(e.value ?? 1)}
              min={1}
              useGrouping={false}
              disabled={readOnly}
            />
          </Field>
        </div>

        <h3 className="form-section">Scripts, in the order they run</h3>
        <DataTable value={scripts} size="small" emptyMessage="No scripts yet. Find scripts below to add them.">
          <Column header="Script" field="scriptName" bodyClassName="ellipsis" />
          <Column
            header="Loop"
            style={{ width: '8rem' }}
            body={(script: ScriptRef, { rowIndex }) => (
              <InputNumber
                value={script.loop ?? 1}
                onValueChange={(e) =>
                  setScripts((current) =>
                    current.map((s, i) => (i === rowIndex ? { ...s, loop: e.value ?? 1 } : s)),
                  )
                }
                min={1}
                useGrouping={false}
                inputClassName="loop-input"
                aria-label={`Loop for ${script.scriptName}`}
                disabled={readOnly}
              />
            )}
          />
          {!readOnly && (
            <Column
              style={{ width: '9rem' }}
              body={(script: ScriptRef, { rowIndex }) => (
                <div className="row-actions">
                  <Button
                    type="button"
                    icon="pi pi-arrow-up"
                    rounded
                    text
                    aria-label={`Move ${script.scriptName} up`}
                    disabled={rowIndex === 0}
                    onClick={() => move(rowIndex, rowIndex - 1)}
                  />
                  <Button
                    type="button"
                    icon="pi pi-arrow-down"
                    rounded
                    text
                    aria-label={`Move ${script.scriptName} down`}
                    disabled={rowIndex === scripts.length - 1}
                    onClick={() => move(rowIndex, rowIndex + 1)}
                  />
                  <Button
                    type="button"
                    icon="pi pi-times"
                    rounded
                    text
                    severity="danger"
                    aria-label={`Remove ${script.scriptName}`}
                    onClick={() => setScripts((current) => current.filter((_, i) => i !== rowIndex))}
                  />
                </div>
              )}
            />
          )}
        </DataTable>

        {!readOnly && (
          <ScriptPicker
            onAdd={(script) =>
              setScripts((current) => [...current, { scriptId: script.id, scriptName: script.name, loop: 1 }])
            }
          />
        )}

        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          {!readOnly && <Button type="submit" label={isNew ? 'Add group' : 'Done'} disabled={!name.trim()} />}
        </div>
      </form>
    </Dialog>
  );
}

const PICKER_SIZE = 10;

/** Finds scripts by name, product or comments (GET /v2/scripts?page&q) */
function ScriptPicker({ onAdd }: { onAdd: (script: Schemas['ScriptSummary']) => void }) {
  const { client } = useSession();
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  useEffect(() => {
    const timer = setTimeout(() => setQ(search.trim()), 300);
    return () => clearTimeout(timer);
  }, [search]);

  const results = useQuery({
    queryKey: ['scripts', { page: 0, size: PICKER_SIZE, q, sort: 'name,asc' }],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/scripts', {
        params: { query: { page: 0, size: PICKER_SIZE, sort: 'name,asc', q: q || undefined } },
        signal,
      });
      if (!data || !('items' in data)) {
        throw toApiError(error, response, 'find scripts');
      }
      return data;
    },
  });

  return (
    <div className="picker">
      <h3 className="form-section">Add scripts</h3>
      <IconField iconPosition="left">
        <InputIcon className="pi pi-search" />
        <InputText
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Find scripts by name, product or comments"
          aria-label="Find scripts"
          className="picker-search"
        />
      </IconField>
      {results.error ? (
        <Message severity="error" text={results.error.message} />
      ) : (
        <DataTable value={results.data?.items ?? []} size="small" loading={results.isFetching} emptyMessage="No scripts found">
          <Column field="name" header="Name" bodyClassName="ellipsis" />
          <Column field="productName" header="Product" style={{ width: '10rem' }} />
          <Column
            style={{ width: '6rem' }}
            body={(script: Schemas['ScriptSummary']) => (
              <Button type="button" label="Add" size="small" text aria-label={`Add ${script.name}`} onClick={() => onAdd(script)} />
            )}
          />
        </DataTable>
      )}
      {results.data && (results.data.total ?? 0) > PICKER_SIZE && (
        <small className="field-help">
          Showing {PICKER_SIZE} of {results.data.total}. Narrow the search to find others.
        </small>
      )}
    </div>
  );
}

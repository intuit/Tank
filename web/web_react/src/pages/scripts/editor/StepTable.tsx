import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { InputNumber } from 'primereact/inputnumber';
import { Menu } from 'primereact/menu';
import type { MenuItem } from 'primereact/menuitem';
import { MultiSelect } from 'primereact/multiselect';
import { useRef, useState, type FormEvent, type ReactNode } from 'react';
import { useTablePreferences, type ColumnPreference } from '../../../hooks/useTablePreferences';
import { useNotify } from '../../../notify';
import { copySteps, useCopiedSteps } from './clipboard';
import { RequestDialog } from './RequestDialog';
import { isSimpleType, StepDialog, type SimpleType } from './StepDialog';
import {
  copiesOf,
  createStep,
  deleteSteps,
  hasAssignment,
  hasValidation,
  insertIndex,
  insertSteps,
  moveSteps,
  STEP_TYPES,
  type ScriptStep,
} from './steps';
import type { ScriptUpdate } from './useScriptDraft';
import { focusOnShow } from '../../../components/focusOnShow';

/**
 * Past this many steps the table scrolls virtually, which keeps large recordings responsive but
 * can't be combined with drag-and-drop, so steps are moved with "Move to" instead.
 */
export const VIRTUAL_SCROLL_FROM = 300;
const ROW_HEIGHT = 41;

type Cell = (step: ScriptStep, position: number, open: (step: ScriptStep) => void) => ReactNode;

/** Steps this editor can open: the simple types and requests */
function isEditable(step: ScriptStep) {
  return isSimpleType(step.type) || step.type === 'request';
}

const check = (on: boolean) => (on ? <i className="pi pi-check" aria-label="Yes" /> : null);

/** Cells by their preference key (TableColumnDefaults.SCRIPT_STEPS_COL_PREFS) */
const CELLS: Record<string, Cell> = {
  indexColumn: (_s, position) => position,
  groupColumn: (s) => s.scriptGroupName,
  nameColumn: (s) => s.name,
  methodColumn: (s) => s.method,
  protocolColumn: (s) => s.protocol,
  hostColumn: (s) => s.hostname,
  pathColumn: (s) => s.simplePath,
  // the server's label for the step, e.g. "GET /cart" or "Think time: 1000 - 3000"
  dataColumn: (s, _p, open) => {
    const content = (
      <>
        {s.type && s.type !== 'request' && <span className="step-type">{STEP_TYPES[s.type] ?? s.type}</span>}
        {s.label}
      </>
    );
    return isEditable(s) ? (
      <button type="button" className="cell-link" title={s.label} onClick={() => open(s)}>
        {content}
      </button>
    ) : (
      <span title={s.label}>{content}</span>
    );
  },
  mimeColumn: (s) => s.mimetype,
  loggingColumn: (s) => s.loggingKey,
  failureColumn: (s) => s.onFail,
  commentsColumn: (s) => s.comments,
  validationColumn: (s) => check(hasValidation(s)),
  assignmentColumn: (s) => check(hasAssignment(s)),
};

/** The script's steps (ScriptEditor's step table), edited in the draft */
export function StepTable({
  scriptId,
  steps,
  update,
  readOnly,
  selection,
  onSelectionChange,
}: {
  scriptId: number;
  steps: ScriptStep[];
  update: ScriptUpdate;
  readOnly: boolean;
  selection: ScriptStep[];
  onSelectionChange: (steps: ScriptStep[]) => void;
}) {
  const preferences = useTablePreferences('scriptSteps');
  const notify = useNotify();
  const copied = useCopiedSteps();
  const addMenu = useRef<Menu>(null);
  const [moving, setMoving] = useState(false);
  /** The step being edited, or the type of one being added */
  const [editing, setEditing] = useState<{ type: SimpleType | 'request'; step?: ScriptStep }>();
  const virtual = steps.length > VIRTUAL_SCROLL_FROM;
  const positions = new Map(steps.map((s, i) => [s.uuid, i + 1]));
  const selected = new Set(selection.map((s) => s.uuid ?? ''));

  const remove = (uuids: Set<string>) => {
    update((d) => void (d.steps = deleteSteps(d.steps ?? [], uuids)));
    onSelectionChange(selection.filter((s) => !uuids.has(s.uuid ?? '')));
  };

  // new and pasted steps go before the first selected step (ScriptEditor.getInsertIndex)
  const at = insertIndex(steps, selected);
  const where = at < steps.length ? `before step ${at + 1}` : 'at the end';
  const add = (added: ScriptStep[]) =>
    update((d) => void (d.steps = insertSteps(d.steps ?? [], added, insertIndex(d.steps ?? [], selected))));
  const open = (step: ScriptStep) => {
    if (step.type === 'request' || isSimpleType(step.type)) {
      setEditing({ type: step.type, step });
    }
  };
  const addItems: MenuItem[] = [
    { label: STEP_TYPES.request, command: () => setEditing({ type: 'request' }) },
    ...(['variable', 'thinkTime', 'sleep', 'cookie', 'authentication'] as SimpleType[]).map((type) => ({
      label: STEP_TYPES[type],
      command: () => setEditing({ type }),
    })),
    { label: STEP_TYPES.clear, command: () => add([createStep.clear()]) },
  ];

  /** Replaces the edited step (by uuid), or adds a new one */
  const save = (step: ScriptStep) => {
    if (editing?.step) {
      update((d) => {
        const i = (d.steps ?? []).findIndex((s) => s.uuid === step.uuid);
        if (i >= 0) {
          d.steps![i] = step;
        }
      });
    } else {
      add([step]);
    }
    setEditing(undefined);
  };

  const hideable = preferences.columns.filter((c) => c.hideable && CELLS[c.colName ?? '']);
  const visible = preferences.columns.filter((c) => c.visible && (CELLS[c.colName ?? ''] || c.colName === 'actionsColumn'));

  return (
    <div>
      <div className="list-toolbar">
        <span className="field-help">
          {steps.length} {steps.length === 1 ? 'step' : 'steps'}
          {selection.length > 0 && `, ${selection.length} selected`}
        </span>
        {!readOnly && (
          <>
            <Menu model={addItems} popup ref={addMenu} id="add-step-menu" />
            <Button
              label="Add step"
              icon="pi pi-plus"
              size="small"
              onClick={(e) => addMenu.current?.toggle(e)}
              aria-controls="add-step-menu"
              aria-haspopup
              tooltip={`Adds ${where}`}
              tooltipOptions={{ position: 'top' }}
            />
          </>
        )}
        {selection.length > 0 && (
          <Button
            label="Copy"
            icon="pi pi-copy"
            outlined
            size="small"
            onClick={() => {
              copySteps(steps.filter((s) => selected.has(s.uuid ?? '')));
              notify.success(`Copied ${selection.length} ${selection.length === 1 ? 'step' : 'steps'}`);
            }}
          />
        )}
        {!readOnly && copied.length > 0 && (
          <Button
            label={`Paste ${copied.length} ${copied.length === 1 ? 'step' : 'steps'}`}
            icon="pi pi-clone"
            outlined
            size="small"
            tooltip={`Pastes ${where}`}
            tooltipOptions={{ position: 'top' }}
            onClick={() => add(copiesOf(copied))}
          />
        )}
        {!readOnly && selection.length > 0 && (
          <>
            <Button label="Move to…" icon="pi pi-sort" outlined size="small" onClick={() => setMoving(true)} />
            <Button
              label={`Delete ${selection.length} selected`}
              icon="pi pi-trash"
              severity="danger"
              outlined
              size="small"
              onClick={() => remove(selected)}
            />
          </>
        )}
        <div className="list-toolbar-end">
          <MultiSelect
            value={hideable.filter((c) => c.visible).map((c) => c.colName)}
            options={hideable.map((c) => ({ label: c.displayName, value: c.colName }))}
            onChange={(e) => preferences.setVisible(e.value as string[])}
            placeholder="Columns"
            selectedItemsLabel="Columns"
            maxSelectedLabels={0}
            aria-label="Visible columns"
            dropdownIcon="pi pi-table"
            className="list-columns"
          />
        </div>
      </div>

      <DataTable
        value={steps}
        dataKey="uuid"
        selectionMode="checkbox"
        selection={selection}
        onSelectionChange={(e) => onSelectionChange(e.value as ScriptStep[])}
        reorderableRows={!readOnly && !virtual}
        onRowReorder={(e) => update((d) => void (d.steps = e.value as ScriptStep[]))}
        scrollable={virtual}
        scrollHeight={virtual ? '70vh' : undefined}
        virtualScrollerOptions={virtual ? { itemSize: ROW_HEIGHT } : undefined}
        loading={preferences.isLoading}
        emptyMessage="No steps yet."
        size="small"
        stripedRows
        resizableColumns
        columnResizeMode="expand"
        onColumnResizeEnd={(e) => {
          const key = (e.column.props.columnKey ?? '') as string;
          if (key) {
            preferences.setWidth(key, e.element.offsetWidth);
          }
        }}
        tableStyle={{ minWidth: '48rem' }}
        className="step-table"
      >
        {!readOnly && !virtual && <Column rowReorder style={{ width: '2.5rem' }} />}
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
        {visible.map((pref) => columnFor(pref, positions, readOnly, (uuid) => remove(new Set([uuid])), open))}
      </DataTable>

      {editing?.type === 'request' && (
        <RequestDialog
          scriptId={scriptId}
          groups={[...new Set(steps.map((s) => s.scriptGroupName?.trim()).filter((g): g is string => !!g))].sort()}
          step={editing.step}
          readOnly={readOnly}
          onHide={() => setEditing(undefined)}
          onSave={save}
        />
      )}
      {editing && editing.type !== 'request' && (
        <StepDialog type={editing.type} step={editing.step} readOnly={readOnly} onHide={() => setEditing(undefined)} onSave={save} />
      )}
      {moving && (
        <MoveDialog
          count={selection.length}
          max={steps.length}
          onHide={() => setMoving(false)}
          onMove={(position) => {
            update((d) => void (d.steps = moveSteps(d.steps ?? [], selected, position)));
            setMoving(false);
          }}
        />
      )}
    </div>
  );
}

function columnFor(
  pref: ColumnPreference,
  positions: Map<string | undefined, number>,
  readOnly: boolean,
  onDelete: (uuid: string) => void,
  open: (step: ScriptStep) => void,
) {
  const key = pref.colName!;
  const width = pref.size ? `${pref.size}px` : undefined;
  if (key === 'actionsColumn') {
    return (
      <Column
        key={key}
        columnKey={key}
        style={{ width: readOnly ? '3rem' : '6rem' }}
        resizeable={false}
        body={(step: ScriptStep) => (
          <div className="row-actions">
            {isEditable(step) && (
              <Button
                icon={readOnly ? 'pi pi-eye' : 'pi pi-pencil'}
                rounded
                text
                aria-label={`${readOnly ? 'View' : 'Edit'} step ${positions.get(step.uuid)}`}
                tooltip={readOnly ? 'View' : 'Edit'}
                tooltipOptions={{ position: 'top' }}
                onClick={() => open(step)}
              />
            )}
            {!readOnly && (
              <Button
                icon="pi pi-trash"
                rounded
                text
                severity="danger"
                aria-label={`Delete step ${positions.get(step.uuid)}`}
                tooltip="Delete"
                tooltipOptions={{ position: 'top' }}
                onClick={() => onDelete(step.uuid ?? '')}
              />
            )}
          </div>
        )}
      />
    );
  }
  const cell = CELLS[key]!;
  return (
    <Column
      key={key}
      columnKey={key}
      header={pref.displayName}
      body={(step: ScriptStep) => cell(step, positions.get(step.uuid) ?? 0, open)}
      // the data summary takes whatever room the other columns leave
      style={key === 'dataColumn' ? undefined : { width }}
      bodyClassName="ellipsis"
    />
  );
}

function MoveDialog({
  count,
  max,
  onHide,
  onMove,
}: {
  count: number;
  max: number;
  onHide: () => void;
  onMove: (position: number) => void;
}) {
  const [position, setPosition] = useState<number | null>(1);
  const input = useRef<HTMLInputElement>(null);
  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (position) {
      onMove(position);
    }
  };
  return (
    <Dialog
      header={`Move ${count} ${count === 1 ? 'step' : 'steps'}`}
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(input)}
    >
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="move-position">New position of the first selected step</label>
        <InputNumber
          inputId="move-position"
          inputRef={input}
          value={position}
          onValueChange={(e) => setPosition(e.value ?? null)}
          min={1}
          max={max}
          useGrouping={false}
        />
        <small className="field-help">Selected steps stay in their current order. 1 moves them to the top.</small>
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Move" disabled={!position} />
        </div>
      </form>
    </Dialog>
  );
}

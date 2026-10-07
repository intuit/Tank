import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { InputNumber } from 'primereact/inputnumber';
import { MultiSelect } from 'primereact/multiselect';
import { useRef, useState, type FormEvent, type ReactNode } from 'react';
import { useTablePreferences, type ColumnPreference } from '../../../hooks/useTablePreferences';
import { deleteSteps, hasAssignment, hasValidation, moveSteps, STEP_TYPES, type ScriptStep } from './steps';
import type { ScriptUpdate } from './useScriptDraft';

/**
 * Past this many steps the table scrolls virtually, which keeps large recordings responsive but
 * can't be combined with drag-and-drop, so steps are moved with "Move to" instead.
 */
export const VIRTUAL_SCROLL_FROM = 300;
const ROW_HEIGHT = 41;

type Cell = (step: ScriptStep, position: number) => ReactNode;

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
  dataColumn: (s) => (
    <span title={s.label}>
      {s.type && s.type !== 'request' && <span className="step-type">{STEP_TYPES[s.type] ?? s.type}</span>}
      {s.label}
    </span>
  ),
  mimeColumn: (s) => s.mimetype,
  loggingColumn: (s) => s.loggingKey,
  failureColumn: (s) => s.onFail,
  commentsColumn: (s) => s.comments,
  validationColumn: (s) => check(hasValidation(s)),
  assignmentColumn: (s) => check(hasAssignment(s)),
};

/** The script's steps (ScriptEditor's step table), edited in the draft */
export function StepTable({
  steps,
  update,
  readOnly,
  selection,
  onSelectionChange,
}: {
  steps: ScriptStep[];
  update: ScriptUpdate;
  readOnly: boolean;
  selection: ScriptStep[];
  onSelectionChange: (steps: ScriptStep[]) => void;
}) {
  const preferences = useTablePreferences('scriptSteps');
  const [moving, setMoving] = useState(false);
  const virtual = steps.length > VIRTUAL_SCROLL_FROM;
  const positions = new Map(steps.map((s, i) => [s.uuid, i + 1]));
  const selected = new Set(selection.map((s) => s.uuid ?? ''));

  const remove = (uuids: Set<string>) => {
    update((d) => void (d.steps = deleteSteps(d.steps ?? [], uuids)));
    onSelectionChange(selection.filter((s) => !uuids.has(s.uuid ?? '')));
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
        {visible.map((pref) => columnFor(pref, positions, readOnly, (uuid) => remove(new Set([uuid]))))}
      </DataTable>

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
) {
  const key = pref.colName!;
  const width = pref.size ? `${pref.size}px` : undefined;
  if (key === 'actionsColumn') {
    return readOnly ? null : (
      <Column
        key={key}
        columnKey={key}
        style={{ width: '4rem' }}
        resizeable={false}
        body={(step: ScriptStep) => (
          <div className="row-actions">
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
      body={(step: ScriptStep) => cell(step, positions.get(step.uuid) ?? 0)}
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
      onShow={() => input.current?.focus()}
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

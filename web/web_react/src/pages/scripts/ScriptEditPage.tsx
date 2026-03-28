import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { DataTable, type DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tag } from 'primereact/tag';
import { InputText } from 'primereact/inputtext';
import { Menu } from 'primereact/menu';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Card } from 'primereact/card';
import { useScriptEditor } from '../../hooks/useScriptEditor';
import { blankStep, stepLabel, type ScriptStep, type StepType } from '../../types/scriptEditor';
import { RequestStepEditor } from './editor/RequestStepEditor';
import {
  ThinkTimeEditor,
  SleepEditor,
  VariableEditor,
  LogicEditor,
  CookieEditor,
  AuthEditor,
  ClearEditor,
  AggregatorEditor,
} from './editor/SimpleStepEditors';

// ── Step-type badge colours ───────────────────────────────────────────────────
const TYPE_SEVERITY: Record<string, 'success' | 'info' | 'warning' | 'danger' | 'secondary'> = {
  request:        'info',
  thinkTime:      'success',
  sleep:          'success',
  variable:       'warning',
  logic:          'danger',
  cookie:         'secondary',
  authentication: 'secondary',
  clear:          'secondary',
  AGGREGATE:      'warning',
};

const TYPE_LABELS: Record<string, string> = {
  request:        'Request',
  thinkTime:      'Think',
  sleep:          'Sleep',
  variable:       'Variable',
  logic:          'Logic',
  cookie:         'Cookie',
  authentication: 'Auth',
  clear:          'Clear',
  AGGREGATE:      'Timer',
};

const INSERT_TYPES: { label: string; type: StepType }[] = [
  { label: 'Request',        type: 'request' },
  { label: 'Think Time',     type: 'thinkTime' },
  { label: 'Sleep',          type: 'sleep' },
  { label: 'Variable',       type: 'variable' },
  { label: 'Logic',          type: 'logic' },
  { label: 'Cookie',         type: 'cookie' },
  { label: 'Authentication', type: 'authentication' },
  { label: 'Clear Session',  type: 'clear' },
  { label: 'Timer Group',    type: 'AGGREGATE' },
];

export function ScriptEditPage() {
  const { id } = useParams<{ id: string }>();
  const scriptId = Number(id);
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const insertMenuRef = useRef<Menu>(null);

  const { script, loading, loadError, saveState, dirty, load, update, save } =
    useScriptEditor(scriptId);

  const [selectedStep, setSelectedStep] = useState<ScriptStep | null>(null);
  const [globalFilter, setGlobalFilter] = useState('');

  useEffect(() => { load(); }, [load]);

  // Keep selectedStep in sync after updates
  useEffect(() => {
    if (!selectedStep || !script) return;
    const fresh = script.steps.find(s => s.uuid === selectedStep.uuid);
    if (fresh) setSelectedStep(fresh);
  }, [script?.steps]);

  if (loading) return <ProgressSpinner />;
  if (loadError) return <Message severity="error" text={loadError} />;
  if (!script)   return null;

  // ── Toolbar save button label ───────────────────────────────────────────────
  const saveLabel =
    saveState === 'saving' ? 'Saving…' :
    saveState === 'saved'  ? 'Saved!' :
    saveState === 'error'  ? 'Error!' :
    'Save';

  const saveSeverity: 'success' | 'danger' | undefined =
    saveState === 'saved'  ? 'success' :
    saveState === 'error'  ? 'danger'  :
    undefined;

  // ── Step insert ─────────────────────────────────────────────────────────────
  const insertStep = (type: StepType) => {
    update(prev => {
      const insertAt = selectedStep
        ? prev.steps.findIndex(s => s.uuid === selectedStep.uuid) + 1
        : prev.steps.length;
      const newStep = blankStep(type, insertAt);
      const steps = [...prev.steps];
      steps.splice(insertAt, 0, newStep);
      return { ...prev, steps: steps.map((s, i) => ({ ...s, stepIndex: i })) };
    });
  };

  // ── Step delete ─────────────────────────────────────────────────────────────
  const deleteStep = (step: ScriptStep) => {
    confirmDialog({
      message: `Delete step "${stepLabel(step)}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: () => {
        update(prev => ({
          ...prev,
          steps: prev.steps
            .filter(s => s.uuid !== step.uuid)
            .map((s, i) => ({ ...s, stepIndex: i })),
        }));
        if (selectedStep?.uuid === step.uuid) setSelectedStep(null);
      },
    });
  };

  // ── Step reorder ────────────────────────────────────────────────────────────
  const moveStep = (step: ScriptStep, direction: 'up' | 'down') => {
    update(prev => {
      const idx = prev.steps.findIndex(s => s.uuid === step.uuid);
      if (idx === -1) return prev;
      if (direction === 'up' && idx === 0) return prev;
      if (direction === 'down' && idx === prev.steps.length - 1) return prev;
      const steps = [...prev.steps];
      const target = direction === 'up' ? idx - 1 : idx + 1;
      [steps[idx], steps[target]] = [steps[target], steps[idx]];
      return { ...prev, steps: steps.map((s, i) => ({ ...s, stepIndex: i })) };
    });
  };

  // ── Step update ─────────────────────────────────────────────────────────────
  const updateStep = (updated: ScriptStep) => {
    update(prev => ({
      ...prev,
      steps: prev.steps.map(s => s.uuid === updated.uuid ? updated : s),
    }));
    setSelectedStep(updated);
  };

  // ── Save with toast ─────────────────────────────────────────────────────────
  const handleSave = async () => {
    await save();
    if (saveState !== 'error') {
      toast.current?.show({ severity: 'success', summary: 'Script saved' });
    } else {
      toast.current?.show({ severity: 'error', summary: 'Save failed' });
    }
  };

  // ── Menu items for Insert button ────────────────────────────────────────────
  const insertMenuItems = INSERT_TYPES.map(({ label, type }) => ({
    label,
    command: () => insertStep(type),
  }));

  // ── Columns ─────────────────────────────────────────────────────────────────
  const indexBody = (row: ScriptStep) => (
    <span className="text-color-secondary text-sm">{row.stepIndex + 1}</span>
  );

  const typeBody = (row: ScriptStep) => (
    <Tag
      value={TYPE_LABELS[row.type] ?? row.type}
      severity={TYPE_SEVERITY[row.type] ?? 'info'}
    />
  );

  const labelBody = (row: ScriptStep) => (
    <span
      className="cursor-pointer text-primary"
      style={{ wordBreak: 'break-all' }}
      onClick={() => setSelectedStep(row)}
    >
      {stepLabel(row)}
    </span>
  );

  const groupBody = (row: ScriptStep) => (
    <span className="text-sm text-color-secondary">{row.scriptGroupName ?? ''}</span>
  );

  const actionsBody = (row: ScriptStep, opts: { rowIndex: number }) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-chevron-up"
        size="small" text severity="secondary"
        onClick={() => moveStep(row, 'up')}
        disabled={opts.rowIndex === 0}
        tooltip="Move up"
      />
      <Button
        icon="pi pi-chevron-down"
        size="small" text severity="secondary"
        onClick={() => moveStep(row, 'down')}
        disabled={opts.rowIndex === (script?.steps.length ?? 0) - 1}
        tooltip="Move down"
      />
      <Button
        icon="pi pi-trash"
        size="small" text severity="danger"
        onClick={() => deleteStep(row)}
        tooltip="Delete"
      />
    </div>
  );

  // ── Step detail panel ────────────────────────────────────────────────────────
  const renderStepEditor = () => {
    if (!selectedStep) {
      return (
        <div className="flex align-items-center justify-content-center h-full text-color-secondary">
          Select a step to edit, or insert a new one.
        </div>
      );
    }

    const editorProps = { step: selectedStep, onChange: updateStep };

    let editor: React.ReactNode;
    switch (selectedStep.type) {
      case 'request':        editor = <RequestStepEditor {...editorProps} />; break;
      case 'thinkTime':      editor = <ThinkTimeEditor   {...editorProps} />; break;
      case 'sleep':          editor = <SleepEditor        {...editorProps} />; break;
      case 'variable':       editor = <VariableEditor     {...editorProps} />; break;
      case 'logic':          editor = <LogicEditor        {...editorProps} />; break;
      case 'cookie':         editor = <CookieEditor       {...editorProps} />; break;
      case 'authentication': editor = <AuthEditor         {...editorProps} />; break;
      case 'clear':          editor = <ClearEditor />; break;
      case 'AGGREGATE':      editor = <AggregatorEditor   {...editorProps} />; break;
      default:               editor = <Message severity="warn" text={`Unknown step type: ${selectedStep.type}`} />;
    }

    return (
      <Card
        title={
          <div className="flex align-items-center gap-2">
            <Tag value={TYPE_LABELS[selectedStep.type] ?? selectedStep.type} severity={TYPE_SEVERITY[selectedStep.type]} />
            <span className="text-base font-semibold">{stepLabel(selectedStep)}</span>
          </div>
        }
        className="h-full"
        style={{ overflowY: 'auto' }}
      >
        {editor}
      </Card>
    );
  };

  // ── Layout ───────────────────────────────────────────────────────────────────
  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <Button icon="pi pi-arrow-left" rounded text onClick={() => {
        if (dirty) {
          confirmDialog({
            message: 'You have unsaved changes. Leave anyway?',
            header: 'Unsaved Changes',
            icon: 'pi pi-exclamation-triangle',
            acceptClassName: 'p-button-danger',
            accept: () => navigate(`/scripts/${scriptId}`),
          });
        } else {
          navigate(`/scripts/${scriptId}`);
        }
      }} />
      <span className="font-bold text-xl">{script.name}</span>
      {dirty && <Tag value="unsaved" severity="warning" />}
    </div>
  );

  const rightToolbar = (
    <div className="flex gap-2 align-items-center">
      <InputText
        placeholder="Search steps…"
        value={globalFilter}
        onChange={e => setGlobalFilter(e.target.value)}
        className="p-inputtext-sm"
        style={{ width: '200px' }}
      />
      <Menu model={insertMenuItems} popup ref={insertMenuRef} />
      <Button
        label="Insert Step"
        icon="pi pi-plus"
        size="small"
        onClick={e => insertMenuRef.current?.toggle(e)}
      />
      <Button
        label={saveLabel}
        icon={saveState === 'saving' ? 'pi pi-spin pi-spinner' : 'pi pi-save'}
        size="small"
        severity={saveSeverity}
        disabled={!dirty || saveState === 'saving'}
        onClick={handleSave}
      />
    </div>
  );

  return (
    <div className="flex flex-column gap-2" style={{ height: 'calc(100vh - 80px)' }}>
      <Toast ref={toast} />
      <ConfirmDialog />
      <Toolbar start={leftToolbar} end={rightToolbar} />

      <Splitter style={{ flex: 1, overflow: 'hidden' }}>
        {/* Left: step list */}
        <SplitterPanel size={45} minSize={25} style={{ overflowY: 'auto' }}>
          <DataTable
            value={script.steps}
            dataKey="uuid"
            size="small"
            selectionMode="single"
            selection={selectedStep ?? undefined}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<ScriptStep[]>) =>
              setSelectedStep(e.value as ScriptStep)
            }
            globalFilter={globalFilter}
            globalFilterFields={['type', 'name', 'hostname', 'simplePath', 'scriptGroupName', 'label']}
            emptyMessage="No steps. Use 'Insert Step' to add one."
            scrollable
            scrollHeight="flex"
            rowClassName={row => row.uuid === selectedStep?.uuid ? 'bg-primary-50' : ''}
          >
            <Column field="stepIndex" header="#" body={indexBody} style={{ width: '44px' }} />
            <Column field="type"      header="Type"  body={typeBody}  style={{ width: '80px' }} />
            <Column field="scriptGroupName" header="Group" body={groupBody} style={{ width: '100px' }} />
            <Column header="Step" body={labelBody} />
            <Column header="" body={actionsBody} style={{ width: '120px' }} />
          </DataTable>
        </SplitterPanel>

        {/* Right: step editor */}
        <SplitterPanel size={55} minSize={30} style={{ overflowY: 'auto', padding: '8px' }}>
          {renderStepEditor()}
        </SplitterPanel>
      </Splitter>
    </div>
  );
}

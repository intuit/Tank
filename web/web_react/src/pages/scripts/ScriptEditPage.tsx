import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { DataTable, type DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tag } from 'primereact/tag';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Menu } from 'primereact/menu';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Dialog } from 'primereact/dialog';
import { ScrollPanel } from 'primereact/scrollpanel';
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
  AssignmentEditor,
  ValidationEditor,
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
  assignment:     'warning',
  validation:     'danger',
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
  assignment:     'Assign',
  validation:     'Validate',
};

const INSERT_TYPES: { label: string; type: StepType }[] = [
  { label: 'Request',        type: 'request' },
  { label: 'Think Time',     type: 'thinkTime' },
  { label: 'Sleep',          type: 'sleep' },
  { label: 'Variable',       type: 'variable' },
  { label: 'Assignment',     type: 'assignment' },
  { label: 'Validation',     type: 'validation' },
  { label: 'Logic',          type: 'logic' },
  { label: 'Cookie',         type: 'cookie' },
  { label: 'Authentication', type: 'authentication' },
  { label: 'Clear Session',  type: 'clear' },
  { label: 'Timer Group',    type: 'AGGREGATE' },
];

// ── Client-side validation ────────────────────────────────────────────────────
function validateScript(steps: ScriptStep[]): string[] {
  const issues: string[] = [];
  steps.forEach((s, i) => {
    const num = i + 1;
    switch (s.type) {
      case 'request':
        if (!s.hostname) issues.push(`Step ${num}: Request is missing a hostname.`);
        if (!s.simplePath) issues.push(`Step ${num}: Request is missing a path.`);
        break;
      case 'thinkTime':
        if (!s.minThinkTime) issues.push(`Step ${num}: Think Time is missing a minimum value.`);
        if (!s.maxThinkTime) issues.push(`Step ${num}: Think Time is missing a maximum value.`);
        break;
      case 'variable':
        if (!s.varKey) issues.push(`Step ${num}: Variable is missing a key.`);
        break;
      case 'logic':
        if (!s.script) issues.push(`Step ${num}: Logic step has no script.`);
        break;
      case 'cookie':
        if (!s.cookieName) issues.push(`Step ${num}: Cookie is missing a name.`);
        break;
      case 'authentication':
        if (!s.authUser) issues.push(`Step ${num}: Authentication is missing a username.`);
        break;
    }
  });
  return issues;
}

export function ScriptEditPage() {
  const { id } = useParams<{ id: string }>();
  const scriptId = Number(id);
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const insertMenuRef = useRef<Menu>(null);

  const { script, loading, loadError, saveState, dirty, load, update, save, saveAs } =
    useScriptEditor(scriptId);

  const [selectedStep, setSelectedStep] = useState<ScriptStep | null>(null);
  const [globalFilter, setGlobalFilter] = useState('');

  // ── Edit / Insert step dialog ─────────────────────────────────────────────
  const [editDialogStep, setEditDialogStep]   = useState<ScriptStep | null>(null);
  const [draftStep, setDraftStep]             = useState<ScriptStep | null>(null);
  const [editDialogIsNew, setEditDialogIsNew] = useState(false);

  // ── Comments dialog ───────────────────────────────────────────────────────
  const [showComments, setShowComments]   = useState(false);
  const [draftComments, setDraftComments] = useState('');

  // ── Save As dialog ────────────────────────────────────────────────────────
  const [showSaveAs, setShowSaveAs]   = useState(false);
  const [saveAsName, setSaveAsName]   = useState('');

  // ── Search & Replace dialog ───────────────────────────────────────────────
  const [showReplace, setShowReplace] = useState(false);
  const [replaceFind, setReplaceFind] = useState('');
  const [replaceWith, setReplaceWith] = useState('');

  // ── Validate dialog ───────────────────────────────────────────────────────
  const [showValidate, setShowValidate]       = useState(false);
  const [validateMessages, setValidateMessages] = useState<string[]>([]);

  useEffect(() => { load(); }, [load]);

  if (loading) return <ProgressSpinner />;
  if (loadError) return <Message severity="error" text={loadError} />;
  if (!script)   return null;

  // ── Toolbar save button label ─────────────────────────────────────────────
  const saveLabel =
    saveState === 'saving' ? 'Saving…' :
    saveState === 'saved'  ? 'Saved!' :
    saveState === 'error'  ? 'Error!' :
    'Save';

  const saveSeverity: 'success' | 'danger' | undefined =
    saveState === 'saved'  ? 'success' :
    saveState === 'error'  ? 'danger'  :
    undefined;

  // ── Open edit dialog for existing step ───────────────────────────────────
  const openEditDialog = (step: ScriptStep) => {
    setEditDialogStep(step);
    setDraftStep({ ...step });
    setEditDialogIsNew(false);
  };

  // ── Open insert dialog for new step ──────────────────────────────────────
  const openInsertDialog = (type: StepType) => {
    const insertAt = selectedStep
      ? script.steps.findIndex(s => s.uuid === selectedStep.uuid) + 1
      : script.steps.length;
    const newStep = blankStep(type, insertAt);
    setEditDialogStep(newStep);
    setDraftStep(newStep);
    setEditDialogIsNew(true);
  };

  // ── Commit edit dialog ────────────────────────────────────────────────────
  const commitEditDialog = () => {
    if (!draftStep) return;
    if (editDialogIsNew) {
      update(prev => {
        const insertAt = selectedStep
          ? prev.steps.findIndex(s => s.uuid === selectedStep.uuid) + 1
          : prev.steps.length;
        const steps = [...prev.steps];
        steps.splice(insertAt, 0, draftStep);
        return { ...prev, steps: steps.map((s, i) => ({ ...s, stepIndex: i })) };
      });
    } else {
      update(prev => ({
        ...prev,
        steps: prev.steps.map(s => s.uuid === draftStep.uuid ? draftStep : s),
      }));
      if (selectedStep?.uuid === draftStep.uuid) setSelectedStep(draftStep);
    }
    setEditDialogStep(null);
    setDraftStep(null);
  };

  const cancelEditDialog = () => {
    setEditDialogStep(null);
    setDraftStep(null);
  };

  // ── Step delete ───────────────────────────────────────────────────────────
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

  // ── Step reorder ──────────────────────────────────────────────────────────
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

  // ── Step duplicate ────────────────────────────────────────────────────────
  const duplicateStep = (step: ScriptStep) => {
    update(prev => {
      const idx = prev.steps.findIndex(s => s.uuid === step.uuid);
      const clone: ScriptStep = { ...step, uuid: crypto.randomUUID() };
      const steps = [...prev.steps];
      steps.splice(idx + 1, 0, clone);
      return { ...prev, steps: steps.map((s, i) => ({ ...s, stepIndex: i })) };
    });
  };

  // ── Replace All ───────────────────────────────────────────────────────────
  const handleReplaceAll = () => {
    if (!replaceFind) return;
    let count = 0;
    update(prev => ({
      ...prev,
      steps: prev.steps.map(step => {
        const updated: ScriptStep = { ...step };
        const stringFields: (keyof ScriptStep)[] = [
          'name', 'label', 'hostname', 'simplePath', 'url', 'payload', 'response',
          'varKey', 'varValue', 'script', 'cookieName', 'cookieValue', 'cookiePath',
          'cookieDomain', 'authUser', 'authHost', 'authRealm', 'loggingKey',
          'assignmentKey', 'assignmentValue', 'validationKey', 'validationValue',
        ];
        for (const field of stringFields) {
          const val = updated[field] as string | undefined;
          if (val && val.includes(replaceFind)) {
            (updated as unknown as Record<string, unknown>)[field] = val.split(replaceFind).join(replaceWith);
            count++;
          }
        }
        return updated;
      }),
    }));
    toast.current?.show({ severity: 'success', summary: `Replaced ${count} occurrence(s)` });
  };

  // ── Save with toast ───────────────────────────────────────────────────────
  const handleSave = async () => {
    await save();
    if (saveState !== 'error') {
      toast.current?.show({ severity: 'success', summary: 'Script saved' });
    } else {
      toast.current?.show({ severity: 'error', summary: 'Save failed' });
    }
  };

  // ── Save As ───────────────────────────────────────────────────────────────
  const handleSaveAs = async () => {
    if (!saveAsName.trim()) return;
    const newId = await saveAs(saveAsName.trim());
    setShowSaveAs(false);
    setSaveAsName('');
    if (newId) {
      toast.current?.show({ severity: 'success', summary: `Saved as "${saveAsName}"` });
      navigate(`/scripts/${newId}/edit`);
    } else {
      toast.current?.show({ severity: 'error', summary: 'Save As failed' });
    }
  };

  // ── Validate ──────────────────────────────────────────────────────────────
  const handleValidate = () => {
    setValidateMessages(validateScript(script.steps));
    setShowValidate(true);
  };

  // ── Menu items for Insert button ──────────────────────────────────────────
  const insertMenuItems = INSERT_TYPES.map(({ label, type }) => ({
    label,
    command: () => openInsertDialog(type),
  }));

  // ── Columns ───────────────────────────────────────────────────────────────
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
      onClick={() => openEditDialog(row)}
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
        icon="pi pi-pencil"
        size="small" text severity="info"
        onClick={() => openEditDialog(row)}
        tooltip="Edit"
      />
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
        icon="pi pi-copy"
        size="small" text severity="secondary"
        onClick={() => duplicateStep(row)}
        tooltip="Duplicate"
      />
      <Button
        icon="pi pi-trash"
        size="small" text severity="danger"
        onClick={() => deleteStep(row)}
        tooltip="Delete"
      />
    </div>
  );

  // ── Step editor content (used inside edit dialog) ─────────────────────────
  const renderStepEditorContent = (step: ScriptStep, onChange: (s: ScriptStep) => void) => {
    const editorProps = { step, onChange };
    switch (step.type) {
      case 'request':        return <RequestStepEditor {...editorProps} />;
      case 'thinkTime':      return <ThinkTimeEditor   {...editorProps} />;
      case 'sleep':          return <SleepEditor        {...editorProps} />;
      case 'variable':       return <VariableEditor     {...editorProps} />;
      case 'logic':          return <LogicEditor        {...editorProps} />;
      case 'cookie':         return <CookieEditor       {...editorProps} />;
      case 'authentication': return <AuthEditor         {...editorProps} />;
      case 'clear':          return <ClearEditor />;
      case 'AGGREGATE':      return <AggregatorEditor   {...editorProps} />;
      case 'assignment':     return <AssignmentEditor   {...editorProps} />;
      case 'validation':     return <ValidationEditor   {...editorProps} />;
      default:               return <Message severity="warn" text={`Unknown step type: ${step.type}`} />;
    }
  };

  // ── Edit dialog width (request steps need more room) ──────────────────────
  const editDialogWidth =
    editDialogStep?.type === 'request' ? '80vw' : '500px';

  // ── Layout ────────────────────────────────────────────────────────────────
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
        style={{ width: '180px' }}
      />
      <Button
        label="Replace"
        icon="pi pi-search"
        size="small"
        severity="secondary"
        onClick={() => setShowReplace(true)}
      />
      <Button
        label="Comments"
        icon="pi pi-comment"
        size="small"
        severity="secondary"
        onClick={() => { setDraftComments(script.comments ?? ''); setShowComments(true); }}
      />
      <Button
        label="Validate"
        icon="pi pi-check-circle"
        size="small"
        severity="secondary"
        onClick={handleValidate}
      />
      <Menu model={insertMenuItems} popup ref={insertMenuRef} />
      <Button
        label="Insert Step"
        icon="pi pi-plus"
        size="small"
        onClick={e => insertMenuRef.current?.toggle(e)}
      />
      <Button
        label="Save As"
        icon="pi pi-copy"
        size="small"
        severity="secondary"
        onClick={() => { setSaveAsName(script.name + ' (copy)'); setShowSaveAs(true); }}
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

      {/* ── Edit / Insert Step Dialog ─────────────────────────────────────── */}
      <Dialog
        header={
          editDialogStep
            ? <div className="flex align-items-center gap-2">
                <Tag
                  value={TYPE_LABELS[editDialogStep.type] ?? editDialogStep.type}
                  severity={TYPE_SEVERITY[editDialogStep.type]}
                />
                <span>{editDialogIsNew ? `Insert ${TYPE_LABELS[editDialogStep.type] ?? editDialogStep.type}` : stepLabel(editDialogStep)}</span>
              </div>
            : undefined
        }
        visible={!!editDialogStep}
        style={{ width: editDialogWidth }}
        maximizable={editDialogStep?.type === 'request'}
        onHide={cancelEditDialog}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={cancelEditDialog} />
            <Button
              label={editDialogIsNew ? 'Insert' : 'Save'}
              icon="pi pi-check"
              onClick={commitEditDialog}
              disabled={!draftStep}
            />
          </div>
        }
      >
        {draftStep && renderStepEditorContent(draftStep, setDraftStep)}
      </Dialog>

      {/* ── Comments Dialog ───────────────────────────────────────────────── */}
      <Dialog
        header={`Comments — ${script.name}`}
        visible={showComments}
        style={{ width: '500px' }}
        onHide={() => setShowComments(false)}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={() => setShowComments(false)} />
            <Button
              label="Save"
              icon="pi pi-check"
              onClick={() => {
                update(prev => ({ ...prev, comments: draftComments }));
                setShowComments(false);
              }}
            />
          </div>
        }
      >
        <InputTextarea
          value={draftComments}
          onChange={e => setDraftComments(e.target.value)}
          rows={6}
          className="w-full"
          placeholder="Enter comments for this script…"
        />
      </Dialog>

      {/* ── Save As Dialog ────────────────────────────────────────────────── */}
      <Dialog
        header="Save As"
        visible={showSaveAs}
        style={{ width: '400px' }}
        onHide={() => setShowSaveAs(false)}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={() => setShowSaveAs(false)} />
            <Button
              label="Save As"
              icon="pi pi-copy"
              onClick={handleSaveAs}
              disabled={!saveAsName.trim() || saveState === 'saving'}
            />
          </div>
        }
      >
        <div className="flex flex-column gap-2 pt-2">
          <label className="font-semibold text-sm">New script name</label>
          <InputText
            value={saveAsName}
            onChange={e => setSaveAsName(e.target.value)}
            className="w-full"
            onKeyDown={e => { if (e.key === 'Enter') handleSaveAs(); }}
            autoFocus
          />
        </div>
      </Dialog>

      {/* ── Search & Replace Dialog ───────────────────────────────────────── */}
      <Dialog
        header="Search & Replace"
        visible={showReplace}
        style={{ width: '420px' }}
        onHide={() => setShowReplace(false)}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={() => setShowReplace(false)} />
            <Button label="Replace All" icon="pi pi-sync" onClick={handleReplaceAll} disabled={!replaceFind} />
          </div>
        }
      >
        <div className="flex flex-column gap-3 pt-2">
          <div className="flex flex-column gap-1">
            <label className="font-semibold text-sm">Find</label>
            <InputText
              value={replaceFind}
              onChange={e => setReplaceFind(e.target.value)}
              className="p-inputtext-sm w-full"
              placeholder="Text to find…"
            />
          </div>
          <div className="flex flex-column gap-1">
            <label className="font-semibold text-sm">Replace with</label>
            <InputText
              value={replaceWith}
              onChange={e => setReplaceWith(e.target.value)}
              className="p-inputtext-sm w-full"
              placeholder="Replacement text…"
            />
          </div>
        </div>
      </Dialog>

      {/* ── Validate Dialog ───────────────────────────────────────────────── */}
      <Dialog
        header="Validate Script"
        visible={showValidate}
        style={{ width: '500px' }}
        onHide={() => setShowValidate(false)}
        footer={
          <Button label="Close" onClick={() => setShowValidate(false)} />
        }
      >
        <ScrollPanel style={{ height: '300px' }}>
          {validateMessages.length === 0
            ? <Message severity="success" text="No issues found." className="w-full" />
            : validateMessages.map((msg, i) => (
                <Message key={i} severity="warn" text={msg} className="w-full mb-2" />
              ))
          }
        </ScrollPanel>
      </Dialog>

      <Toolbar start={leftToolbar} end={rightToolbar} />

      {/* ── Step list (full width) ────────────────────────────────────────── */}
      <div style={{ flex: 1, overflow: 'hidden' }}>
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
          <Column field="stepIndex" header="#"      body={indexBody} style={{ width: '44px' }} />
          <Column field="type"      header="Type"   body={typeBody}  style={{ width: '80px' }} />
          <Column field="scriptGroupName" header="Group" body={groupBody} style={{ width: '120px' }} />
          <Column header="Step"    body={labelBody} />
          <Column header=""        body={actionsBody} style={{ width: '160px' }} />
        </DataTable>
      </div>
    </div>
  );
}

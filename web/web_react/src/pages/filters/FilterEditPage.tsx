import { useState, useRef, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dialog } from 'primereact/dialog';
import { Toast } from 'primereact/toast';
import { ProgressSpinner } from 'primereact/progressspinner';
import { SelectButton } from 'primereact/selectbutton';
import { useFilter, useCreateFilter, useUpdateFilter } from '../../hooks/useFilters';
import type { FilterConditionTO, FilterActionTO } from '../../types/filter';
import {
  CONDITION_SCOPES,
  CONDITION_TYPES,
  ACTION_TYPES,
  scopesForAction,
} from '../../types/filter';

const FILTER_TYPE_OPTIONS = [
  { label: 'Internal Filter', value: 'INTERNAL' },
  { label: 'External Filter', value: 'EXTERNAL' },
];

const ALL_ANY_OPTIONS = [
  { label: 'All', value: true },
  { label: 'Any', value: false },
];

const emptyCondition = (): FilterConditionTO => ({ scope: CONDITION_SCOPES[0], condition: CONDITION_TYPES[0], value: '' });
const emptyAction = (): FilterActionTO => ({ action: 'remove', scope: 'request', key: '', value: '' });

export function FilterEditPage() {
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id && id !== 'new';
  const filterId = isEdit ? parseInt(id) : 0;
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  const { data: existing, isLoading } = useFilter(filterId);
  const createFilter = useCreateFilter();
  const updateFilter = useUpdateFilter();

  const [name, setName] = useState('');
  const [productName, setProductName] = useState('');
  const [filterType, setFilterType] = useState<'INTERNAL' | 'EXTERNAL'>('INTERNAL');
  const [allConditionsMustPass, setAllConditionsMustPass] = useState(true);
  const [conditions, setConditions] = useState<FilterConditionTO[]>([]);
  const [actions, setActions] = useState<FilterActionTO[]>([]);

  // Condition dialog
  const [showConditionDialog, setShowConditionDialog] = useState(false);
  const [editingConditionIdx, setEditingConditionIdx] = useState<number | null>(null);
  const [conditionDraft, setConditionDraft] = useState<FilterConditionTO>(emptyCondition());

  // Action dialog
  const [showActionDialog, setShowActionDialog] = useState(false);
  const [editingActionIdx, setEditingActionIdx] = useState<number | null>(null);
  const [actionDraft, setActionDraft] = useState<FilterActionTO>(emptyAction());

  useEffect(() => {
    if (existing) {
      setName(existing.name ?? '');
      setProductName(existing.productName ?? '');
      setFilterType(existing.filterType ?? 'INTERNAL');
      setAllConditionsMustPass(existing.allConditionsMustPass ?? true);
      setConditions(existing.conditions ?? []);
      setActions(existing.actions ?? []);
    }
  }, [existing]);

  const handleSave = async () => {
    if (!name.trim()) {
      toast.current?.show({ severity: 'warn', summary: 'Name is required' });
      return;
    }
    const payload = { name, productName, filterType, allConditionsMustPass, conditions, actions };
    try {
      if (isEdit) {
        await updateFilter.mutateAsync({ id: filterId, filter: { ...payload, id: filterId } });
        toast.current?.show({ severity: 'success', summary: 'Filter saved' });
      } else {
        await createFilter.mutateAsync(payload);
        toast.current?.show({ severity: 'success', summary: 'Filter created' });
        navigate('/filters');
      }
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save filter' });
    }
  };

  // --- Condition helpers ---
  const openNewCondition = () => {
    setEditingConditionIdx(null);
    setConditionDraft(emptyCondition());
    setShowConditionDialog(true);
  };

  const openEditCondition = (idx: number) => {
    setEditingConditionIdx(idx);
    setConditionDraft({ ...conditions[idx] });
    setShowConditionDialog(true);
  };

  const saveCondition = () => {
    if (editingConditionIdx !== null) {
      setConditions((prev) => prev.map((c, i) => (i === editingConditionIdx ? conditionDraft : c)));
    } else {
      setConditions((prev) => [...prev, conditionDraft]);
    }
    setShowConditionDialog(false);
  };

  const removeCondition = (idx: number) => setConditions((prev) => prev.filter((_, i) => i !== idx));

  // --- Action helpers ---
  const openNewAction = () => {
    setEditingActionIdx(null);
    setActionDraft(emptyAction());
    setShowActionDialog(true);
  };

  const openEditAction = (idx: number) => {
    setEditingActionIdx(idx);
    setActionDraft({ ...actions[idx] });
    setShowActionDialog(true);
  };

  const saveAction = () => {
    if (editingActionIdx !== null) {
      setActions((prev) => prev.map((a, i) => (i === editingActionIdx ? actionDraft : a)));
    } else {
      setActions((prev) => [...prev, actionDraft]);
    }
    setShowActionDialog(false);
  };

  const removeAction = (idx: number) => setActions((prev) => prev.filter((_, i) => i !== idx));

  if (isEdit && isLoading) return <ProgressSpinner />;

  const isSaving = createFilter.isPending || updateFilter.isPending;
  const scopeOptions = scopesForAction(actionDraft.action).map((s) => ({ label: s, value: s }));

  return (
    <>
      <Toast ref={toast} />

      {/* Header toolbar */}
      <div className="flex align-items-center justify-content-between mb-3">
        <span className="text-xl font-bold">{isEdit ? 'Edit Filter' : 'New Filter'}</span>
        <div className="flex gap-2">
          <Button label="Cancel" severity="secondary" icon="pi pi-times" onClick={() => navigate('/filters')} />
          <Button label="Save" icon="pi pi-check" loading={isSaving} onClick={handleSave} disabled={!name.trim()} />
        </div>
      </div>

      {/* Metadata fields */}
      <div className="card p-fluid mb-3">
        <div className="formgrid grid">
          <div className="field col-12 md:col-6">
            <label htmlFor="filterName" className="font-bold">Filter Name *</label>
            <InputText id="filterName" value={name} onChange={(e) => setName(e.target.value)} autoFocus />
          </div>
          <div className="field col-12 md:col-6">
            <label htmlFor="productName" className="font-bold">Product Group</label>
            <InputText id="productName" value={productName} onChange={(e) => setProductName(e.target.value)} placeholder="Optional" />
          </div>
          <div className="field col-12 md:col-6">
            <label className="font-bold">Filter Type</label>
            <Dropdown
              value={filterType}
              options={FILTER_TYPE_OPTIONS}
              onChange={(e) => setFilterType(e.value)}
            />
          </div>
        </div>
      </div>

      {filterType === 'INTERNAL' && (
        <>
          {/* Conditions section */}
          <div className="card mb-3">
            <div className="flex align-items-center justify-content-between mb-2">
              <div className="flex align-items-center gap-3">
                <span className="font-bold">Conditions — If</span>
                <SelectButton
                  value={allConditionsMustPass}
                  options={ALL_ANY_OPTIONS}
                  onChange={(e) => setAllConditionsMustPass(e.value)}
                  className="p-selectbutton-sm"
                />
                <span className="text-color-secondary text-sm">conditions pass</span>
              </div>
              <Button label="Add Condition" icon="pi pi-plus" size="small" onClick={openNewCondition} />
            </div>
            <DataTable
              value={conditions}
              showGridlines
              stripedRows
              emptyMessage="No conditions. Click Add Condition to add one."
            >
              <Column
                field="scope"
                header="Scope"
                body={(_, { rowIndex }) => (
                  <span className="cursor-pointer text-primary" onClick={() => openEditCondition(rowIndex)}>
                    {conditions[rowIndex].scope}
                  </span>
                )}
              />
              <Column field="condition" header="Condition" />
              <Column field="value" header="Value" />
              <Column
                header=""
                style={{ width: '60px' }}
                body={(_, { rowIndex }) => (
                  <Button
                    icon="pi pi-trash"
                    size="small"
                    text
                    severity="danger"
                    onClick={() => removeCondition(rowIndex)}
                  />
                )}
              />
            </DataTable>
          </div>

          {/* Actions section */}
          <div className="card">
            <div className="flex align-items-center justify-content-between mb-2">
              <span className="font-bold">Actions — Then</span>
              <Button label="Add Action" icon="pi pi-plus" size="small" onClick={openNewAction} />
            </div>
            <DataTable
              value={actions}
              showGridlines
              stripedRows
              emptyMessage="No actions. Click Add Action to add one."
            >
              <Column
                field="action"
                header="Action"
                body={(_, { rowIndex }) => (
                  <span className="cursor-pointer text-primary" onClick={() => openEditAction(rowIndex)}>
                    {actions[rowIndex].action}
                  </span>
                )}
              />
              <Column field="scope" header="Scope" />
              <Column field="key" header="Key" />
              <Column field="value" header="Value" />
              <Column
                header=""
                style={{ width: '60px' }}
                body={(_, { rowIndex }) => (
                  <Button
                    icon="pi pi-trash"
                    size="small"
                    text
                    severity="danger"
                    onClick={() => removeAction(rowIndex)}
                  />
                )}
              />
            </DataTable>
          </div>
        </>
      )}

      {/* Condition Dialog */}
      <Dialog
        header={editingConditionIdx !== null ? 'Edit Condition' : 'Add Condition'}
        visible={showConditionDialog}
        style={{ width: '420px' }}
        onHide={() => setShowConditionDialog(false)}
        footer={
          <div className="flex justify-content-end gap-2">
            <Button label="Cancel" severity="secondary" onClick={() => setShowConditionDialog(false)} />
            <Button label={editingConditionIdx !== null ? 'Update' : 'Add'} icon="pi pi-check" onClick={saveCondition} />
          </div>
        }
      >
        <div className="flex flex-column gap-3 p-fluid">
          <div className="field">
            <label className="font-bold">Scope *</label>
            <Dropdown
              value={conditionDraft.scope}
              options={CONDITION_SCOPES.map((s) => ({ label: s, value: s }))}
              onChange={(e) => setConditionDraft((d) => ({ ...d, scope: e.value }))}
            />
          </div>
          <div className="field">
            <label className="font-bold">Condition *</label>
            <Dropdown
              value={conditionDraft.condition}
              options={CONDITION_TYPES.map((c) => ({ label: c, value: c }))}
              onChange={(e) => setConditionDraft((d) => ({ ...d, condition: e.value }))}
            />
          </div>
          <div className="field">
            <label className="font-bold">Value *</label>
            <InputText
              value={conditionDraft.value}
              onChange={(e) => setConditionDraft((d) => ({ ...d, value: e.target.value }))}
              placeholder="Match value"
            />
          </div>
        </div>
      </Dialog>

      {/* Action Dialog */}
      <Dialog
        header={editingActionIdx !== null ? 'Edit Action' : 'Add Action'}
        visible={showActionDialog}
        style={{ width: '460px' }}
        onHide={() => setShowActionDialog(false)}
        footer={
          <div className="flex justify-content-end gap-2">
            <Button label="Cancel" severity="secondary" onClick={() => setShowActionDialog(false)} />
            <Button label={editingActionIdx !== null ? 'Update' : 'Add'} icon="pi pi-check" onClick={saveAction} />
          </div>
        }
      >
        <div className="flex flex-column gap-3 p-fluid">
          <div className="field">
            <label className="font-bold">Action *</label>
            <Dropdown
              value={actionDraft.action}
              options={ACTION_TYPES.map((a) => ({ label: a, value: a }))}
              onChange={(e) => setActionDraft((d) => ({ ...d, action: e.value, scope: scopesForAction(e.value)[0] ?? '' }))}
            />
          </div>
          <div className="field">
            <label className="font-bold">Scope *</label>
            <Dropdown
              value={actionDraft.scope}
              options={scopeOptions}
              onChange={(e) => setActionDraft((d) => ({ ...d, scope: e.value }))}
            />
          </div>
          {actionDraft.action !== 'remove' && (
            <>
              {actionDraft.scope !== 'path' && actionDraft.scope !== 'host' && actionDraft.scope !== 'request' && (
                <div className="field">
                  <label className="font-bold">Key</label>
                  <InputText
                    value={actionDraft.key ?? ''}
                    onChange={(e) => setActionDraft((d) => ({ ...d, key: e.target.value }))}
                    placeholder="Header name, param name, etc."
                  />
                </div>
              )}
              <div className="field">
                <label className="font-bold">Value</label>
                <InputText
                  value={actionDraft.value ?? ''}
                  onChange={(e) => setActionDraft((d) => ({ ...d, value: e.target.value }))}
                  placeholder="Replacement or new value"
                />
              </div>
            </>
          )}
        </div>
      </Dialog>
    </>
  );
}

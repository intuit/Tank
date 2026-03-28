/**
 * Reusable editable key/value table for request headers, post data, etc.
 */
import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import type { StepData } from '../../../types/scriptEditor';

interface Props {
  items: StepData[];
  onChange: (items: StepData[]) => void;
  defaultType?: string;
  defaultPhase?: string;
  keyHeader?: string;
  valueHeader?: string;
}

export function StepDataTable({
  items,
  onChange,
  defaultType = '',
  defaultPhase = 'POST_REQUEST',
  keyHeader = 'Name',
  valueHeader = 'Value',
}: Props) {
  const [newKey, setNewKey] = useState('');
  const [newValue, setNewValue] = useState('');

  const add = () => {
    if (!newKey.trim()) return;
    onChange([...items, { key: newKey.trim(), value: newValue, type: defaultType, phase: defaultPhase }]);
    setNewKey('');
    setNewValue('');
  };

  const remove = (index: number) => {
    onChange(items.filter((_, i) => i !== index));
  };

  const updateField = (index: number, field: 'key' | 'value', val: string) => {
    onChange(items.map((item, i) => i === index ? { ...item, [field]: val } : item));
  };

  const keyEditor = (item: StepData, index: number) => (
    <InputText
      value={item.key}
      onChange={e => updateField(index, 'key', e.target.value)}
      className="w-full p-inputtext-sm"
    />
  );

  const valueEditor = (item: StepData, index: number) => (
    <InputText
      value={item.value}
      onChange={e => updateField(index, 'value', e.target.value)}
      className="w-full p-inputtext-sm"
    />
  );

  const actionBody = (_: StepData, opts: { rowIndex: number }) => (
    <Button
      icon="pi pi-trash"
      size="small"
      text
      severity="danger"
      onClick={() => remove(opts.rowIndex)}
    />
  );

  return (
    <div className="flex flex-column gap-2">
      <DataTable value={items} size="small" emptyMessage="None" showGridlines>
        <Column
          header={keyHeader}
          body={(row: StepData, opts) => keyEditor(row, opts.rowIndex)}
          style={{ width: '40%' }}
        />
        <Column
          header={valueHeader}
          body={(row: StepData, opts) => valueEditor(row, opts.rowIndex)}
        />
        <Column body={actionBody} style={{ width: '48px' }} />
      </DataTable>
      <div className="flex gap-2">
        <InputText
          placeholder={keyHeader}
          value={newKey}
          onChange={e => setNewKey(e.target.value)}
          className="p-inputtext-sm flex-1"
          onKeyDown={e => { if (e.key === 'Enter') add(); }}
        />
        <InputText
          placeholder={valueHeader}
          value={newValue}
          onChange={e => setNewValue(e.target.value)}
          className="p-inputtext-sm flex-1"
          onKeyDown={e => { if (e.key === 'Enter') add(); }}
        />
        <Button icon="pi pi-plus" size="small" onClick={add} disabled={!newKey.trim()} />
      </div>
    </div>
  );
}

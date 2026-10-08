import { Button } from 'primereact/button';
import { InputSwitch } from 'primereact/inputswitch';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useEffect, useState } from 'react';
import type { Update } from './useProjectDraft';
import type { ProjectDetail } from './validation';

interface Row {
  key: string;
  value: string;
}

/**
 * Project variables (ProjectVariableEditor). Rows are edited as a list so a key can be retyped
 * without losing its place, and written back to the map as they change. Blank keys stay in the list
 * until they're named; duplicate keys keep the last value, so they're flagged.
 */
export function VariablesTab({ detail, update, readOnly }: { detail: ProjectDetail; update: Update; readOnly: boolean }) {
  const [rows, setRows] = useState<Row[]>(() => toRows(detail.variables));

  // Follow outside changes (revert, reload, save) without fighting the edits made here
  useEffect(() => {
    setRows((current) => (sameVariables(fromRows(current), detail.variables ?? {}) ? current : toRows(detail.variables)));
  }, [detail.variables]);

  const change = (next: Row[]) => {
    setRows(next);
    update((d) => void (d.variables = fromRows(next)));
  };
  const keys = rows.map((r) => r.key.trim()).filter(Boolean);
  const duplicates = [...new Set(keys.filter((k, i) => keys.indexOf(k) !== i))];
  const unnamed = rows.some((r) => !r.key.trim() && r.value);

  return (
    <div>
      <div className="field-inline">
        <InputSwitch
          inputId="allow-override"
          checked={!!detail.settings?.allowOverride}
          onChange={(e) => update((d) => void (d.settings = { ...d.settings, allowOverride: !!e.value }))}
          disabled={readOnly}
        />
        <label htmlFor="allow-override">Let scripts override project variables</label>
      </div>
      {duplicates.length > 0 && (
        <Message severity="warn" text={`Each name can be used once: ${duplicates.join(', ')}`} className="tab-message" />
      )}
      {unnamed && <Message severity="warn" text="Variables without a name aren't saved" className="tab-message" />}
      <table className="kv-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Value</th>
            <th aria-label="Actions" />
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index}>
              <td>
                <InputText
                  value={row.key}
                  onChange={(e) => change(rows.map((r, i) => (i === index ? { ...r, key: e.target.value } : r)))}
                  aria-label={`Variable ${index + 1} name`}
                  disabled={readOnly}
                />
              </td>
              <td>
                <InputText
                  value={row.value}
                  onChange={(e) => change(rows.map((r, i) => (i === index ? { ...r, value: e.target.value } : r)))}
                  aria-label={`Variable ${index + 1} value`}
                  disabled={readOnly}
                />
              </td>
              <td>
                {!readOnly && (
                  <Button
                    icon="pi pi-trash"
                    rounded
                    text
                    severity="danger"
                    aria-label={`Delete variable ${row.key || index + 1}`}
                    onClick={() => change(rows.filter((_, i) => i !== index))}
                  />
                )}
              </td>
            </tr>
          ))}
          {rows.length === 0 && (
            <tr>
              <td colSpan={3} className="field-help">
                No variables.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && (
        <Button
          label="Add variable"
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => setRows([...rows, { key: '', value: '' }])}
        />
      )}
    </div>
  );
}

function toRows(variables: Record<string, string> | undefined): Row[] {
  return Object.entries(variables ?? {}).map(([key, value]) => ({ key, value }));
}

function fromRows(rows: Row[]): Record<string, string> {
  const map: Record<string, string> = {};
  for (const row of rows) {
    if (row.key.trim()) {
      map[row.key.trim()] = row.value;
    }
  }
  return map;
}

function sameVariables(a: Record<string, string>, b: Record<string, string>): boolean {
  const keys = Object.keys(a);
  return keys.length === Object.keys(b).length && keys.every((k) => a[k] === b[k]);
}

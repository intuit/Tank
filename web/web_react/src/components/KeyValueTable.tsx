import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import type { Schemas } from '../api/client';

type Entry = Schemas['StepDataTO'];

/**
 * An editable list of key/value entries, such as a request's headers. Entries keep any other fields
 * they came with (type, phase); new ones get `newType`.
 */
export function KeyValueTable({
  entries,
  onChange,
  newType,
  keyLabel,
  valueLabel,
  addLabel,
  readOnly,
  emptyMessage,
}: {
  entries: Entry[];
  onChange: (entries: Entry[]) => void;
  newType?: string;
  keyLabel: string;
  valueLabel: string;
  addLabel?: string;
  readOnly: boolean;
  emptyMessage: string;
}) {
  const set = (index: number, change: Partial<Entry>) =>
    onChange(entries.map((e, i) => (i === index ? { ...e, ...change } : e)));
  return (
    <div>
      <table className="kv-table kv-table-wide">
        <thead>
          <tr>
            <th>{keyLabel}</th>
            <th>{valueLabel}</th>
            {!readOnly && <th aria-label="Actions" />}
          </tr>
        </thead>
        <tbody>
          {entries.map((entry, index) => (
            <tr key={index}>
              <td>
                {readOnly ? (
                  <span className="kv-readonly">{entry.key}</span>
                ) : (
                  <InputText
                    value={entry.key ?? ''}
                    onChange={(e) => set(index, { key: e.target.value })}
                    aria-label={`${keyLabel} ${index + 1}`}
                  />
                )}
              </td>
              <td>
                {readOnly ? (
                  <span className="kv-readonly">{entry.value}</span>
                ) : (
                  <InputText
                    value={entry.value ?? ''}
                    onChange={(e) => set(index, { value: e.target.value })}
                    aria-label={`${valueLabel} ${index + 1}`}
                  />
                )}
              </td>
              {!readOnly && (
                <td>
                  <Button
                    type="button"
                    icon="pi pi-times"
                    rounded
                    text
                    severity="danger"
                    aria-label={`Remove ${entry.key || `${keyLabel.toLowerCase()} ${index + 1}`}`}
                    onClick={() => onChange(entries.filter((_, i) => i !== index))}
                  />
                </td>
              )}
            </tr>
          ))}
          {entries.length === 0 && (
            <tr>
              <td colSpan={3} className="field-help">
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && addLabel && (
        <Button
          type="button"
          label={addLabel}
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => onChange([...entries, { key: '', value: '', ...(newType ? { type: newType } : {}) }])}
        />
      )}
    </div>
  );
}

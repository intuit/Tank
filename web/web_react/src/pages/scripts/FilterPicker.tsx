import { Checkbox } from 'primereact/checkbox';
import { Message } from 'primereact/message';
import { useState } from 'react';
import { useFilters } from '../filters/useFilters';

/**
 * Picks script filters, directly or by filter group (as ScriptCreationBean does: a group selects or
 * clears its filters). Reports the chosen ids in list order, the order the server applies them in.
 */
export function FilterPicker({ value, onChange }: { value: number[]; onChange: (filterIds: number[]) => void }) {
  const [groupIds, setGroupIds] = useState<number[]>([]);
  const filters = useFilters();

  const ordered = (ids: number[]) => (filters.data?.filters ?? []).map((f) => f.id!).filter((id) => ids.includes(id));
  const toggleGroup = (id: number, members: number[], on: boolean) => {
    setGroupIds((current) => (on ? [...current, id] : current.filter((g) => g !== id)));
    onChange(ordered(on ? [...value, ...members] : value.filter((f) => !members.includes(f))));
  };

  if (filters.error) return <Message severity="error" text={filters.error.message} />;
  if (!filters.data) return <span className="field-help">Loading filters…</span>;
  return (
    <div className="filter-picker">
      <fieldset>
        <legend>Filter groups</legend>
        {filters.data.groups.length === 0 && <span className="field-help">No filter groups</span>}
        {filters.data.groups.map((g) => (
          <div key={g.id} className="field-inline">
            <Checkbox
              inputId={`group-${g.id}`}
              checked={groupIds.includes(g.id!)}
              onChange={(e) => toggleGroup(g.id!, g.filterIds ?? [], !!e.checked)}
            />
            <label htmlFor={`group-${g.id}`}>
              {g.name}
              {g.productName && <span className="field-help"> · {g.productName}</span>}
            </label>
          </div>
        ))}
      </fieldset>
      <fieldset>
        <legend>Filters, applied in this order</legend>
        {filters.data.filters.length === 0 && <span className="field-help">No filters</span>}
        {filters.data.filters.map((f) => (
          <div key={f.id} className="field-inline">
            <Checkbox
              inputId={`filter-${f.id}`}
              checked={value.includes(f.id!)}
              onChange={(e) => onChange(ordered(e.checked ? [...value, f.id!] : value.filter((x) => x !== f.id)))}
            />
            <label htmlFor={`filter-${f.id}`}>{f.name}</label>
          </div>
        ))}
      </fieldset>
    </div>
  );
}

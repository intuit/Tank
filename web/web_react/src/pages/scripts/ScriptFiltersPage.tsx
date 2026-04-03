import { useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { useScript } from '../../hooks/useScripts';
import { useFilters, useFilterGroups } from '../../hooks/useFilters';
import { scriptsApi } from '../../api/scripts';
import type { FilterTO, FilterGroupTO } from '../../types/filter';

export function ScriptFiltersPage() {
  const { id } = useParams<{ id: string }>();
  const scriptId = Number(id);
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  const { data: script, isLoading: loadingScript, error: scriptError } = useScript(scriptId);
  const { data: groups, isLoading: loadingGroups } = useFilterGroups();
  const { data: allFilters, isLoading: loadingFilters } = useFilters();

  const [selectedGroups, setSelectedGroups] = useState<FilterGroupTO[]>([]);
  const [selectedFilters, setSelectedFilters] = useState<FilterTO[]>([]);
  const [applying, setApplying] = useState(false);

  if (loadingScript || loadingGroups || loadingFilters) return <ProgressSpinner />;
  if (scriptError || !script) return <Message severity="error" text="Script not found." />;

  // Filters that belong to any selected group
  const groupFilterIds = new Set(
    selectedGroups.flatMap((g) => (g.filters ?? []).map((f) => f.id))
  );

  // Right panel: filters in selected groups, or all filters if no group selected
  const visibleFilters =
    selectedGroups.length > 0
      ? (allFilters ?? []).filter((f) => groupFilterIds.has(f.id))
      : (allFilters ?? []);

  const handleApply = async () => {
    const groupIds = selectedGroups.map((g) => g.id);
    const filterIds = selectedFilters.map((f) => f.id);
    if (groupIds.length === 0 && filterIds.length === 0) {
      toast.current?.show({ severity: 'warn', summary: 'Select at least one filter or group' });
      return;
    }
    setApplying(true);
    try {
      await scriptsApi.applyFilters(scriptId, filterIds, groupIds);
      toast.current?.show({ severity: 'success', summary: 'Filters applied successfully' });
      navigate(`/scripts/${scriptId}`);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to apply filters' });
    } finally {
      setApplying(false);
    }
  };

  return (
    <>
      <Toast ref={toast} />

      {/* Toolbar */}
      <div className="flex align-items-center justify-content-between mb-3">
        <div className="flex align-items-center gap-2">
          <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate(`/scripts/${scriptId}`)} />
          <span className="text-xl font-bold">Apply Filters — {script.name}</span>
        </div>
        <div className="flex gap-2">
          <Button label="Cancel" severity="secondary" icon="pi pi-times" onClick={() => navigate(`/scripts/${scriptId}`)} />
          <Button
            label="Apply Filters"
            icon="pi pi-check"
            loading={applying}
            onClick={handleApply}
            disabled={selectedGroups.length === 0 && selectedFilters.length === 0}
          />
        </div>
      </div>

      {/* Side-by-side panels */}
      <div className="grid" style={{ height: 'calc(100vh - 120px)' }}>
        {/* Left: Filter Groups */}
        <div className="col-6 flex flex-column" style={{ height: '100%' }}>
          <div className="font-bold mb-2">
            Filter Groups
            {selectedGroups.length > 0 && (
              <span className="ml-2 text-sm text-color-secondary">({selectedGroups.length} selected)</span>
            )}
          </div>
          <DataTable
            value={groups ?? []}
            selection={selectedGroups}
            onSelectionChange={(e) => {
              setSelectedGroups(e.value as FilterGroupTO[]);
              setSelectedFilters([]);
            }}
            selectionMode="multiple"
            dataKey="id"
            showGridlines
            stripedRows
            scrollable
            scrollHeight="flex"
            style={{ flex: 1 }}
            emptyMessage="No filter groups available."
          >
            <Column selectionMode="multiple" style={{ width: '40px' }} />
            <Column field="id" header="ID" style={{ width: '60px' }} sortable />
            <Column field="name" header="Name" sortable />
            <Column field="productName" header="Product" sortable />
          </DataTable>
        </div>

        {/* Right: Filters */}
        <div className="col-6 flex flex-column" style={{ height: '100%' }}>
          <div className="font-bold mb-2">
            Filters
            {selectedGroups.length > 0
              ? <span className="ml-2 text-sm text-color-secondary">(from selected groups)</span>
              : <span className="ml-2 text-sm text-color-secondary">(all filters)</span>}
            {selectedFilters.length > 0 && (
              <span className="ml-2 text-sm text-color-secondary">— {selectedFilters.length} selected</span>
            )}
          </div>
          <DataTable
            value={visibleFilters}
            selection={selectedFilters}
            onSelectionChange={(e) => setSelectedFilters(e.value as FilterTO[])}
            selectionMode="multiple"
            dataKey="id"
            showGridlines
            stripedRows
            scrollable
            scrollHeight="flex"
            style={{ flex: 1 }}
            emptyMessage={
              selectedGroups.length > 0
                ? 'No filters in selected groups.'
                : 'No filters available.'
            }
          >
            <Column selectionMode="multiple" style={{ width: '40px' }} />
            <Column field="id" header="ID" style={{ width: '60px' }} sortable />
            <Column field="name" header="Name" sortable />
            <Column field="productName" header="Product" sortable />
          </DataTable>
        </div>
      </div>
    </>
  );
}

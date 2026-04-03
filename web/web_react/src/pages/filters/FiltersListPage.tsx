import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '../../utils/formatDate';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Dialog } from 'primereact/dialog';
import { TabView, TabPanel } from 'primereact/tabview';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useFilters, useFilterGroups, useDeleteFilter, useDeleteFilterGroup, useCreateFilterGroup } from '../../hooks/useFilters';
import { scriptsApi } from '../../api/scripts';
import type { FilterTO, FilterGroupTO } from '../../types/filter';

export function FiltersListPage() {
  const navigate = useNavigate();
  const { data: filters, isLoading: loadingFilters, error: filterError } = useFilters();
  const { data: groups, isLoading: loadingGroups } = useFilterGroups();
  const deleteFilter = useDeleteFilter();
  const deleteFilterGroup = useDeleteFilterGroup();
  const createFilterGroup = useCreateFilterGroup();
  const [filterSearch, setFilterSearch] = useState('');
  const [groupSearch, setGroupSearch] = useState('');
  const toast = useRef<Toast>(null);

  // New Group dialog state
  const [showNewGroup, setShowNewGroup] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupProduct, setNewGroupProduct] = useState('');

  // Apply Filters dialog state
  const [showApplyFilters, setShowApplyFilters] = useState(false);
  const [applyScriptId, setApplyScriptId] = useState<number | null>(null);
  const [selectedFilterIds, setSelectedFilterIds] = useState<number[]>([]);

  if (loadingFilters || loadingGroups) return <ProgressSpinner />;
  if (filterError) return <Message severity="error" text="Failed to load filters." />;

  const handleDeleteFilter = (row: FilterTO) => {
    confirmDialog({
      message: `Delete filter "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteFilter.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: 'Filter deleted' });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete filter' });
        }
      },
    });
  };

  const handleDeleteGroup = (row: FilterGroupTO) => {
    confirmDialog({
      message: `Delete filter group "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteFilterGroup.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: 'Filter group deleted' });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete filter group' });
        }
      },
    });
  };

  const handleCreateGroup = async () => {
    if (!newGroupName.trim()) return;
    try {
      await createFilterGroup.mutateAsync({
        name: newGroupName.trim(),
        productName: newGroupProduct.trim() || undefined,
      });
      toast.current?.show({ severity: 'success', summary: 'Filter group created' });
      setShowNewGroup(false);
      setNewGroupName('');
      setNewGroupProduct('');
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to create filter group' });
    }
  };

  const handleApplyFilters = async () => {
    if (!applyScriptId || selectedFilterIds.length === 0) return;
    try {
      await scriptsApi.applyFilters(applyScriptId, selectedFilterIds);
      toast.current?.show({ severity: 'success', summary: 'Filters applied to script' });
      setShowApplyFilters(false);
      setApplyScriptId(null);
      setSelectedFilterIds([]);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to apply filters' });
    }
  };

  const filterActionsBody = (row: FilterTO) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-pencil"
        size="small"
        text
        tooltip="Edit"
        onClick={() => navigate(`/filters/${row.id}/edit`)}
      />
      <Button
        icon="pi pi-trash"
        size="small"
        text
        severity="danger"
        tooltip="Delete"
        onClick={() => handleDeleteFilter(row)}
      />
    </div>
  );

  const groupActionsBody = (row: FilterGroupTO) => (
    <Button
      icon="pi pi-trash"
      size="small"
      text
      severity="danger"
      tooltip="Delete"
      onClick={() => handleDeleteGroup(row)}
    />
  );

  const filtersToolbar = (
    <>
      <span className="font-bold text-xl">Filters</span>
      <InputText
        placeholder="Search…"
        value={filterSearch}
        onChange={(e) => setFilterSearch(e.target.value)}
        className="p-inputtext-sm ml-3"
      />
      <Button
        label="New Filter"
        icon="pi pi-plus"
        size="small"
        className="ml-3"
        onClick={() => navigate('/filters/new')}
      />
      <Button
        label="Apply Filters to Script"
        icon="pi pi-filter"
        size="small"
        className="ml-3"
        onClick={() => setShowApplyFilters(true)}
      />
    </>
  );

  const groupsToolbar = (
    <>
      <span className="font-bold text-xl">Filter Groups</span>
      <InputText
        placeholder="Search…"
        value={groupSearch}
        onChange={(e) => setGroupSearch(e.target.value)}
        className="p-inputtext-sm ml-3"
      />
      <Button
        label="New Group"
        icon="pi pi-plus"
        size="small"
        className="ml-3"
        onClick={() => setShowNewGroup(true)}
      />
    </>
  );

  return (
    <>
      <Toast ref={toast} />
      <ConfirmDialog />

      {/* New Filter Group Dialog */}
      <Dialog
        header="New Filter Group"
        visible={showNewGroup}
        style={{ width: '400px' }}
        onHide={() => { setShowNewGroup(false); setNewGroupName(''); setNewGroupProduct(''); }}
        footer={
          <div className="flex justify-content-end gap-2">
            <Button label="Cancel" severity="secondary" onClick={() => { setShowNewGroup(false); setNewGroupName(''); setNewGroupProduct(''); }} />
            <Button label="Create" icon="pi pi-check" onClick={handleCreateGroup} disabled={!newGroupName.trim()} loading={createFilterGroup.isPending} />
          </div>
        }
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="newGroupName" className="font-bold">Name *</label>
            <InputText
              id="newGroupName"
              value={newGroupName}
              onChange={(e) => setNewGroupName(e.target.value)}
              placeholder="Filter group name"
              autoFocus
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="newGroupProduct" className="font-bold">Product</label>
            <InputText
              id="newGroupProduct"
              value={newGroupProduct}
              onChange={(e) => setNewGroupProduct(e.target.value)}
              placeholder="Product name (optional)"
            />
          </div>
        </div>
      </Dialog>

      {/* Apply Filters Dialog */}
      <Dialog
        header="Apply Filters to Script"
        visible={showApplyFilters}
        style={{ width: '500px' }}
        onHide={() => { setShowApplyFilters(false); setApplyScriptId(null); setSelectedFilterIds([]); }}
        footer={
          <div className="flex justify-content-end gap-2">
            <Button label="Cancel" severity="secondary" onClick={() => { setShowApplyFilters(false); setApplyScriptId(null); setSelectedFilterIds([]); }} />
            <Button
              label="Apply"
              icon="pi pi-check"
              onClick={handleApplyFilters}
              disabled={!applyScriptId || selectedFilterIds.length === 0}
            />
          </div>
        }
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="applyScriptId" className="font-bold">Script ID</label>
            <InputNumber
              id="applyScriptId"
              value={applyScriptId}
              onValueChange={(e) => setApplyScriptId(e.value ?? null)}
              placeholder="Enter script ID"
              min={1}
            />
          </div>
          <div className="flex flex-column gap-1">
            <span className="font-bold">Filters to apply</span>
            <DataTable
              value={filters}
              selection={filters?.filter((f) => selectedFilterIds.includes(f.id)) ?? []}
              onSelectionChange={(e) => setSelectedFilterIds((e.value as FilterTO[]).map((f) => f.id))}
              selectionMode="multiple"
              dataKey="id"
              scrollable
              scrollHeight="250px"
              showGridlines
              stripedRows
              emptyMessage="No filters available."
            >
              <Column selectionMode="multiple" style={{ width: '40px' }} />
              <Column field="name" header="Name" />
              <Column field="productName" header="Product" />
            </DataTable>
          </div>
        </div>
      </Dialog>

      <TabView>
        <TabPanel header="Filters">
          <Toolbar start={filtersToolbar} className="mb-3 ui-tank-theme" />
          <DataTable
            value={filters}
            dataKey="id"
            showGridlines
            stripedRows
            paginator
            rows={25}
            globalFilter={filterSearch}
            globalFilterFields={['name', 'productName', 'creator']}
            emptyMessage="No filters found."
            sortField="name"
            sortOrder={1}
          >
            <Column field="name" header="Name" sortable />
            <Column field="productName" header="Product" sortable />
            <Column field="creator" header="Owner" sortable />
            <Column field="modified" header="Modified" sortable body={(row) => formatDate(row.modified)} />
            <Column header="" body={filterActionsBody} style={{ width: '100px' }} />
          </DataTable>
        </TabPanel>
        <TabPanel header="Filter Groups">
          <Toolbar start={groupsToolbar} className="mb-3 ui-tank-theme" />
          <DataTable
            value={groups}
            dataKey="id"
            showGridlines
            stripedRows
            paginator
            rows={25}
            globalFilter={groupSearch}
            globalFilterFields={['name', 'productName', 'creator']}
            emptyMessage="No filter groups found."
            sortField="name"
            sortOrder={1}
          >
            <Column field="name" header="Name" sortable />
            <Column field="productName" header="Product" sortable />
            <Column field="creator" header="Owner" sortable />
            <Column field="modified" header="Modified" sortable body={(row) => formatDate(row.modified)} />
            <Column header="" body={groupActionsBody} style={{ width: '60px' }} />
          </DataTable>
        </TabPanel>
      </TabView>
    </>
  );
}

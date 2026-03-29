import { useState, useRef } from 'react';
import { formatDate } from '../../utils/formatDate';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { TabView, TabPanel } from 'primereact/tabview';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useFilters, useFilterGroups, useDeleteFilter, useDeleteFilterGroup } from '../../hooks/useFilters';
import type { FilterTO, FilterGroupTO } from '../../types/filter';

export function FiltersListPage() {
  const { data: filters, isLoading: loadingFilters, error: filterError } = useFilters();
  const { data: groups, isLoading: loadingGroups } = useFilterGroups();
  const deleteFilter = useDeleteFilter();
  const deleteFilterGroup = useDeleteFilterGroup();
  const [filterSearch, setFilterSearch] = useState('');
  const [groupSearch, setGroupSearch] = useState('');
  const toast = useRef<Toast>(null);

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

  const filterActionsBody = (row: FilterTO) => (
    <Button
      icon="pi pi-trash"
      size="small"
      text
      severity="danger"
      tooltip="Delete"
      onClick={() => handleDeleteFilter(row)}
    />
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
    </>
  );

  return (
    <>
      <Toast ref={toast} />
      <ConfirmDialog />
      <TabView>
        <TabPanel header="Filters">
          <Toolbar start={filtersToolbar} className="mb-3 ui-tank-theme" />
          <DataTable
            value={filters}
            dataKey="id"
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
            <Column header="" body={filterActionsBody} style={{ width: '60px' }} />
          </DataTable>
        </TabPanel>
        <TabPanel header="Filter Groups">
          <Toolbar start={groupsToolbar} className="mb-3 ui-tank-theme" />
          <DataTable
            value={groups}
            dataKey="id"
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

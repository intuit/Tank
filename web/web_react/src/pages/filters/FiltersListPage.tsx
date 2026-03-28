import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { TabView, TabPanel } from 'primereact/tabview';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useFilters, useFilterGroups } from '../../hooks/useFilters';

export function FiltersListPage() {
  const { data: filters, isLoading: loadingFilters, error: filterError } = useFilters();
  const { data: groups, isLoading: loadingGroups } = useFilterGroups();
  const [filterSearch, setFilterSearch] = useState('');
  const [groupSearch, setGroupSearch] = useState('');

  if (loadingFilters || loadingGroups) return <ProgressSpinner />;
  if (filterError) return <Message severity="error" text="Failed to load filters." />;

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
    <TabView>
      <TabPanel header="Filters">
        <Toolbar start={filtersToolbar} className="mb-3" />
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
          <Column field="modified" header="Modified" sortable />
        </DataTable>
      </TabPanel>
      <TabPanel header="Filter Groups">
        <Toolbar start={groupsToolbar} className="mb-3" />
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
          <Column field="modified" header="Modified" sortable />
        </DataTable>
      </TabPanel>
    </TabView>
  );
}

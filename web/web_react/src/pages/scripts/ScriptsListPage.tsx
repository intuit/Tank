import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useScripts } from '../../hooks/useScripts';
import type { ScriptDescription } from '../../types/script';

export function ScriptsListPage() {
  const { data: scripts, isLoading, error } = useScripts();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load scripts." />;

  const nameBody = (row: ScriptDescription) => (
    <Button label={row.name} link onClick={() => navigate(`/scripts/${row.id}`)} />
  );

  const leftToolbar = <span className="font-bold text-xl">Scripts</span>;
  const rightToolbar = (
    <InputText
      placeholder="Search…"
      value={globalFilter}
      onChange={(e) => setGlobalFilter(e.target.value)}
      className="p-inputtext-sm"
    />
  );

  return (
    <div>
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3" />
      <DataTable
        value={scripts}
        dataKey="id"
        paginator
        rows={25}
        globalFilter={globalFilter}
        globalFilterFields={['name', 'productName', 'creator']}
        emptyMessage="No scripts found."
        sortField="name"
        sortOrder={1}
      >
        <Column field="name" header="Name" sortable body={nameBody} />
        <Column field="productName" header="Product" sortable />
        <Column field="runtime" header="Runtime (ms)" sortable />
        <Column field="creator" header="Owner" sortable />
        <Column field="modified" header="Modified" sortable />
      </DataTable>
    </div>
  );
}

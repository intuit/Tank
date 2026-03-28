import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useProjects } from '../../hooks/useProjects';
import type { ProjectTO } from '../../types/project';

export function ProjectsListPage() {
  const { data: projects, isLoading, error } = useProjects();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load projects." />;

  const leftToolbar = (
    <div className="flex gap-2">
      <span className="font-bold text-xl">Projects</span>
    </div>
  );

  const rightToolbar = (
    <div className="flex gap-2 align-items-center">
      <InputText
        placeholder="Search…"
        value={globalFilter}
        onChange={(e) => setGlobalFilter(e.target.value)}
        className="p-inputtext-sm"
      />
    </div>
  );

  const nameBody = (row: ProjectTO) => (
    <Button
      label={row.name}
      link
      onClick={() => navigate(`/projects/${row.id}`)}
    />
  );

  return (
    <div>
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3" />
      <DataTable
        value={projects}
        dataKey="id"
        paginator
        rows={20}
        globalFilter={globalFilter}
        globalFilterFields={['name', 'productName', 'creator']}
        emptyMessage="No projects found."
        sortField="name"
        sortOrder={1}
      >
        <Column field="name" header="Name" sortable body={nameBody} />
        <Column field="productName" header="Product" sortable />
        <Column field="creator" header="Owner" sortable />
        <Column field="modified" header="Modified" sortable />
      </DataTable>
    </div>
  );
}

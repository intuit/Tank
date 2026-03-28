import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useDataFiles } from '../../hooks/useDataFiles';
import { datafilesApi } from '../../api/datafiles';
import type { DataFileDescriptor } from '../../types/datafile';

export function DataFilesListPage() {
  const { data: files, isLoading, error } = useDataFiles();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load data files." />;

  const nameBody = (row: DataFileDescriptor) => (
    <Button label={row.name} link onClick={() => navigate(`/datafiles/${row.id}`)} />
  );

  const downloadBody = (row: DataFileDescriptor) => (
    <Button
      icon="pi pi-download"
      size="small"
      text
      severity="secondary"
      tooltip="Download"
      onClick={async () => {
        const res = await datafilesApi.download(row.id);
        const url = URL.createObjectURL(new Blob([res.data as BlobPart]));
        const a = document.createElement('a');
        a.href = url;
        a.download = row.name;
        a.click();
        URL.revokeObjectURL(url);
      }}
    />
  );

  const leftToolbar = <span className="font-bold text-xl">Data Files</span>;
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
        value={files}
        dataKey="id"
        paginator
        rows={25}
        globalFilter={globalFilter}
        globalFilterFields={['name', 'creator']}
        emptyMessage="No data files found."
        sortField="name"
        sortOrder={1}
      >
        <Column field="name" header="Name" sortable body={nameBody} />
        <Column field="path" header="Path" sortable />
        <Column field="creator" header="Owner" sortable />
        <Column field="modified" header="Modified" sortable />
        <Column header="" body={downloadBody} style={{ width: '60px' }} />
      </DataTable>
    </div>
  );
}

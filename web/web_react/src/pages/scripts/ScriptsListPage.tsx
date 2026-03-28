import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { FileUpload } from 'primereact/fileupload';
import { useScripts, useUploadScript, useDeleteScript } from '../../hooks/useScripts';
import { scriptsApi } from '../../api/scripts';
import type { ScriptDescription } from '../../types/script';

export function ScriptsListPage() {
  const { data: scripts, isLoading, error } = useScripts();
  const uploadScript = useUploadScript();
  const deleteScript = useDeleteScript();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const fileUploadRef = useRef<FileUpload>(null);

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load scripts." />;

  const handleUpload = async (event: { files: File[] }) => {
    const file = event.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    try {
      await uploadScript.mutateAsync(formData);
      toast.current?.show({ severity: 'success', summary: `Uploaded "${file.name}"` });
      fileUploadRef.current?.clear();
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Upload failed' });
    }
  };

  const handleDelete = (row: ScriptDescription) => {
    confirmDialog({
      message: `Delete script "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteScript.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: 'Script deleted' });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete script' });
        }
      },
    });
  };

  const nameBody = (row: ScriptDescription) => (
    <Button label={row.name} link onClick={() => navigate(`/scripts/${row.id}`)} />
  );

  const actionsBody = (row: ScriptDescription) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-download"
        size="small"
        text
        severity="secondary"
        tooltip="Download"
        onClick={async () => {
          const res = await scriptsApi.download(row.id);
          const url = URL.createObjectURL(new Blob([res.data as BlobPart]));
          const a = document.createElement('a');
          a.href = url;
          a.download = `${row.name}.xml`;
          a.click();
          URL.revokeObjectURL(url);
        }}
      />
      <Button
        icon="pi pi-trash"
        size="small"
        text
        severity="danger"
        tooltip="Delete"
        onClick={() => handleDelete(row)}
      />
    </div>
  );

  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <span className="font-bold text-xl">Scripts</span>
      <FileUpload
        ref={fileUploadRef}
        mode="basic"
        auto
        chooseLabel="Upload"
        customUpload
        uploadHandler={handleUpload}
        className="p-button-sm"
        accept=".xml"
      />
    </div>
  );

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
      <Toast ref={toast} />
      <ConfirmDialog />
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
        <Column header="" body={actionsBody} style={{ width: '90px' }} />
      </DataTable>
    </div>
  );
}

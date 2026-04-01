import { useState, useRef } from 'react';
import { formatDate } from '../../utils/formatDate';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { FileUpload } from 'primereact/fileupload';
import { Dialog } from 'primereact/dialog';
import { TabView, TabPanel } from 'primereact/tabview';
import { useScripts, useUploadScript, useDeleteScript, useExternalScripts, useCreateExternalScript, useDeleteExternalScript } from '../../hooks/useScripts';
import { scriptsApi } from '../../api/scripts';
import type { ScriptDescription, ExternalScriptTO } from '../../types/script';

export function ScriptsListPage() {
  const { data: scripts, isLoading, error } = useScripts();
  const uploadScript = useUploadScript();
  const deleteScript = useDeleteScript();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const fileUploadRef = useRef<FileUpload>(null);

  // External scripts state
  const { data: externalScripts, isLoading: extLoading, error: extError } = useExternalScripts();
  const createExternal = useCreateExternalScript();
  const deleteExternal = useDeleteExternalScript();
  const [extGlobalFilter, setExtGlobalFilter] = useState('');
  const [newExtDialogVisible, setNewExtDialogVisible] = useState(false);
  const [newExtName, setNewExtName] = useState('');
  const [newExtProduct, setNewExtProduct] = useState('');
  const [newExtScript, setNewExtScript] = useState('');

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

  const handleDeleteExternal = (row: ExternalScriptTO) => {
    confirmDialog({
      message: `Delete external script "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteExternal.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: 'External script deleted' });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete external script' });
        }
      },
    });
  };

  const handleCreateExternal = async () => {
    if (!newExtName.trim()) {
      toast.current?.show({ severity: 'warn', summary: 'Name is required' });
      return;
    }
    try {
      await createExternal.mutateAsync({
        name: newExtName.trim(),
        productName: newExtProduct.trim() || undefined,
        script: newExtScript.trim() || undefined,
      });
      toast.current?.show({ severity: 'success', summary: `Created "${newExtName.trim()}"` });
      setNewExtDialogVisible(false);
      setNewExtName('');
      setNewExtProduct('');
      setNewExtScript('');
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to create external script' });
    }
  };

  const nameBody = (row: ScriptDescription) => (
    <Button label={row.name} link onClick={() => navigate(`/scripts/${row.id}`)} />
  );

  const actionsBody = (row: ScriptDescription) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-file-edit"
        size="small"
        text
        severity="info"
        tooltip="Edit"
        onClick={() => navigate(`/scripts/${row.id}`)}
      />
      <Button
        icon="pi pi-download"
        size="small"
        text
        severity="secondary"
        tooltip="Download Harness XML"
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

  const extActionsBody = (row: ExternalScriptTO) => (
    <Button
      icon="pi pi-trash"
      size="small"
      text
      severity="danger"
      tooltip="Delete"
      onClick={() => handleDeleteExternal(row)}
    />
  );

  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <span className="font-bold text-xl">Scripts</span>
      <FileUpload
        ref={fileUploadRef}
        mode="basic"
        auto
        chooseLabel="Upload"
        chooseOptions={{ label: 'Upload (XML, HAR, proxy recording)' }}
        customUpload
        uploadHandler={handleUpload}
        className="p-button-sm"
        accept=".xml,.json,.har"
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

  const extLeftToolbar = (
    <div className="flex gap-2 align-items-center">
      <span className="font-bold text-xl">External Scripts</span>
      <Button
        label="New External Script"
        icon="pi pi-plus"
        size="small"
        onClick={() => setNewExtDialogVisible(true)}
      />
    </div>
  );

  const extRightToolbar = (
    <InputText
      placeholder="Search…"
      value={extGlobalFilter}
      onChange={(e) => setExtGlobalFilter(e.target.value)}
      className="p-inputtext-sm"
    />
  );

  const newExtDialogFooter = (
    <div className="flex gap-2 justify-content-end">
      <Button label="Cancel" severity="secondary" outlined onClick={() => setNewExtDialogVisible(false)} />
      <Button label="Create" icon="pi pi-check" onClick={handleCreateExternal} loading={createExternal.isPending} />
    </div>
  );

  return (
    <div>
      <Toast ref={toast} />
      <ConfirmDialog />
      <TabView>
        <TabPanel header="Scripts">
          <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3 ui-tank-theme" />
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
            <Column field="created" header="Created" sortable body={(row) => formatDate(row.created)} />
            <Column field="modified" header="Modified" sortable body={(row) => formatDate(row.modified)} />
            <Column header="" body={actionsBody} style={{ width: '90px' }} />
          </DataTable>
        </TabPanel>
        <TabPanel header="External Scripts">
          {extLoading && <ProgressSpinner />}
          {extError && <Message severity="error" text="Failed to load external scripts." />}
          {!extLoading && !extError && (
            <>
              <Toolbar start={extLeftToolbar} end={extRightToolbar} className="mb-3 ui-tank-theme" />
              <DataTable
                value={externalScripts}
                dataKey="id"
                paginator
                rows={25}
                globalFilter={extGlobalFilter}
                globalFilterFields={['name', 'productName', 'creator']}
                emptyMessage="No external scripts found."
                sortField="name"
                sortOrder={1}
              >
                <Column field="name" header="Name" sortable />
                <Column field="productName" header="Product" sortable />
                <Column field="creator" header="Owner" sortable />
                <Column field="created" header="Created" sortable body={(row) => formatDate(row.created)} />
                <Column field="modified" header="Modified" sortable body={(row) => formatDate(row.modified)} />
                <Column header="Actions" body={extActionsBody} style={{ width: '70px' }} />
              </DataTable>
            </>
          )}
          <Dialog
            header="New External Script"
            visible={newExtDialogVisible}
            style={{ width: '500px' }}
            footer={newExtDialogFooter}
            onHide={() => setNewExtDialogVisible(false)}
          >
            <div className="flex flex-column gap-3">
              <div className="flex flex-column gap-1">
                <label htmlFor="ext-name" className="font-semibold">
                  Name <span className="text-red-500">*</span>
                </label>
                <InputText
                  id="ext-name"
                  value={newExtName}
                  onChange={(e) => setNewExtName(e.target.value)}
                  placeholder="Script name"
                />
              </div>
              <div className="flex flex-column gap-1">
                <label htmlFor="ext-product" className="font-semibold">Product</label>
                <InputText
                  id="ext-product"
                  value={newExtProduct}
                  onChange={(e) => setNewExtProduct(e.target.value)}
                  placeholder="Product name"
                />
              </div>
              <div className="flex flex-column gap-1">
                <label htmlFor="ext-script" className="font-semibold">Script Content</label>
                <InputTextarea
                  id="ext-script"
                  value={newExtScript}
                  onChange={(e) => setNewExtScript(e.target.value)}
                  rows={6}
                  placeholder="Script content or command"
                  autoResize
                />
              </div>
            </div>
          </Dialog>
        </TabPanel>
      </TabView>
    </div>
  );
}

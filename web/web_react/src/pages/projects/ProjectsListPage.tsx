import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { Dialog } from 'primereact/dialog';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useProjects, useCreateProject, useDeleteProject } from '../../hooks/useProjects';
import type { ProjectTO, AutomationRequest } from '../../types/project';

export function ProjectsListPage() {
  const { data: projects, isLoading, error } = useProjects();
  const createProject = useCreateProject();
  const deleteProject = useDeleteProject();
  const [globalFilter, setGlobalFilter] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [newName, setNewName] = useState('');
  const [newProduct, setNewProduct] = useState('');
  const [newComments, setNewComments] = useState('');
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load projects." />;

  const handleCreate = async () => {
    if (!newName.trim()) return;
    const req: AutomationRequest = {
      name: newName.trim(),
      productName: newProduct.trim() || undefined,
      comments: newComments.trim() || undefined,
      location: 'us-east-1',
      stopBehavior: 'END_OF_SCRIPT_GROUP',
    };
    try {
      const res = await createProject.mutateAsync(req);
      toast.current?.show({ severity: 'success', summary: 'Project created' });
      setShowCreate(false);
      setNewName('');
      setNewProduct('');
      setNewComments('');
      const projectId = res.data?.ProjectId;
      if (projectId) navigate(`/projects/${projectId}`);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to create project' });
    }
  };

  const handleDelete = (row: ProjectTO) => {
    confirmDialog({
      message: `Delete project "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteProject.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: 'Project deleted' });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete project' });
        }
      },
    });
  };

  const nameBody = (row: ProjectTO) => (
    <Button
      label={row.name}
      link
      onClick={() => navigate(`/projects/${row.id}`)}
    />
  );

  const actionsBody = (row: ProjectTO) => (
    <Button
      icon="pi pi-trash"
      size="small"
      text
      severity="danger"
      tooltip="Delete"
      onClick={() => handleDelete(row)}
    />
  );

  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <span className="font-bold text-xl">Projects</span>
      <Button
        icon="pi pi-plus"
        label="New"
        size="small"
        onClick={() => setShowCreate(true)}
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

  const createFooter = (
    <div className="flex gap-2 justify-content-end">
      <Button label="Cancel" text onClick={() => setShowCreate(false)} />
      <Button
        label="Create"
        onClick={handleCreate}
        loading={createProject.isPending}
        disabled={!newName.trim()}
      />
    </div>
  );

  return (
    <div>
      <Toast ref={toast} />
      <ConfirmDialog />
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
        <Column header="" body={actionsBody} style={{ width: '60px' }} />
      </DataTable>

      <Dialog
        header="New Project"
        visible={showCreate}
        style={{ width: '400px' }}
        onHide={() => setShowCreate(false)}
        footer={createFooter}
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="proj-name" className="font-semibold">Name *</label>
            <InputText
              id="proj-name"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              autoFocus
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="proj-product" className="font-semibold">Product</label>
            <InputText
              id="proj-product"
              value={newProduct}
              onChange={(e) => setNewProduct(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="proj-comments" className="font-semibold">Comments</label>
            <InputText
              id="proj-comments"
              value={newComments}
              onChange={(e) => setNewComments(e.target.value)}
            />
          </div>
        </div>
      </Dialog>
    </div>
  );
}

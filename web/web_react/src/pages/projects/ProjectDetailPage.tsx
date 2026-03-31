import { useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useProject, useUpdateProject, useDeleteProject } from '../../hooks/useProjects';
import { useJobsByProject } from '../../hooks/useJobs';
import { WorkloadScriptsPanel } from './WorkloadScriptsPanel';
import type { JobTO } from '../../types/job';
import type { AutomationRequest } from '../../types/project';

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const projectId = Number(id);
  const navigate = useNavigate();
  const { data: project, isLoading, error } = useProject(projectId);
  const { data: jobs } = useJobsByProject(projectId);
  const updateProject = useUpdateProject(projectId);
  const deleteProject = useDeleteProject();
  const toast = useRef<Toast>(null);

  const [showEdit, setShowEdit] = useState(false);
  const [editName, setEditName] = useState('');
  const [editProduct, setEditProduct] = useState('');
  const [editComments, setEditComments] = useState('');
  const [editRampTime, setEditRampTime] = useState('');
  const [editSimTime, setEditSimTime] = useState('');

  if (isLoading) return <ProgressSpinner />;
  if (error || !project) return <Message severity="error" text="Project not found." />;

  const openEdit = () => {
    setEditName(project.name ?? '');
    setEditProduct(project.productName ?? '');
    setEditComments(project.comments ?? '');
    setEditRampTime(project.rampTime ?? '');
    setEditSimTime(project.simulationTime != null ? String(project.simulationTime) : '');
    setShowEdit(true);
  };

  const handleSave = async () => {
    const req: AutomationRequest = {
      name: editName.trim(),
      productName: editProduct.trim() || undefined,
      comments: editComments.trim() || undefined,
      rampTime: editRampTime.trim() || undefined,
      simulationTime: editSimTime.trim() || undefined,
      location: 'us-east-1',
      stopBehavior: 'END_OF_SCRIPT_GROUP',
    };
    try {
      await updateProject.mutateAsync(req);
      toast.current?.show({ severity: 'success', summary: 'Project saved' });
      setShowEdit(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save project' });
    }
  };

  const handleDelete = () => {
    confirmDialog({
      message: `Delete project "${project.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteProject.mutateAsync(projectId);
          navigate('/projects');
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete project' });
        }
      },
    });
  };

  const statusBody = (row: JobTO) => {
    const severity =
      row.status === 'Running' ? 'success' :
      row.status === 'Failed' ? 'danger' :
      'info';
    return <span className={`p-tag p-tag-${severity}`}>{row.status ?? '—'}</span>;
  };

  const editFooter = (
    <div className="flex gap-2 justify-content-end">
      <Button label="Cancel" text onClick={() => setShowEdit(false)} />
      <Button
        label="Save"
        onClick={handleSave}
        loading={updateProject.isPending}
        disabled={!editName.trim()}
      />
    </div>
  );

  return (
    <div className="flex flex-column gap-3">
      <Toast ref={toast} />
      <ConfirmDialog />
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/projects')} />
        <h2 className="m-0 flex-1">{project.name}</h2>
        <Button icon="pi pi-pencil" label="Edit" size="small" onClick={openEdit} />
        <Button
          icon="pi pi-trash"
          label="Delete"
          size="small"
          severity="danger"
          text
          onClick={handleDelete}
        />
      </div>

      <div className="grid">
        <div className="col-12 md:col-6">
          <Card title="Details">
            <div className="grid">
              <div className="col-4 font-bold">Product</div>
              <div className="col-8">{project.productName ?? '—'}</div>
              <div className="col-4 font-bold">Owner</div>
              <div className="col-8">{project.creator ?? '—'}</div>
              <div className="col-4 font-bold">Simulation Time</div>
              <div className="col-8">{project.simulationTime != null ? `${project.simulationTime}s` : '—'}</div>
              <div className="col-4 font-bold">Ramp Time</div>
              <div className="col-8">{project.rampTime ?? '—'}</div>
              <div className="col-4 font-bold">Comments</div>
              <div className="col-8">{project.comments ?? '—'}</div>
            </div>
          </Card>
        </div>

        <div className="col-12 md:col-6">
          <Card title="Workload Scripts">
            <WorkloadScriptsPanel projectId={projectId} />
          </Card>
        </div>
      </div>

      <Card title="Job Queue">
        <DataTable
          value={jobs ?? []}
          dataKey="id"
          showGridlines
          stripedRows
          paginator
          rows={10}
          emptyMessage="No jobs."
          sortField="created"
          sortOrder={-1}
        >
          <Column field="id" header="Job ID" sortable style={{ width: '80px' }} />
          <Column field="status" header="Status" body={statusBody} sortable />
          <Column field="totalVirtualUsers" header="Users" sortable />
          <Column field="startTime" header="Start" sortable />
          <Column field="endTime" header="End" sortable />
          <Column field="creator" header="Owner" sortable />
        </DataTable>
      </Card>

      <Dialog
        header="Edit Project"
        visible={showEdit}
        style={{ width: '450px' }}
        onHide={() => setShowEdit(false)}
        footer={editFooter}
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="edit-name" className="font-semibold">Name *</label>
            <InputText
              id="edit-name"
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="edit-product" className="font-semibold">Product</label>
            <InputText
              id="edit-product"
              value={editProduct}
              onChange={(e) => setEditProduct(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="edit-ramp" className="font-semibold">Ramp Time (e.g. 5m, 60s)</label>
            <InputText
              id="edit-ramp"
              value={editRampTime}
              onChange={(e) => setEditRampTime(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="edit-sim" className="font-semibold">Simulation Time (e.g. 30m, 1h)</label>
            <InputText
              id="edit-sim"
              value={editSimTime}
              onChange={(e) => setEditSimTime(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="edit-comments" className="font-semibold">Comments</label>
            <InputTextarea
              id="edit-comments"
              value={editComments}
              onChange={(e) => setEditComments(e.target.value)}
              rows={3}
            />
          </div>
        </div>
      </Dialog>
    </div>
  );
}

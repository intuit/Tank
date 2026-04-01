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
import { InputNumber } from 'primereact/inputnumber';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useProject, useUpdateProject, useDeleteProject } from '../../hooks/useProjects';
import { useJobsByProject, useCreateJob } from '../../hooks/useJobs';
import { useDataFiles } from '../../hooks/useDataFiles';
import { WorkloadScriptsPanel } from './WorkloadScriptsPanel';
import { projectsApi } from '../../api/projects';
import type { JobTO, CreateJobRequest } from '../../types/job';
import type { AutomationRequest, KeyValuePair } from '../../types/project';

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const projectId = Number(id);
  const navigate = useNavigate();
  const { data: project, isLoading, error } = useProject(projectId);
  const { data: jobs } = useJobsByProject(projectId);
  const { data: allDataFiles } = useDataFiles();
  const updateProject = useUpdateProject(projectId);
  const deleteProject = useDeleteProject();
  const createJob = useCreateJob();
  const toast = useRef<Toast>(null);

  // New Job dialog
  const [showNewJob, setShowNewJob] = useState(false);
  const [jobUsers, setJobUsers] = useState<number>(10);
  const [jobRampTime, setJobRampTime] = useState('5m');
  const [jobSimTime, setJobSimTime] = useState('30m');
  const [jobLocation, setJobLocation] = useState('us-east-1');
  const [jobStopBehavior, setJobStopBehavior] = useState('END_OF_SCRIPT_GROUP');

  // Edit project dialog
  const [showEdit, setShowEdit] = useState(false);
  const [editName, setEditName] = useState('');
  const [editProduct, setEditProduct] = useState('');
  const [editComments, setEditComments] = useState('');
  const [editRampTime, setEditRampTime] = useState('');
  const [editSimTime, setEditSimTime] = useState('');

  // Variables editor
  const [variables, setVariables] = useState<KeyValuePair[]>([]);
  const [varsDirty, setVarsDirty] = useState(false);
  const [showAddVar, setShowAddVar] = useState(false);
  const [newVarKey, setNewVarKey] = useState('');
  const [newVarValue, setNewVarValue] = useState('');

  // Data files association
  const [dataFileIds, setDataFileIds] = useState<number[]>([]);
  const [dfDirty, setDfDirty] = useState(false);
  const [showAddDf, setShowAddDf] = useState(false);

  if (isLoading) return <ProgressSpinner />;
  if (error || !project) return <Message severity="error" text="Project not found." />;

  // ── Task 1: Download XML ──────────────────────────────────────────────────
  const handleDownload = async () => {
    const res = await projectsApi.download(projectId);
    const url = URL.createObjectURL(new Blob([res.data as BlobPart]));
    const a = document.createElement('a');
    a.href = url;
    a.download = `${project.name}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  // ── Edit project dialog ───────────────────────────────────────────────────
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

  // ── New Job ───────────────────────────────────────────────────────────────
  const openNewJob = () => {
    setJobUsers(10);
    setJobRampTime(project.rampTime ?? '5m');
    setJobSimTime(project.simulationTime != null ? `${project.simulationTime}s` : '30m');
    setJobLocation(project.location ?? 'us-east-1');
    setJobStopBehavior(project.stopBehavior ?? 'END_OF_SCRIPT_GROUP');
    setShowNewJob(true);
  };

  const handleCreateJob = async () => {
    const req: CreateJobRequest = {
      projectId,
      totalVirtualUsers: jobUsers,
      rampTime: jobRampTime.trim() || undefined,
      simulationTime: jobSimTime.trim() || undefined,
      location: jobLocation.trim() || 'us-east-1',
      stopBehavior: jobStopBehavior,
    };
    try {
      await createJob.mutateAsync(req);
      toast.current?.show({ severity: 'success', summary: 'Job created' });
      setShowNewJob(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to create job' });
    }
  };

  // Initialise variables state from project on first render (lazy)
  if (!varsDirty && variables.length === 0 && (project.variables?.length ?? 0) > 0) {
    setVariables([...(project.variables ?? [])]);
  }

  const openAddVar = () => {
    setNewVarKey('');
    setNewVarValue('');
    setShowAddVar(true);
  };

  const confirmAddVar = () => {
    if (!newVarKey.trim()) return;
    setVariables((prev) => [...prev, { key: newVarKey.trim(), value: newVarValue }]);
    setVarsDirty(true);
    setShowAddVar(false);
  };

  const removeVariable = (index: number) => {
    setVariables((prev) => prev.filter((_, i) => i !== index));
    setVarsDirty(true);
  };

  const saveVariables = async () => {
    const varsRecord: Record<string, string> = {};
    variables.forEach(({ key, value }) => { varsRecord[key] = value; });
    try {
      await updateProject.mutateAsync({
        name: project.name,
        location: project.location ?? 'us-east-1',
        stopBehavior: project.stopBehavior ?? 'END_OF_SCRIPT_GROUP',
        variables: varsRecord,
        dataFileIds: project.dataFileIds,
      });
      toast.current?.show({ severity: 'success', summary: 'Variables saved' });
      setVarsDirty(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save variables' });
    }
  };

  // ── Task 3: Data Files ────────────────────────────────────────────────────
  // Initialise dataFileIds state from project on first render (lazy)
  if (!dfDirty && dataFileIds.length === 0 && (project.dataFileIds?.length ?? 0) > 0) {
    setDataFileIds([...(project.dataFileIds ?? [])]);
  }

  const currentDfIds = dfDirty ? dataFileIds : (project.dataFileIds ?? []);

  const openAddDf = () => {
    if (!dfDirty) setDataFileIds([...(project.dataFileIds ?? [])]);
    setShowAddDf(true);
  };

  const addDataFile = (dfId: number) => {
    if (currentDfIds.includes(dfId)) return;
    setDataFileIds((prev) => [...prev, dfId]);
    setDfDirty(true);
    setShowAddDf(false);
  };

  const removeDataFile = (dfId: number) => {
    setDataFileIds((prev) => prev.filter((x) => x !== dfId));
    setDfDirty(true);
  };

  const saveDataFiles = async () => {
    try {
      await updateProject.mutateAsync({
        name: project.name,
        location: project.location ?? 'us-east-1',
        stopBehavior: project.stopBehavior ?? 'END_OF_SCRIPT_GROUP',
        variables: Object.fromEntries(variables.map(({ key, value }) => [key, value])),
        dataFileIds: currentDfIds,
      });
      toast.current?.show({ severity: 'success', summary: 'Data files saved' });
      setDfDirty(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save data files' });
    }
  };

  // ── Helpers ───────────────────────────────────────────────────────────────
  const dfNameMap = new Map((allDataFiles ?? []).map((df) => [df.id, df.name]));

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

      {/* Header row */}
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/projects')} />
        <h2 className="m-0 flex-1">{project.name}</h2>
        <Button
          icon="pi pi-download"
          label="Download XML"
          size="small"
          severity="secondary"
          onClick={handleDownload}
        />
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

      {/* Details + Workload Scripts */}
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

      {/* Job Queue */}
      <Card
        header={
          <div className="flex align-items-center justify-content-between px-3 pt-3">
            <span className="font-bold text-lg">Job Queue</span>
            <Button
              label="New Job"
              icon="pi pi-plus"
              size="small"
              onClick={openNewJob}
            />
          </div>
        }
      >
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

      {/* Task 2: Variables */}
      <Card
        title="Variables"
        subTitle="Key/value pairs passed to test scripts at runtime"
      >
        <div className="flex flex-column gap-2">
          <DataTable
            value={variables}
            dataKey="key"
            showGridlines
            stripedRows
            emptyMessage="No variables defined."
          >
            <Column field="key" header="Key" />
            <Column field="value" header="Value" />
            <Column
              header=""
              style={{ width: '60px' }}
              body={(_row: KeyValuePair, opts) => (
                <Button
                  icon="pi pi-trash"
                  rounded
                  text
                  severity="danger"
                  size="small"
                  onClick={() => removeVariable(opts.rowIndex)}
                />
              )}
            />
          </DataTable>
          <div className="flex gap-2 justify-content-end">
            <Button
              label="Add Variable"
              icon="pi pi-plus"
              size="small"
              severity="secondary"
              onClick={openAddVar}
            />
            <Button
              label="Save Variables"
              icon="pi pi-save"
              size="small"
              loading={updateProject.isPending}
              onClick={saveVariables}
            />
          </div>
        </div>
      </Card>

      {/* Task 3: Data Files */}
      <Card title="Data Files" subTitle="Data files associated with this project's workload">
        <div className="flex flex-column gap-2">
          <DataTable
            value={currentDfIds.map((dfId) => ({ id: dfId, name: dfNameMap.get(dfId) ?? String(dfId) }))}
            dataKey="id"
            showGridlines
            stripedRows
            emptyMessage="No data files associated."
          >
            <Column field="id" header="ID" style={{ width: '80px' }} />
            <Column field="name" header="Name" />
            <Column
              header=""
              style={{ width: '60px' }}
              body={(row: { id: number }) => (
                <Button
                  icon="pi pi-trash"
                  rounded
                  text
                  severity="danger"
                  size="small"
                  onClick={() => removeDataFile(row.id)}
                />
              )}
            />
          </DataTable>
          <div className="flex gap-2 justify-content-end">
            <Button
              label="Add Data File"
              icon="pi pi-plus"
              size="small"
              severity="secondary"
              onClick={openAddDf}
            />
            <Button
              label="Save"
              icon="pi pi-save"
              size="small"
              loading={updateProject.isPending}
              onClick={saveDataFiles}
              disabled={!dfDirty}
            />
          </div>
        </div>
      </Card>

      {/* Edit Project Dialog */}
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

      {/* Add Variable Dialog */}
      <Dialog
        header="Add Variable"
        visible={showAddVar}
        style={{ width: '380px' }}
        onHide={() => setShowAddVar(false)}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={() => setShowAddVar(false)} />
            <Button label="Add" icon="pi pi-check" onClick={confirmAddVar} disabled={!newVarKey.trim()} />
          </div>
        }
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="var-key" className="font-semibold">Key *</label>
            <InputText
              id="var-key"
              value={newVarKey}
              onChange={(e) => setNewVarKey(e.target.value)}
              autoFocus
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="var-value" className="font-semibold">Value</label>
            <InputText
              id="var-value"
              value={newVarValue}
              onChange={(e) => setNewVarValue(e.target.value)}
            />
          </div>
        </div>
      </Dialog>

      {/* New Job Dialog */}
      <Dialog
        header="New Job"
        visible={showNewJob}
        style={{ width: '450px' }}
        onHide={() => setShowNewJob(false)}
        footer={
          <div className="flex gap-2 justify-content-end">
            <Button label="Cancel" text onClick={() => setShowNewJob(false)} />
            <Button
              label="Create"
              icon="pi pi-plus"
              onClick={handleCreateJob}
              loading={createJob.isPending}
              disabled={!jobUsers}
            />
          </div>
        }
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="job-users" className="font-semibold">Total Virtual Users *</label>
            <InputNumber
              id="job-users"
              value={jobUsers}
              onValueChange={(e) => setJobUsers(e.value ?? 1)}
              min={1}
              showButtons
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="job-ramp" className="font-semibold">Ramp Time (e.g. 5m, 60s)</label>
            <InputText
              id="job-ramp"
              value={jobRampTime}
              onChange={(e) => setJobRampTime(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="job-sim" className="font-semibold">Simulation Time (e.g. 30m, 1h)</label>
            <InputText
              id="job-sim"
              value={jobSimTime}
              onChange={(e) => setJobSimTime(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="job-location" className="font-semibold">Location / Region</label>
            <InputText
              id="job-location"
              value={jobLocation}
              onChange={(e) => setJobLocation(e.target.value)}
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="job-stop" className="font-semibold">Stop Behavior</label>
            <Dropdown
              id="job-stop"
              value={jobStopBehavior}
              onChange={(e) => setJobStopBehavior(e.value)}
              options={[
                { label: 'End of Script Group', value: 'END_OF_SCRIPT_GROUP' },
                { label: 'End of Period', value: 'END_OF_PERIOD' },
                { label: 'End of Unique Users', value: 'END_OF_UNIQUE_USERS' },
              ]}
            />
          </div>
        </div>
      </Dialog>

      {/* Add Data File Dialog */}
      <Dialog
        header="Add Data File"
        visible={showAddDf}
        style={{ width: '600px' }}
        onHide={() => setShowAddDf(false)}
      >
        <DataTable
          value={(allDataFiles ?? []).filter((df) => !currentDfIds.includes(df.id))}
          dataKey="id"
          showGridlines
          stripedRows
          paginator
          rows={10}
          emptyMessage="No data files available."
        >
          <Column field="id" header="ID" style={{ width: '80px' }} />
          <Column field="name" header="Name" />
          <Column field="creator" header="Owner" />
          <Column
            header=""
            style={{ width: '80px' }}
            body={(row: { id: number }) => (
              <Button
                label="Select"
                size="small"
                onClick={() => addDataFile(row.id)}
              />
            )}
          />
        </DataTable>
      </Dialog>
    </div>
  );
}

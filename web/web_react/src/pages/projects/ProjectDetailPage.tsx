import { useState, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
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
import { TabView, TabPanel } from 'primereact/tabview';
import { Toolbar } from 'primereact/toolbar';
import { useProject, useUpdateProject, useDeleteProject } from '../../hooks/useProjects';
import { useJobsByProject, useCreateJob, useStartJob, useStopJob, useKillJob } from '../../hooks/useJobs';
import { useDataFiles } from '../../hooks/useDataFiles';
import { WorkloadScriptsPanel } from './WorkloadScriptsPanel';
import { UsersAndTimesDialog } from '../../components/projects/UsersAndTimesDialog';
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
  const startJob  = useStartJob();
  const stopJob   = useStopJob();
  const killJob   = useKillJob();
  const toast = useRef<Toast>(null);

  // Create Job tab
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

  // Users & Times dialog
  const [showUsersAndTimes, setShowUsersAndTimes] = useState(false);

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

  // ── Create Job ────────────────────────────────────────────────────────────
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
      toast.current?.show({ severity: 'success', summary: 'Job created and added to queue' });
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

  // ── Users & Times ─────────────────────────────────────────────────────────
  const handleSaveUsersAndTimes = async (patch: Partial<AutomationRequest>) => {
    try {
      await updateProject.mutateAsync({
        name: project.name,
        location: project.location ?? 'us-east-1',
        stopBehavior: project.stopBehavior ?? 'END_OF_SCRIPT_GROUP',
        variables: Object.fromEntries(variables.map(({ key, value }) => [key, value])),
        dataFileIds: project.dataFileIds,
        ...patch,
      });
      toast.current?.show({ severity: 'success', summary: 'Workload configuration saved' });
      setShowUsersAndTimes(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save workload configuration' });
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
      row.status === 'Failed'  ? 'danger'  :
      'info';
    return <span className={`p-tag p-tag-${severity}`}>{row.status ?? '—'}</span>;
  };

  const jobActionsBody = (row: JobTO) => {
    const terminal = new Set(['Completed', 'Stopped', 'Killed', 'Failed']);
    const pending  = row.status === 'Created' || row.status === 'Queued';
    return (
      <div className="flex gap-1">
        <Button icon="pi pi-play"  size="small" text severity="success" tooltip="Start"
          disabled={!pending} onClick={() => startJob.mutateAsync(row.id).catch(() => {})} />
        <Button icon="pi pi-stop"  size="small" text severity="warning" tooltip="Stop"
          disabled={terminal.has(row.status ?? '') || pending}
          onClick={() => stopJob.mutateAsync(row.id).catch(() => {})} />
        <Button icon="pi pi-times-circle" size="small" text severity="danger" tooltip="Kill"
          disabled={terminal.has(row.status ?? '')}
          onClick={() => killJob.mutateAsync(row.id).catch(() => {})} />
        <Link to={`/jobs/${row.id}`}>
          <Button icon="pi pi-eye" size="small" text tooltip="View Details" />
        </Link>
      </div>
    );
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

  const toolbarStart = (
    <div className="flex align-items-center gap-2">
      <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/projects')} />
      <span className="font-bold text-xl">Project: {project.name}</span>
    </div>
  );

  const toolbarEnd = (
    <div className="flex gap-2">
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
  );

  return (
    <div className="flex flex-column gap-3">
      <Toast ref={toast} />
      <ConfirmDialog />

      <Toolbar start={toolbarStart} end={toolbarEnd} />

      <TabView>
        {/* ── Users and Times ─────────────────────────────────────────────── */}
        <TabPanel header="Users and Times" leftIcon="pi pi-users mr-2">
          <div className="flex flex-column gap-3">
            <Card>
              <div className="grid">
                <div className="col-4 font-bold">Product</div>
                <div className="col-8">{project.productName ?? '—'}</div>
                <div className="col-4 font-bold">Owner</div>
                <div className="col-8">{project.creator ?? '—'}</div>
                <div className="col-4 font-bold">Workload Type</div>
                <div className="col-8">{(project as any).workloadType === 'standard' ? 'Nonlinear (Standard)' : 'Linear (Increasing)'}</div>
                <div className="col-4 font-bold">Ramp Time</div>
                <div className="col-8">{project.rampTime ?? '—'}</div>
                <div className="col-4 font-bold">Simulation Time</div>
                <div className="col-8">{project.simulationTime != null ? `${project.simulationTime / 1000}s` : '—'}</div>
                <div className="col-4 font-bold">User Increment</div>
                <div className="col-8">{project.userIntervalIncrement ?? '—'}</div>
                <div className="col-4 font-bold">Termination Policy</div>
                <div className="col-8">{(project as any).terminationPolicy === 'script' ? 'Script Loops Completed' : 'Simulation Time Reached'}</div>
                {(project.jobRegions ?? []).length > 0 && (
                  <>
                    <div className="col-4 font-bold">Regions</div>
                    <div className="col-8">
                      {(project.jobRegions ?? []).map((r, i) => (
                        <div key={i}>{r.region}: {r.users ?? r.percentage ?? '—'}</div>
                      ))}
                    </div>
                  </>
                )}
                <div className="col-4 font-bold">Comments</div>
                <div className="col-8">{project.comments ?? '—'}</div>
              </div>
            </Card>
            <div className="flex justify-content-end">
              <Button
                label="Edit Workload Configuration"
                icon="pi pi-pencil"
                size="small"
                onClick={() => setShowUsersAndTimes(true)}
              />
            </div>
          </div>
        </TabPanel>

        {/* ── Scripts ─────────────────────────────────────────────────────── */}
        <TabPanel header="Scripts" leftIcon="pi pi-file mr-2">
          <WorkloadScriptsPanel projectId={projectId} />
        </TabPanel>

        {/* ── Data Files ──────────────────────────────────────────────────── */}
        <TabPanel header="Data Files" leftIcon="pi pi-database mr-2">
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
        </TabPanel>

        {/* ── Variables ───────────────────────────────────────────────────── */}
        <TabPanel header="Variables" leftIcon="pi pi-tag mr-2">
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
        </TabPanel>

        {/* ── Create Job ──────────────────────────────────────────────────── */}
        <TabPanel header="Create Job" leftIcon="pi pi-plus-circle mr-2">
          <div className="flex flex-column gap-3" style={{ maxWidth: '480px' }}>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Total Virtual Users *</label>
              <InputNumber
                value={jobUsers}
                onValueChange={(e) => setJobUsers(e.value ?? 1)}
                min={1}
                showButtons
              />
            </div>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Ramp Time (e.g. 5m, 60s)</label>
              <InputText
                value={jobRampTime}
                onChange={(e) => setJobRampTime(e.target.value)}
              />
            </div>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Simulation Time (e.g. 30m, 1h)</label>
              <InputText
                value={jobSimTime}
                onChange={(e) => setJobSimTime(e.target.value)}
              />
            </div>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Location / Region</label>
              <InputText
                value={jobLocation}
                onChange={(e) => setJobLocation(e.target.value)}
              />
            </div>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Stop Behavior</label>
              <Dropdown
                value={jobStopBehavior}
                onChange={(e) => setJobStopBehavior(e.value)}
                options={[
                  { label: 'End of Script Group', value: 'END_OF_SCRIPT_GROUP' },
                  { label: 'End of Period', value: 'END_OF_PERIOD' },
                  { label: 'End of Unique Users', value: 'END_OF_UNIQUE_USERS' },
                ]}
              />
            </div>
            <div className="flex justify-content-end">
              <Button
                label="Add to Job Queue"
                icon="pi pi-plus"
                onClick={handleCreateJob}
                loading={createJob.isPending}
                disabled={!jobUsers}
              />
            </div>
          </div>
        </TabPanel>

        {/* ── Job Queue ───────────────────────────────────────────────────── */}
        <TabPanel header="Job Queue" leftIcon="pi pi-list mr-2">
          <div className="flex flex-column gap-2">
            <div className="flex justify-content-end">
              <Link to={`/projects/${projectId}/queue`}>
                <Button label="Full Queue" icon="pi pi-external-link" size="small" severity="secondary" />
              </Link>
            </div>
            <DataTable
              value={jobs ?? []}
              dataKey="id"
              showGridlines
              stripedRows
              paginator
              rows={10}
              emptyMessage="No jobs."
              sortField="id"
              sortOrder={-1}
            >
              <Column field="id" header="Job ID" sortable style={{ width: '80px' }} />
              <Column field="status" header="Status" body={statusBody} sortable style={{ width: '120px' }} />
              <Column field="totalVirtualUsers" header="Users" sortable style={{ width: '80px' }} />
              <Column field="startTime" header="Start" sortable />
              <Column field="endTime" header="End" sortable />
              <Column field="creator" header="Owner" sortable />
              <Column header="Actions" body={jobActionsBody} style={{ width: '160px' }} />
            </DataTable>
          </div>
        </TabPanel>
      </TabView>

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

      {/* Users & Times Dialog */}
      <UsersAndTimesDialog
        visible={showUsersAndTimes}
        project={project}
        saving={updateProject.isPending}
        onHide={() => setShowUsersAndTimes(false)}
        onSave={handleSaveUsersAndTimes}
      />

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

import { useState, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { Tag } from 'primereact/tag';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { ToggleButton } from 'primereact/togglebutton';
import { useProject } from '../../hooks/useProjects';
import {
  useJobsByProject,
  useAllJobStatuses,
  useStartJob,
  useStopJob,
  usePauseJob,
  useResumeJob,
  useKillJob,
} from '../../hooks/useJobs';
import { formatDate } from '../../utils/formatDate';
import type { JobTO } from '../../types/job';

const TERMINAL_STATUSES = new Set(['Completed', 'Stopped', 'Killed', 'Failed']);
const RUNNING_STATUSES  = new Set(['Running', 'Starting', 'RampPaused']);

function statusSeverity(status: string): 'success' | 'warning' | 'danger' | 'info' {
  if (status === 'Running') return 'success';
  if (status === 'RampPaused' || status === 'Starting') return 'warning';
  if (status === 'Failed' || status === 'Killed') return 'danger';
  return 'info';
}

export function ProjectJobQueuePage() {
  const { id } = useParams<{ id: string }>();
  const projectId = Number(id);
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  const [polling, setPolling] = useState(false);
  const [filterFinished, setFilterFinished] = useState(false);

  const { data: project, isLoading: loadingProject } = useProject(projectId);
  const { data: jobs, isLoading: loadingJobs, error, refetch } = useJobsByProject(projectId);
  const { data: statuses } = useAllJobStatuses(polling);

  const startJob  = useStartJob();
  const stopJob   = useStopJob();
  const pauseJob  = usePauseJob();
  const resumeJob = useResumeJob();
  const killJob   = useKillJob();

  if (loadingProject || loadingJobs) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load job queue." />;

  // Build live status map from polling data
  const statusMap: Record<string, string> = {};
  statuses?.forEach((s) => { if (s['jobId']) statusMap[s['jobId']] = s['status'] ?? ''; });

  const effectiveStatus = (job: JobTO) => statusMap[String(job.id)] ?? job.status ?? 'Unknown';

  const visibleJobs = filterFinished
    ? (jobs ?? []).filter((j) => !TERMINAL_STATUSES.has(effectiveStatus(j)))
    : (jobs ?? []);

  const runControl = async (action: () => Promise<unknown>, label: string) => {
    try {
      await action();
      toast.current?.show({ severity: 'success', summary: `${label} sent` });
      refetch();
    } catch {
      toast.current?.show({ severity: 'error', summary: `Failed: ${label}` });
    }
  };

  const confirmKill = (job: JobTO) =>
    confirmDialog({
      message: `Kill job #${job.id}? This will terminate all agents immediately.`,
      header: 'Confirm Kill',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: () => runControl(() => killJob.mutateAsync(job.id), 'Kill'),
    });

  const statusBody = (job: JobTO) => {
    const status = effectiveStatus(job);
    return <Tag value={status} severity={statusSeverity(status)} />;
  };

  const actionsBody = (job: JobTO) => {
    const status = effectiveStatus(job);
    const isRunning  = RUNNING_STATUSES.has(status);
    const isTerminal = TERMINAL_STATUSES.has(status);
    const isPaused   = status === 'RampPaused';
    const isPending  = status === 'Created' || status === 'Queued';

    return (
      <div className="flex gap-1">
        <Button
          icon="pi pi-play"
          size="small"
          text
          severity="success"
          tooltip="Start"
          disabled={!isPending}
          onClick={() => runControl(() => startJob.mutateAsync(job.id), 'Start')}
        />
        <Button
          icon="pi pi-pause"
          size="small"
          text
          severity="warning"
          tooltip="Pause Ramp"
          disabled={!isRunning || isPaused}
          onClick={() => runControl(() => pauseJob.mutateAsync(job.id), 'Pause Ramp')}
        />
        <Button
          icon="pi pi-refresh"
          size="small"
          text
          severity="info"
          tooltip="Resume"
          disabled={!isPaused}
          onClick={() => runControl(() => resumeJob.mutateAsync(job.id), 'Resume')}
        />
        <Button
          icon="pi pi-stop"
          size="small"
          text
          severity="warning"
          tooltip="Stop"
          disabled={isTerminal || isPending}
          onClick={() => runControl(() => stopJob.mutateAsync(job.id), 'Stop')}
        />
        <Button
          icon="pi pi-times-circle"
          size="small"
          text
          severity="danger"
          tooltip="Kill"
          disabled={isTerminal}
          onClick={() => confirmKill(job)}
        />
        <Link to={`/jobs/${job.id}`}>
          <Button icon="pi pi-eye" size="small" text tooltip="View Details" />
        </Link>
      </div>
    );
  };

  return (
    <>
      <Toast ref={toast} />
      <ConfirmDialog />

      {/* Toolbar */}
      <div className="flex align-items-center justify-content-between mb-3">
        <div className="flex align-items-center gap-2">
          <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate(`/projects/${projectId}`)} />
          <span className="text-xl font-bold">
            Job Queue — {project?.name ?? `Project ${projectId}`}
          </span>
        </div>
        <div className="flex align-items-center gap-2">
          <ToggleButton
            checked={filterFinished}
            onChange={(e) => setFilterFinished(e.value)}
            onLabel="Hide Finished"
            offLabel="Show All"
            onIcon="pi pi-filter-slash"
            offIcon="pi pi-filter"
          />
          <ToggleButton
            checked={polling}
            onChange={(e) => setPolling(e.value)}
            onLabel="Live (5s)"
            offLabel="Auto-refresh Off"
            onIcon="pi pi-wifi"
            offIcon="pi pi-wifi"
          />
          <Button
            icon="pi pi-refresh"
            size="small"
            severity="secondary"
            tooltip="Refresh now"
            onClick={() => refetch()}
          />
        </div>
      </div>

      <DataTable
        value={visibleJobs}
        dataKey="id"
        showGridlines
        stripedRows
        paginator
        rows={25}
        sortField="id"
        sortOrder={-1}
        emptyMessage="No jobs found for this project."
      >
        <Column field="id" header="Job ID" sortable style={{ width: '80px' }} />
        <Column field="name" header="Name" sortable />
        <Column header="Status" body={statusBody} sortable sortField="status" style={{ width: '140px' }} />
        <Column field="totalVirtualUsers" header="Users" sortable style={{ width: '80px' }} />
        <Column field="rampTime" header="Ramp Time" style={{ width: '110px' }} />
        <Column
          field="startTime"
          header="Start Time"
          sortable
          body={(row: JobTO) => formatDate(row.startTime)}
          style={{ width: '160px' }}
        />
        <Column
          field="endTime"
          header="End Time"
          sortable
          body={(row: JobTO) => formatDate(row.endTime)}
          style={{ width: '160px' }}
        />
        <Column field="creator" header="Owner" sortable style={{ width: '120px' }} />
        <Column header="Actions" body={actionsBody} style={{ width: '200px' }} />
      </DataTable>
    </>
  );
}

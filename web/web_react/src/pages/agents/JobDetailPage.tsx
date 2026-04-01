import { useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useJobById, useJobInstanceStatuses } from '../../hooks/useJobs';
import { jobsApi } from '../../api/jobs';
import type { CloudVmStatus } from '../../types/job';

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

export function JobDetailPage() {
  const { id } = useParams<{ id: string }>();
  const jobId = Number(id);
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const [polling, setPolling] = useState(false);

  const { data: job, isLoading, error } = useJobById(jobId);
  const { data: instances } = useJobInstanceStatuses(jobId, polling);

  if (isLoading) return <ProgressSpinner />;
  if (error || !job) return <Message severity="error" text="Job not found." />;

  const runControl = async (
    action: 'start' | 'stop' | 'pause' | 'resume' | 'kill',
    label: string,
  ) => {
    try {
      await jobsApi[action](jobId);
      toast.current?.show({ severity: 'success', summary: `${label} sent` });
    } catch {
      toast.current?.show({ severity: 'error', summary: `Failed to ${label.toLowerCase()} job` });
    }
  };

  const handleKill = () => {
    confirmDialog({
      message: `Kill job ${jobId}? This cannot be undone.`,
      header: 'Confirm Kill',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: () => runControl('kill', 'Kill'),
    });
  };

  const handleDownload = async (type: 'script' | 'job') => {
    try {
      const res = type === 'script'
        ? await jobsApi.downloadScript(jobId)
        : await jobsApi.downloadJob(jobId);
      downloadBlob(res.data as Blob, `job-${jobId}-${type}.xml`);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Download failed' });
    }
  };

  const statusSeverity = (status?: string) => {
    if (status === 'Running') return 'success';
    if (status === 'Failed') return 'danger';
    return 'info';
  };

  const instanceStatusBody = (row: CloudVmStatus) => (
    <span className={`p-tag p-tag-${statusSeverity(row.vmStatus)}`}>
      {row.vmStatus ?? '—'}
    </span>
  );

  return (
    <div className="flex flex-column gap-3">
      <Toast ref={toast} />
      <ConfirmDialog />

      {/* Header */}
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/agents')} />
        <h2 className="m-0 flex-1">Job {jobId}</h2>
        <Button
          label={polling ? 'Stop Polling' : 'Start Polling'}
          icon={polling ? 'pi pi-pause' : 'pi pi-play'}
          severity={polling ? 'warning' : 'success'}
          size="small"
          onClick={() => setPolling((p) => !p)}
        />
      </div>

      {/* Metadata + Controls */}
      <div className="grid">
        <div className="col-12 md:col-6">
          <Card title="Details">
            <div className="grid">
              <div className="col-4 font-bold">Job ID</div>
              <div className="col-8">{job.id}</div>
              <div className="col-4 font-bold">Project</div>
              <div className="col-8">{job.projectName ?? '—'}</div>
              <div className="col-4 font-bold">Status</div>
              <div className="col-8">
                <span className={`p-tag p-tag-${statusSeverity(job.status)}`}>
                  {job.status ?? '—'}
                </span>
              </div>
              <div className="col-4 font-bold">Owner</div>
              <div className="col-8">{job.creator ?? '—'}</div>
              <div className="col-4 font-bold">Total Users</div>
              <div className="col-8">{job.totalVirtualUsers ?? '—'}</div>
              <div className="col-4 font-bold">Ramp Time</div>
              <div className="col-8">{job.rampTime ?? '—'}</div>
              <div className="col-4 font-bold">Simulation Time</div>
              <div className="col-8">
                {job.simulationTime != null ? `${job.simulationTime}s` : '—'}
              </div>
              <div className="col-4 font-bold">Start Time</div>
              <div className="col-8">{job.startTime ?? '—'}</div>
              <div className="col-4 font-bold">End Time</div>
              <div className="col-8">{job.endTime ?? '—'}</div>
            </div>
          </Card>
        </div>

        <div className="col-12 md:col-6">
          <Card title="Controls">
            <div className="flex flex-wrap gap-2 mb-4">
              <Button
                label="Start"
                icon="pi pi-play"
                severity="success"
                onClick={() => runControl('start', 'Start')}
              />
              <Button
                label="Pause"
                icon="pi pi-pause"
                severity="warning"
                onClick={() => runControl('pause', 'Pause')}
              />
              <Button
                label="Resume"
                icon="pi pi-refresh"
                severity="info"
                onClick={() => runControl('resume', 'Resume')}
              />
              <Button
                label="Stop"
                icon="pi pi-stop"
                severity="danger"
                onClick={() => runControl('stop', 'Stop')}
              />
              <Button
                label="Kill"
                icon="pi pi-times-circle"
                severity="danger"
                outlined
                onClick={handleKill}
              />
            </div>
            <div className="flex flex-wrap gap-2">
              <Button
                label="Download Script XML"
                icon="pi pi-download"
                severity="secondary"
                size="small"
                onClick={() => handleDownload('script')}
              />
              <Button
                label="Download Job XML"
                icon="pi pi-download"
                severity="secondary"
                size="small"
                onClick={() => handleDownload('job')}
              />
            </div>
          </Card>
        </div>
      </div>

      {/* Per-instance status table */}
      <Card title="Instance Status">
        <DataTable
          value={instances ?? []}
          dataKey="instanceId"
          showGridlines
          stripedRows
          paginator
          rows={25}
          emptyMessage="No instance data."
        >
          <Column field="instanceId" header="Instance ID" sortable />
          <Column field="vmStatus" header="Status" body={instanceStatusBody} sortable />
          <Column field="userCount" header="Users" sortable />
          <Column field="region" header="Region" sortable />
          <Column field="startTime" header="Start" sortable />
          <Column field="endTime" header="End" sortable />
        </DataTable>
      </Card>
    </div>
  );
}

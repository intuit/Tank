import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useJobs, useAllJobStatuses } from '../../hooks/useJobs';
import { jobsApi } from '../../api/jobs';
import type { JobTO } from '../../types/job';

export function AgentTrackerPage() {
  const [polling, setPolling] = useState(false);
  const { data: jobs, isLoading, error } = useJobs();
  const { data: statuses } = useAllJobStatuses(polling);

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load job data." />;

  const statusMap: Record<string, string> = {};
  statuses?.forEach((s) => {
    if (s['jobId']) statusMap[s['jobId']] = s['status'] ?? '';
  });

  const statusBody = (row: JobTO) => {
    const status = statusMap[String(row.id)] ?? row.status ?? '—';
    const severity =
      status === 'Running' ? 'success' :
      status === 'Failed' ? 'danger' :
      'info';
    return <span className={`p-tag p-tag-${severity}`}>{status}</span>;
  };

  const actionsBody = (row: JobTO) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-play"
        size="small"
        severity="success"
        text
        tooltip="Start"
        onClick={() => jobsApi.start(row.id)}
      />
      <Button
        icon="pi pi-pause"
        size="small"
        severity="warning"
        text
        tooltip="Pause"
        onClick={() => jobsApi.pause(row.id)}
      />
      <Button
        icon="pi pi-refresh"
        size="small"
        severity="info"
        text
        tooltip="Resume"
        onClick={() => jobsApi.resume(row.id)}
      />
      <Button
        icon="pi pi-stop"
        size="small"
        severity="danger"
        text
        tooltip="Stop"
        onClick={() => jobsApi.stop(row.id)}
      />
    </div>
  );

  const leftToolbar = <span className="font-bold text-xl">Agent Tracker</span>;
  const rightToolbar = (
    <Button
      label={polling ? 'Stop Polling' : 'Start Polling'}
      icon={polling ? 'pi pi-pause' : 'pi pi-play'}
      severity={polling ? 'warning' : 'success'}
      size="small"
      onClick={() => setPolling((p) => !p)}
    />
  );

  return (
    <div>
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3" />
      <DataTable
        value={jobs}
        dataKey="id"
        paginator
        rows={25}
        emptyMessage="No jobs."
        sortField="created"
        sortOrder={-1}
      >
        <Column field="id" header="Job ID" sortable style={{ width: '80px' }} />
        <Column field="projectName" header="Project" sortable />
        <Column field="status" header="Status" body={statusBody} sortable />
        <Column field="totalVirtualUsers" header="Users" sortable />
        <Column field="startTime" header="Start" sortable />
        <Column field="endTime" header="End" sortable />
        <Column header="Actions" body={actionsBody} style={{ width: '160px' }} />
      </DataTable>
    </div>
  );
}

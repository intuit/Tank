import { useParams, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useProject } from '../../hooks/useProjects';
import { useJobsByProject } from '../../hooks/useJobs';
import type { JobTO } from '../../types/job';

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const projectId = Number(id);
  const navigate = useNavigate();
  const { data: project, isLoading, error } = useProject(projectId);
  const { data: jobs } = useJobsByProject(projectId);

  if (isLoading) return <ProgressSpinner />;
  if (error || !project) return <Message severity="error" text="Project not found." />;

  const statusBody = (row: JobTO) => {
    const severity =
      row.status === 'Running' ? 'success' :
      row.status === 'Failed' ? 'danger' :
      'info';
    return <span className={`p-tag p-tag-${severity}`}>{row.status ?? '—'}</span>;
  };

  return (
    <div className="flex flex-column gap-3">
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/projects')} />
        <h2 className="m-0">{project.name}</h2>
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
          <Card title="Script Groups">
            {project.scriptGroupList?.length ? (
              <ul className="m-0 pl-3">
                {project.scriptGroupList.map((sg, i) => (
                  <li key={i}>{sg.name ?? `Group ${i + 1}`} (loop: {sg.loop ?? 1})</li>
                ))}
              </ul>
            ) : (
              <span className="text-color-secondary">No script groups configured.</span>
            )}
          </Card>
        </div>
      </div>

      <Card title="Job Queue">
        <DataTable
          value={jobs ?? []}
          dataKey="id"
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
    </div>
  );
}

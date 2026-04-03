import { useNavigate } from 'react-router-dom';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import { Skeleton } from 'primereact/skeleton';
import { useUsers } from '../../hooks/useUsers';
import { useProjects } from '../../hooks/useProjects';
import { useScripts } from '../../hooks/useScripts';
import { useAllJobStatuses } from '../../hooks/useJobs';
import { useFilters } from '../../hooks/useFilters';

function StatCard({ label, value, icon, loading }: {
  label: string;
  value: number | undefined;
  icon: string;
  loading: boolean;
}) {
  return (
    <div className="col-6 md:col-3">
      <Card>
        <div className="flex align-items-center gap-3">
          <i className={`${icon} text-4xl text-primary`} />
          <div>
            <div className="text-500 text-sm">{label}</div>
            {loading ? (
              <Skeleton width="3rem" height="1.5rem" />
            ) : (
              <div className="text-2xl font-bold">{value ?? 0}</div>
            )}
          </div>
        </div>
      </Card>
    </div>
  );
}

export function AdminPage() {
  const navigate = useNavigate();
  const { data: users, isLoading: usersLoading } = useUsers();
  const { data: projects, isLoading: projectsLoading } = useProjects();
  const { data: scripts, isLoading: scriptsLoading } = useScripts();
  const { data: statuses, isLoading: jobsLoading } = useAllJobStatuses();
  const { data: filtersData, isLoading: filtersLoading } = useFilters();

  const activeJobs = statuses
    ? statuses.filter((s) => {
        const status = Object.values(s)[0] ?? '';
        return ['Running', 'Ramp_Paused', 'Starting'].includes(status);
      }).length
    : undefined;

  return (
    <div>
      <h2>Admin Dashboard</h2>

      <div className="grid mb-4">
        <StatCard
          label="Users"
          value={users?.length}
          icon="pi pi-users"
          loading={usersLoading}
        />
        <StatCard
          label="Projects"
          value={projects?.length}
          icon="pi pi-folder"
          loading={projectsLoading}
        />
        <StatCard
          label="Scripts"
          value={scripts?.length}
          icon="pi pi-file"
          loading={scriptsLoading}
        />
        <StatCard
          label="Active Jobs"
          value={activeJobs}
          icon="pi pi-play-circle"
          loading={jobsLoading}
        />
      </div>

      <h3 className="mt-4 mb-3">Admin Sections</h3>
      <div className="grid">

        <div className="col-12 md:col-4">
          <Card title="User Management">
            <p>Create, edit, and delete user accounts.</p>
            <div className="flex gap-2 flex-wrap">
              <Button
                label="Manage Users"
                icon="pi pi-users"
                onClick={() => navigate('/admin/users')}
              />
              <Button
                label="User Groups"
                icon="pi pi-sitemap"
                severity="secondary"
                onClick={() => navigate('/admin/groups')}
              />
            </div>
          </Card>
        </div>

        <div className="col-12 md:col-4">
          <Card title="Server Logs">
            <p>Browse and download server log files.</p>
            <Button
              label="View Logs"
              icon="pi pi-file-edit"
              severity="secondary"
              onClick={() => navigate('/admin/logs')}
            />
          </Card>
        </div>

        <div className="col-12 md:col-4">
          <Card title="Filters">
            <p>
              Manage script filters and filter groups.{' '}
              {!filtersLoading && filtersData !== undefined && (
                <span className="text-500 text-sm">({filtersData.length} filters)</span>
              )}
            </p>
            <Button
              label="Manage Filters"
              icon="pi pi-filter"
              severity="secondary"
              onClick={() => navigate('/filters')}
            />
          </Card>
        </div>

        <div className="col-12 md:col-4">
          <Card title="Projects">
            <p>
              View all load test projects and job queues.{' '}
              {!projectsLoading && projects !== undefined && (
                <span className="text-500 text-sm">({projects.length} projects)</span>
              )}
            </p>
            <Button
              label="View Projects"
              icon="pi pi-folder-open"
              severity="secondary"
              onClick={() => navigate('/projects')}
            />
          </Card>
        </div>

        <div className="col-12 md:col-4">
          <Card title="Scripts">
            <p>
              Browse and manage test scripts.{' '}
              {!scriptsLoading && scripts !== undefined && (
                <span className="text-500 text-sm">({scripts.length} scripts)</span>
              )}
            </p>
            <Button
              label="View Scripts"
              icon="pi pi-code"
              severity="secondary"
              onClick={() => navigate('/scripts')}
            />
          </Card>
        </div>

        <div className="col-12 md:col-4">
          <Card title="Maintenance Tools">
            <p>Run upgrade utilities and download agent tools.</p>
            <Button
              label="Tools"
              icon="pi pi-wrench"
              severity="secondary"
              onClick={() => navigate('/tools')}
            />
          </Card>
        </div>

      </div>
    </div>
  );
}

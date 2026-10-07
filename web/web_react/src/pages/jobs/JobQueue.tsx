import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dialog } from 'primereact/dialog';
import { InputSwitch } from 'primereact/inputswitch';
import { Message } from 'primereact/message';
import { Tag } from 'primereact/tag';
import { TreeTable } from 'primereact/treetable';
import type { TreeNode } from 'primereact/treenode';
import { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router';
import { contextPath, type Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { formatDateTime } from '../../format';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { JobCharts } from './JobCharts';

type JobTree = Schemas['JobTree'];
type JobNode = Schemas['JobNode'];
type ProjectJobs = Schemas['ProjectJobs'];
type AgentNode = Schemas['AgentNode'];
type Actions = Schemas['Actions'];

/** The server asks for no faster than this (GET /v2/jobs/tree) */
export const REFRESH_MS = 10_000;

/** Actions as the REST API names them, with the flag in Actions that allows each */
const ACTIONS = [
  { action: 'start', flag: 'start', label: 'Run', icon: 'pi pi-play', severity: 'success' },
  { action: 'start-load', flag: 'startLoad', label: 'Start load', icon: 'pi pi-bolt', severity: 'success' },
  { action: 'pause-ramp', flag: 'pauseRamp', label: 'Pause ramp', icon: 'pi pi-pause', severity: 'secondary' },
  { action: 'resume-ramp', flag: 'resumeRamp', label: 'Resume ramp', icon: 'pi pi-step-forward', severity: 'secondary' },
  { action: 'pause', flag: 'pause', label: 'Pause', icon: 'pi pi-pause-circle', severity: 'secondary' },
  { action: 'resume', flag: 'resume', label: 'Resume', icon: 'pi pi-play-circle', severity: 'secondary' },
  { action: 'stop', flag: 'stop', label: 'Stop', icon: 'pi pi-stop', severity: 'warning' },
  { action: 'kill', flag: 'kill', label: 'Kill', icon: 'pi pi-times-circle', severity: 'danger' },
] as const;

type Row =
  /** a project, or (no projectId) the jobs whose project no longer exists */
  | { kind: 'project'; project: ProjectJobs; orphans?: boolean }
  | { kind: 'job'; job: JobNode }
  | { kind: 'agent'; agent: AgentNode; job: JobNode };

/** The rows actions can be sent to */
type Target = Exclude<Row, { kind: 'project' }>;

/**
 * Jobs and their agents, live (JobTreeTableBean, projectjobqueue.xhtml and agents/index.xhtml). With
 * a projectId it lists that project's jobs; without one, every project with recent jobs, each
 * expandable to its jobs. Figures come from the controller serving the request, as in the JSF UI.
 */
export function JobQueue({ projectId }: { projectId?: number }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [includeFinished, setIncludeFinished] = useState(false);
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [detailsFor, setDetailsFor] = useState<{ job: JobNode; agent?: AgentNode }>();
  const queryKey = ['jobs', 'tree', { projectId, includeFinished }] as const;

  const tree = useQuery({
    queryKey,
    queryFn: async ({ signal }): Promise<JobTree> => {
      const { data, error, response } = await client.GET('/v2/jobs/tree', {
        params: { query: { projectId, includeFinished } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the job queue');
      }
      return data;
    },
    refetchInterval: REFRESH_MS,
    staleTime: 0,
  });

  const act = useMutation({
    mutationFn: async ({ target, action }: { target: Target; action: string }) => {
      const result =
        target.kind === 'job'
          ? await client.POST('/v2/jobs/{jobId}/{action}', {
              params: { path: { jobId: Number(target.job.jobId), action } },
            })
          : await client.POST('/v2/jobs/instances/{instanceId}/{action}', {
              params: { path: { instanceId: target.agent.instanceId!, action } },
            });
      if (!result.data) {
        throw toApiError(result.error, result.response, `${action} the ${target.kind}`);
      }
      return result.data;
    },
    onError: (error) => notify.error('Action not sent', error.message),
    // actions are asynchronous: refresh now, and again once agents have had a moment to react
    onSettled: () => {
      void queryClient.invalidateQueries({ queryKey: ['jobs', 'tree'] });
      setTimeout(() => void queryClient.invalidateQueries({ queryKey: ['jobs', 'tree'] }), 2000);
    },
  });

  const remove = useMutation({
    mutationFn: async (job: JobNode) => {
      const { error, response } = await client.DELETE('/v2/jobs/{jobId}', {
        params: { path: { jobId: Number(job.jobId) } },
      });
      if (!response.ok) {
        throw toApiError(error, response, 'delete the job');
      }
    },
    onSuccess: (_result, job) => notify.success('Job deleted', job.name),
    onError: (error) => notify.error('Job not deleted', error.message),
    onSettled: () => void queryClient.invalidateQueries({ queryKey: ['jobs', 'tree'] }),
  });

  const nodes = useMemo(() => (tree.data ? toNodes(tree.data, projectId === undefined) : []), [tree.data, projectId]);

  // Projects start expanded so their jobs show; agents start collapsed
  const expandedOnce = useRef(false);
  useEffect(() => {
    if (!expandedOnce.current && tree.data && projectId === undefined) {
      expandedOnce.current = true;
      setExpanded(Object.fromEntries(nodes.map((n) => [n.key as string, true])));
    }
  }, [tree.data, nodes, projectId]);

  const confirmThen = (target: Target, action: string, label: string) => {
    const what = target.kind === 'job' ? `job "${target.job.name}"` : `agent ${target.agent.instanceId}`;
    confirmDialog({
      header: `${label} ${target.kind}`,
      message:
        action === 'kill'
          ? `Kill ${what}? Its agents stop immediately, without finishing their scripts.`
          : `Delete ${what}? This can't be undone.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: label,
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => (action === 'delete' && target.kind === 'job' ? remove.mutate(target.job) : act.mutate({ target, action })),
    });
  };

  const actionsFor = (row: Row) => {
    if (row.kind === 'project') {
      return row.orphans ? null : (
        <div className="row-actions">
          <a
            className="p-button p-button-icon-only p-button-text p-button-rounded plain-link"
            href={`${contextPath()}/v2/projects/download/${row.project.projectId}`}
            aria-label={`Download harness XML for ${row.project.name}`}
            title="Download harness XML"
          >
            <i className="pi pi-download" aria-hidden />
          </a>
        </div>
      );
    }
    const allowed: Actions = (row.kind === 'job' ? row.job.actions : row.agent.actions) ?? {};
    const name = row.kind === 'job' ? row.job.name : row.agent.instanceId;
    return (
      <div className="row-actions">
        <Button
          icon="pi pi-chart-line"
          rounded
          text
          aria-label={`Charts for ${name}`}
          tooltip={row.kind === 'job' ? 'Users and TPS' : 'TPS for this agent'}
          tooltipOptions={{ position: 'top' }}
          onClick={() => setDetailsFor(row.kind === 'job' ? { job: row.job } : { job: row.job, agent: row.agent })}
        />
        {ACTIONS.filter((a) => allowed[a.flag]).map((a) => (
          <Button
            key={a.action}
            icon={a.icon}
            rounded
            text
            severity={a.severity}
            aria-label={`${a.label} ${name}`}
            tooltip={a.label}
            tooltipOptions={{ position: 'top' }}
            disabled={act.isPending}
            onClick={() => (a.action === 'kill' ? confirmThen(row, 'kill', 'Kill') : act.mutate({ target: row, action: a.action }))}
          />
        ))}
        {row.kind === 'job' && allowed.delete && (
          <Button
            icon="pi pi-trash"
            rounded
            text
            severity="danger"
            aria-label={`Delete ${name}`}
            tooltip="Delete"
            tooltipOptions={{ position: 'top' }}
            onClick={() => confirmThen(row, 'delete', 'Delete')}
          />
        )}
      </div>
    );
  };

  const nameCell = (row: Row) =>
    row.kind === 'project' ? (
      row.orphans ? (
        <em>{row.project.name}</em>
      ) : (
        <Link to={`/projects/${row.project.projectId}`} className="project-cell">
          {row.project.name}
        </Link>
      )
    ) : row.kind === 'job' ? (
      <Button label={row.job.name} link className="link-cell" onClick={() => setDetailsFor({ job: row.job })} />
    ) : (
      <span title={row.agent.instanceId}>{row.agent.instanceId}</span>
    );

  return (
    <div>
      <div className="tab-toolbar">
        <div className="field-inline">
          <InputSwitch
            inputId="include-finished"
            checked={includeFinished}
            onChange={(e) => setIncludeFinished(!!e.value)}
          />
          <label htmlFor="include-finished">Show finished jobs</label>
        </div>
        <Button
          label="Refresh"
          icon="pi pi-refresh"
          text
          size="small"
          loading={tree.isFetching}
          onClick={() => void tree.refetch()}
        />
        {tree.data?.generatedAt && (
          <span className="field-help">
            Updated {formatTime(tree.data.generatedAt)}; refreshes every {REFRESH_MS / 1000}s
          </span>
        )}
      </div>

      {tree.error && !tree.data ? (
        <Message severity="error" text={tree.error.message} className="tab-message" />
      ) : (
        <TreeTable
          value={nodes}
          expandedKeys={expanded}
          onToggle={(e) => setExpanded(e.value as Record<string, boolean>)}
          emptyMessage={
            includeFinished
              ? projectId === undefined
                ? 'No jobs in the last week'
                : 'No jobs for this project yet'
              : 'No queued or running jobs. Turn on "Show finished jobs" to see past ones.'
          }
          loading={tree.isPending}
          tableStyle={{ minWidth: '56rem' }}
          className="p-treetable-sm job-tree"
        >
          <Column field="name" header="Name" expander body={(n: TreeNode) => nameCell(n.data as Row)} style={{ minWidth: '12rem' }} />
          <Column header="ID" body={(n: TreeNode) => idOf(n.data as Row)} style={{ width: '4rem' }} />
          <Column header="Status" body={(n: TreeNode) => statusCell(n.data as Row)} style={{ minWidth: '11rem' }} />
          <Column header="Region" body={(n: TreeNode) => regionOf(n.data as Row)} style={{ minWidth: '9rem' }} />
          <Column header="Users" body={(n: TreeNode) => usersCell(n.data as Row)} style={{ whiteSpace: 'nowrap' }} />
          <Column header="TPS" body={(n: TreeNode) => figures(n.data as Row).tps ?? 0} />
          <Column header="Failures" body={(n: TreeNode) => figures(n.data as Row).failures?.total ?? 0} />
          <Column header="Started" body={(n: TreeNode) => formatDateTime(figures(n.data as Row).startTime)} style={{ whiteSpace: 'nowrap' }} />
          <Column body={(n: TreeNode) => actionsFor(n.data as Row)} />
        </TreeTable>
      )}

      {detailsFor && (
        <JobDetailsDialog job={detailsFor.job} agent={detailsFor.agent} onHide={() => setDetailsFor(undefined)} />
      )}
    </div>
  );
}

function toNodes(data: JobTree, byProject: boolean): TreeNode[] {
  const jobNode = (job: JobNode): TreeNode => ({
    key: `job-${job.jobId}`,
    data: { kind: 'job', job } satisfies Row,
    children: (job.agents ?? []).map((agent) => ({
      key: `agent-${agent.instanceId}`,
      data: { kind: 'agent', agent, job } satisfies Row,
    })),
  });
  if (!byProject) {
    return [...(data.projects ?? []).flatMap((p) => p.jobs ?? []), ...(data.otherJobs ?? [])].map(jobNode);
  }
  const projects: TreeNode[] = (data.projects ?? []).map((project) => ({
    key: `project-${project.projectId}`,
    data: { kind: 'project', project } satisfies Row,
    children: (project.jobs ?? []).map(jobNode),
  }));
  const others = data.otherJobs ?? [];
  if (others.length) {
    const sum = (pick: (j: JobNode) => number | undefined) => others.reduce((total, j) => total + (pick(j) ?? 0), 0);
    projects.push({
      key: 'project-none',
      data: {
        kind: 'project',
        orphans: true,
        project: {
          name: 'Jobs without a project',
          jobs: others,
          activeUsers: sum((j) => j.activeUsers),
          totalUsers: sum((j) => j.totalUsers),
          tps: sum((j) => j.tps),
          failures: { total: sum((j) => j.failures?.total) },
        },
      } satisfies Row,
      children: others.map(jobNode),
    });
  }
  return projects;
}

type Figures = Pick<JobNode, 'activeUsers' | 'totalUsers' | 'tps' | 'failures' | 'startTime'>;

function figures(row: Row): Figures {
  return row.kind === 'project' ? row.project : row.kind === 'job' ? row.job : row.agent;
}

/** Job IDs only; a project's ID would read as one (as in the JSF tree) */
function idOf(row: Row) {
  return row.kind === 'job' ? row.job.jobId : '';
}

function regionOf(row: Row) {
  return row.kind === 'agent' ? row.agent.region : '';
}

function statusCell(row: Row) {
  if (row.kind === 'project') {
    const jobs = row.project.jobs ?? [];
    const running = jobs.filter((j) => j.status === 'Running').length;
    const completed = jobs.length > 0 && jobs.every((j) => j.status === 'Completed');
    return (
      <span className="status-cell">
        <Tag
          value={completed ? 'Completed' : `${running}/${jobs.length} jobs running`}
          severity={running > 0 ? 'success' : completed ? 'secondary' : 'info'}
        />
      </span>
    );
  }
  if (row.kind === 'agent') {
    const { agent } = row;
    return (
      <span className="status-cell">
        <Tag value={agent.status} severity={severityOf(agent.status)} />
        {agent.wsState && <span className="field-help">{agent.wsState}</span>}
        {agent.lastSeenMs && <span className="field-help">seen {formatTime(new Date(agent.lastSeenMs).toISOString())}</span>}
      </span>
    );
  }
  const { job } = row;
  const agents = job.agents ?? [];
  const count = (test: (a: AgentNode) => boolean) => agents.filter(test).length;
  const running = count((a) => a.status === 'running');
  // as ActJobNodeBean counts them for two-step jobs
  const connected = count((a) => !!a.wsState && a.wsState !== 'disconnected');
  const ready = count((a) => a.status === 'ready');
  return (
    <span className="status-cell">
      <Tag value={job.status} severity={severityOf(job.status)} />
      {agents.length > 0 && (
        <span className="field-help">
          {job.useTwoStep && running === 0
            ? `${connected}/${agents.length} connected, ${ready}/${agents.length} ready`
            : `${running}/${agents.length} agents running`}
        </span>
      )}
    </span>
  );
}

function usersCell(row: Row) {
  const f = figures(row);
  if (row.kind === 'job' && row.job.incrementStrategy?.toLowerCase().startsWith('non')) {
    return `${f.activeUsers ?? 0} active`;
  }
  return `${f.activeUsers ?? 0} / ${f.totalUsers ?? 0}`;
}

/** Colours JobQueueStatus and VMStatus (vmManager) names; unknown ones stay neutral */
export function severityOf(status: string | undefined): 'success' | 'info' | 'warning' | 'danger' | 'secondary' {
  switch (status?.toLowerCase()) {
    case 'running':
      return 'success';
    case 'created':
    case 'queued':
    case 'starting':
    case 'pending':
    case 'ready':
    case 'rebooting':
      return 'info';
    case 'paused':
    case 'ramppaused':
    case 'stopping':
      return 'warning';
    case 'aborted':
    case 'terminated':
    case 'disconnected':
    case 'replaced':
      return 'danger';
    default:
      // Completed, Stopped, Deleted, shutting_down, unknown: neutral, not the primary blue
      return 'secondary';
  }
}

function formatTime(iso: string): string {
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? '' : date.toLocaleTimeString();
}

/** A job's summary, failure counts and charts (GET /v2/jobs/{id}/details); for an agent, its TPS */
function JobDetailsDialog({ job, agent, onHide }: { job: JobNode; agent?: AgentNode; onHide: () => void }) {
  const { client } = useSession();
  const details = useQuery({
    queryKey: ['jobs', job.jobId, 'details'],
    enabled: !agent,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/jobs/{jobId}/details', {
        params: { path: { jobId: Number(job.jobId) } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the job details');
      }
      return data;
    },
  });
  const d = details.data;
  const failures = d?.failures ?? {};
  return (
    <Dialog
      header={agent ? `Agent ${agent.instanceId} (job ${job.jobId})` : `Job ${job.jobId}: ${job.name}`}
      visible
      onHide={onHide}
      className="wide-dialog"
      modal
      draggable={false}
    >
      {details.error && <Message severity="error" text={details.error.message} />}
      {d && (
        <dl className="facts">
          <dt>Status</dt>
          <dd>{d.status}</dd>
          <dt>Created</dt>
          <dd>
            {formatDateTime(d.created)} by {d.creator}
          </dd>
          <dt>Started</dt>
          <dd>{formatDateTime(d.startTime) || '—'}</dd>
          <dt>Ended</dt>
          <dd>{formatDateTime(d.endTime) || '—'}</dd>
          <dt>Users</dt>
          <dd>
            {d.activeUsers ?? 0} active of {d.totalUsers ?? 0}
          </dd>
          <dt>Failures</dt>
          <dd>
            {failures.total ?? 0} in all: {failures.kills ?? 0} kills, {failures.aborts ?? 0} aborts,{' '}
            {failures.restarts ?? 0} restarts, {failures.gotos ?? 0} gotos, {failures.skips ?? 0} skips,{' '}
            {failures.skipGroups ?? 0} group skips
          </dd>
        </dl>
      )}
      <JobCharts jobId={Number(job.jobId)} instanceId={agent?.instanceId} />
    </Dialog>
  );
}

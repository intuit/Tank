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
import { useMemo, useState } from 'react';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { formatDateTime } from '../../format';
import { useNotify } from '../../notify';
import { useSession } from '../../session';

type JobTree = Schemas['JobTree'];
type JobNode = Schemas['JobNode'];
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
  | { kind: 'job'; job: JobNode }
  | { kind: 'agent'; agent: AgentNode; job: JobNode };

/**
 * Jobs and their agents, live (JobTreeTableBean and projectjobqueue.xhtml). Figures come from the
 * controller serving the request, as in the JSF UI.
 */
export function JobQueue({ projectId }: { projectId?: number }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [includeFinished, setIncludeFinished] = useState(false);
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [detailsFor, setDetailsFor] = useState<JobNode>();
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
    mutationFn: async ({ target, action }: { target: Row; action: string }) => {
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

  const jobs = useMemo(() => {
    const data = tree.data;
    if (!data) {
      return [];
    }
    return [...(data.projects ?? []).flatMap((p) => p.jobs ?? []), ...(data.otherJobs ?? [])];
  }, [tree.data]);

  const nodes: TreeNode[] = jobs.map((job) => ({
    key: `job-${job.jobId}`,
    data: { kind: 'job', job } satisfies Row,
    children: (job.agents ?? []).map((agent) => ({
      key: `agent-${agent.instanceId}`,
      data: { kind: 'agent', agent, job } satisfies Row,
    })),
  }));

  const confirmThen = (target: Row, action: string, label: string) => {
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
    const allowed: Actions = (row.kind === 'job' ? row.job.actions : row.agent.actions) ?? {};
    const name = row.kind === 'job' ? row.job.name : row.agent.instanceId;
    return (
      <div className="row-actions">
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
    row.kind === 'job' ? (
      <Button label={row.job.name} link className="link-cell" onClick={() => setDetailsFor(row.job)} />
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
          emptyMessage={includeFinished ? 'No jobs for this project yet' : 'No queued or running jobs. Turn on "Show finished jobs" to see past ones.'}
          loading={tree.isPending}
          tableStyle={{ minWidth: '56rem' }}
          className="p-treetable-sm job-tree"
        >
          <Column field="name" header="Name" expander body={(n: TreeNode) => nameCell(n.data as Row)} style={{ minWidth: '12rem' }} />
          <Column header="ID" body={(n: TreeNode) => ((n.data as Row).kind === 'job' ? (n.data as Row).job.jobId : '')} style={{ width: '4rem' }} />
          <Column header="Status" body={(n: TreeNode) => statusCell(n.data as Row)} style={{ minWidth: '11rem' }} />
          <Column header="Region" body={(n: TreeNode) => ((n.data as Row).kind === 'agent' ? (n.data as { agent: AgentNode }).agent.region : '')} style={{ minWidth: '9rem' }} />
          <Column header="Users" body={(n: TreeNode) => usersCell(n.data as Row)} style={{ whiteSpace: 'nowrap' }} />
          <Column header="TPS" body={(n: TreeNode) => figures(n.data as Row).tps ?? 0} />
          <Column header="Failures" body={(n: TreeNode) => figures(n.data as Row).failures?.total ?? 0} />
          <Column header="Started" body={(n: TreeNode) => formatDateTime(figures(n.data as Row).startTime)} style={{ whiteSpace: 'nowrap' }} />
          <Column body={(n: TreeNode) => actionsFor(n.data as Row)} />
        </TreeTable>
      )}

      {detailsFor && <JobDetailsDialog job={detailsFor} onHide={() => setDetailsFor(undefined)} />}
    </div>
  );
}

function figures(row: Row): JobNode | AgentNode {
  return row.kind === 'job' ? row.job : row.agent;
}

function statusCell(row: Row) {
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
export function severityOf(status: string | undefined): 'success' | 'info' | 'warning' | 'danger' | undefined {
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
      // Completed, Stopped, Deleted, shutting_down, unknown
      return undefined;
  }
}

function formatTime(iso: string): string {
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? '' : date.toLocaleTimeString();
}

/** The validated job's summary and failure counts (GET /v2/jobs/{id}/details) */
function JobDetailsDialog({ job, onHide }: { job: JobNode; onHide: () => void }) {
  const { client } = useSession();
  const details = useQuery({
    queryKey: ['jobs', job.jobId, 'details'],
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
    <Dialog header={`Job ${job.jobId}: ${job.name}`} visible onHide={onHide} className="form-dialog" modal draggable={false}>
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
    </Dialog>
  );
}

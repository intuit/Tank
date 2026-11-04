import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputSwitch } from 'primereact/inputswitch';
import { Message } from 'primereact/message';
import { MultiSelect } from 'primereact/multiselect';
import { SelectButton } from 'primereact/selectbutton';
import { Tag } from 'primereact/tag';
import { TreeTable } from 'primereact/treetable';
import type { TreeNode } from 'primereact/treenode';
import { useEffect, useMemo, useRef, useState, type CSSProperties, type ReactNode } from 'react';
import { Link } from 'react-router';
import { contextPath, type Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { formatDateTime, htmlToText } from '../../format';
import { useTablePreferences } from '../../hooks/useTablePreferences';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { AgentHeatMap } from './AgentHeatMap';
import { JobCards, useTpsHistory } from './JobCards';
import { JobCharts } from './JobCharts';
import {
  agentSummary,
  formatCount,
  formatTime,
  severityOf,
  stripeOf,
  type AgentNode,
  type JobNode,
  type JobTree,
  type Row,
  type Target,
} from './jobModel';
import { JobTimeline } from './JobTimeline';
import { RegionSummary } from './RegionSummary';

type Actions = Schemas['Actions'];

/** The server asks for no faster than this (GET /v2/jobs/tree), as JobTreeTableBean's MIN_REFRESH */
export const REFRESH_MS = 10_000;

/** Refresh intervals to choose from, in seconds; 0 turns refreshing off, as an empty JSF interval does */
export const REFRESH_CHOICES = [10, 15, 30, 60, 120, 0];
const REFRESH_STORAGE_KEY = 'tank.jobs.refreshSeconds';

/** The interval this browser last chose; storage can be missing or blocked */
function storedRefreshSeconds(): number {
  try {
    const stored = Number(localStorage.getItem(REFRESH_STORAGE_KEY) ?? NaN);
    return REFRESH_CHOICES.includes(stored) ? stored : REFRESH_MS / 1000;
  } catch {
    return REFRESH_MS / 1000;
  }
}

function storeRefreshSeconds(seconds: number) {
  try {
    localStorage.setItem(REFRESH_STORAGE_KEY, String(seconds));
  } catch {
    // the choice lasts only for this page
  }
}

/** Ways to look at the queue; the table is the JSF tree's */
export const VIEWS = [
  { value: 'table', label: 'Table', icon: 'pi pi-list' },
  { value: 'heatmap', label: 'Heat map', icon: 'pi pi-th-large' },
  { value: 'jobs', label: 'Jobs', icon: 'pi pi-clone' },
  { value: 'regions', label: 'Regions', icon: 'pi pi-globe' },
  { value: 'timeline', label: 'Timeline', icon: 'pi pi-calendar' },
] as const;
type View = (typeof VIEWS)[number]['value'];
const VIEW_STORAGE_KEY = 'tank.jobs.view';

/** The view this browser last chose; storage can be missing or blocked */
function storedView(): View {
  try {
    const stored = localStorage.getItem(VIEW_STORAGE_KEY);
    return VIEWS.find((v) => v.value === stored)?.value ?? 'table';
  } catch {
    return 'table';
  }
}

function storeView(view: View) {
  try {
    localStorage.setItem(VIEW_STORAGE_KEY, view);
  } catch {
    // the choice lasts only for this page
  }
}

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
  const [refreshSeconds, setRefreshSeconds] = useState(storedRefreshSeconds);
  const [view, setView] = useState(storedView);
  const preferences = useTablePreferences('jobs');
  // the timeline is about the past as much as the present
  const withFinished = includeFinished || view === 'timeline';
  const queryKey = ['jobs', 'tree', { projectId, includeFinished: withFinished }] as const;

  const tree = useQuery({
    queryKey,
    queryFn: async ({ signal }): Promise<JobTree> => {
      const { data, error, response } = await client.GET('/v2/jobs/tree', {
        params: { query: { projectId, includeFinished: withFinished } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the job queue');
      }
      return data;
    },
    refetchInterval: refreshSeconds ? refreshSeconds * 1000 : false,
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

  const tpsHistory = useTpsHistory(tree.data);
  const nodes = useMemo(() => (tree.data ? toNodes(tree.data, projectId === undefined) : []), [tree.data, projectId]);

  // Projects start expanded so their jobs show; agents start collapsed
  const expandedOnce = useRef(false);
  useEffect(() => {
    if (!expandedOnce.current && tree.data && projectId === undefined) {
      expandedOnce.current = true;
      setExpanded(Object.fromEntries(nodes.map((n) => [n.key as string, true])));
    }
  }, [tree.data, nodes, projectId]);

  // Saved column visibility and widths (the JSF tablePrefs); columns stay shown if they can't load
  const prefOf = (key: string | undefined) => preferences.columns.find((c) => c.colName === key);
  const shown = (key: string | undefined) => prefOf(key)?.visible !== false;
  const widthOf = (key: string | undefined) => (prefOf(key)?.size ? `${prefOf(key)!.size}px` : undefined);
  const hideable = preferences.columns.filter((c) => c.hideable && COLUMNS.some((col) => col.pref === c.colName));

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

  const viewProps = (data: JobTree) => ({
    tree: data,
    actionsFor,
    onDetails: (job: JobNode, agent?: AgentNode) => setDetailsFor({ job, agent }),
  });

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
      <Button
        label={row.job.name}
        link
        className="link-cell"
        tooltip="Job details"
        tooltipOptions={{ position: 'top', showDelay: 500 }}
        onClick={() => setDetailsFor({ job: row.job })}
      />
    ) : (
      <span className="agent-id">{row.agent.instanceId}</span>
    );

  return (
    <div className="job-queue-views">
      <div className="tab-toolbar job-toolbar">
        <SelectButton
          value={view}
          options={[...VIEWS]}
          optionLabel="label"
          optionValue="value"
          itemTemplate={(option: (typeof VIEWS)[number]) => (
            <>
              <i className={option.icon} aria-hidden />
              <span>{option.label}</span>
            </>
          )}
          onChange={(e) => {
            if (e.value) {
              setView(e.value as View);
              storeView(e.value as View);
            }
          }}
          allowEmpty={false}
          aria-label="View"
          className="job-view-switch"
        />
        <div className="field-inline">
          <InputSwitch
            inputId="include-finished"
            checked={withFinished}
            disabled={view === 'timeline'}
            onChange={(e) => setIncludeFinished(!!e.value)}
          />
          <label htmlFor="include-finished">Show finished jobs</label>
        </div>
        {tree.data?.generatedAt && (
          <span className="field-help tab-toolbar-end">Updated {formatTime(tree.data.generatedAt)}</span>
        )}
        <Button
          label="Refresh"
          icon="pi pi-refresh"
          text
          size="small"
          loading={tree.isFetching}
          onClick={() => void tree.refetch()}
        />
        <div className="field-inline">
          <label htmlFor="refresh-interval">Refresh every</label>
          <Dropdown
            inputId="refresh-interval"
            value={refreshSeconds}
            options={REFRESH_CHOICES.map((seconds) => ({ label: seconds ? `${seconds}s` : 'Off', value: seconds }))}
            onChange={(e) => {
              setRefreshSeconds(e.value as number);
              storeRefreshSeconds(e.value as number);
            }}
            className="p-inputtext-sm"
          />
        </div>
        {view === 'table' && (
        <MultiSelect
          value={hideable.filter((c) => c.visible).map((c) => c.colName)}
          options={hideable.map((c) => ({ label: c.displayName, value: c.colName }))}
          onChange={(e) => preferences.setVisible(e.value as string[])}
          placeholder="Columns"
          selectedItemsLabel="Columns"
          maxSelectedLabels={0}
          aria-label="Visible columns"
          dropdownIcon="pi pi-table"
          className="list-columns"
          disabled={hideable.length === 0}
        />
        )}
      </div>

      {tree.error && !tree.data ? (
        <Message severity="error" text={tree.error.message} className="tab-message" />
      ) : view !== 'table' ? (
        !tree.data ? (
          <p className="field-help">Loading jobs…</p>
        ) : view === 'heatmap' ? (
          <AgentHeatMap {...viewProps(tree.data)} />
        ) : view === 'jobs' ? (
          <JobCards {...viewProps(tree.data)} tpsHistory={tpsHistory} />
        ) : view === 'regions' ? (
          <RegionSummary {...viewProps(tree.data)} />
        ) : (
          <JobTimeline {...viewProps(tree.data)} />
        )
      ) : (
        <TreeTable
          value={nodes}
          expandedKeys={expanded}
          rowClassName={(n) => ({ [`job-tree-${(n.data as Row).kind}`]: true })}
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
          resizableColumns
          columnResizeMode="expand"
          onColumnResizeEnd={(e) => {
            const key = (e.column.props.columnKey ?? '') as string;
            if (key) {
              preferences.setWidth(key, e.element.offsetWidth);
            }
          }}
          className="p-treetable-sm job-tree"
        >
          {COLUMNS.filter((c) => shown(c.pref)).map((c) => (
            <Column
              key={c.pref ?? c.header ?? 'actions'}
              columnKey={c.pref}
              field={c.expander ? 'name' : undefined}
              header={c.header}
              expander={c.expander}
              body={(n: TreeNode) => (c.expander ? nameCell(n.data as Row) : c.actions ? actionsFor(n.data as Row) : c.body!(n.data as Row))}
              className={c.className}
              style={{ ...c.style, width: widthOf(c.pref) }}
            />
          ))}
        </TreeTable>
      )}

      {detailsFor && (
        <JobDetailsDialog job={detailsFor.job} agent={detailsFor.agent} onHide={() => setDetailsFor(undefined)} />
      )}
    </div>
  );
}

interface JobColumn {
  /** The column's key in the user's saved jobs table settings (TableColumnDefaults.JOBS_COL_PREFS) */
  pref?: string;
  header?: string;
  body?: (row: Row) => ReactNode;
  style?: CSSProperties;
  /** For both the header and body cells: num-col right-aligns figures, date-col keeps times quiet */
  className?: string;
  expander?: boolean;
  actions?: boolean;
}

const COLUMNS: JobColumn[] = [
  { pref: 'nameColumn', header: 'Name', expander: true, className: 'name-col', style: { minWidth: '12rem' } },
  { pref: 'idColumn', header: 'ID', body: idOf, className: 'id-col', style: { width: '4rem' } },
  { pref: 'statusColumn', header: 'Status', body: statusCell, style: { minWidth: '11rem' } },
  { header: 'WS State', body: wsStateCell, style: { whiteSpace: 'nowrap' } },
  { pref: 'regionColumn', header: 'Region', body: regionOf, style: { minWidth: '7rem' } },
  { pref: 'activeUsersColumn', header: 'Active users', body: (row) => formatCount(figures(row).activeUsers), className: 'num-col' },
  { pref: 'totalUsersColumn', header: 'Target users', body: targetUsers, className: 'num-col' },
  { pref: 'tpsColumn', header: 'TPS', body: (row) => formatCount(figures(row).tps), className: 'num-col' },
  { pref: 'validationColumn', header: 'Failures', body: failuresCell, className: 'num-col' },
  { pref: 'startTimeColumn', header: 'Started', body: (row) => dateCell(figures(row).startTime), className: 'date-col' },
  { pref: 'endTimeColumn', header: 'Completed', body: (row) => dateCell(endTimeOf(row)), className: 'date-col' },
  { pref: 'actionsColumn', actions: true, className: 'actions-col' },
];

/** A date and time that, when the table is tight, breaks between the two rather than anywhere */
function dateCell(iso: string | undefined) {
  const text = formatDateTime(iso);
  const split = text.lastIndexOf(', ');
  return split < 0 ? (
    text
  ) : (
    <>
      <span className="nowrap">{text.slice(0, split + 1)}</span> <span className="nowrap">{text.slice(split + 2)}</span>
    </>
  );
}

/** Failures stand out once there are any */
function failuresCell(row: Row) {
  const total = figures(row).failures?.total ?? 0;
  return total > 0 ? <span className="failure-count">{formatCount(total)}</span> : formatCount(total);
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

type Figures = Pick<JobNode, 'activeUsers' | 'totalUsers' | 'tps' | 'failures' | 'startTime' | 'endTime'>;

function figures(row: Row): Figures {
  return row.kind === 'project' ? row.project : row.kind === 'job' ? row.job : row.agent;
}

/** Job IDs only; a project's ID would read as one (as in the JSF tree) */
function idOf(row: Row) {
  return row.kind === 'job' ? row.job.jobId : '';
}

/** Projects have no end time of their own */
function endTimeOf(row: Row) {
  return row.kind === 'project' ? undefined : figures(row).endTime;
}

/** An agent's WebSocket state, labelled as JobNodeBean.getWsStateDisplay labels it */
function wsStateCell(row: Row) {
  if (row.kind !== 'agent' || !row.agent.wsState) {
    return '';
  }
  const { wsState, transferProgress, status } = row.agent;
  const closed = ['completed', 'stopped', 'terminated', 'replaced', 'successful'].includes(status?.toLowerCase() ?? '');
  const [label, severity]: [string, ReturnType<typeof severityOf>] =
    wsState === 'connected' || wsState === 'running'
      ? ['Connected', 'success']
      : wsState === 'registered'
        ? ['Registered', 'info']
        : wsState === 'bootstrap_transferring' || wsState === 'bootstrap_sent' || wsState === 'transferring'
          ? [transferProgress ? `Setting Up (${transferProgress})` : 'Setting Up', 'info']
          : wsState === 'ready'
            ? ['Ready', 'success']
            : wsState === 'disconnected'
              ? closed
                ? ['Closed', 'secondary']
                : ['Disconnected', 'danger']
              : [wsState, 'secondary'];
  const settingUp = wsState === 'bootstrap_transferring' || wsState === 'bootstrap_sent' || wsState === 'transferring';
  return <Tag value={label} severity={severity} className={settingUp ? stripeOf('starting') : undefined} />;
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
          className={running > 0 ? stripeOf('running') : undefined}
        />
      </span>
    );
  }
  if (row.kind === 'agent') {
    const { agent } = row;
    return (
      <span className="status-cell">
        <Tag value={agent.status} severity={severityOf(agent.status)} className={stripeOf(agent.status)} />
        {agent.lastSeenMs && <span className="field-help">seen {formatTime(new Date(agent.lastSeenMs).toISOString())}</span>}
      </span>
    );
  }
  const { job } = row;
  const summary = agentSummary(job);
  return (
    <span className="status-cell">
      <Tag value={job.status} severity={severityOf(job.status)} className={stripeOf(job.status)} />
      {summary && <span className="field-help">{summary}</span>}
    </span>
  );
}

/** A nonlinear job has no fixed target, so JSF shows its steady state instead, which the tree doesn't carry */
function targetUsers(row: Row) {
  if (row.kind === 'job' && row.job.incrementStrategy?.toLowerCase().startsWith('non')) {
    return '';
  }
  return formatCount(figures(row).totalUsers);
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
      {d?.detailsHtml && (
        <details className="job-recorded-details">
          <summary>Job details as queued</summary>
          {/* the server's HTML report, shown as text: it carries user-entered names and values */}
          <pre className="job-details-text">{htmlToText(d.detailsHtml)}</pre>
        </details>
      )}
      <JobCharts jobId={Number(job.jobId)} instanceId={agent?.instanceId} />
    </Dialog>
  );
}

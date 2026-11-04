import { Tag } from 'primereact/tag';
import { useRef } from 'react';
import { formatDateTime } from '../../format';
import {
  agentHealth,
  agentSummary,
  formatCount,
  jobEntries,
  secondsSinceSeen,
  severityOf,
  SILENT_SECONDS,
  stripeOf,
  type AgentNode,
  type JobTree,
  type JobViewProps,
} from './jobModel';

/** Refreshes of TPS kept per job for the cards' trend lines */
const HISTORY_LENGTH = 60;

/** Each job's TPS at every refresh since the page opened, oldest first */
export type TpsHistory = ReadonlyMap<string, readonly number[]>;

/**
 * Collects each job's TPS from every refresh of the tree, so the cards can draw a trend without a
 * request per job. It lasts as long as the page.
 */
export function useTpsHistory(tree: JobTree | undefined): TpsHistory {
  const history = useRef(new Map<string, number[]>());
  const recorded = useRef<string | undefined>(undefined);
  // once per refresh, keyed by when the server built the tree
  if (tree?.generatedAt && tree.generatedAt !== recorded.current) {
    recorded.current = tree.generatedAt;
    for (const { job } of jobEntries(tree)) {
      const key = String(job.jobId);
      const samples = [...(history.current.get(key) ?? []), job.tps ?? 0];
      history.current.set(key, samples.slice(-HISTORY_LENGTH));
    }
  }
  return history.current;
}

const TIME = new Intl.DateTimeFormat(undefined, { timeStyle: 'short' });

/** Finished jobs fade, so what is live reads first */
const FINISHED = ['completed', 'stopped', 'aborted', 'deleted'];

/** One card per job: users against target, TPS and its trend, failures, its agents at a glance, and its actions */
export function JobCards({ tree, actionsFor, onDetails, tpsHistory }: JobViewProps & { tpsHistory: TpsHistory }) {
  const entries = jobEntries(tree);
  if (entries.length === 0) {
    return <p className="job-view-empty">No queued or running jobs. Turn on &quot;Show finished jobs&quot; to see past ones.</p>;
  }
  return (
    <>
      <div className="job-cards">
        {entries.map(({ job, project }) => {
          const target = job.totalUsers ?? 0;
          const active = job.activeUsers ?? 0;
          const failures = job.failures?.total ?? 0;
          const finished = FINISHED.includes(job.status?.toLowerCase() ?? '');
          const agents = job.agents ?? [];
          return (
            <article
              key={job.jobId}
              className={finished ? 'job-view-card job-card job-card-finished' : 'job-view-card job-card'}
              aria-label={`Job ${job.name}`}
            >
              <div className="job-card-header">
                <div className="job-card-title">
                  <button type="button" className="job-view-name" onClick={() => onDetails(job)}>
                    {job.name}
                  </button>
                  <span className="field-help">
                    #{job.jobId}
                    {project ? ` · ${project.name}` : ''}
                  </span>
                </div>
                <Tag value={job.status} severity={severityOf(job.status)} className={stripeOf(job.status)} />
              </div>

              <div className="job-card-users">
                <div className="job-card-users-label">
                  <span>Users</span>
                  <span>
                    <strong>{formatCount(active)}</strong> / {formatCount(target)}
                  </span>
                </div>
                <div
                  className="job-card-bar"
                  role="progressbar"
                  aria-label="Active users"
                  aria-valuemin={0}
                  aria-valuemax={target}
                  aria-valuenow={active}
                >
                  <div style={{ width: `${target ? Math.min(100, (100 * active) / target) : 0}%` }} />
                </div>
              </div>

              <div className="job-card-figures">
                <div>
                  <span className="job-stat-label">TPS</span>
                  <span className="job-card-figure">{formatCount(job.tps)}</span>
                </div>
                <Sparkline values={tpsHistory.get(String(job.jobId)) ?? []} />
                <div className="job-card-failures">
                  <span className="job-stat-label">Failures</span>
                  <span className={failures > 0 ? 'job-card-figure failure-count' : 'job-card-figure'}>
                    {formatCount(failures)}
                  </span>
                </div>
              </div>

              <div className="job-card-agents">
                {agents.length > 0 && (
                  <div className="agent-pips" aria-hidden>
                    {agents.map((agent) => (
                      <span key={agent.instanceId} className={`agent-pip agent-pip-${pipOf(agent, tree.generatedAt)}`} />
                    ))}
                  </div>
                )}
                <span className="field-help">
                  {agents.length === 0 && finished ? 'No agents' : agentLine(agents, agentSummary(job), tree.generatedAt)}
                </span>
              </div>

              <div className="job-card-footer">
                <span className="field-help">{timesOf(job.startTime, job.endTime)}</span>
                <span className="job-view-actions">{actionsFor({ kind: 'job', job })}</span>
              </div>
            </article>
          );
        })}
      </div>
      <PipLegend />
    </>
  );
}

/** The state an agent's pip shows; colour with a legend, and the line under the pips says it in words */
function pipOf(agent: AgentNode, generatedAt: string | undefined) {
  const health = agentHealth(agent);
  if (health !== 'live') {
    return health === 'ready' ? 'setup' : health;
  }
  const paused = ['paused', 'ramppaused', 'stopping'].includes(agent.status?.toLowerCase() ?? '');
  return paused || (secondsSinceSeen(agent, generatedAt) ?? 0) > SILENT_SECONDS ? 'warn' : 'live';
}

function agentLine(agents: AgentNode[], summary: string, generatedAt: string | undefined) {
  if (agents.length === 0) {
    return 'No agents yet';
  }
  const lost = agents.filter((a) => agentHealth(a) === 'lost').length;
  const silent = agents.filter(
    (a) => agentHealth(a) === 'live' && (secondsSinceSeen(a, generatedAt) ?? 0) > SILENT_SECONDS,
  ).length;
  return [summary, silent ? `${silent} silent over ${SILENT_SECONDS}s` : '', lost ? `${lost} lost` : '']
    .filter(Boolean)
    .join(' · ');
}

function timesOf(start: string | undefined, end: string | undefined) {
  if (!start) {
    return 'Not started';
  }
  const minutes = Math.round(((end ? Date.parse(end) : Date.now()) - Date.parse(start)) / 60_000);
  const span = Number.isNaN(minutes) ? '' : ` · ${minutes < 120 ? `${minutes} min` : `${Math.round(minutes / 60)} h`}`;
  if (!end) {
    return `Started ${formatDateTime(start)}${span}`;
  }
  const sameDay = new Date(start).toDateString() === new Date(end).toDateString();
  return `${formatDateTime(start)} – ${sameDay ? TIME.format(new Date(end)) : formatDateTime(end)}${span}`;
}

/** TPS across the refreshes seen so far; nothing until there are two to join */
function Sparkline({ values }: { values: readonly number[] }) {
  if (values.length < 2 || values.every((v) => v === 0)) {
    return <span className="job-card-spark" />;
  }
  const max = Math.max(1, ...values);
  const points = values.map((v, i) => `${((i * 120) / (values.length - 1)).toFixed(1)},${(34 - (v / max) * 30).toFixed(1)}`);
  return (
    <svg
      className="job-card-spark"
      viewBox="0 0 120 36"
      preserveAspectRatio="none"
      role="img"
      aria-label={`TPS over the last ${values.length} refreshes`}
    >
      <polyline points={points.join(' ')} vectorEffect="non-scaling-stroke" />
    </svg>
  );
}

export function PipLegend() {
  return (
    <div className="job-view-legend">
      <span>
        <span className="agent-pip agent-pip-live" /> Running
      </span>
      <span>
        <span className="agent-pip agent-pip-setup" /> Starting or ready
      </span>
      <span>
        <span className="agent-pip agent-pip-warn" /> Paused or silent over {SILENT_SECONDS}s
      </span>
      <span>
        <span className="agent-pip agent-pip-lost" /> Lost
      </span>
      <span>
        <span className="agent-pip agent-pip-done" /> Finished
      </span>
    </div>
  );
}

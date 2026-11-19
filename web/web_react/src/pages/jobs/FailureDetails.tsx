import { Button } from 'primereact/button';
import { Tag } from 'primereact/tag';
import { useRef } from 'react';
import type { Schemas } from '../../api/client';
import { formatCount, isFinished, jobEntries, severityOf, type AgentNode, type JobNode, type JobTree } from './jobModel';

type Failures = Schemas['Failures'];

/**
 * The counters an agent keeps, one per "On failure" action (TestPlanRunner). A validation set to
 * "Continue to next request" counts in none of them.
 */
export const FAILURE_KINDS = [
  { key: 'aborts', label: 'Aborts', action: 'Abort script, goto next script' },
  { key: 'skipGroups', label: 'Group skips', action: 'Abort script group, goto next script group' },
  { key: 'skips', label: 'Skips', action: 'Skip remaining requests in group' },
  { key: 'gotos', label: 'Gotos', action: 'Goto specified group' },
  { key: 'restarts', label: 'Restarts', action: 'Restart user' },
  { key: 'kills', label: 'Kills', action: 'Terminate user (or Restart user once the simulation time is up)' },
] as const satisfies readonly { key: keyof Failures; label: string; action: string }[];

/** Refreshes of failure totals kept per job and agent: five minutes at the fastest refresh */
const HISTORY_LENGTH = 30;

/** How far back "recent" failures reach */
const RECENT_MS = 60_000;

interface Sample {
  at: number;
  total: number;
}

/** Each job's and agent's failure total at every refresh since the page opened, oldest first */
export type FailureHistory = ReadonlyMap<string, readonly Sample[]>;

export const jobKey = (job: JobNode) => `job-${job.jobId}`;
export const agentKey = (agent: AgentNode) => `agent-${agent.instanceId}`;

/**
 * Collects failure totals from every refresh of the tree, so the details can say how many are new
 * without a request of their own. It lasts as long as the page.
 */
export function useFailureHistory(tree: JobTree | undefined): FailureHistory {
  const history = useRef(new Map<string, Sample[]>());
  const recorded = useRef<string | undefined>(undefined);
  // once per refresh, keyed by when the server built the tree
  if (tree?.generatedAt && tree.generatedAt !== recorded.current) {
    recorded.current = tree.generatedAt;
    const at = Date.parse(tree.generatedAt);
    const record = (key: string, total: number | undefined) => {
      const samples = [...(history.current.get(key) ?? []), { at, total: total ?? 0 }];
      history.current.set(key, samples.slice(-HISTORY_LENGTH));
    };
    for (const { job } of jobEntries(tree)) {
      record(jobKey(job), job.failures?.total);
      for (const agent of job.agents ?? []) {
        record(agentKey(agent), agent.failures?.total);
      }
    }
  }
  return history.current;
}

/** Failures added over about the last minute, and the seconds that covers; undefined before two refreshes */
export function recentFailures(samples: readonly Sample[] | undefined): { added: number; seconds: number } | undefined {
  if (!samples || samples.length < 2) {
    return undefined;
  }
  const newest = samples[samples.length - 1]!;
  // the newest refresh at least a minute old; while the page is younger, its first
  const since = [...samples].reverse().find((s) => s.at <= newest.at - RECENT_MS) ?? samples[0]!;
  return { added: Math.max(0, newest.total - since.total), seconds: Math.round((newest.at - since.at) / 1000) };
}

/** Each kind of failure with its count and share of the total, largest first; kinds with none on one line */
export function FailureBreakdown({ failures }: { failures: Failures | undefined }) {
  const counts = FAILURE_KINDS.map((kind) => ({ ...kind, count: failures?.[kind.key] ?? 0 }));
  const total = failures?.total ?? counts.reduce((sum, c) => sum + c.count, 0);
  const some = counts.filter((c) => c.count > 0).sort((a, b) => b.count - a.count);
  const none = counts.filter((c) => c.count === 0);
  return (
    <div className="failure-breakdown">
      {some.length > 0 && (
        <table className="failure-kinds" aria-label="Failures by On failure action">
          <tbody>
            {some.map((c) => (
              <tr key={c.key}>
                <th scope="row">
                  {c.label}
                  <span className="field-help">{c.action}</span>
                </th>
                <td className="failure-kind-bar" aria-hidden>
                  <span style={{ width: `${total ? (100 * c.count) / total : 0}%` }} />
                </td>
                <td className="num-col">
                  <strong>{formatCount(c.count)}</strong>
                </td>
                <td className="num-col field-help">{share(c.count, total)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {none.length > 0 && (
        <p className="field-help failure-kinds-none">
          {some.length > 0 ? 'None' : 'No failures'}: {none.map((c) => c.label.toLowerCase()).join(', ')}
        </p>
      )}
    </div>
  );
}

/** Which job or agent the failures panel is about */
export type FailuresTarget = { jobId: string; instanceId?: string };

/** Finds the target in the newest tree, so the panel follows each refresh */
export function findFailuresTarget(tree: JobTree | undefined, target: FailuresTarget | undefined) {
  if (!tree || !target) {
    return undefined;
  }
  const job = jobEntries(tree).find((e) => e.job.jobId === target.jobId)?.job;
  const agent = target.instanceId ? job?.agents?.find((a) => a.instanceId === target.instanceId) : undefined;
  return job && (!target.instanceId || agent) ? { job, agent } : undefined;
}

/**
 * A job's or agent's failures (the JSF Validation Errors popup): what the scripts did about them, how
 * many came in the last minute, and for a job, which agents they came from
 */
export function FailuresPanel({
  job,
  agent,
  history,
  onSelect,
  onDetails,
}: {
  job: JobNode;
  agent?: AgentNode;
  history: FailureHistory;
  onSelect: (target: FailuresTarget) => void;
  onDetails: () => void;
}) {
  const failures = agent ? agent.failures : job.failures;
  const total = failures?.total ?? 0;
  const recent = isFinished(job) ? undefined : recentFailures(history.get(agent ? agentKey(agent) : jobKey(job)));
  const agents = agent
    ? []
    : (job.agents ?? []).filter((a) => (a.failures?.total ?? 0) > 0).sort((a, b) => (b.failures?.total ?? 0) - (a.failures?.total ?? 0));
  const shownAgents = agents.slice(0, 5);
  return (
    <div className="failures-panel">
      <div className="failures-panel-header">
        <div>
          <div className="failures-panel-title">{agent ? `Agent ${agent.instanceId}` : `Job ${job.jobId}: ${job.name}`}</div>
          {agent && (
            <Button
              label={`Job ${job.jobId}: ${formatCount(job.failures?.total)} in all`}
              icon="pi pi-arrow-left"
              link
              className="failures-panel-back"
              onClick={() => onSelect({ jobId: job.jobId! })}
            />
          )}
        </div>
        <div className="failures-panel-total">
          <span className={total > 0 ? 'failure-count' : undefined}>{formatCount(total)}</span>
          <span className="field-help">{total === 1 ? 'failure' : 'failures'}</span>
        </div>
      </div>

      {recent && (
        <p className={recent.added > 0 ? 'failures-recent failures-rising' : 'failures-recent'}>
          {recent.added > 0 ? (
            <>
              <i className="pi pi-arrow-up" aria-hidden /> {formatCount(recent.added)} in the last {recent.seconds} s
            </>
          ) : (
            `None in the last ${recent.seconds} s`
          )}
        </p>
      )}

      <FailureBreakdown failures={failures} />

      {shownAgents.length > 0 && (
        <div className="failures-agents">
          <div className="job-stat-label">By agent</div>
          <table>
            <tbody>
              {shownAgents.map((a) => (
                <tr key={a.instanceId}>
                  <td>
                    <Button
                      label={a.instanceId}
                      link
                      className="link-cell agent-id"
                      aria-label={`Failures for agent ${a.instanceId}`}
                      onClick={() => onSelect({ jobId: job.jobId!, instanceId: a.instanceId })}
                    />
                  </td>
                  <td>
                    <Tag value={a.status} severity={severityOf(a.status)} />
                  </td>
                  <td className="num-col">
                    <strong>{formatCount(a.failures?.total)}</strong>
                  </td>
                  <td className="num-col field-help">{share(a.failures?.total ?? 0, total)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {agents.length > shownAgents.length && (
            <p className="field-help">
              and {agents.length - shownAgents.length} more agents with failures
            </p>
          )}
        </div>
      )}

      <p className="field-help failures-note">
        Counts validations that failed, by each request&apos;s <em>On failure</em> action. Failures set to continue
        to the next request aren&apos;t counted.
      </p>
      <div className="failures-panel-footer">
        <Button label={agent ? 'Agent charts' : 'Job details and charts'} icon="pi pi-chart-line" text size="small" onClick={onDetails} />
      </div>
    </div>
  );
}

function share(count: number, total: number) {
  return total > 0 ? `${Math.round((100 * count) / total)}%` : '';
}

import {
  agentHealth,
  formatCount,
  jobEntries,
  regionLabel,
  type AgentNode,
  type JobNode,
  type JobViewProps,
} from './jobModel';

/** States the region bars split agents into, in bar order, each with its legend word */
const STATES = [
  { key: 'live', label: 'running' },
  { key: 'warn', label: 'paused' },
  { key: 'setup', label: 'starting' },
  { key: 'lost', label: 'lost' },
  { key: 'done', label: 'finished' },
] as const;
type State = (typeof STATES)[number]['key'];

function stateOf(agent: AgentNode): State {
  const health = agentHealth(agent);
  if (health === 'ready') {
    return 'setup';
  }
  if (health === 'live' && ['paused', 'ramppaused', 'stopping'].includes(agent.status?.toLowerCase() ?? '')) {
    return 'warn';
  }
  return health;
}

interface Region {
  name: string;
  agents: AgentNode[];
  jobs: { job: JobNode; count: number }[];
}

/** Agents by AWS region: how many in each state, their users and TPS, and the jobs they serve */
export function RegionSummary({ tree, onDetails }: JobViewProps) {
  const regions: Region[] = [];
  for (const { job } of jobEntries(tree)) {
    for (const agent of job.agents ?? []) {
      const name = regionLabel(agent.region);
      let region = regions.find((r) => r.name === name);
      if (!region) {
        region = { name, agents: [], jobs: [] };
        regions.push(region);
      }
      region.agents.push(agent);
      const served = region.jobs.find((j) => j.job === job);
      if (served) {
        served.count += 1;
      } else {
        region.jobs.push({ job, count: 1 });
      }
    }
  }
  regions.sort((a, b) => a.name.localeCompare(b.name));

  if (regions.length === 0) {
    return <p className="job-view-empty">No agents yet. Regions show here once agents start.</p>;
  }

  const sum = (agents: AgentNode[], pick: (a: AgentNode) => number | undefined) =>
    agents.reduce((total, a) => total + (agentHealth(a) === 'live' ? (pick(a) ?? 0) : 0), 0);
  const mostAgents = Math.max(...regions.map((r) => r.agents.length));
  const mostTps = Math.max(1, ...regions.map((r) => sum(r.agents, (a) => a.tps)));

  return (
    <>
      <div className="job-view-card region-table-wrap">
        <table className="region-table">
          <thead>
            <tr>
              <th scope="col">Region</th>
              <th scope="col">Agents by state</th>
              <th scope="col" className="num-col">
                Active users
              </th>
              <th scope="col">TPS</th>
              <th scope="col">Jobs</th>
            </tr>
          </thead>
          <tbody>
            {regions.map((region) => {
              const counts = STATES.map((s) => ({
                ...s,
                count: region.agents.filter((a) => stateOf(a) === s.key).length,
              })).filter((s) => s.count > 0);
              const tps = sum(region.agents, (a) => a.tps);
              return (
                <tr key={region.name}>
                  <th scope="row">{region.name}</th>
                  <td>
                    <div className="region-states" aria-hidden>
                      {counts.map((s) => (
                        <span
                          key={s.key}
                          className={`region-state agent-pip-${s.key}`}
                          style={{ width: `${(100 * s.count) / mostAgents}%` }}
                          title={`${s.count} ${s.label}`}
                        />
                      ))}
                    </div>
                    <span className="field-help">
                      {region.agents.length} {region.agents.length === 1 ? 'agent' : 'agents'} ·{' '}
                      {counts.map((s) => `${s.count} ${s.label}`).join(' · ')}
                    </span>
                  </td>
                  <td className="num-col">{formatCount(sum(region.agents, (a) => a.activeUsers))}</td>
                  <td>
                    <div className="region-tps">
                      <div className="region-tps-track">
                        <div style={{ width: `${(100 * tps) / mostTps}%` }} />
                      </div>
                      <span className="num-col">{formatCount(tps)}</span>
                    </div>
                  </td>
                  <td>
                    <div className="region-jobs">
                      {region.jobs.map(({ job, count }) => (
                        <button key={job.jobId} type="button" className="region-job" onClick={() => onDetails(job)}>
                          {job.name} · {count}
                        </button>
                      ))}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      <div className="job-view-legend region-legend">
        {STATES.map((s) => (
          <span key={s.key}>
            <span className={`agent-pip agent-pip-${s.key}`} /> {s.label[0]!.toUpperCase() + s.label.slice(1)}
          </span>
        ))}
      </div>
    </>
  );
}

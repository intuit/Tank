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

/** The agents in one availability zone, or in no known zone of their region */
interface Zone {
  region: string;
  /** undefined when the agents didn't report one */
  name?: string;
  agents: AgentNode[];
  jobs: { job: JobNode; count: number }[];
}

/**
 * Agents by AWS region, a row per availability zone: how many in each state, their users and TPS, and the
 * jobs they serve
 */
export function RegionSummary({ tree, onDetails }: JobViewProps) {
  const zones: Zone[] = [];
  for (const { job } of jobEntries(tree)) {
    for (const agent of job.agents ?? []) {
      const region = regionLabel(agent.region);
      const name = agent.zone || undefined;
      let zone = zones.find((z) => z.region === region && z.name === name);
      if (!zone) {
        zone = { region, name, agents: [], jobs: [] };
        zones.push(zone);
      }
      zone.agents.push(agent);
      const served = zone.jobs.find((j) => j.job === job);
      if (served) {
        served.count += 1;
      } else {
        zone.jobs.push({ job, count: 1 });
      }
    }
  }
  // by region, then zone, with a region's agents of unknown zone last
  zones.sort(
    (a, b) =>
      a.region.localeCompare(b.region) ||
      (a.name === undefined ? 1 : 0) - (b.name === undefined ? 1 : 0) ||
      (a.name ?? '').localeCompare(b.name ?? ''),
  );

  if (zones.length === 0) {
    return <p className="job-view-empty">No agents yet. Regions show here once agents start.</p>;
  }

  const sum = (agents: AgentNode[], pick: (a: AgentNode) => number | undefined) =>
    agents.reduce((total, a) => total + (agentHealth(a) === 'live' ? (pick(a) ?? 0) : 0), 0);
  const mostAgents = Math.max(...zones.map((z) => z.agents.length));
  const mostTps = Math.max(1, ...zones.map((z) => sum(z.agents, (a) => a.tps)));
  const zonesIn = (region: string) => zones.filter((z) => z.region === region).length;

  return (
    <>
      <div className="job-view-card region-table-wrap">
        <table className="region-table">
          <thead>
            <tr>
              <th scope="col">Region</th>
              <th scope="col">Availability zone</th>
              <th scope="col" className="region-states-col">
                Agents by state
              </th>
              <th scope="col" className="num-col">
                Active users
              </th>
              <th scope="col" className="region-tps-col">
                TPS
              </th>
              <th scope="col">Jobs</th>
            </tr>
          </thead>
          <tbody>
            {zones.map((zone, i) => {
              const counts = STATES.map((s) => ({
                ...s,
                count: zone.agents.filter((a) => stateOf(a) === s.key).length,
              })).filter((s) => s.count > 0);
              const tps = sum(zone.agents, (a) => a.tps);
              const firstInRegion = i === 0 || zones[i - 1]!.region !== zone.region;
              return (
                <tr key={`${zone.region} ${zone.name ?? ''}`}>
                  {firstInRegion && (
                    <th scope="rowgroup" rowSpan={zonesIn(zone.region)} className="region-name">
                      {zone.region}
                    </th>
                  )}
                  <th scope="row">{zone.name ?? <span className="field-help">Unknown zone</span>}</th>
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
                      {zone.agents.length} {zone.agents.length === 1 ? 'agent' : 'agents'} ·{' '}
                      {counts.map((s) => `${s.count} ${s.label}`).join(' · ')}
                    </span>
                  </td>
                  <td className="num-col">{formatCount(sum(zone.agents, (a) => a.activeUsers))}</td>
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
                      {zone.jobs.map(({ job, count }) => (
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

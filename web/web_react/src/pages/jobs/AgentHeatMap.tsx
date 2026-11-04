import { SelectButton } from 'primereact/selectbutton';
import { Tag } from 'primereact/tag';
import { useState } from 'react';
import {
  agentHealth,
  agentSummary,
  formatCount,
  formatTime,
  jobEntries,
  regionLabel,
  secondsSinceSeen,
  severityOf,
  SILENT_SECONDS,
  stripeOf,
  type AgentNode,
  type JobEntry,
  type JobViewProps,
} from './jobModel';
import { formatDateTime } from '../../format';

/** What the tiles can be shaded by: throughput in blue, trouble in orange */
const METRICS = [
  { value: 'tps', label: 'TPS', ramp: 'blue' },
  { value: 'users', label: 'Active users', ramp: 'blue' },
  { value: 'failures', label: 'Failures', ramp: 'orange' },
  { value: 'seen', label: 'Last seen', ramp: 'orange' },
] as const;
type Metric = (typeof METRICS)[number]['value'];

/** The shades of a ramp, lightest (zero) first */
const STEPS = [0, 1, 2, 3, 4];

interface Tile {
  agent: AgentNode;
  entry: JobEntry;
  seen: number | undefined;
}

/**
 * Every agent as a tile, job by job and region by region, shaded by the figure the user picks, so an
 * agent that is slow, failing or silent stands out among dozens. A tile opens the agent's figures
 * and actions beside the map.
 */
export function AgentHeatMap({ tree, actionsFor, onDetails }: JobViewProps) {
  const [metric, setMetric] = useState<Metric>('tps');
  const [selectedId, setSelectedId] = useState<string>();
  const ramp = METRICS.find((m) => m.value === metric)!.ramp;

  const entries = jobEntries(tree).filter((e) => (e.job.agents ?? []).length > 0);
  const tiles: Tile[] = entries.flatMap((entry) =>
    (entry.job.agents ?? []).map((agent) => ({ agent, entry, seen: secondsSinceSeen(agent, tree.generatedAt) })),
  );
  const valueOf = (tile: Tile) =>
    metric === 'tps'
      ? (tile.agent.tps ?? 0)
      : metric === 'users'
        ? (tile.agent.activeUsers ?? 0)
        : metric === 'failures'
          ? (tile.agent.failures?.total ?? 0)
          : (tile.seen ?? 0);
  const live = tiles.filter((t) => agentHealth(t.agent) === 'live');
  // shades spread over the live agents' range, so a slow or hot agent stands out from its peers; zero
  // keeps the lightest shade of its own
  const nonZero = live.map(valueOf).filter((v) => v > 0);
  const min = nonZero.length ? Math.min(...nonZero) : 0;
  const max = nonZero.length ? Math.max(...nonZero) : 0;
  const shade = (value: number) =>
    value <= 0 ? 0 : max === min ? 4 : Math.min(4, 1 + Math.floor((3.999 * (value - min)) / (max - min)));
  const label = (value: number) => (metric === 'seen' ? `${formatCount(value)}s` : formatCount(value));

  const lost = tiles.filter((t) => agentHealth(t.agent) === 'lost').length;
  const silent = live.filter((t) => (t.seen ?? 0) > SILENT_SECONDS).length;
  const sum = (pick: (t: Tile) => number | undefined) => live.reduce((total, t) => total + (pick(t) ?? 0), 0);
  const selected = tiles.find((t) => t.agent.instanceId === selectedId);

  if (entries.length === 0) {
    return <p className="job-view-empty">No agents yet. Jobs show here once their agents start.</p>;
  }

  return (
    <div className="heat-map">
      <div className="job-stats">
        <Stat label="Jobs with agents" value={formatCount(entries.length)} />
        <Stat label="Agents" value={formatCount(tiles.length)} sub={`${formatCount(live.length)} reporting`} />
        <Stat label="Active users" value={formatCount(sum((t) => t.agent.activeUsers))} />
        <Stat label="TPS" value={formatCount(sum((t) => t.agent.tps))} />
        <Stat
          label="Need attention"
          value={formatCount(lost + silent)}
          sub={`${lost} lost · ${silent} silent over ${SILENT_SECONDS}s`}
          alert={lost + silent > 0}
        />
      </div>

      <div className="heat-map-controls">
        <span className="heat-map-controls-label" id="heat-metric-label">
          Shade by
        </span>
        <SelectButton
          value={metric}
          options={[...METRICS]}
          optionLabel="label"
          optionValue="value"
          onChange={(e) => e.value && setMetric(e.value as Metric)}
          allowEmpty={false}
          aria-labelledby="heat-metric-label"
          className="p-selectbutton-sm"
        />
        <div className="heat-legend" aria-label="Legend">
          <span className={`heat-swatch heat-${ramp}-0`} />
          <span>0</span>
          <span>{label(min)}</span>
          {STEPS.slice(1).map((step) => (
            <span key={step} className={`heat-swatch heat-${ramp}-${step}`} />
          ))}
          <span>{label(max)}</span>
          <span className="heat-swatch heat-lost" />
          <span>Lost</span>
          <span className="heat-swatch heat-setup" />
          <span>Setting up</span>
          <span className="heat-swatch heat-ready" />
          <span>Ready</span>
        </div>
      </div>

      <div className="heat-map-body">
        <div className="heat-map-jobs">
          {entries.map((entry) => {
            const { job } = entry;
            const agents = tiles.filter((t) => t.entry === entry);
            const regions = [...new Set(agents.map((t) => regionLabel(t.agent.region)))];
            return (
              <section key={job.jobId} className="job-view-card" aria-label={`Job ${job.name}`}>
                <div className="job-view-card-header">
                  <button type="button" className="job-view-name" onClick={() => onDetails(job)}>
                    {job.name}
                  </button>
                  <span className="field-help">
                    #{job.jobId}
                    {entry.project ? ` · ${entry.project.name}` : ''}
                  </span>
                  <Tag value={job.status} severity={severityOf(job.status)} className={stripeOf(job.status)} />
                  <span className="field-help">
                    {agentSummary(job)} · {formatCount(job.activeUsers)} / {formatCount(job.totalUsers)} users ·{' '}
                    {formatCount(job.tps)} TPS
                  </span>
                  <span className="job-view-actions">{actionsFor({ kind: 'job', job })}</span>
                </div>
                {regions.map((region) => {
                  const inRegion = agents.filter((t) => regionLabel(t.agent.region) === region);
                  return (
                    <div key={region} className="heat-region">
                      <span className="heat-region-label">
                        {region} · {inRegion.length} {inRegion.length === 1 ? 'agent' : 'agents'}
                      </span>
                      <div className="heat-tiles">
                        {inRegion.map((tile) => {
                          const health = agentHealth(tile.agent);
                          const value = valueOf(tile);
                          const id = tile.agent.instanceId ?? '';
                          const className =
                            health === 'live'
                              ? `heat-${ramp}-${shade(value)}`
                              : health === 'done'
                                ? 'heat-done'
                                : `heat-${health}`;
                          const text =
                            health === 'live'
                              ? label(value)
                              : health === 'lost'
                                ? 'Lost'
                                : health === 'setup'
                                  ? (tile.agent.transferProgress ?? 'Setup')
                                  : health === 'ready'
                                    ? 'Ready'
                                    : 'Done';
                          return (
                            <button
                              key={id}
                              type="button"
                              className={`heat-tile ${className}`}
                              aria-pressed={id === selectedId}
                              aria-label={tileDescription(tile)}
                              title={tileDescription(tile)}
                              onClick={() => setSelectedId(id === selectedId ? undefined : id)}
                            >
                              <span className="heat-tile-id">{id.slice(-4)}</span>
                              <span className="heat-tile-value">{text}</span>
                            </button>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </section>
            );
          })}
        </div>

        <aside className="job-view-card heat-map-panel" aria-label="Selected agent">
          <span className="heat-region-label">Selected agent</span>
          {selected ? (
            <>
              <div>
                <div className="heat-panel-id">{selected.agent.instanceId}</div>
                <div className="field-help">
                  {selected.entry.job.name} · {regionLabel(selected.agent.region)}
                </div>
              </div>
              <div className="heat-panel-tags">
                <Tag
                  value={selected.agent.status}
                  severity={severityOf(selected.agent.status)}
                  className={stripeOf(selected.agent.status)}
                />
                {selected.agent.wsState && (
                  <Tag value={selected.agent.wsState} severity={severityOf(selected.agent.wsState)} />
                )}
              </div>
              <dl className="heat-panel-facts">
                <Fact label="Active users" value={formatCount(selected.agent.activeUsers)} />
                <Fact label="TPS" value={formatCount(selected.agent.tps)} />
                <Fact label="Failures" value={formatCount(selected.agent.failures?.total)} />
                <Fact
                  label="Last seen"
                  value={
                    selected.agent.lastSeenMs
                      ? `${formatTime(new Date(selected.agent.lastSeenMs).toISOString())} (${selected.seen ?? 0}s ago)`
                      : '—'
                  }
                />
                <Fact label="Started" value={formatDateTime(selected.agent.startTime) || '—'} />
              </dl>
              <div className="heat-panel-actions">
                {actionsFor({ kind: 'agent', agent: selected.agent, job: selected.entry.job })}
              </div>
            </>
          ) : (
            <p className="field-help">Pick a tile to see that agent&apos;s figures and actions.</p>
          )}
        </aside>
      </div>
    </div>
  );
}

function tileDescription({ agent, entry, seen }: Tile) {
  return [
    agent.instanceId,
    regionLabel(agent.region),
    agent.status,
    `${formatCount(agent.tps)} TPS`,
    `${formatCount(agent.activeUsers)} users`,
    `${formatCount(agent.failures?.total)} failures`,
    seen === undefined ? undefined : `seen ${seen}s ago`,
    entry.job.name,
  ]
    .filter(Boolean)
    .join(' · ');
}

export function Stat({ label, value, sub, alert }: { label: string; value: string; sub?: string; alert?: boolean }) {
  return (
    <div className="job-stat">
      <span className="job-stat-label">{label}</span>
      <span className={alert ? 'job-stat-value job-stat-alert' : 'job-stat-value'}>{value}</span>
      {sub && <span className="field-help">{sub}</span>}
    </div>
  );
}

function Fact({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value}</dd>
    </div>
  );
}

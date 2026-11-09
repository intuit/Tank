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
import { SERIES_COLORS } from '../../charts/LineChart';
import { agentFigure, type AgentMetricSeries, type AgentMetricsLookup } from './agentMetricsModel';
import { Sparkline } from './MetricWidgets';

/** What the tiles can be shaded by: throughput in blue, trouble in orange */
const METRICS = [
  { value: 'tps', label: 'TPS', ramp: 'blue', group: 'load' },
  { value: 'users', label: 'Active users', ramp: 'blue', group: 'load' },
  { value: 'failures', label: 'Failures', ramp: 'orange', group: 'load' },
  { value: 'seen', label: 'Last seen', ramp: 'orange', group: 'load' },
  // from CloudWatch: percentages are shaded on a fixed 0-100% scale, so "hot" means hot
  { value: 'cpu', label: 'CPU', ramp: 'orange', group: 'host', pct: true },
  { value: 'mem', label: 'Memory', ramp: 'orange', group: 'host', pct: true },
  { value: 'heap', label: 'Heap', ramp: 'orange', group: 'host', pct: true },
  { value: 'threads', label: 'JVM threads', ramp: 'blue', group: 'host' },
] as const;
type Metric = (typeof METRICS)[number]['value'];
type MetricDef = (typeof METRICS)[number];
const LOAD_METRICS = METRICS.filter((m) => m.group === 'load');
const HOST_METRICS = METRICS.filter((m) => m.group === 'host');

/** Under 25%, 25-50%, 50-75%, 75-90%, 90% and over */
const pctShade = (value: number) => (value < 25 ? 0 : value < 50 ? 1 : value < 75 ? 2 : value < 90 ? 3 : 4);

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
export function AgentHeatMap({ tree, actionsFor, onDetails, metrics }: JobViewProps) {
  const [metric, setMetric] = useState<Metric>('tps');
  const [selectedId, setSelectedId] = useState<string>();
  const def: MetricDef = METRICS.find((m) => m.value === metric)!;
  const ramp = def.ramp;
  const isPct = 'pct' in def;

  const entries = jobEntries(tree).filter((e) => (e.job.agents ?? []).length > 0);
  const tiles: Tile[] = entries.flatMap((entry) =>
    (entry.job.agents ?? []).map((agent) => ({ agent, entry, seen: secondsSinceSeen(agent, tree.generatedAt) })),
  );
  // undefined: the controller has no CloudWatch figure for this agent (older AMI, or not read yet)
  const valueOf = (tile: Tile): number | undefined =>
    metric === 'tps'
      ? (tile.agent.tps ?? 0)
      : metric === 'users'
        ? (tile.agent.activeUsers ?? 0)
        : metric === 'failures'
          ? (tile.agent.failures?.total ?? 0)
          : metric === 'seen'
            ? (tile.seen ?? 0)
            : (agentFigure(metrics, tile.agent.instanceId, metric) ?? undefined);
  const live = tiles.filter((t) => agentHealth(t.agent) === 'live');
  // shades spread over the live agents' range, so a slow or hot agent stands out from its peers; zero
  // keeps the lightest shade of its own
  const nonZero = live.map(valueOf).filter((v): v is number => v !== undefined && v > 0);
  const min = nonZero.length ? Math.min(...nonZero) : 0;
  const max = nonZero.length ? Math.max(...nonZero) : 0;
  const shade = (value: number) =>
    isPct
      ? pctShade(value)
      : value <= 0
        ? 0
        : max === min
          ? 4
          : Math.min(4, 1 + Math.floor((3.999 * (value - min)) / (max - min)));
  const label = (value: number) =>
    metric === 'seen' ? `${formatCount(value)}s` : isPct ? `${Math.round(value)}%` : formatCount(value);

  const lost = tiles.filter((t) => agentHealth(t.agent) === 'lost').length;
  const silent = live.filter((t) => (t.seen ?? 0) > SILENT_SECONDS).length;
  // an agent at 85% CPU or more is likely what limits its TPS
  const hot = live.filter((t) => (agentFigure(metrics, t.agent.instanceId, 'cpu') ?? 0) >= HOT_CPU).length;
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
          value={formatCount(lost + silent + hot)}
          sub={`${lost} lost · ${silent} silent over ${SILENT_SECONDS}s${hot ? ` · ${hot} at ${HOT_CPU}% CPU or more` : ''}`}
          alert={lost + silent + hot > 0}
        />
      </div>

      <div className="heat-map-controls">
        <span className="heat-map-controls-label" id="heat-metric-label">
          Shade by
        </span>
        <div className="heat-metric-group" role="group" aria-labelledby="heat-metric-label">
          {[
            { name: 'Load', options: LOAD_METRICS },
            { name: 'Host and JVM (CloudWatch)', options: HOST_METRICS },
          ].map((group) => (
            <div key={group.name} className="field-inline">
              <span className="field-help">{group.name}</span>
              <SelectButton
                value={group.options.some((o) => o.value === metric) ? metric : null}
                options={[...group.options]}
                optionLabel="label"
                optionValue="value"
                onChange={(e) => e.value && setMetric(e.value as Metric)}
                allowEmpty={false}
                aria-label={group.name}
                className="p-selectbutton-sm"
              />
            </div>
          ))}
        </div>
        <div className="heat-legend" aria-label="Legend">
          <span className={`heat-swatch heat-${ramp}-0`} />
          <span>{isPct ? '0%' : '0'}</span>
          {!isPct && <span>{label(min)}</span>}
          {STEPS.slice(1).map((step) => (
            <span key={step} className={`heat-swatch heat-${ramp}-${step}`} />
          ))}
          <span>{isPct ? '100%' : label(max)}</span>
          <span className="heat-swatch heat-lost" />
          <span>Lost</span>
          <span className="heat-swatch heat-setup" />
          <span>Setting up</span>
          <span className="heat-swatch heat-ready" />
          <span>Ready</span>
          {def.group === 'host' && (
            <>
              <span className="heat-swatch heat-nodata" />
              <span>No data</span>
            </>
          )}
        </div>
      </div>
      <p className="field-help heat-scale-note">
        {isPct
          ? 'Shaded on a fixed scale: under 25%, 25–50%, 50–75%, 75–90%, 90% and over.'
          : 'Shaded across the range of the running agents.'}
        {def.group === 'host' && ' CloudWatch figures refresh every minute.'}
      </p>

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
                              ? value === undefined
                                ? 'heat-nodata'
                                : `heat-${ramp}-${shade(value)}`
                              : health === 'done'
                                ? 'heat-done'
                                : `heat-${health}`;
                          const text =
                            health === 'live'
                              ? value === undefined
                                ? 'n/a'
                                : label(value)
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
                              aria-label={tileDescription(tile, metrics)}
                              title={tileDescription(tile, metrics)}
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
                {agentFigure(metrics, selected.agent.instanceId, 'threads') != null && (
                  <Fact label="JVM threads" value={formatCount(agentFigure(metrics, selected.agent.instanceId, 'threads')!)} />
                )}
              </dl>
              <AgentSparks series={metrics?.agent(selected.agent.instanceId)} />
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

const HOT_CPU = 85;

/** CPU, memory and heap over the last 30 minutes, when the controller has them for this agent */
function AgentSparks({ series }: { series: AgentMetricSeries | undefined }) {
  if (!series) {
    return null;
  }
  return (
    <div className="heat-panel-sparks">
      <span className="heat-region-label">Last 30 minutes</span>
      <Sparkline label="CPU" values={series.metrics.cpu} color={SERIES_COLORS[0]!} />
      <Sparkline label="Memory" values={series.metrics.mem} color={SERIES_COLORS[1]!} />
      <Sparkline label="Heap" values={series.metrics.heap} color={SERIES_COLORS[2]!} />
      <span className="field-help">Dashed line marks 80%.</span>
    </div>
  );
}

function tileDescription({ agent, entry, seen }: Tile, metrics: AgentMetricsLookup | undefined) {
  const figure = (key: 'cpu' | 'mem' | 'heap', name: string) => {
    const v = agentFigure(metrics, agent.instanceId, key);
    return v === undefined ? undefined : `${name} ${v === null ? 'n/a' : `${Math.round(v)}%`}`;
  };
  return [
    agent.instanceId,
    regionLabel(agent.region),
    agent.status,
    `${formatCount(agent.tps)} TPS`,
    `${formatCount(agent.activeUsers)} users`,
    `${formatCount(agent.failures?.total)} failures`,
    seen === undefined ? undefined : `seen ${seen}s ago`,
    figure('cpu', 'CPU'),
    figure('mem', 'memory'),
    figure('heap', 'heap'),
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

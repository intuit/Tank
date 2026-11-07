import { Dropdown } from 'primereact/dropdown';
import { Message } from 'primereact/message';
import { SelectButton } from 'primereact/selectbutton';
import { Tag } from 'primereact/tag';
import { useMemo, useState } from 'react';
import { LineChart, SERIES_COLORS, type LineSeries } from '../../charts/LineChart';
import {
  formatMetric,
  isStale,
  latest,
  levelOf,
  NO_METRICS,
  peak,
  type AgentMetrics,
  type AgentMetricSeries,
  type AgentMetricsLookup,
} from './agentMetricsModel';
import { Meter } from './MetricWidgets';
import {
  agentHealth,
  formatTime,
  jobEntries,
  regionLabel,
  severityOf,
  type AgentNode,
  type JobViewProps,
} from './jobModel';

/** How the agents are ordered: the hottest first answers "which one is struggling" */
const SORTS = [
  { value: 'cpu', label: 'Highest CPU' },
  { value: 'mem', label: 'Memory' },
  { value: 'heap', label: 'Heap' },
  { value: 'id', label: 'Instance' },
] as const;
type Sort = (typeof SORTS)[number]['value'];

const pct = (v: number) => `${Math.round(v)}%`;

/**
 * Host and JVM health of a job's agents, from the controller's CloudWatch cache: a fleet summary, a CPU
 * chart of the busiest agents, and one row per agent with meters that open into charts. Levels are said
 * in words as well as colour.
 */
export function AgentMetricsView({ tree, metrics = NO_METRICS }: JobViewProps & { metrics?: AgentMetricsLookup }) {
  const entries = jobEntries(tree).filter((e) => (e.job.agents ?? []).length > 0);
  const [jobId, setJobId] = useState<string>();
  const [sort, setSort] = useState<Sort>('cpu');
  const [open, setOpen] = useState<string>();

  const entry = entries.find((e) => e.job.jobId === jobId) ?? entries[0];
  const data = entry ? metrics.jobs.get(Number(entry.job.jobId)) : undefined;
  const nodes = useMemo(() => new Map((entry?.job.agents ?? []).map((a) => [a.instanceId, a])), [entry]);

  if (!entry) {
    return <p className="job-view-empty">No agents yet. Metrics show here once a job&apos;s agents start.</p>;
  }

  return (
    <div className="agent-metrics">
      {isStale(data) && (
        <Message
          severity="warn"
          className="tab-message"
          text={`Metrics are behind. CloudWatch hasn't answered since ${formatTime(data!.generatedAt)}; these are the last figures the controller has.`}
        />
      )}
      <div className="tab-toolbar">
        {entries.length > 1 && (
          <div className="field-inline">
            <label htmlFor="metrics-job">Job</label>
            <Dropdown
              inputId="metrics-job"
              value={entry.job.jobId}
              options={entries.map((e) => ({
                label: `${e.job.jobId}: ${e.job.name} (${(e.job.agents ?? []).length} agents)`,
                value: e.job.jobId,
              }))}
              onChange={(e) => setJobId(e.value as string)}
              className="p-inputtext-sm"
            />
          </div>
        )}
        <div className="field-inline">
          <span id="metrics-sort-label">Order by</span>
          <SelectButton
            value={sort}
            options={[...SORTS]}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => e.value && setSort(e.value as Sort)}
            allowEmpty={false}
            aria-labelledby="metrics-sort-label"
            className="p-selectbutton-sm"
          />
        </div>
        {data && <span className="field-help tab-toolbar-end">CloudWatch read {formatTime(data.generatedAt)}, every 60s</span>}
      </div>

      {!data || data.agents.length === 0 ? (
        <p className="job-view-empty">
          No metrics yet for this job. The controller reads CloudWatch once a minute once agents are running; agents
          also need the CloudWatch agent installed.
        </p>
      ) : (
        <MetricsBody data={data} nodes={nodes} sort={sort} open={open} onOpen={setOpen} />
      )}
    </div>
  );
}

function MetricsBody({
  data,
  nodes,
  sort,
  open,
  onOpen,
}: {
  data: AgentMetrics;
  nodes: Map<string | undefined, AgentNode>;
  sort: Sort;
  open: string | undefined;
  onOpen: (id: string | undefined) => void;
}) {
  const times = data.times.map((t) => new Date(t));
  const sorted = [...data.agents].sort((a, b) =>
    sort === 'id'
      ? a.instanceId.localeCompare(b.instanceId)
      : (latest(b.metrics[sort]) ?? -1) - (latest(a.metrics[sort]) ?? -1),
  );
  return (
    <>
      <FleetSummary data={data} />
      <LineChart
        title="CPU: busiest agents and fleet average (%)"
        times={times}
        series={fleetCpuSeries(data)}
        formatValue={pct}
        emptyMessage="No CPU figures yet."
      />
      <div className="metrics-list" role="list" aria-label="Agents">
        <div className="metrics-row metrics-head" aria-hidden>
          <span>Agent</span>
          <span>CPU</span>
          <span>Memory</span>
          <span>Heap</span>
          <span>JVM threads</span>
          <span>GC time</span>
          <span>TCP est.</span>
        </div>
        {sorted.map((series) => (
          <AgentRow
            key={series.instanceId}
            series={series}
            agent={nodes.get(series.instanceId)}
            times={times}
            open={open === series.instanceId}
            onToggle={() => onOpen(open === series.instanceId ? undefined : series.instanceId)}
          />
        ))}
      </div>
    </>
  );
}

/** The three busiest agents in colour and the fleet average, so the chart stays legible at any fleet size */
function fleetCpuSeries(data: AgentMetrics): LineSeries[] {
  const busiest = [...data.agents]
    .filter((a) => latest(a.metrics.cpu) !== null)
    .sort((a, b) => (latest(b.metrics.cpu) ?? 0) - (latest(a.metrics.cpu) ?? 0))
    .slice(0, 3);
  const average = data.times.map((_, i) => {
    const present = data.agents.map((a) => a.metrics.cpu[i]).filter((v): v is number => v !== null && v !== undefined);
    return present.length ? present.reduce((a, b) => a + b, 0) / present.length : null;
  });
  return [
    ...busiest.map((a, i) => ({ name: a.instanceId.slice(-6), values: a.metrics.cpu, color: SERIES_COLORS[i + 1]! })),
    { name: 'Fleet average', values: average, color: SERIES_COLORS[0]! },
  ];
}

/** Fleet-wide figures from each agent's newest sample */
function FleetSummary({ data }: { data: AgentMetrics }) {
  const newest = (key: 'cpu' | 'heap' | 'threads' | 'gc') =>
    data.agents.map((a) => latest(a.metrics[key])).filter((v): v is number => v !== null);
  const cpu = newest('cpu');
  const heap = newest('heap');
  const threads = newest('threads');
  const gc = newest('gc');
  const hot = cpu.filter((v) => levelOf('cpu', v) !== 'ok').length;
  const noJvm = data.agents.length - heap.length;
  const average = (values: number[]) => (values.length ? values.reduce((a, b) => a + b, 0) / values.length : null);
  const tiles = [
    { label: 'Average CPU', value: formatMetric('cpu', average(cpu)), note: `peak ${formatMetric('cpu', cpu.length ? Math.max(...cpu) : null)}` },
    {
      label: 'Agents at 80% CPU or more',
      value: `${hot} of ${data.agents.length}`,
      note: hot ? 'see the top rows' : 'all within range',
      warn: hot > 0,
    },
    { label: 'Average heap', value: formatMetric('heap', average(heap)), note: noJvm ? `${noJvm} without JVM metrics` : 'all reporting' },
    { label: 'JVM threads', value: threads.length ? Math.round(threads.reduce((a, b) => a + b, 0)).toLocaleString() : 'n/a', note: 'across agents reporting' },
    { label: 'GC time', value: gc.length ? formatMetric('gc', Math.max(...gc)) : 'n/a', note: 'worst agent, last minute' },
  ];
  return (
    <ul className="metrics-summary">
      {tiles.map((t) => (
        <li key={t.label} className={`job-view-card metrics-tile${t.warn ? ' metrics-warn' : ''}`}>
          <span className="metrics-tile-label">{t.label}</span>
          <strong className="metrics-tile-value">{t.value}</strong>
          <span className="metrics-tile-note">{t.note}</span>
        </li>
      ))}
    </ul>
  );
}

function AgentRow({
  series,
  agent,
  times,
  open,
  onToggle,
}: {
  series: AgentMetricSeries;
  agent: AgentNode | undefined;
  times: Date[];
  open: boolean;
  onToggle: () => void;
}) {
  const detailId = `metrics-detail-${series.instanceId}`;
  const m = series.metrics;
  const cpu = latest(m.cpu);
  const heap = latest(m.heap);
  const hint =
    cpu !== null && cpu >= 90 ? ['CPU-bound', 'danger'] : cpu !== null && cpu >= 80 ? ['High CPU', 'warning'] : heap === null ? ['No JVM metrics', 'secondary'] : ['Healthy', 'success'];
  const noJvm = 'This agent reports no JVM figures; its AMI predates the JMX collection.';
  return (
    <div role="listitem" className={`job-view-card metrics-agent${open ? ' is-open' : ''}`}>
      <button
        type="button"
        className="metrics-row metrics-summary-row"
        aria-expanded={open}
        aria-controls={detailId}
        onClick={onToggle}
      >
        <span className="metrics-agent-id">
          <i className={`pi ${open ? 'pi-chevron-down' : 'pi-chevron-right'}`} aria-hidden />
          <span className="agent-id">{series.instanceId}</span>
          <span className="metrics-agent-tags">
            {agent && <span className="field-help">{regionLabel(agent.region)}</span>}
            <Tag value={hint[0]} severity={hint[1] as 'danger' | 'warning' | 'secondary' | 'success'} />
            {agent && agentHealth(agent) === 'lost' && <Tag value="Lost" severity={severityOf('disconnected')} />}
          </span>
        </span>
        <Meter metric="cpu" value={cpu} peakValue={peak(m.cpu)} note={peak(m.cpu) === null ? undefined : `peak ${pct(peak(m.cpu)!)} in 30 min`} />
        <Meter metric="mem" value={latest(m.mem)} peakValue={peak(m.mem)} />
        <Meter metric="heap" value={heap} peakValue={peak(m.heap)} note={heapNote(series)} />
        <Figure metric="threads" values={m.threads} />
        <Figure metric="gc" values={m.gc} />
        <Figure metric="tcp" values={m.tcp} />
      </button>
      {open && (
        <div id={detailId} className="metrics-detail">
          <LineChart
            title="CPU and memory (%)"
            times={times}
            series={[
              { name: 'CPU', values: m.cpu, color: SERIES_COLORS[0]! },
              { name: 'Memory', values: m.mem, color: SERIES_COLORS[1]! },
            ]}
            formatValue={pct}
          />
          <LineChart
            title="JVM heap used (%)"
            times={times}
            series={[{ name: 'Heap', values: m.heap, color: SERIES_COLORS[2]! }]}
            formatValue={pct}
            emptyMessage={noJvm}
          />
          <LineChart
            title="JVM threads"
            times={times}
            series={[{ name: 'Threads', values: m.threads, color: SERIES_COLORS[6]! }]}
            emptyMessage={noJvm}
          />
          <LineChart
            title="TCP connections established"
            times={times}
            series={[{ name: 'Established', values: m.tcp, color: SERIES_COLORS[4]! }]}
          />
        </div>
      )}
    </div>
  );
}

function heapNote(series: AgentMetricSeries): string | undefined {
  const used = latest(series.metrics.heap);
  if (used === null) {
    return 'not reported';
  }
  return series.heapMaxMb === null
    ? undefined
    : `${Math.round((used / 100) * series.heapMaxMb).toLocaleString()} of ${series.heapMaxMb.toLocaleString()} MB`;
}

function Figure({ metric, values }: { metric: 'threads' | 'gc' | 'tcp'; values: (number | null)[] }) {
  const value = latest(values);
  return <span className={`metrics-cell metrics-figure${value === null ? ' metrics-na' : ''}`}>{formatMetric(metric, value)}</span>;
}

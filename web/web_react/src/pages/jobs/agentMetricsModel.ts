/**
 * Agent host and JVM figures from CloudWatch, as the controller will serve them (GET
 * /v2/jobs/{jobId}/agent-metrics). Hand-written until that endpoint exists in the generated schema;
 * then these become aliases of Schemas[...] like the types in jobModel.
 *
 * Every metric is a series aligned with `times`, null where CloudWatch had no sample.
 */
export const METRIC_KEYS = ['cpu', 'mem', 'heap', 'threads', 'gc', 'tcp'] as const;
export type MetricKey = (typeof METRIC_KEYS)[number];

export interface AgentMetricSeries {
  instanceId: string;
  /** Agents on an AMI without the JMX block have no heap, thread or GC series: all null */
  metrics: Record<MetricKey, (number | null)[]>;
  /** jvm.memory.heap.max, for the "used of max" label */
  heapMaxMb: number | null;
}

export interface AgentMetrics {
  jobId: number;
  /** When the collector last read CloudWatch */
  generatedAt: string;
  /** The collector is behind (CloudWatch throttled or unreachable); the figures are the last it had */
  stale: boolean;
  times: string[];
  agents: AgentMetricSeries[];
}

export interface MetricInfo {
  key: MetricKey;
  label: string;
  /** What CloudWatch calls it, shown in the help text */
  source: string;
  unit: '%' | 'count' | 'ms';
  /** Percent metrics get meters and warn/danger thresholds; the others are plain figures */
  warnAt?: number;
  dangerAt?: number;
}

export const METRICS: Record<MetricKey, MetricInfo> = {
  cpu: {
    key: 'cpu',
    label: 'CPU',
    source: 'CWAgent procstat_cpu_usage (java), per vCPU',
    unit: '%',
    warnAt: 80,
    dangerAt: 90,
  },
  mem: { key: 'mem', label: 'Memory', source: 'CWAgent mem_used_percent', unit: '%', warnAt: 80, dangerAt: 90 },
  heap: {
    key: 'heap',
    label: 'Heap',
    source: 'CWAgent jvm.memory.heap.used / .max',
    unit: '%',
    warnAt: 85,
    dangerAt: 95,
  },
  threads: { key: 'threads', label: 'JVM threads', source: 'CWAgent jvm.threads.count', unit: 'count' },
  gc: { key: 'gc', label: 'GC time', source: 'CWAgent jvm.gc.collections.elapsed', unit: 'ms' },
  tcp: { key: 'tcp', label: 'TCP established', source: 'CWAgent tcp_established', unit: 'count' },
};

export type Level = 'ok' | 'warn' | 'danger';

export function levelOf(metric: MetricKey, value: number | null): Level {
  const { warnAt, dangerAt } = METRICS[metric];
  if (value === null || warnAt === undefined || dangerAt === undefined) {
    return 'ok';
  }
  return value >= dangerAt ? 'danger' : value >= warnAt ? 'warn' : 'ok';
}

/** The newest sample, or null when the series has none */
export function latest(values: (number | null)[]): number | null {
  for (let i = values.length - 1; i >= 0; i--) {
    if (values[i] !== null && values[i] !== undefined) {
      return values[i]!;
    }
  }
  return null;
}

export function peak(values: (number | null)[]): number | null {
  const present = values.filter((v): v is number => v !== null && v !== undefined);
  return present.length ? Math.max(...present) : null;
}

export function formatMetric(metric: MetricKey, value: number | null): string {
  if (value === null) {
    return 'n/a';
  }
  switch (METRICS[metric].unit) {
    case '%':
      return `${Math.round(value)}%`;
    case 'ms':
      return `${Math.round(value).toLocaleString()} ms`;
    default:
      return Math.round(value).toLocaleString();
  }
}

/** A past-sample age past which the figures are worth a warning, whatever the collector says */
export const STALE_SECONDS = 120;

/** What the views get from useAgentMetrics: every job's figures and a lookup by agent */
export interface AgentMetricsLookup {
  jobs: Map<number, AgentMetrics>;
  agent: (instanceId: string | undefined) => AgentMetricSeries | undefined;
}

export const NO_METRICS: AgentMetricsLookup = { jobs: new Map(), agent: () => undefined };

/** An agent's newest figure for a metric; undefined when the controller has nothing for the agent */
export function agentFigure(lookup: AgentMetricsLookup | undefined, instanceId: string | undefined, key: MetricKey) {
  const series = lookup?.agent(instanceId);
  return series ? latest(series.metrics[key]) : undefined;
}

/** True when the collector flags the job's figures stale or they are older than STALE_SECONDS */
export function isStale(metrics: AgentMetrics | undefined, now = Date.now()): boolean {
  return !!metrics && (metrics.stale || (now - Date.parse(metrics.generatedAt)) / 1000 > STALE_SECONDS);
}

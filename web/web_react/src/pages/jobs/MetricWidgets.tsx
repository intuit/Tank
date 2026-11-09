import { formatMetric, levelOf, METRICS, peak, type Level, type MetricKey } from './agentMetricsModel';

const LEVEL_WORD: Record<Level, string> = { ok: '', warn: 'High', danger: 'Critical' };

/**
 * A percentage as a bar, its number and, past a threshold, the word for the level. A metric the agent
 * doesn't report reads "n/a" ("—" when it is not running at all). Never colour alone.
 */
export function Meter({
  metric,
  value,
  peakValue,
  note,
  missing = 'n/a',
}: {
  metric: MetricKey;
  value: number | null | undefined;
  peakValue?: number | null;
  note?: string;
  missing?: string;
}) {
  const label = METRICS[metric].label;
  if (value === null || value === undefined) {
    return (
      <span className="metrics-cell metrics-na" title={`${label} isn't reported`}>
        {missing}
      </span>
    );
  }
  const level = levelOf(metric, value);
  return (
    <span
      className="metrics-cell"
      title={`${label}: now ${formatMetric(metric, value)}${peakValue != null ? `, peak ${formatMetric(metric, peakValue)}` : ''}`}
    >
      <span
        className="metrics-meter"
        role="meter"
        aria-label={label}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={Math.round(value)}
      >
        <span className={`metrics-fill metrics-${level}`} style={{ width: `${Math.min(100, value)}%` }} />
      </span>
      <span className="metrics-value">
        {formatMetric(metric, value)}
        {LEVEL_WORD[level] && <span className={`metrics-word metrics-${level}`}> {LEVEL_WORD[level]}</span>}
      </span>
      {note && <span className="metrics-note">{note}</span>}
    </span>
  );
}

export { peak };

/** A 30-minute line with the 80% mark dashed, for the heat map's side panel */
export function Sparkline({ label, values, color }: { label: string; values: (number | null)[]; color: string }) {
  const present = values.filter((v): v is number => v !== null);
  if (present.length < 2) {
    return null;
  }
  const step = 200 / Math.max(1, values.length - 1);
  const y = (v: number) => (42 - (Math.min(100, v) / 100) * 40).toFixed(1);
  const path = values
    .map((v, i) => (v === null ? '' : `${i === 0 || values[i - 1] === null ? 'M' : 'L'}${(i * step).toFixed(1)},${y(v)}`))
    .join('');
  const now = present[present.length - 1]!;
  return (
    <div className="metrics-spark">
      <div className="metrics-spark-head">
        <span>{label}</span>
        <strong>{Math.round(now)}%</strong>
      </div>
      <svg viewBox="0 0 200 44" role="img" aria-label={`${label} over the last 30 minutes, now ${Math.round(now)}%`}>
        <line x1="0" x2="200" y1={y(80)} y2={y(80)} className="metrics-spark-mark" />
        <path d={path} stroke={color} className="viz-line" fill="none" />
      </svg>
    </div>
  );
}

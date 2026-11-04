import { Button } from 'primereact/button';
import { useEffect, useMemo, useRef, useState, type KeyboardEvent, type PointerEvent, type ReactNode } from 'react';

/** Categorical slots in their validated order (dataviz reference palette, light mode) */
export const SERIES_COLORS = ['#2a78d6', '#eb6834', '#1baf7a', '#eda100', '#e87ba4', '#008300', '#4a3aa7', '#e34948'];
/** De-emphasis gray for a folded "Other" series */
export const OTHER_COLOR = '#898781';

export interface LineSeries {
  name: string;
  values: (number | null)[];
  color: string;
}

const MARGIN = { top: 12, right: 20, bottom: 28, left: 52 };
const HEIGHT = 220;

/**
 * A multi-series line chart over time: 2px lines with gaps where samples are missing, a crosshair
 * that snaps to the nearest sample and lists every series, a legend for two or more series and a
 * table view. Values are never only in the tooltip.
 */
export function LineChart({
  title,
  times,
  series,
  formatValue = (v) => v.toLocaleString(),
  loading = false,
  emptyMessage = 'No data yet',
  table,
}: {
  title: string;
  times: Date[];
  series: LineSeries[];
  formatValue?: (value: number) => string;
  /** Refetching: the last render stays, dimmed */
  loading?: boolean;
  emptyMessage?: string;
  /** Replaces the default time-by-series table view */
  table?: ReactNode;
}) {
  const container = useRef<HTMLDivElement>(null);
  const [width, setWidth] = useState(640);
  const [hover, setHover] = useState<number>();
  const [showTable, setShowTable] = useState(false);

  useEffect(() => {
    const element = container.current;
    if (!element || typeof ResizeObserver === 'undefined') {
      return;
    }
    const observer = new ResizeObserver(([entry]) => entry && setWidth(Math.max(280, entry.contentRect.width)));
    observer.observe(element);
    return () => observer.disconnect();
  }, []);

  const plotWidth = width - MARGIN.left - MARGIN.right;
  const plotHeight = HEIGHT - MARGIN.top - MARGIN.bottom;
  const hasData = times.length > 0 && series.some((s) => s.values.some((v) => v !== null));

  const scales = useMemo(() => {
    const start = times[0]?.getTime() ?? 0;
    const end = times.at(-1)?.getTime() ?? start;
    const span = Math.max(end - start, 1);
    const max = Math.max(0, ...series.flatMap((s) => s.values.filter((v): v is number => v !== null)));
    const ticks = niceTicks(max);
    const top = ticks.at(-1) || 1;
    return {
      x: (t: Date) => (times.length === 1 ? plotWidth / 2 : ((t.getTime() - start) / span) * plotWidth),
      y: (v: number) => plotHeight - (v / top) * plotHeight,
      yTicks: ticks,
      xTicks: timeTicks(start, end, Math.max(2, Math.floor(plotWidth / 110))),
    };
  }, [times, series, plotWidth, plotHeight]);

  const nearestIndex = (px: number) => {
    let best = 0;
    let bestDistance = Infinity;
    times.forEach((t, i) => {
      const distance = Math.abs(scales.x(t) - px);
      if (distance < bestDistance) {
        best = i;
        bestDistance = distance;
      }
    });
    return best;
  };
  const onPointerMove = (event: PointerEvent<SVGRectElement>) => {
    const box = event.currentTarget.getBoundingClientRect();
    setHover(nearestIndex(event.clientX - box.left));
  };
  const onKeyDown = (event: KeyboardEvent<SVGSVGElement>) => {
    if (!times.length) {
      return;
    }
    if (event.key === 'ArrowRight' || event.key === 'ArrowLeft') {
      event.preventDefault();
      const step = event.key === 'ArrowRight' ? 1 : -1;
      setHover((current) => Math.min(times.length - 1, Math.max(0, (current ?? (step > 0 ? -1 : times.length)) + step)));
    } else if (event.key === 'Escape') {
      setHover(undefined);
    }
  };

  const hoverX = hover !== undefined && times[hover] ? scales.x(times[hover]) : undefined;
  const tooltipLeft = hoverX !== undefined ? Math.min(MARGIN.left + hoverX + 12, width - 200) : 0;

  return (
    <figure className={`viz-root line-chart${loading ? ' is-loading' : ''}`}>
      <figcaption className="viz-header">
        <span className="viz-title">{title}</span>
        {hasData && (
          <Button
            label={showTable ? 'Show chart' : 'Show table'}
            icon={showTable ? 'pi pi-chart-line' : 'pi pi-table'}
            text
            size="small"
            onClick={() => setShowTable(!showTable)}
          />
        )}
      </figcaption>

      {series.length > 1 && hasData && !showTable && (
        <ul className="viz-legend" aria-label={`${title} series`}>
          {series.map((s) => (
            <li key={s.name}>
              <span className="viz-key" style={{ background: s.color }} aria-hidden />
              {s.name}
            </li>
          ))}
        </ul>
      )}

      <div ref={container} className="viz-plot">
        {!hasData ? (
          <p className="viz-empty">{emptyMessage}</p>
        ) : showTable ? (
          (table ?? <SeriesTable times={times} series={series} formatValue={formatValue} />)
        ) : (
          <>
            <svg
              width={width}
              height={HEIGHT}
              role="img"
              aria-label={`${title}. Use the arrow keys to read values.`}
              tabIndex={0}
              onKeyDown={onKeyDown}
              onBlur={() => setHover(undefined)}
            >
              <g transform={`translate(${MARGIN.left},${MARGIN.top})`}>
                {scales.yTicks.map((tick) => (
                  <g key={tick} transform={`translate(0,${scales.y(tick)})`}>
                    <line x2={plotWidth} className={tick === 0 ? 'viz-baseline' : 'viz-grid'} />
                    <text x={-8} dy="0.32em" textAnchor="end" className="viz-tick">
                      {compact(tick)}
                    </text>
                  </g>
                ))}
                {scales.xTicks.map((tick) => (
                  <text key={tick} x={scales.x(new Date(tick))} y={plotHeight + 18} textAnchor="middle" className="viz-tick">
                    {new Date(tick).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' })}
                  </text>
                ))}
                {series.map((s) => (
                  <path key={s.name} d={linePath(times, s.values, scales.x, scales.y)} stroke={s.color} className="viz-line" />
                ))}
                {series.map((s) => {
                  const last = lastIndex(s.values);
                  return last === undefined ? null : (
                    <circle key={s.name} cx={scales.x(times[last]!)} cy={scales.y(s.values[last]!)} r={4} fill={s.color} className="viz-dot" />
                  );
                })}
                {hoverX !== undefined && (
                  <g>
                    <line x1={hoverX} x2={hoverX} y2={plotHeight} className="viz-crosshair" />
                    {series.map((s) => {
                      const v = s.values[hover!];
                      return v === null || v === undefined ? null : (
                        <circle key={s.name} cx={hoverX} cy={scales.y(v)} r={4} fill={s.color} className="viz-dot" />
                      );
                    })}
                  </g>
                )}
                <rect
                  width={plotWidth}
                  height={plotHeight}
                  fill="transparent"
                  onPointerMove={onPointerMove}
                  onPointerLeave={() => setHover(undefined)}
                />
              </g>
            </svg>
            {hover !== undefined && times[hover] && (
              <div className="viz-tooltip" style={{ left: tooltipLeft, top: MARGIN.top }} role="status">
                <div className="viz-tooltip-time">{times[hover].toLocaleTimeString()}</div>
                {series.map((s) => (
                  <div key={s.name} className="viz-tooltip-row">
                    <span className="viz-key" style={{ background: s.color }} aria-hidden />
                    <strong>{s.values[hover] === null || s.values[hover] === undefined ? '—' : formatValue(s.values[hover]!)}</strong>
                    <span className="viz-tooltip-name">{s.name}</span>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </figure>
  );
}

function SeriesTable({
  times,
  series,
  formatValue,
}: {
  times: Date[];
  series: LineSeries[];
  formatValue: (v: number) => string;
}) {
  return (
    <div className="viz-table-wrap">
      <table className="viz-table">
        <thead>
          <tr>
            <th>Time</th>
            {series.map((s) => (
              <th key={s.name}>{s.name}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {times.map((t, i) => (
            <tr key={t.getTime()}>
              <td>{t.toLocaleTimeString()}</td>
              {series.map((s) => (
                <td key={s.name}>{s.values[i] === null || s.values[i] === undefined ? '—' : formatValue(s.values[i]!)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** The SVG path of a series, broken where a sample is missing */
export function linePath(
  times: Date[],
  values: (number | null)[],
  x: (t: Date) => number,
  y: (v: number) => number,
): string {
  let d = '';
  let drawing = false;
  values.forEach((v, i) => {
    if (v === null || v === undefined || !times[i]) {
      drawing = false;
      return;
    }
    d += `${drawing ? 'L' : 'M'}${x(times[i]).toFixed(1)},${y(v).toFixed(1)}`;
    drawing = true;
  });
  return d;
}

/** 0 up to a round number at or above max, in 1, 2 or 5 × 10^n steps */
export function niceTicks(max: number, count = 4): number[] {
  if (max <= 0) {
    return [0, 1];
  }
  const raw = max / count;
  const magnitude = 10 ** Math.floor(Math.log10(raw));
  const step = [1, 2, 5, 10].map((m) => m * magnitude).find((s) => s >= raw)!;
  const ticks: number[] = [];
  for (let v = 0; v < max + step; v += step) {
    ticks.push(Math.round(v * 1e6) / 1e6);
    if (v >= max) {
      break;
    }
  }
  return ticks;
}

/** Evenly spaced times rounded to whole minutes, at most `count` */
function timeTicks(start: number, end: number, count: number): number[] {
  if (end <= start) {
    return [start];
  }
  const minute = 60_000;
  const steps = [1, 2, 5, 10, 15, 30, 60, 120, 240].map((m) => m * minute);
  const step = steps.find((s) => (end - start) / s <= count) ?? steps.at(-1)!;
  const ticks: number[] = [];
  for (let t = Math.ceil(start / step) * step; t <= end; t += step) {
    ticks.push(t);
  }
  return ticks.length ? ticks : [start];
}

function lastIndex(values: (number | null)[]): number | undefined {
  for (let i = values.length - 1; i >= 0; i--) {
    if (values[i] !== null && values[i] !== undefined) {
      return i;
    }
  }
  return undefined;
}

/** 950, 1.2K, 3.4M */
export function compact(value: number): string {
  return new Intl.NumberFormat(undefined, { notation: 'compact', maximumFractionDigits: 1 }).format(value);
}

/**
 * At most eight series get colors (in name order, so a series keeps its color while the set is
 * stable); past eight, the largest seven by peak stay and the rest are summed as "Other" in gray.
 */
export function colorSeries(series: { name: string; values: (number | null)[] }[]): LineSeries[] {
  if (series.length <= SERIES_COLORS.length) {
    return series.map((s, i) => ({ ...s, color: SERIES_COLORS[i]! }));
  }
  const peak = (s: { values: (number | null)[] }) => Math.max(0, ...s.values.map((v) => v ?? 0));
  const kept = new Set([...series].sort((a, b) => peak(b) - peak(a)).slice(0, SERIES_COLORS.length - 1));
  const rest = series.filter((s) => !kept.has(s));
  const length = Math.max(...series.map((s) => s.values.length));
  const other = Array.from({ length }, (_, i) => {
    const present = rest.map((s) => s.values[i]).filter((v): v is number => v !== null && v !== undefined);
    return present.length ? present.reduce((a, b) => a + b, 0) : null;
  });
  return [
    ...series.filter((s) => kept.has(s)).map((s, i) => ({ ...s, color: SERIES_COLORS[i]! })),
    { name: `Other (${rest.length})`, values: other, color: OTHER_COLOR },
  ];
}

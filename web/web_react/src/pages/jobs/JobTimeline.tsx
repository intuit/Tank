import { SelectButton } from 'primereact/selectbutton';
import { Tag } from 'primereact/tag';
import { useState } from 'react';
import { formatDateTime } from '../../format';
import { formatCount, jobEntries, severityOf, stripeOf, type JobViewProps } from './jobModel';

const HOUR = 3_600_000;

/** Time windows to show, each with its tick spacing; the tree holds a week of finished jobs */
const WINDOWS = [
  { value: '6h', label: '6 hours', span: 6 * HOUR, tick: HOUR },
  { value: '24h', label: '24 hours', span: 24 * HOUR, tick: 3 * HOUR },
  { value: '7d', label: '7 days', span: 168 * HOUR, tick: 24 * HOUR },
] as const;
type TimeWindow = (typeof WINDOWS)[number]['value'];

/** Statuses a job ends in; one without an end time (older controllers didn't always record it) has no known span */
const FINISHED = ['completed', 'aborted'];
/** Statuses of a job still to start; a job in any other without a start time never ran */
const WAITING = ['created', 'queued', 'starting'];

const HOUR_LABEL = new Intl.DateTimeFormat(undefined, { hour: 'numeric' });
const DAY_LABEL = new Intl.DateTimeFormat(undefined, { weekday: 'short', day: 'numeric' });

/**
 * Jobs as bars from start to end (or now) across the last few hours or days, coloured by status, so
 * overlapping runs and gaps show at a glance. Queued jobs are a ring at now; a bar opens the job.
 */
export function JobTimeline({ tree, onDetails }: JobViewProps) {
  const [windowId, setWindowId] = useState<TimeWindow>('24h');
  const range = WINDOWS.find((w) => w.value === windowId)!;
  const parsed = tree.generatedAt ? Date.parse(tree.generatedAt) : NaN;
  const now = Number.isNaN(parsed) ? Date.now() : parsed;
  const from = now - range.span;
  const at = (time: number) => `${(100 * (Math.max(from, Math.min(now, time)) - from)) / range.span}%`;

  const rows = jobEntries(tree)
    .flatMap((entry) => {
      const status = entry.job.status?.toLowerCase() ?? '';
      const start = entry.job.startTime ? Date.parse(entry.job.startTime) : NaN;
      if (Number.isNaN(start)) {
        return WAITING.includes(status) ? [{ ...entry, start, end: now }] : [];
      }
      // only a job still going runs to now; a finished one with no end time can't be placed
      const end = entry.job.endTime ? Date.parse(entry.job.endTime) : FINISHED.includes(status) ? NaN : now;
      return end >= from ? [{ ...entry, start, end }] : [];
    })
    // queued (not started) first, then the most recent
    .sort((a, b) => (Number.isNaN(b.start) ? Infinity : b.start) - (Number.isNaN(a.start) ? Infinity : a.start));

  // ticks on round hours or local midnights
  const ticks: number[] = [];
  const first = new Date(from);
  if (range.tick >= 24 * HOUR) {
    first.setHours(24, 0, 0, 0);
  } else {
    first.setMinutes(0, 0, 0);
    while (first.getTime() < from || first.getHours() % (range.tick / HOUR) !== 0) {
      first.setTime(first.getTime() + HOUR);
    }
  }
  for (let t = first.getTime(); t <= now; t += range.tick) {
    ticks.push(t);
  }
  const tickLabel = (t: number) => (range.tick >= 24 * HOUR ? DAY_LABEL : HOUR_LABEL).format(t);

  return (
    <>
      <div className="timeline-controls">
        <span className="heat-region-label" id="timeline-window-label">
          Window
        </span>
        <SelectButton
          value={windowId}
          options={[...WINDOWS]}
          optionLabel="label"
          optionValue="value"
          onChange={(e) => e.value && setWindowId(e.value as TimeWindow)}
          allowEmpty={false}
          aria-labelledby="timeline-window-label"
          className="p-selectbutton-sm"
        />
        <span className="field-help">
          {formatDateTime(new Date(from).toISOString())} – {formatDateTime(new Date(now).toISOString())}
        </span>
      </div>

      {rows.length === 0 ? (
        <p className="job-view-empty">No jobs in the last {range.label}.</p>
      ) : (
        <div className="job-view-card timeline-wrap">
          <div className="timeline">
            <span className="timeline-head">Job</span>
            <div className="timeline-axis" aria-hidden>
              {ticks.map((t) => (
                <span key={t} style={{ left: at(t) }}>
                  {tickLabel(t)}
                </span>
              ))}
            </div>
            {rows.map(({ job, project, start, end }) => {
              const queued = Number.isNaN(start);
              const severity = severityOf(job.status);
              const when = queued
                ? 'Not started'
                : `${formatDateTime(job.startTime)}${job.endTime ? ` – ${formatDateTime(job.endTime)}` : ', still going'}`;
              return (
                <div key={job.jobId} className="timeline-row">
                  <div className="timeline-label">
                    <div className="timeline-label-name">
                      <button type="button" className="job-view-name" onClick={() => onDetails(job)}>
                        {job.name}
                      </button>
                      <Tag value={job.status} severity={severity} className={stripeOf(job.status)} />
                    </div>
                    <span className="field-help">
                      {project ? `${project.name} · ` : ''}
                      {(job.agents ?? []).length} agents · {formatCount(job.totalUsers)} users
                    </span>
                  </div>
                  <div className="timeline-track">
                    {ticks.map((t) => (
                      <span key={t} className="timeline-grid" style={{ left: at(t) }} />
                    ))}
                    <button
                      type="button"
                      className={queued ? 'timeline-queued' : `timeline-bar timeline-${severity}`}
                      style={
                        queued ? { left: '100%' } : { left: at(start), width: `calc(${at(end)} - ${at(start)})` }
                      }
                      title={`${job.name}: ${when}`}
                      aria-label={`${job.name}: ${when}`}
                      onClick={() => onDetails(job)}
                    />
                    <span className="timeline-now" />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      <div className="job-view-legend">
        <span>
          <span className="timeline-key timeline-success" /> Running
        </span>
        <span>
          <span className="timeline-key timeline-warning" /> Paused or stopping
        </span>
        <span>
          <span className="timeline-key timeline-info" /> Starting
        </span>
        <span>
          <span className="timeline-key timeline-secondary" /> Completed or stopped
        </span>
        <span>
          <span className="timeline-key timeline-danger" /> Aborted
        </span>
        <span>
          <span className="timeline-key timeline-queued-key" /> Queued
        </span>
        <span>
          <span className="timeline-key timeline-now-key" /> Now
        </span>
      </div>
    </>
  );
}

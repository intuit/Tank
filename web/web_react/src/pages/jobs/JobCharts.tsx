import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { Message } from 'primereact/message';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { colorSeries, LineChart, SERIES_COLORS } from '../../charts/LineChart';
import { useSession } from '../../session';
import { REFRESH_MS } from './JobQueue';

type Timeseries = Schemas['Timeseries'];
/** JobCharts.TOTAL_TPS: the first TPS series sums the requests */
const TOTAL_TPS = 'Total TPS';

/**
 * Active users by script and total TPS over time, refreshed while open (initChartModel and getTpsMap
 * in JobTreeTableBean). Two charts, never two axes on one. For one agent only TPS is available.
 */
export function JobCharts({ jobId, instanceId }: { jobId: number; instanceId?: string }) {
  const { client } = useSession();

  const users = useQuery({
    queryKey: ['jobs', jobId, 'users-timeseries'],
    enabled: !instanceId,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/jobs/{jobId}/users-timeseries', {
        params: { path: { jobId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load users over time');
      }
      return data;
    },
    refetchInterval: REFRESH_MS,
    placeholderData: keepPreviousData,
  });

  const tps = useQuery({
    queryKey: ['jobs', jobId, 'tps-timeseries', instanceId],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/jobs/{jobId}/tps-timeseries', {
        params: { path: { jobId }, query: { instanceId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load TPS over time');
      }
      return data;
    },
    refetchInterval: REFRESH_MS,
    placeholderData: keepPreviousData,
  });

  const userTimes = toDates(users.data);
  const tpsTimes = toDates(tps.data);
  const total = tps.data?.series?.find((s) => s.name === TOTAL_TPS);
  const requests = (tps.data?.series ?? []).filter((s) => s.name !== TOTAL_TPS);

  return (
    <div className="job-charts">
      {!instanceId &&
        (users.error ? (
          <Message severity="error" text={users.error.message} />
        ) : (
          <LineChart
            title="Active users by script"
            times={userTimes}
            series={colorSeries(seriesOf(users.data))}
            loading={users.isFetching && !users.isPending}
            emptyMessage={users.isPending ? 'Loading…' : 'No user reports yet. They start when agents begin running.'}
          />
        ))}
      {tps.error ? (
        <Message severity="error" text={tps.error.message} />
      ) : (
        <LineChart
          title={instanceId ? `Transactions per second on ${instanceId}` : 'Transactions per second'}
          times={tpsTimes}
          series={total ? [{ name: TOTAL_TPS, values: total.values ?? [], color: SERIES_COLORS[0]! }] : []}
          loading={tps.isFetching && !tps.isPending}
          emptyMessage={tps.isPending ? 'Loading…' : 'No TPS reported yet.'}
          table={<RequestTable requests={requests} />}
        />
      )}
    </div>
  );
}

/** Every request's TPS, busiest first: the detail the single Total TPS line leaves out */
function RequestTable({ requests }: { requests: Schemas['Series'][] }) {
  const rows = requests
    .map((s) => {
      const values = (s.values ?? []).filter((v): v is number => v !== null && v !== undefined);
      return {
        name: s.name ?? '',
        latest: values.at(-1) ?? 0,
        peak: Math.max(0, ...values),
        average: values.length ? values.reduce((a, b) => a + b, 0) / values.length : 0,
      };
    })
    .sort((a, b) => b.peak - a.peak);
  return (
    <div className="viz-table-wrap">
      <table className="viz-table">
        <thead>
          <tr>
            <th>Request</th>
            <th>Latest</th>
            <th>Peak</th>
            <th>Average</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.name}>
              <td>{r.name}</td>
              <td>{r.latest.toLocaleString()}</td>
              <td>{r.peak.toLocaleString()}</td>
              <td>{r.average.toLocaleString(undefined, { maximumFractionDigits: 1 })}</td>
            </tr>
          ))}
          {rows.length === 0 && (
            <tr>
              <td colSpan={4}>No requests reported yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

function toDates(series: Timeseries | undefined): Date[] {
  return (series?.times ?? []).map((t) => new Date(t));
}

function seriesOf(series: Timeseries | undefined) {
  return (series?.series ?? []).map((s) => ({ name: s.name ?? '', values: (s.values ?? []) as (number | null)[] }));
}

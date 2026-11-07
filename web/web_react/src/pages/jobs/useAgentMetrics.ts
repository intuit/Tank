import { keepPreviousData, useQueries } from '@tanstack/react-query';
import { useMemo } from 'react';
import { toApiError } from '../../api/errors';
import { useSession } from '../../session';
import type { AgentMetrics, AgentMetricsLookup, AgentMetricSeries } from './agentMetricsModel';

/** The controller reads CloudWatch once a minute, so asking more often only repeats its answer */
export const METRICS_REFRESH_MS = 60_000;

/**
 * Host and JVM figures for the agents of the given jobs (GET /v2/jobs/{jobId}/agent-metrics). The
 * controller serves them from its own cache, so any number of open pages costs CloudWatch nothing.
 * A job with no figures yet, or one this controller doesn't own, simply has none.
 */
export function useAgentMetrics(jobIds: number[], enabled: boolean): AgentMetricsLookup {
  const { client } = useSession();
  const results = useQueries({
    queries: jobIds.map((jobId) => ({
      queryKey: ['jobs', jobId, 'agent-metrics'],
      enabled,
      queryFn: async ({ signal }: { signal: AbortSignal }): Promise<AgentMetrics> => {
        const { data, error, response } = await client.GET('/v2/jobs/{jobId}/agent-metrics', {
          params: { path: { jobId } },
          signal,
        });
        if (!data) {
          throw toApiError(error, response, 'load the agent metrics');
        }
        // the generated type marks every record component optional; the server always sends them
        return data as AgentMetrics;
      },
      refetchInterval: METRICS_REFRESH_MS,
      placeholderData: keepPreviousData,
      // metrics are optional decoration: a failing read must not spam retries
      retry: false,
    })),
  });

  // useQueries returns a new array each render; key the lookup on what it holds
  const stamp = results.map((r) => r.dataUpdatedAt).join(',');
  return useMemo(() => {
    const jobs = new Map<number, AgentMetrics>();
    const byAgent = new Map<string, AgentMetricSeries>();
    results.forEach((result, i) => {
      if (result.data) {
        jobs.set(jobIds[i]!, result.data);
        result.data.agents.forEach((a) => byAgent.set(a.instanceId, a));
      }
    });
    return { jobs, agent: (id: string | undefined) => (id ? byAgent.get(id) : undefined) };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [stamp, jobIds.join(',')]);
}

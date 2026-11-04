import type { ReactNode } from 'react';
import type { Schemas } from '../../api/client';

export type JobTree = Schemas['JobTree'];
export type JobNode = Schemas['JobNode'];
export type ProjectJobs = Schemas['ProjectJobs'];
export type AgentNode = Schemas['AgentNode'];

export type Row =
  /** a project, or (no projectId) the jobs whose project no longer exists */
  | { kind: 'project'; project: ProjectJobs; orphans?: boolean }
  | { kind: 'job'; job: JobNode }
  | { kind: 'agent'; agent: AgentNode; job: JobNode };

/** The rows actions can be sent to */
export type Target = Exclude<Row, { kind: 'project' }>;

/** What every view of the queue gets from JobQueue: the data, its actions and the details dialog */
export interface JobViewProps {
  tree: JobTree;
  actionsFor: (row: Target) => ReactNode;
  onDetails: (job: JobNode, agent?: AgentNode) => void;
}

/** A job with the project it was listed under, if any */
export interface JobEntry {
  job: JobNode;
  project?: ProjectJobs;
}

/** Every job in the tree, project by project, then those without one */
export function jobEntries(tree: JobTree): JobEntry[] {
  return [
    ...(tree.projects ?? []).flatMap((project) => (project.jobs ?? []).map((job) => ({ job, project }))),
    ...(tree.otherJobs ?? []).map((job) => ({ job })),
  ];
}

/** An agent silent this long is worth a look, though still connected */
export const SILENT_SECONDS = 30;

/**
 * How an agent is doing, for colouring it: live agents carry figures; the rest are lost (disconnected
 * mid-run), setting up, ready to start, or done
 */
export type AgentHealth = 'live' | 'lost' | 'setup' | 'ready' | 'done';

const DONE = ['completed', 'stopped', 'terminated', 'replaced', 'shutting_down', 'successful'];
const SETTING_UP = ['bootstrap_transferring', 'bootstrap_sent', 'transferring'];

export function agentHealth(agent: AgentNode): AgentHealth {
  const status = agent.status?.toLowerCase() ?? '';
  if (DONE.includes(status)) {
    return 'done';
  }
  if (agent.wsState === 'disconnected') {
    return 'lost';
  }
  if (SETTING_UP.includes(agent.wsState ?? '') || status === 'pending' || status === 'starting' || status === 'rebooting') {
    return 'setup';
  }
  return status === 'ready' ? 'ready' : 'live';
}

/** Seconds between the agent's last report and when the tree was generated; undefined if unknown */
export function secondsSinceSeen(agent: AgentNode, generatedAt: string | undefined): number | undefined {
  const now = generatedAt ? Date.parse(generatedAt) : Date.now();
  return agent.lastSeenMs && !Number.isNaN(now) ? Math.max(0, Math.round((now - agent.lastSeenMs) / 1000)) : undefined;
}

/** Regions as the server names them (US_WEST_2), written as AWS writes them (us-west-2) */
export function regionLabel(region: string | undefined): string {
  return region ? region.toLowerCase().replace(/_/g, '-') : 'Unknown region';
}

/** How a job's agents are doing, as the tree's status column summarises them (ActJobNodeBean) */
export function agentSummary(job: JobNode): string {
  const agents = job.agents ?? [];
  if (agents.length === 0) {
    return '';
  }
  const count = (test: (a: AgentNode) => boolean) => agents.filter(test).length;
  const running = count((a) => a.status === 'running');
  // as ActJobNodeBean counts them for two-step jobs
  const connected = count((a) => !!a.wsState && a.wsState !== 'disconnected');
  const ready = count((a) => a.status === 'ready');
  return job.useTwoStep && running === 0
    ? `${connected}/${agents.length} connected, ${ready}/${agents.length} ready`
    : `${running}/${agents.length} agents running`;
}

/** A figure with thousands separators, so large user counts and TPS read at a glance */
export function formatCount(value: number | undefined) {
  return (value ?? 0).toLocaleString();
}

export function formatTime(iso: string): string {
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? '' : date.toLocaleTimeString();
}

/** The JSF tree's moving stripes for work in progress (tank.css status-badge-running and -starting) */
export function stripeOf(status: string | undefined): string | undefined {
  switch (status?.toLowerCase()) {
    case 'running':
      return 'status-striped status-striped-running';
    case 'starting':
      return 'status-striped status-striped-starting';
    default:
      return undefined;
  }
}

/** Colours JobQueueStatus and VMStatus (vmManager) names; unknown ones stay neutral */
export function severityOf(status: string | undefined): 'success' | 'info' | 'warning' | 'danger' | 'secondary' {
  switch (status?.toLowerCase()) {
    case 'running':
      return 'success';
    case 'created':
    case 'queued':
    case 'starting':
    case 'pending':
    case 'ready':
    case 'rebooting':
      return 'info';
    case 'paused':
    case 'ramppaused':
    case 'stopping':
      return 'warning';
    case 'aborted':
    case 'terminated':
    case 'disconnected':
    case 'replaced':
      return 'danger';
    default:
      // Completed, Stopped, Deleted, shutting_down, unknown: neutral, not the primary blue
      return 'secondary';
  }
}

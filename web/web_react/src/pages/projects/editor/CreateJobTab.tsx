import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { InputSwitch } from 'primereact/inputswitch';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useState } from 'react';
import type { Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { useNotify } from '../../../notify';
import { useSession } from '../../../session';
import { Field } from '../../../components/Field';
import type { Update } from './useProjectDraft';
import type { ProjectDetail } from './validation';

type JobPreview = Schemas['JobPreview'];

const TWO_STEP_HELP =
  'Launches every agent first and waits for Start load, so load begins at an exact moment (for spike tests). ' +
  'Send Start load within 5 to 10 minutes of the agents being ready.';

/**
 * The job settings and queueing (JobMaker and addWorkloadToJobQueue.xhtml). The settings are part of
 * the project; queueing builds the job from the saved project, so unsaved changes are saved first.
 */
export function CreateJobTab({
  detail,
  update,
  readOnly,
  canQueue,
  ensureSaved,
  onQueued,
}: {
  detail: ProjectDetail;
  update: Update;
  readOnly: boolean;
  canQueue: boolean;
  /** Saves pending edits; false when they couldn't be saved (the editor shows why) */
  ensureSaved: () => Promise<boolean>;
  onQueued: (job: Schemas['QueuedJob']) => void;
}) {
  const { client, config } = useSession();
  const options = useConfigOptions();
  const notify = useNotify();
  const [jobName, setJobName] = useState('');
  const [preview, setPreview] = useState<JobPreview>();
  const settings = detail.settings ?? {};
  const increasing = settings.incrementStrategy !== 'standard';
  const set = (changes: Partial<NonNullable<ProjectDetail['settings']>>) =>
    update((d) => void (d.settings = { ...d.settings, ...changes }));

  const validate = useMutation({
    mutationFn: async () => {
      if (!(await ensureSaved())) {
        return undefined;
      }
      const { data, error, response } = await client.POST('/v2/projects/{projectId}/jobs/preview', {
        params: { path: { projectId: detail.id! } },
        body: { name: jobName.trim() || undefined },
      });
      if (!data) {
        throw toApiError(error, response, 'check the job');
      }
      return data;
    },
    onSuccess: (result) => result && setPreview(result),
  });

  const queue = useMutation({
    mutationFn: async (name: string | undefined) => {
      const { data, error, response } = await client.POST('/v2/projects/{projectId}/jobs', {
        params: { path: { projectId: detail.id! } },
        body: { name },
      });
      if (!data) {
        throw toApiError(error, response, 'queue the job');
      }
      return data;
    },
    onSuccess: (job) => {
      notify.success('Job queued', `${job.name} (job ${job.jobId})`);
      setPreview(undefined);
      setJobName('');
      onQueued(job);
    },
  });

  const instanceTypes = options.data?.vmInstanceTypes ?? [];
  return (
    <div>
      <div className="form-columns">
        <Field label="Job name" htmlFor="job-name" help="Leave blank for the default name">
          <InputText id="job-name" value={jobName} onChange={(e) => setJobName(e.target.value)} maxLength={255} />
        </Field>
        <Field label="Logging profile" htmlFor="logging-profile">
          <Dropdown
            inputId="logging-profile"
            value={settings.loggingProfile}
            options={options.data?.loggingProfiles ?? []}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => set({ loggingProfile: e.value as string })}
            disabled={readOnly}
          />
        </Field>
        <Field label="HTTP client" htmlFor="http-client">
          <Dropdown
            inputId="http-client"
            value={settings.tankClientClass}
            options={options.data?.httpClients ?? []}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => set({ tankClientClass: e.value as string })}
            disabled={readOnly}
          />
        </Field>
        <Field label="Stop behavior" htmlFor="stop-behavior" help="Where users finish when the job is stopped">
          <Dropdown
            inputId="stop-behavior"
            value={settings.stopBehavior}
            options={options.data?.stopBehaviors ?? []}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => set({ stopBehavior: e.value as string })}
            disabled={readOnly}
          />
        </Field>
        <Field label="Location" htmlFor="location">
          <Dropdown
            inputId="location"
            value={settings.location}
            options={options.data?.locations ?? []}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => set({ location: e.value as string })}
            disabled={readOnly}
          />
        </Field>
        {!config?.standalone && (
          <Field label="Agent instance type" htmlFor="instance-type">
            <Dropdown
              inputId="instance-type"
              value={settings.vmInstanceType}
              options={instanceTypes}
              optionLabel="label"
              optionValue="value"
              onChange={(e) => {
                // as JobMaker does: a new type starts from its default users per agent
                const type = instanceTypes.find((t) => t.value === e.value);
                set({
                  vmInstanceType: e.value as string,
                  ...(type?.usersPerAgent && type.value !== settings.vmInstanceType
                    ? { numUsersPerAgent: type.usersPerAgent }
                    : {}),
                });
              }}
              disabled={readOnly}
            />
          </Field>
        )}
        {increasing ? (
          <Field label="Users per agent" htmlFor="users-per-agent" help="Decides how many agents the job starts">
            <InputNumber
              inputId="users-per-agent"
              value={settings.numUsersPerAgent ?? 1}
              onValueChange={(e) => set({ numUsersPerAgent: e.value ?? 1 })}
              min={1}
              useGrouping={false}
              disabled={readOnly}
            />
          </Field>
        ) : (
          <Field label="Target rate per agent" htmlFor="rate-per-agent" help="Users per second each agent injects">
            <InputNumber
              inputId="rate-per-agent"
              value={settings.targetRatePerAgent ?? 1}
              onValueChange={(e) => set({ targetRatePerAgent: e.value ?? 1 })}
              min={0}
              maxFractionDigits={3}
              disabled={readOnly}
            />
          </Field>
        )}
        <Field label="Two-step start" htmlFor="two-step" help={TWO_STEP_HELP}>
          <InputSwitch
            inputId="two-step"
            checked={!!settings.useTwoStep}
            onChange={(e) => set({ useTwoStep: !!e.value })}
            disabled={readOnly}
          />
        </Field>
      </div>

      {!canQueue && (
        <Message
          severity="info"
          text="You don't have permission to run this project's jobs. Ask a Tank admin for the Control Job right."
          className="tab-message"
        />
      )}
      {validate.error && <Message severity="error" text={validate.error.message} className="tab-message" />}
      <div className="tab-toolbar">
        <Button
          label="Validate and queue…"
          icon="pi pi-play"
          loading={validate.isPending}
          disabled={!canQueue}
          onClick={() => validate.mutate()}
        />
      </div>

      {preview && (
        <Dialog
          header={preview.valid ? 'Queue this job?' : "This job can't run yet"}
          visible
          onHide={() => setPreview(undefined)}
          className="form-dialog"
          modal
          draggable={false}
          footer={
            <div className="form-actions">
              <Button label={preview.valid ? 'Cancel' : 'Close'} text onClick={() => setPreview(undefined)} />
              {preview.valid && (
                <Button
                  label="Queue job"
                  icon="pi pi-play"
                  loading={queue.isPending}
                  onClick={() => queue.mutate(jobName.trim() || preview.name)}
                />
              )}
            </div>
          }
        >
          <dl className="facts">
            <dt>Name</dt>
            <dd>{jobName.trim() || preview.name}</dd>
            <dt>Users</dt>
            <dd>{preview.totalUsers}</dd>
            <dt>Ramp time</dt>
            <dd>{formatDuration(preview.rampTimeMs)}</dd>
            <dt>Simulation time</dt>
            <dd>{formatDuration(preview.simulationTimeMs)}</dd>
            <dt>Expected run time</dt>
            <dd>{formatDuration(preview.executionTimeMs)}</dd>
          </dl>
          {(preview.errors ?? []).length > 0 && (
            <Message
              severity="error"
              className="editor-message"
              content={<ProblemList title="Fix these first:" items={preview.errors!} />}
            />
          )}
          {(preview.warnings ?? []).length > 0 && (
            <Message
              severity="warn"
              className="editor-message"
              content={<ProblemList title="Check these:" items={preview.warnings!} />}
            />
          )}
          {queue.error && <Message severity="error" text={queue.error.message} className="editor-message" />}
        </Dialog>
      )}
    </div>
  );
}

function ProblemList({ title, items }: { title: string; items: string[] }) {
  return (
    <div>
      <strong>{title}</strong>
      <ul className="problem-list">
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

/** e.g. 1h 30m, 45s; "—" when unknown */
export function formatDuration(ms: number | undefined): string {
  if (ms === undefined || ms < 0) {
    return '—';
  }
  const seconds = Math.round(ms / 1000);
  const parts: string[] = [];
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  if (h) parts.push(`${h}h`);
  if (m) parts.push(`${m}m`);
  if (s || parts.length === 0) parts.push(`${s}s`);
  return parts.join(' ');
}

import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import type { ReactNode } from 'react';
import type { Update } from './useProjectDraft';
import { totalUsers, type ProjectDetail } from './validation';

const WORKLOAD_TYPES = [
  { label: 'Increasing (linear user ramp)', value: 'increasing' },
  { label: 'Non-linear (target users per second)', value: 'standard' },
];

const TERMINATION_POLICIES = [
  { label: 'Simulation time is reached', value: 'time' },
  { label: 'Scripts finish', value: 'script' },
];

const TIME_HELP =
  "Use d, h, m, s and ms, e.g. '1h 30m' or '90m'. Times can be relative to the other times: " +
  'RT (ramp), ST (simulation) and ET (execution), e.g. 3ET - .5RT. Separate math symbols with spaces.';
const USERS_HELP =
  'A number, or an expression using ST, RT and ET, e.g. 4 * RT / 1000 for 4 users a second. ' +
  'Separate math symbols with spaces.';

export function UsersAndTimesTab({
  detail,
  update,
  readOnly,
}: {
  detail: ProjectDetail;
  update: Update;
  readOnly: boolean;
}) {
  const settings = detail.settings ?? {};
  const increasing = settings.incrementStrategy !== 'standard';
  const total = totalUsers(detail);

  return (
    <div className="form-columns">
      <Field label="Workload type" htmlFor="workload-type">
        <Dropdown
          inputId="workload-type"
          value={settings.incrementStrategy}
          options={WORKLOAD_TYPES}
          onChange={(e) => update((d) => void (d.settings = { ...d.settings, incrementStrategy: e.value }))}
          disabled={readOnly}
        />
      </Field>
      <Field label="Simulation time" htmlFor="simulation-time">
        <InputText
          id="simulation-time"
          value={settings.simulationTime ?? ''}
          onChange={(e) => update((d) => void (d.settings = { ...d.settings, simulationTime: e.target.value }))}
          placeholder="e.g. 1h 30m"
          disabled={readOnly}
        />
      </Field>
      <Field label="Ramp time" htmlFor="ramp-time" help={TIME_HELP}>
        <InputText
          id="ramp-time"
          value={settings.rampTime ?? ''}
          onChange={(e) => update((d) => void (d.settings = { ...d.settings, rampTime: e.target.value }))}
          placeholder="e.g. 10m"
          disabled={readOnly}
        />
      </Field>
      <Field label="Initial users" htmlFor="initial-users">
        <InputNumber
          inputId="initial-users"
          value={settings.baselineVirtualUsers ?? 0}
          onValueChange={(e) => update((d) => void (d.settings = { ...d.settings, baselineVirtualUsers: e.value ?? 0 }))}
          min={0}
          useGrouping={false}
          disabled={readOnly}
        />
      </Field>
      {increasing ? (
        <>
          <Field label="User increment" htmlFor="user-increment" help="Users added at each step of the ramp">
            <InputNumber
              inputId="user-increment"
              value={settings.userIntervalIncrement ?? 1}
              onValueChange={(e) =>
                update((d) => void (d.settings = { ...d.settings, userIntervalIncrement: e.value ?? 1 }))
              }
              min={1}
              useGrouping={false}
              disabled={readOnly}
            />
          </Field>
          <Field label="Run scripts until" htmlFor="termination-policy">
            <Dropdown
              inputId="termination-policy"
              value={settings.terminationPolicy}
              options={TERMINATION_POLICIES}
              onChange={(e) => update((d) => void (d.settings = { ...d.settings, terminationPolicy: e.value }))}
              disabled={readOnly}
            />
          </Field>
        </>
      ) : (
        <Field
          label="Target users per second"
          htmlFor="target-rate"
          help="Users are injected at a rate rising from 0 to this many a second over the ramp time"
        >
          <InputNumber
            inputId="target-rate"
            value={settings.targetRampRate ?? 0}
            onValueChange={(e) => update((d) => void (d.settings = { ...d.settings, targetRampRate: e.value ?? 0 }))}
            min={0}
            maxFractionDigits={3}
            disabled={readOnly}
          />
        </Field>
      )}

      <h3 className="form-section">{increasing ? 'Users by region' : 'Share of users by region'}</h3>
      {increasing && <p className="form-note field-help">{USERS_HELP}</p>}
      {(detail.regions ?? []).map((region, index) => {
        const id = `region-${index}`;
        return increasing ? (
          <Field key={region.region} label={region.region ?? ''} htmlFor={id}>
            <InputText
              id={id}
              value={region.users ?? ''}
              onChange={(e) => update((d) => void (d.regions![index]!.users = e.target.value))}
              disabled={readOnly}
            />
          </Field>
        ) : (
          <Field key={region.region} label={`${region.region} %`} htmlFor={id}>
            <InputNumber
              inputId={id}
              value={Number(region.percentage ?? 0)}
              onValueChange={(e) => update((d) => void (d.regions![index]!.percentage = String(e.value ?? 0)))}
              min={0}
              max={100}
              suffix="%"
              disabled={readOnly}
            />
          </Field>
        );
      })}
      {increasing && (
        <Field label="Total users">
          <span className="field-value">{total ?? 'Depends on the expressions'}</span>
        </Field>
      )}
    </div>
  );
}

export function Field({
  label,
  htmlFor,
  help,
  children,
}: {
  label: string;
  htmlFor?: string;
  help?: string;
  children: ReactNode;
}) {
  return (
    <div className="field">
      <label htmlFor={htmlFor}>{label}</label>
      <div className="field-input">
        {children}
        {help && <small className="field-help">{help}</small>}
      </div>
    </div>
  );
}

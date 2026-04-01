/**
 * Editors for all non-request step types:
 * ThinkTime, Sleep, Variable, Logic, Cookie, Authentication, Clear, AGGREGATE
 */
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { InputNumber } from 'primereact/inputnumber';
import { Dropdown } from 'primereact/dropdown';
import { Message } from 'primereact/message';
import type { ScriptStep } from '../../../types/scriptEditor';

interface Props {
  step: ScriptStep;
  onChange: (updated: ScriptStep) => void;
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex flex-column gap-1">
      <label className="text-sm font-semibold">{label}</label>
      {children}
    </div>
  );
}

// ── Think Time ────────────────────────────────────────────────────────────────
export function ThinkTimeEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <div className="flex gap-3">
        <Row label="Min Think Time (ms)">
          <InputNumber
            value={Number(step.minThinkTime ?? 0)}
            onValueChange={e => onChange({ ...step, minThinkTime: String(e.value ?? 0) })}
            min={0}
            style={{ width: '160px' }}
          />
        </Row>
        <Row label="Max Think Time (ms)">
          <InputNumber
            value={Number(step.maxThinkTime ?? 0)}
            onValueChange={e => onChange({ ...step, maxThinkTime: String(e.value ?? 0) })}
            min={0}
            style={{ width: '160px' }}
          />
        </Row>
      </div>
      <Row label="Group">
        <InputText
          value={step.scriptGroupName ?? ''}
          onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '280px' }}
        />
      </Row>
    </div>
  );
}

// ── Sleep ────────────────────────────────────────────────────────────────────
export function SleepEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <Row label="Sleep Duration (ms)">
        <InputNumber
          value={Number(step.payload ?? 0)}
          onValueChange={e => onChange({ ...step, payload: String(e.value ?? 0) })}
          min={0}
          style={{ width: '160px' }}
        />
      </Row>
      <Row label="Group">
        <InputText
          value={step.scriptGroupName ?? ''}
          onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '280px' }}
        />
      </Row>
    </div>
  );
}

// ── Variable ─────────────────────────────────────────────────────────────────
export function VariableEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <Row label="Key">
        <InputText
          value={step.varKey ?? ''}
          onChange={e => onChange({ ...step, varKey: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
        />
      </Row>
      <Row label="Value">
        <InputText
          value={step.varValue ?? ''}
          onChange={e => onChange({ ...step, varValue: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
        />
      </Row>
      <Row label="Group">
        <InputText
          value={step.scriptGroupName ?? ''}
          onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '280px' }}
        />
      </Row>
    </div>
  );
}

// ── Logic ────────────────────────────────────────────────────────────────────
export function LogicEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <div className="flex gap-3">
        <Row label="Name">
          <InputText
            value={step.name ?? ''}
            onChange={e => onChange({ ...step, name: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '240px' }}
          />
        </Row>
        <Row label="Group">
          <InputText
            value={step.scriptGroupName ?? ''}
            onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '240px' }}
          />
        </Row>
      </div>
      <Row label="JavaScript">
        <InputTextarea
          value={step.script ?? ''}
          onChange={e => onChange({ ...step, script: e.target.value })}
          rows={10}
          className="w-full"
          style={{ fontFamily: 'monospace', fontSize: '13px' }}
        />
      </Row>
      <Message
        severity="info"
        text="Available objects: variables, request, response. Use goto('groupName'), restartPlan(), abortScript(), terminateUser()."
      />
    </div>
  );
}

// ── Cookie ───────────────────────────────────────────────────────────────────
export function CookieEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <div className="flex gap-3 flex-wrap">
        <Row label="Cookie Name *">
          <InputText
            value={step.cookieName ?? ''}
            onChange={e => onChange({ ...step, cookieName: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '220px' }}
          />
        </Row>
        <Row label="Cookie Value">
          <InputText
            value={step.cookieValue ?? ''}
            onChange={e => onChange({ ...step, cookieValue: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '220px' }}
          />
        </Row>
      </div>
      <div className="flex gap-3 flex-wrap">
        <Row label="Path">
          <InputText
            value={step.cookiePath ?? ''}
            onChange={e => onChange({ ...step, cookiePath: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '220px' }}
          />
        </Row>
        <Row label="Domain">
          <InputText
            value={step.cookieDomain ?? ''}
            onChange={e => onChange({ ...step, cookieDomain: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '220px' }}
          />
        </Row>
        <Row label="Group">
          <InputText
            value={step.scriptGroupName ?? ''}
            onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '220px' }}
          />
        </Row>
      </div>
    </div>
  );
}

// ── Authentication ────────────────────────────────────────────────────────────
const AUTH_SCHEMES = ['BASIC', 'DIGEST', 'NTLM'];

export function AuthEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <div className="flex gap-3 flex-wrap">
        <Row label="Scheme">
          <Dropdown
            value={step.authScheme ?? 'BASIC'}
            options={AUTH_SCHEMES}
            onChange={e => onChange({ ...step, authScheme: e.value })}
            style={{ width: '130px' }}
          />
        </Row>
        <Row label="Username *">
          <InputText
            value={step.authUser ?? ''}
            onChange={e => onChange({ ...step, authUser: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '200px' }}
          />
        </Row>
        <Row label="Password *">
          <InputText
            value={step.authPassword ?? ''}
            onChange={e => onChange({ ...step, authPassword: e.target.value })}
            type="password"
            className="p-inputtext-sm"
            style={{ width: '200px' }}
          />
        </Row>
      </div>
      <div className="flex gap-3 flex-wrap">
        <Row label="Host">
          <InputText
            value={step.authHost ?? ''}
            onChange={e => onChange({ ...step, authHost: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '200px' }}
          />
        </Row>
        <Row label="Port">
          <InputText
            value={step.authPort ?? ''}
            onChange={e => onChange({ ...step, authPort: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '100px' }}
          />
        </Row>
        <Row label="Realm">
          <InputText
            value={step.authRealm ?? ''}
            onChange={e => onChange({ ...step, authRealm: e.target.value })}
            className="p-inputtext-sm"
            style={{ width: '200px' }}
          />
        </Row>
      </div>
    </div>
  );
}

// ── Clear Session ─────────────────────────────────────────────────────────────
export function ClearEditor() {
  return (
    <Message
      severity="info"
      text="This step clears all cookies and session data. No configuration needed."
    />
  );
}

// ── Aggregator / Timer Group ──────────────────────────────────────────────────
export function AggregatorEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <Row label="Logging Key (Timer Group Name)">
        <InputText
          value={step.loggingKey ?? ''}
          onChange={e => onChange({ ...step, loggingKey: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
        />
      </Row>
    </div>
  );
}

// ── Assignment ────────────────────────────────────────────────────────────────
export function AssignmentEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <Row label="Variable Key *">
        <InputText
          value={step.assignmentKey ?? ''}
          onChange={e => onChange({ ...step, assignmentKey: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
        />
      </Row>
      <Row label="Expression / Value">
        <InputText
          value={step.assignmentValue ?? ''}
          onChange={e => onChange({ ...step, assignmentValue: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
          placeholder="e.g. #{response.body} or literal value"
        />
      </Row>
      <Row label="Group">
        <InputText
          value={step.scriptGroupName ?? ''}
          onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '280px' }}
        />
      </Row>
    </div>
  );
}

// ── Validation ────────────────────────────────────────────────────────────────
const VALIDATION_CONDITIONS = ['equals', 'not-equals', 'contains', 'not-contains', 'matches', 'not-matches', 'exists', 'not-exists'];

export function ValidationEditor({ step, onChange }: Props) {
  return (
    <div className="flex flex-column gap-3">
      <Row label="Key / Expression *">
        <InputText
          value={step.validationKey ?? ''}
          onChange={e => onChange({ ...step, validationKey: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
          placeholder="e.g. #{response.status} or header name"
        />
      </Row>
      <Row label="Condition">
        <Dropdown
          value={step.validationCondition ?? 'equals'}
          options={VALIDATION_CONDITIONS}
          onChange={e => onChange({ ...step, validationCondition: e.value })}
          style={{ width: '180px' }}
        />
      </Row>
      <Row label="Expected Value">
        <InputText
          value={step.validationValue ?? ''}
          onChange={e => onChange({ ...step, validationValue: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '320px' }}
        />
      </Row>
      <Row label="On Failure">
        <Dropdown
          value={step.onFail ?? 'continue'}
          options={['continue', 'abort', 'skipGroup']}
          onChange={e => onChange({ ...step, onFail: e.value })}
          style={{ width: '160px' }}
        />
      </Row>
      <Row label="Group">
        <InputText
          value={step.scriptGroupName ?? ''}
          onChange={e => onChange({ ...step, scriptGroupName: e.target.value })}
          className="p-inputtext-sm"
          style={{ width: '280px' }}
        />
      </Row>
    </div>
  );
}

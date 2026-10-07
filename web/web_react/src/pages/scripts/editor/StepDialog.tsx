import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { Password } from 'primereact/password';
import { useRef, useState, type FormEvent, type ReactNode } from 'react';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import {
  authenticationData,
  createStep,
  dataValue,
  isTimeValue,
  KEYS,
  MASKED_PASSWORD,
  STEP_TYPES,
  withLabel,
  type ScriptStep,
} from './steps';

/** Step types edited in a StepDialog; requests, logic and timers have editors of their own */
export const SIMPLE_TYPES = ['variable', 'thinkTime', 'sleep', 'cookie', 'authentication'] as const;
export type SimpleType = (typeof SIMPLE_TYPES)[number];

export function isSimpleType(type: string | undefined): type is SimpleType {
  return (SIMPLE_TYPES as readonly string[]).includes(type ?? '');
}

/**
 * Adds or edits a variable, think time, sleep, cookie or authentication step (VariableEditor,
 * ThinkTimeEditor, SleepTimeEditor, CookieStepEditor, AuthenticationEditor). An edit keeps the step's
 * uuid, so the server keeps what it stores by uuid.
 */
export function StepDialog({
  type,
  step,
  readOnly,
  onHide,
  onSave,
}: {
  type: SimpleType;
  /** The step to edit; a new one is added when absent */
  step?: ScriptStep;
  readOnly: boolean;
  onHide: () => void;
  onSave: (step: ScriptStep) => void;
}) {
  const first = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<string>();
  const Form = FORMS[type];
  const [values, setValues] = useState<Record<string, string>>(() => Form.initial(step));

  const submit = (event: FormEvent) => {
    event.preventDefault();
    const problem = Form.check(values, !!step);
    if (problem) {
      setError(problem);
      return;
    }
    const built = Form.build(values);
    onSave(step ? withLabel({ ...step, data: built.data, name: built.name ?? step.name }) : built);
  };
  const set = (key: string) => (value: string) => {
    setError(undefined);
    setValues((v) => ({ ...v, [key]: value }));
  };

  return (
    <Dialog
      header={`${step ? (readOnly ? '' : 'Edit ') : 'Add '}${STEP_TYPES[type]?.toLowerCase()}`.replace(/^./, (c) => c.toUpperCase())}
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={() => first.current?.focus()}
    >
      <form onSubmit={submit} className="form-grid">
        <Form.Fields values={values} set={set} first={first} readOnly={readOnly} editing={!!step} />
        {error && <Message severity="error" text={error} />}
        <div className="form-actions">
          <Button type="button" label={readOnly ? 'Close' : 'Cancel'} text onClick={onHide} />
          {!readOnly && <Button type="submit" label={step ? 'Done' : 'Add'} />}
        </div>
      </form>
    </Dialog>
  );
}

interface FieldsProps {
  values: Record<string, string>;
  set: (key: string) => (value: string) => void;
  first: React.RefObject<HTMLInputElement | null>;
  readOnly: boolean;
  editing: boolean;
}

interface FormSpec {
  initial: (step: ScriptStep | undefined) => Record<string, string>;
  /** @returns a message when the values can't be saved */
  check: (values: Record<string, string>, editing: boolean) => string | undefined;
  build: (values: Record<string, string>) => ScriptStep;
  Fields: (props: FieldsProps) => ReactNode;
}

const TIME_HELP = 'Milliseconds, a variable (@delay), a function (#function...) or an expression (#{...})';

function Text({
  id,
  label,
  value,
  onChange,
  inputRef,
  readOnly,
  help,
  placeholder,
}: {
  id: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
  inputRef?: React.RefObject<HTMLInputElement | null>;
  readOnly: boolean;
  help?: string;
  placeholder?: string;
}) {
  return (
    <>
      <label htmlFor={id}>{label}</label>
      <InputText
        id={id}
        ref={inputRef}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={readOnly}
        placeholder={placeholder}
      />
      {help && <small className="field-help">{help}</small>}
    </>
  );
}

const FORMS: Record<SimpleType, FormSpec> = {
  variable: {
    initial: (s) => ({ name: s?.data?.[0]?.key ?? '', value: s?.data?.[0]?.value ?? '' }),
    check: (v) => (!v.name!.trim() ? 'Name is required' : !v.value ? 'Value is required' : undefined),
    build: (v) => createStep.variable(v.name!.trim(), v.value!),
    Fields: ({ values, set, first, readOnly }) => (
      <>
        <Text id="var-name" label="Name" value={values.name!} onChange={set('name')} inputRef={first} readOnly={readOnly} help="Use it in later steps as @name" />
        <Text id="var-value" label="Value" value={values.value!} onChange={set('value')} readOnly={readOnly} />
      </>
    ),
  },
  thinkTime: {
    initial: (s) => ({ min: s ? (dataValue(s, KEYS.minTime) ?? '') : '', max: s ? (dataValue(s, KEYS.maxTime) ?? '') : '' }),
    check: (v) => {
      if (!isTimeValue(v.min!)) return 'Minimum has to be a whole number of milliseconds, a variable, a function or an expression';
      if (!isTimeValue(v.max!)) return 'Maximum has to be a whole number of milliseconds, a variable, a function or an expression';
      if (/^\d+$/.test(v.min!.trim()) && /^\d+$/.test(v.max!.trim()) && Number(v.min) > Number(v.max)) {
        return 'Minimum has to be no more than maximum';
      }
      return undefined;
    },
    build: (v) => createStep.thinkTime(v.min!.trim(), v.max!.trim()),
    Fields: ({ values, set, first, readOnly }) => (
      <>
        <Text id="think-min" label="Minimum" value={values.min!} onChange={set('min')} inputRef={first} readOnly={readOnly} placeholder="1000" />
        <Text id="think-max" label="Maximum" value={values.max!} onChange={set('max')} readOnly={readOnly} placeholder="3000" help={`A random pause between the two. ${TIME_HELP}`} />
      </>
    ),
  },
  sleep: {
    initial: (s) => ({ time: s ? (dataValue(s, KEYS.time) ?? '') : '' }),
    check: (v) =>
      !isTimeValue(v.time!)
        ? 'Sleep time has to be a whole number of milliseconds, a variable, a function or an expression'
        : /^\d+$/.test(v.time!.trim()) && Number(v.time) <= 0
          ? 'Sleep time has to be more than 0'
          : undefined,
    build: (v) => createStep.sleep(v.time!.trim()),
    Fields: ({ values, set, first, readOnly }) => (
      <Text id="sleep-time" label="Sleep time" value={values.time!} onChange={set('time')} inputRef={first} readOnly={readOnly} placeholder="500" help={TIME_HELP} />
    ),
  },
  cookie: {
    initial: (s) => ({
      name: s ? (dataValue(s, KEYS.cookieName) ?? '') : '',
      value: s ? (dataValue(s, KEYS.cookieValue) ?? '') : '',
      domain: s ? (dataValue(s, KEYS.cookieDomain) ?? '') : '',
      path: s ? (dataValue(s, KEYS.cookiePath) ?? '') : '',
    }),
    check: (v) => (!v.name!.trim() ? 'Cookie name is required' : undefined),
    build: (v) => createStep.cookie({ name: v.name!.trim(), value: v.value!, domain: v.domain!.trim(), path: v.path!.trim() }),
    Fields: ({ values, set, first, readOnly }) => (
      <>
        <Text id="cookie-name" label="Name" value={values.name!} onChange={set('name')} inputRef={first} readOnly={readOnly} />
        <Text id="cookie-value" label="Value" value={values.value!} onChange={set('value')} readOnly={readOnly} />
        <Text id="cookie-domain" label="Domain" value={values.domain!} onChange={set('domain')} readOnly={readOnly} placeholder="store.example.com" />
        <Text id="cookie-path" label="Path" value={values.path!} onChange={set('path')} readOnly={readOnly} placeholder="/" />
      </>
    ),
  },
  authentication: {
    initial: (s) => ({
      userName: s ? (dataValue(s, KEYS.userName) ?? '') : '',
      // a stored password comes back masked; leaving the field empty keeps it
      password: '',
      hasStored: s && dataValue(s, KEYS.password) ? 'yes' : '',
      realm: s ? (dataValue(s, KEYS.realm) ?? '') : '',
      scheme: s ? (dataValue(s, KEYS.scheme) ?? '') : '',
      host: s ? (dataValue(s, KEYS.host) ?? '') : '',
      port: s ? (dataValue(s, KEYS.port) ?? '') : '',
    }),
    check: (v) =>
      !v.userName!.trim() ? 'User name is required' : !v.password && !v.hasStored ? 'Password is required' : undefined,
    build: (v) => {
      const auth = {
        userName: v.userName!.trim(),
        password: v.password || MASKED_PASSWORD,
        realm: v.realm!.trim(),
        scheme: v.scheme || undefined,
        host: v.host!.trim(),
        port: v.port!.trim(),
      };
      return { ...createStep.authentication(auth), data: authenticationData(auth) };
    },
    Fields: ({ values, set, first, readOnly }) => <AuthenticationFields values={values} set={set} first={first} readOnly={readOnly} />,
  },
};

function AuthenticationFields({ values, set, first, readOnly }: Omit<FieldsProps, 'editing'>) {
  const options = useConfigOptions();
  const schemes = options.data?.stepOptions?.['authSchemes'] ?? [];
  return (
    <>
      <Text id="auth-user" label="User name" value={values.userName!} onChange={set('userName')} inputRef={first} readOnly={readOnly} />
      <label htmlFor="auth-password">Password</label>
      <Password
        inputId="auth-password"
        value={values.password}
        onChange={(e) => set('password')(e.target.value)}
        feedback={false}
        toggleMask
        disabled={readOnly}
        placeholder={values.hasStored ? 'Unchanged' : undefined}
        autoComplete="new-password"
      />
      {values.hasStored && <small className="field-help">Leave empty to keep the stored password</small>}
      <label htmlFor="auth-scheme">Scheme</label>
      <Dropdown
        inputId="auth-scheme"
        value={values.scheme || null}
        options={schemes}
        optionLabel="label"
        optionValue="value"
        onChange={(e) => set('scheme')((e.value as string | null) ?? '')}
        placeholder="Any"
        showClear={!readOnly}
        disabled={readOnly}
      />
      <Text id="auth-realm" label="Realm" value={values.realm!} onChange={set('realm')} readOnly={readOnly} />
      <Text id="auth-host" label="Host" value={values.host!} onChange={set('host')} readOnly={readOnly} />
      <Text id="auth-port" label="Port" value={values.port!} onChange={set('port')} readOnly={readOnly} />
    </>
  );
}

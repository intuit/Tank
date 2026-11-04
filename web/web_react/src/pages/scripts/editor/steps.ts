import type { Schemas } from '../../../api/client';

export type ScriptStep = Schemas['ScriptStepTO'];
export type ScriptDocument = Schemas['ScriptDocument'];
type StepData = Schemas['StepDataTO'];

/**
 * Moves the steps with these uuids, keeping their order, so the first of them ends up at `position`
 * (1-based) in the result. Positions past the end put them last.
 */
export function moveSteps(steps: ScriptStep[], uuids: Set<string>, position: number): ScriptStep[] {
  const moving = steps.filter((s) => uuids.has(s.uuid ?? ''));
  const staying = steps.filter((s) => !uuids.has(s.uuid ?? ''));
  const at = Math.min(Math.max(position - 1, 0), staying.length);
  return [...staying.slice(0, at), ...moving, ...staying.slice(at)];
}

/** Deletes the steps, and the other half of any timer among them (ScriptEditor.doDelete) */
export function deleteSteps(steps: ScriptStep[], uuids: Set<string>): ScriptStep[] {
  const all = new Set(uuids);
  for (const step of steps) {
    if (all.has(step.uuid ?? '') && step.type === 'timer') {
      const pair = timerPairId(step);
      if (pair) all.add(pair);
    }
  }
  return steps.filter((s) => !all.has(s.uuid ?? ''));
}

/**
 * An assignment stores a response value in a variable (ConverterUtil.isAssignment): its type ends in
 * "Assignment", or a legacy "responseContent" entry's value starts with a single "=".
 */
export function isAssignment(data: StepData): boolean {
  if (data.type?.endsWith('Assignment')) {
    return true;
  }
  return data.type === 'responseContent' && /^=(?!=)/.test(data.value ?? '');
}

export function hasValidation(step: ScriptStep): boolean {
  return (step.responseData ?? []).some((d) => !isAssignment(d));
}

export function hasAssignment(step: ScriptStep): boolean {
  return (step.responseData ?? []).some(isAssignment);
}

/** How a step type reads in the editor (ScriptConstants) */
export const STEP_TYPES: Record<string, string> = {
  request: 'Request',
  variable: 'Variable',
  thinkTime: 'Think time',
  sleep: 'Sleep time',
  logic: 'Logic',
  cookie: 'Cookie',
  authentication: 'Authentication',
  clear: 'Clear session',
  timer: 'Timer',
};

export interface Problem {
  message: string;
}

const MAX_NAME = 255;
const MAX_COMMENTS = 1024;

/** ScriptDocumentMapper.validate's checks */
export function validateScript(doc: ScriptDocument): Problem[] {
  const problems: Problem[] = [];
  const name = doc.name?.trim() ?? '';
  if (!name) {
    problems.push({ message: 'Name is required' });
  } else if (name.length > MAX_NAME) {
    problems.push({ message: `Name must be at most ${MAX_NAME} characters` });
  }
  if ((doc.productName?.length ?? 0) > MAX_NAME) {
    problems.push({ message: `Product must be at most ${MAX_NAME} characters` });
  }
  if ((doc.comments?.length ?? 0) > MAX_COMMENTS) {
    problems.push({ message: `Comments must be at most ${MAX_COMMENTS} characters` });
  }
  (doc.steps ?? []).forEach((step, i) => {
    if (!step.type) {
      problems.push({ message: `Step ${i + 1} needs a type` });
    }
  });
  return problems;
}

/** The value ScriptDocument.MASKED_PASSWORD gives a stored password; sending it back keeps the stored one */
export const MASKED_PASSWORD = '********';

/** Data keys (ScriptConstants) */
export const KEYS = {
  minTime: 'minTime',
  maxTime: 'maxTime',
  time: 'time',
  cookieName: 'cookie-name',
  cookieValue: 'cookie-value',
  cookieDomain: 'cookie-domain',
  cookiePath: 'cookie-path',
  userName: 'userName',
  password: 'password',
  realm: 'realm',
  scheme: 'scheme',
  host: 'host',
  port: 'port',
  loggingKey: 'logging-key',
  isStart: 'is-start',
  aggregatorPair: 'aggregator-pair',
} as const;

export function dataValue(step: ScriptStep, key: string): string | undefined {
  return step.data?.find((d) => d.key === key)?.value;
}

export function newUuid(): string {
  return crypto.randomUUID();
}

/**
 * The label the server would give the step (ScriptUtil.getStepLabel), so steps added or changed in
 * the draft read the same as saved ones; think and sleep times also get their comment, as there.
 */
export function withLabel(step: ScriptStep): ScriptStep {
  const value = (key: string) => dataValue(step, key);
  let label = '';
  let comments = step.comments;
  switch (step.type?.toLowerCase()) {
    case 'request':
      label =
        `${step.protocol ?? ''}://${step.hostname ?? ''}${step.simplePath ?? ''}` +
        (step.queryStrings ?? []).map((q, i) => `${i === 0 ? '?' : '&'}${q.key}=${q.value}`).join('');
      break;
    case 'variable':
      label = (step.data ?? []).map((d) => `Variable definition ${d.key}=>${d.value}`).join('');
      break;
    case 'authentication':
      label = `Authentication ${value(KEYS.scheme) ?? 'ALL'} [host: ${value(KEYS.host) ?? ''} user: ${value(KEYS.userName) ?? ''}]`;
      break;
    case 'thinktime':
      label = `Think time ${value(KEYS.minTime) ?? '0'}-${value(KEYS.maxTime) ?? '0'}`;
      comments = `ThinkTime ${value(KEYS.minTime)}-${value(KEYS.maxTime)}`;
      break;
    case 'logic':
      label = `Logic Step: ${step.name}`;
      break;
    case 'cookie':
      label = `Set Cookie: ${value(KEYS.cookieName) ?? ''} = ${value(KEYS.cookieValue) ?? ''}`;
      break;
    case 'sleep':
      label = (step.data ?? []).map((d) => `Sleep for ${d.value}`).join('');
      comments = `SLEEP ${value(KEYS.time)}`;
      break;
    case 'clear':
      label = 'Clear session';
      break;
    case 'timer':
      label = `${value(KEYS.loggingKey)}:${value(KEYS.isStart)}`;
      break;
  }
  return { ...step, label: label.length > 1024 ? `${label.slice(0, 1021)}...` : label, comments };
}

function data(type: string | undefined, entries: Record<string, string | undefined>): Schemas['StepDataTO'][] {
  return Object.entries(entries)
    .filter(([, v]) => v !== undefined && v !== '')
    .map(([key, value]) => ({ key, value, ...(type ? { type } : {}) }));
}

/** New steps as ScriptStepFactory builds them */
export const createStep = {
  variable: (name: string, value: string): ScriptStep =>
    withLabel({ uuid: newUuid(), type: 'variable', data: [{ key: name, value }] }),
  thinkTime: (min: string, max: string): ScriptStep =>
    withLabel({ uuid: newUuid(), type: 'thinkTime', data: data('thinkTime', { [KEYS.minTime]: min, [KEYS.maxTime]: max }) }),
  sleep: (time: string): ScriptStep => withLabel({ uuid: newUuid(), type: 'sleep', data: data('sleep', { [KEYS.time]: time }) }),
  cookie: (c: { name: string; value: string; domain?: string; path?: string }): ScriptStep =>
    withLabel({
      uuid: newUuid(),
      type: 'cookie',
      data: data('cookie', {
        [KEYS.cookieName]: c.name,
        [KEYS.cookieValue]: c.value,
        [KEYS.cookieDomain]: c.domain,
        [KEYS.cookiePath]: c.path,
      }),
    }),
  authentication: (a: Authentication): ScriptStep =>
    withLabel({ uuid: newUuid(), type: 'authentication', data: authenticationData(a) }),
  clear: (): ScriptStep => withLabel({ uuid: newUuid(), type: 'clear', data: [] }),
};

export interface Authentication {
  userName: string;
  /** MASKED_PASSWORD keeps the stored password */
  password: string;
  realm?: string;
  scheme?: string;
  host?: string;
  port?: string;
}

export function authenticationData(a: Authentication): Schemas['StepDataTO'][] {
  return data('authentication', {
    [KEYS.userName]: a.userName,
    [KEYS.password]: a.password,
    [KEYS.realm]: a.realm,
    [KEYS.scheme]: a.scheme,
    [KEYS.host]: a.host,
    [KEYS.port]: a.port,
  });
}

/** Where a new or pasted step goes: before the first selected step, else at the end (ScriptEditor.getInsertIndex) */
export function insertIndex(steps: ScriptStep[], selected: Set<string>): number {
  const first = steps.findIndex((s) => selected.has(s.uuid ?? ''));
  return first >= 0 ? first : steps.length;
}

export function insertSteps(steps: ScriptStep[], added: ScriptStep[], index: number): ScriptStep[] {
  return [...steps.slice(0, index), ...added, ...steps.slice(index)];
}

/**
 * Copies of steps to paste (ScriptUtil.copyScriptStep): new uuids, so the server treats them as new
 * steps. A copy doesn't carry its recorded response, which the editor never loads.
 */
export function copiesOf(steps: ScriptStep[]): ScriptStep[] {
  const uuids = new Map(steps.map((s) => [s.uuid, newUuid()]));
  return steps.map((s) => {
    const copy = { ...structuredClone(s), uuid: uuids.get(s.uuid)!, stepIndex: undefined, response: undefined };
    // a timer copied with its other half points at that half's copy
    copy.data = copy.data?.map((d) =>
      d.key === KEYS.aggregatorPair && uuids.has(d.value) ? { ...d, value: uuids.get(d.value) } : d,
    );
    return copy;
  });
}

/**
 * A think or sleep time: a whole number of milliseconds, a variable (@name), a function (#function...)
 * or an expression (#{...}), as ThinkTimeEditor and SleepTimeEditor accept.
 */
export function isTimeValue(value: string): boolean {
  const v = value.trim();
  return /^\d+$/.test(v) || v.startsWith('@') || v.startsWith('#function') || /^#\{[^}]+\}$/.test(v);
}

export const METHODS = ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'];
export const PROTOCOLS = ['http', 'https'];

/** Request data types for entries added in the editor (RequestHeaderEditor, QueryStringEditor, PostDataEditor) */
export const ENTRY_TYPES = {
  header: 'requestHeader',
  queryString: 'queryString',
  postData: 'requestPostData',
} as const;

/** A new request (ScriptRequestEditor.insertRequest), with the editor's usual choices filled in */
export function newRequest(): ScriptStep {
  return {
    uuid: newUuid(),
    type: 'request',
    protocol: 'https',
    method: 'GET',
    reqFormat: 'nvp',
    requestheaders: [],
    queryStrings: [],
    postDatas: [],
    requestCookies: [],
    responseheaders: [],
    responseCookies: [],
    responseData: [],
  };
}

/** ScriptConstants.SCRIPT and TEST_DATA */
const SCRIPT_KEY = 'script';
const TEST_DATA = 'test-data';

/** Made-up inputs for trying a logic step, stored in the step as JSF's LogicTestData does */
export interface LogicTestData {
  variables: Record<string, string>;
  requestHeaders: Record<string, string>;
  responseHeaders: Record<string, string>;
  requestBody: string;
  responseBody: string;
}

export function logicScript(step: ScriptStep): string {
  return step.data?.find((d) => d.key === SCRIPT_KEY)?.value ?? '';
}

const toMap = (entries: Schemas['StepDataTO'][] | undefined, only: (e: Schemas['StepDataTO']) => boolean) =>
  Object.fromEntries((entries ?? []).filter(only).map((e) => [e.key ?? '', e.value ?? '']));
const testEntries = (map: Record<string, string>) =>
  Object.entries(map)
    .filter(([key]) => key.trim())
    .map(([key, value]) => ({ key, value, type: TEST_DATA }));

/** The test data a logic step keeps (variables in its data, headers in its header lists) */
export function readTestData(step: ScriptStep): LogicTestData {
  return {
    variables: toMap(step.data, (d) => d.key !== SCRIPT_KEY && d.type !== SCRIPT_KEY),
    requestHeaders: toMap(step.requestheaders, () => true),
    responseHeaders: toMap(step.responseheaders, () => true),
    requestBody: step.payload ?? '',
    // a recorded response isn't loaded with the script; LogicDialog fetches it
    responseBody: '',
  };
}

/** Variables the script declares (ScriptUtil.getDeclaredVariables), plus those its assignments set */
export function declaredVariables(steps: ScriptStep[]): string[] {
  const names = new Set<string>();
  for (const step of steps) {
    if (step.type === 'variable') {
      step.data?.forEach((d) => d.key && names.add(d.key));
    }
    step.responseData?.filter(isAssignment).forEach((d) => d.key && names.add(d.key));
  }
  return [...names].sort();
}

/** The last request before a position, whose exchange a new logic step is tested against */
export function previousRequest(steps: ScriptStep[], index: number): ScriptStep | undefined {
  return steps.slice(0, index).reverse().find((s) => s.type === 'request');
}

/** Test data from a request (new LogicTestData(previousRequest, script)) */
export function testDataFrom(request: ScriptStep | undefined, steps: ScriptStep[]): LogicTestData {
  const variables: Record<string, string> = { mode: 'test', THREAD_ID: '1' };
  for (const name of declaredVariables(steps)) {
    variables[name] ??= '';
  }
  const body =
    request?.payload ||
    (request?.postDatas ?? []).map((p) => `${encodeURIComponent(p.key ?? '')}=${encodeURIComponent(p.value ?? '')}`).join('&');
  return {
    variables,
    requestHeaders: toMap(request?.requestheaders, () => true),
    responseHeaders: toMap(request?.responseheaders, () => true),
    requestBody: body,
    responseBody: '',
  };
}

/** A logic step with its script and test data (ScriptStepFactory.createLogic, LogicTestData.setInStep) */
export function logicStep(
  base: ScriptStep | undefined,
  fields: { name: string; group: string; script: string; testData: LogicTestData },
): ScriptStep {
  return withLabel({
    ...(base ?? { uuid: newUuid(), type: 'logic' }),
    type: 'logic',
    name: fields.name,
    scriptGroupName: fields.group || undefined,
    comments: `Logic Step: ${fields.name}`,
    data: [{ key: SCRIPT_KEY, value: fields.script, type: SCRIPT_KEY }, ...testEntries(fields.testData.variables)],
    requestheaders: testEntries(fields.testData.requestHeaders),
    responseheaders: testEntries(fields.testData.responseHeaders),
    payload: fields.testData.requestBody || undefined,
  });
}

/** The uuid of a timer's other half (AggregatorEditor.getAggregatorPair) */
export function timerPairId(step: ScriptStep): string | undefined {
  return dataValue(step, KEYS.aggregatorPair);
}

export function isTimerStart(step: ScriptStep): boolean {
  return dataValue(step, KEYS.isStart) === 'START';
}

function timer(uuid: string, pair: string, name: string, start: boolean): ScriptStep {
  return withLabel({
    uuid,
    type: 'timer',
    data: [
      { key: KEYS.loggingKey, value: name, type: 'timer' },
      { key: KEYS.isStart, value: start ? 'START' : 'STOP', type: 'timer' },
      { key: KEYS.aggregatorPair, value: pair, type: 'timer' },
    ],
  });
}

/**
 * Why the selected steps can't be timed together, or undefined: a timer group needs two or more
 * steps next to each other (AggregatorEditor.aggregateCheck).
 */
export function timerGroupProblem(steps: ScriptStep[], selected: Set<string>): string | undefined {
  const positions = steps.flatMap((s, i) => (selected.has(s.uuid ?? '') ? [i] : []));
  if (positions.length < 2) return 'Select two or more steps next to each other';
  if (positions.some((p, i) => p !== positions[0]! + i)) return 'The selected steps have to be next to each other';
  return undefined;
}

/** Wraps the selected steps in a timer: a start before the first, a stop after the last (AggregatorEditor.insert) */
export function addTimerGroup(steps: ScriptStep[], selected: Set<string>, name: string): ScriptStep[] {
  const first = steps.findIndex((s) => selected.has(s.uuid ?? ''));
  const last = steps.length - 1 - [...steps].reverse().findIndex((s) => selected.has(s.uuid ?? ''));
  const startId = newUuid();
  const stopId = newUuid();
  return [
    ...steps.slice(0, first),
    timer(startId, stopId, name, true),
    ...steps.slice(first, last + 1),
    timer(stopId, startId, name, false),
    ...steps.slice(last + 1),
  ];
}

/** Renames a timer and its other half (AggregatorEditor.edit) */
export function renameTimer(steps: ScriptStep[], uuid: string, name: string): ScriptStep[] {
  const step = steps.find((s) => s.uuid === uuid);
  const both = new Set([uuid, step ? timerPairId(step) : undefined]);
  return steps.map((s) =>
    both.has(s.uuid) && s.type === 'timer'
      ? withLabel({ ...s, data: s.data?.map((d) => (d.key === KEYS.loggingKey ? { ...d, value: name } : d)) })
      : s,
  );
}

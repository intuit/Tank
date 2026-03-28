/** Rich in-memory model for the script editor (parsed from Tank XML). */

export type StepType =
  | 'request'
  | 'thinkTime'
  | 'sleep'
  | 'variable'
  | 'logic'
  | 'cookie'
  | 'authentication'
  | 'clear'
  | 'AGGREGATE';

export interface StepData {
  key: string;
  value: string;
  type: string;
  phase: string;
}

export interface ScriptStep {
  uuid: string;
  stepIndex: number;
  type: StepType;
  scriptGroupName?: string;
  name?: string;
  label?: string;
  method?: string;
  protocol?: string;
  hostname?: string;
  simplePath?: string;
  url?: string;
  loggingKey?: string;
  onFail?: string;
  comments?: string;
  payload?: string;
  response?: string;
  respFormat?: string;
  reqFormat?: string;
  mimetype?: string;
  result?: string;
  // For think time / sleep
  minThinkTime?: string;
  maxThinkTime?: string;
  // For variable
  varKey?: string;
  varValue?: string;
  // For logic
  script?: string;
  // For cookie
  cookieName?: string;
  cookieValue?: string;
  cookiePath?: string;
  cookieDomain?: string;
  // For authentication
  authScheme?: string;
  authUser?: string;
  authPassword?: string;
  authHost?: string;
  authPort?: string;
  authRealm?: string;
  // Collections (request type)
  requestheaders: StepData[];
  responseheaders: StepData[];
  requestCookies: StepData[];
  responseCookies: StepData[];
  postDatas: StepData[];
  queryStrings: StepData[];
  data: StepData[];
  responseData: StepData[];
}

export interface EditableScript {
  id: number;
  name: string;
  productName?: string;
  comments?: string;
  creator?: string;
  steps: ScriptStep[];
}

/** Build a blank step of the given type with a new UUID */
export function blankStep(type: StepType, stepIndex: number): ScriptStep {
  return {
    uuid: crypto.randomUUID(),
    stepIndex,
    type,
    requestheaders: [],
    responseheaders: [],
    requestCookies: [],
    responseCookies: [],
    postDatas: [],
    queryStrings: [],
    data: [],
    responseData: [],
    method: type === 'request' ? 'GET' : undefined,
    protocol: type === 'request' ? 'https' : undefined,
    reqFormat: type === 'request' ? 'nvp' : undefined,
    respFormat: type === 'request' ? 'json' : undefined,
    onFail: type === 'request' ? 'continue' : undefined,
  };
}

export function stepLabel(step: ScriptStep): string {
  switch (step.type) {
    case 'request':
      return step.label || `${step.method ?? 'GET'} ${step.hostname ?? ''}${step.simplePath ?? ''}`;
    case 'thinkTime':
      return `Think Time (${step.minThinkTime ?? 0}–${step.maxThinkTime ?? 0} ms)`;
    case 'sleep':
      return `Sleep ${step.payload ?? 0} ms`;
    case 'variable':
      return `Variable: ${step.varKey ?? ''} = ${step.varValue ?? ''}`;
    case 'logic':
      return `Logic: ${step.name ?? '(unnamed)'}`;
    case 'cookie':
      return `Set Cookie: ${step.cookieName ?? ''}`;
    case 'authentication':
      return `Auth: ${step.authUser ?? ''} @ ${step.authHost ?? ''}`;
    case 'clear':
      return 'Clear Session';
    case 'AGGREGATE':
      return `Timer Group: ${step.loggingKey ?? ''}`;
    default:
      return step.type;
  }
}

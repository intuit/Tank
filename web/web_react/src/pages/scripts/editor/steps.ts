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

export function deleteSteps(steps: ScriptStep[], uuids: Set<string>): ScriptStep[] {
  return steps.filter((s) => !uuids.has(s.uuid ?? ''));
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

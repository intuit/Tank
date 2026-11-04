import type { Schemas } from '../../../api/client';
import { isAssignment } from './steps';

type StepData = Schemas['StepDataTO'];

/**
 * Validation operators in ValidationType's order, which is the order a stored value's prefix is
 * matched in (ResponseContentParser.extractCondition), except that "==Any" is checked before "==".
 */
export const OPERATORS = [
  { name: 'equals', prefix: '==', label: 'Equals' },
  { name: 'notequals', prefix: '!=', label: 'Not equals' },
  { name: 'empty', prefix: 'Empty', label: 'Is empty' },
  { name: 'notempty', prefix: 'Not empty', label: 'Is not empty' },
  { name: 'contains', prefix: 'Contains', label: 'Contains' },
  { name: 'doesnotcontain', prefix: 'Does not contain', label: 'Does not contain' },
  { name: 'lessthan', prefix: 'Less Than', label: 'Less than' },
  { name: 'greaterthan', prefix: 'Greater Than', label: 'Greater than' },
  { name: 'equalsany', prefix: '==Any', label: 'Equals any' },
] as const;
export type OperatorName = (typeof OPERATORS)[number]['name'];

/** Operators that compare nothing, so they take no value */
export const NO_VALUE = new Set<OperatorName>(['empty', 'notempty']);

export const LOCATIONS = ['Body', 'Header', 'Cookie'] as const;
export type Location = (typeof LOCATIONS)[number];

export type Phase = 'PRE_REQUEST' | 'POST_REQUEST';

export interface Validation {
  phase: Phase;
  location: Location;
  /** The lookup: a header or cookie name, or a JSON path or XPath into the body */
  key: string;
  operator: OperatorName;
  /** The expected value, or a variable */
  value: string;
  /** As loaded; written back unchanged when nothing above changes */
  original?: StepData;
}

export interface Assignment {
  location: Location;
  /** The variable to set */
  variable: string;
  /** The lookup, as for a validation */
  lookup: string;
  original?: StepData;
}

/** A response entry's location, from its type (RequestDataContentWrapper.getDataType) */
export function locationOf(type: string | undefined): Location {
  if (type?.startsWith('header')) return 'Header';
  if (type?.startsWith('cookie')) return 'Cookie';
  return 'Body';
}

export function parseOperator(value: string): { operator: OperatorName; rest: string } {
  if (value.startsWith('==Any')) {
    return { operator: 'equalsany', rest: value.slice('==Any'.length) };
  }
  const op = OPERATORS.find((o) => value.startsWith(o.prefix));
  // a value with no operator compares as equals, with the whole value
  return op ? { operator: op.name, rest: value.slice(op.prefix.length) } : { operator: 'equals', rest: value };
}

/** Splits a request's responseData into its validations and assignments, in order */
export function parseRules(data: StepData[] | undefined): { validations: Validation[]; assignments: Assignment[] } {
  const validations: Validation[] = [];
  const assignments: Assignment[] = [];
  for (const entry of data ?? []) {
    const value = entry.value ?? '';
    if (isAssignment(entry)) {
      assignments.push({
        location: locationOf(entry.type),
        variable: entry.key ?? '',
        lookup: value.startsWith('=') ? value.slice(1) : value,
        original: entry,
      });
    } else {
      const { operator, rest } = parseOperator(value);
      validations.push({
        phase: entry.phase === 'PRE_REQUEST' ? 'PRE_REQUEST' : 'POST_REQUEST',
        location: locationOf(entry.type),
        key: entry.key ?? '',
        operator,
        value: rest,
        original: entry,
      });
    }
  }
  return { validations, assignments };
}

function sameValidation(v: Validation): boolean {
  if (!v.original) return false;
  const parsed = parseRules([v.original]).validations[0]!;
  return parsed.phase === v.phase && parsed.location === v.location && parsed.key === v.key
    && parsed.operator === v.operator && parsed.value === v.value;
}

function sameAssignment(a: Assignment): boolean {
  if (!a.original) return false;
  const parsed = parseRules([a.original]).assignments[0]!;
  return parsed.location === a.location && parsed.variable === a.variable && parsed.lookup === a.lookup;
}

/**
 * Back to responseData entries (RequestDataContentWrapper.getData). Unchanged entries are written
 * exactly as loaded, so legacy types such as statusValidation survive an edit of something else.
 */
export function encodeRules(validations: Validation[], assignments: Assignment[]): StepData[] {
  return [
    ...validations.map((v) => {
      if (sameValidation(v)) return v.original!;
      const prefix = OPERATORS.find((o) => o.name === v.operator)!.prefix;
      return {
        ...v.original,
        key: v.key,
        type: `${v.location.toLowerCase()}Validation`,
        phase: v.phase,
        value: prefix + (NO_VALUE.has(v.operator) ? '' : v.value),
      };
    }),
    ...assignments.map((a) => {
      if (sameAssignment(a)) return a.original!;
      return {
        ...a.original,
        key: a.variable,
        type: `${a.location.toLowerCase()}Assignment`,
        phase: 'POST_REQUEST',
        value: `=${a.lookup}`,
      };
    }),
  ];
}

/** On-failure actions keep a "goto" target in the same field (Request.onFail, default "abort") */
export function parseOnFail(onFail: string | undefined): { action: string; group: string } {
  const value = onFail || 'abort';
  return value.startsWith('goto') ? { action: 'goto', group: value.slice('goto'.length).trim() } : { action: value, group: '' };
}

/** The agent reads the whole rest of the value as the group name (TestPlanRunner), spaces and all */
export function encodeOnFail(action: string, group: string): string {
  return action === 'goto' ? `goto ${group.trim()}` : action;
}

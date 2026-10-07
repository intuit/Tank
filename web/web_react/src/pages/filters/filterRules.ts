import type { Schemas } from '../../api/client';
import type { Filter } from './useFilters';

export type Condition = Schemas['FilterConditionTO'];
export type Action = Schemas['FilterActionTO'];
export type ActionType = NonNullable<Action['action']>;
export type ActionField = Schemas['FilterActionField'];
type Option = Schemas['Option'];

export const MAX_NAME_LENGTH = 255;

export const ACTION_TYPES: { label: string; value: ActionType }[] = [
  { label: 'Remove', value: 'remove' },
  { label: 'Replace', value: 'replace' },
  { label: 'Add', value: 'add' },
];

/** A filter before it's saved (ScriptFilterCreationBean.newFilter) */
export function blankFilter(): Filter {
  return { name: '', allConditionsMustPass: true, filterType: 'INTERNAL', persist: true, conditions: [], actions: [] };
}

/** ScriptFilterConditionBean's defaults; scope and match are stored as text the filter engine compares */
export const newCondition = (): Condition => ({ scope: 'Hostname', condition: 'Contains', value: '' });

/** ScriptFilterActionBean's defaults */
export const newAction = (): Action => ({ action: 'remove', scope: 'request', key: '', value: '' });

/** The scopes an action type works on (addActionScopes, removeActionScopes, replaceActionScopes), labelled readably */
export function scopesFor(type: ActionType | undefined, options: Record<string, Option[]> | undefined): Option[] {
  return (options?.[`${type ?? 'remove'}ActionScopes`] ?? []).map((o) => ({ ...o, label: scopeLabel(o.value) }));
}

/** "responseData" as "Response data"; the server's scope names are the stored values */
export function scopeLabel(scope: string | undefined): string {
  const words = (scope ?? '').replace(/([a-z])([A-Z])/g, '$1 $2').toLowerCase();
  return words.charAt(0).toUpperCase() + words.slice(1);
}

/** Which inputs an action shows (ConfigServiceV2Impl.filterActionFields, from ScriptFilterActionBean) */
export function fieldFor(fields: ActionField[] | undefined, action: Action): ActionField {
  return (
    fields?.find((f) => f.actionType === action.action && f.scope === action.scope) ?? {
      key: true,
      value: true,
      onFail: false,
      prefix: 'NONE',
    }
  );
}

/**
 * Changes an action's type or scope. A scope the new type doesn't have becomes its first one, and
 * inputs the result doesn't show are cleared, so nothing hidden is saved.
 */
export function changeAction(
  action: Action,
  change: Partial<Pick<Action, 'action' | 'scope'>>,
  options: Record<string, Option[]> | undefined,
  fields: ActionField[] | undefined,
): Action {
  const next = { ...action, ...change };
  const scopes = scopesFor(next.action, options);
  if (scopes.length && !scopes.some((s) => s.value === next.scope)) {
    next.scope = scopes[0]!.value;
  }
  const before = fieldFor(fields, action);
  const field = fieldFor(fields, next);
  if (!field.key) next.key = '';
  if (!field.value && !field.onFail) next.value = '';
  // a different kind of value (a failure type, or a value with another prefix) starts empty
  if (field.onFail !== before.onFail || field.prefix !== before.prefix) next.value = '';
  return next;
}

/**
 * An assignment or validation value split into its prefix and the rest, as ResponseContentParser
 * reads them: "=" for an assignment, the validation operator (==, !=, Contains, ==Any...) for a
 * validation. Longer operators are tried first, so "==Any" isn't read as "==".
 */
export function splitValue(value: string | undefined, prefix: ActionField['prefix'], operators: string[]): { prefix: string; rest: string } {
  const text = value ?? '';
  if (prefix === 'ASSIGNMENT') {
    return { prefix: '=', rest: text.startsWith('=') ? text.slice(1) : text };
  }
  if (prefix === 'VALIDATION') {
    const operator = [...operators].sort((a, b) => b.length - a.length).find((o) => text.startsWith(o));
    return operator ? { prefix: operator, rest: text.slice(operator.length) } : { prefix: operators[0] ?? '==', rest: text };
  }
  return { prefix: '', rest: text };
}

/** What a filter needs before it can be saved */
export function filterProblems(filter: Filter): string[] {
  const problems: string[] = [];
  const name = filter.name?.trim() ?? '';
  if (!name) problems.push('Name is required');
  if (name.length > MAX_NAME_LENGTH) problems.push(`Name can be at most ${MAX_NAME_LENGTH} characters`);
  return problems;
}

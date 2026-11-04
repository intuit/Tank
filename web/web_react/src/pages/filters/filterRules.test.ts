import { describe, expect, it } from 'vitest';
import { changeAction, filterProblems, scopeLabel, splitValue, type ActionField } from './filterRules';

const OPTIONS = {
  addActionScopes: [{ value: 'validation' }, { value: 'assignment' }, { value: 'sleepTime' }],
  removeActionScopes: [{ value: 'request' }, { value: 'requestHeader' }],
  replaceActionScopes: [{ value: 'requestHeader' }, { value: 'onFailure' }, { value: 'validation' }],
};
const FIELDS: ActionField[] = [
  { actionType: 'add', scope: 'validation', key: true, value: true, prefix: 'VALIDATION' },
  { actionType: 'add', scope: 'assignment', key: true, value: true, prefix: 'ASSIGNMENT' },
  { actionType: 'add', scope: 'sleepTime', key: false, value: true, prefix: 'NONE' },
  { actionType: 'remove', scope: 'request', key: false, value: false, prefix: 'NONE' },
  { actionType: 'remove', scope: 'requestHeader', key: true, value: false, prefix: 'NONE' },
  { actionType: 'replace', scope: 'requestHeader', key: true, value: true, prefix: 'NONE' },
  { actionType: 'replace', scope: 'onFailure', key: false, value: false, onFail: true, prefix: 'NONE' },
  { actionType: 'replace', scope: 'validation', key: true, value: true, prefix: 'VALIDATION' },
];
const OPERATORS = ['==', '!=', 'Empty', 'Not empty', 'Contains', 'Does not contain', 'Less Than', 'Greater Than', '==Any'];

describe('filter actions', () => {
  it('moves to a scope the new type has, clearing hidden inputs', () => {
    const header = { action: 'replace' as const, scope: 'requestHeader', key: 'X-Id', value: '#{id}' };
    expect(changeAction(header, { action: 'remove' }, OPTIONS, FIELDS)).toEqual({ action: 'remove', scope: 'requestHeader', key: 'X-Id', value: '' });
    expect(changeAction(header, { action: 'add' }, OPTIONS, FIELDS)).toEqual({ action: 'add', scope: 'validation', key: 'X-Id', value: '' });
    expect(changeAction(header, { scope: 'onFailure' }, OPTIONS, FIELDS)).toEqual({ action: 'replace', scope: 'onFailure', key: '', value: '' });
  });

  it('keeps a validation value when only the type changes', () => {
    const v = { action: 'add' as const, scope: 'validation', key: '$.ok', value: '==true' };
    expect(changeAction(v, { action: 'replace' }, OPTIONS, FIELDS)).toEqual({ ...v, action: 'replace' });
  });

  it('splits stored values into prefix and rest', () => {
    expect(splitValue('==Anya|b', 'VALIDATION', OPERATORS)).toEqual({ prefix: '==Any', rest: 'a|b' });
    expect(splitValue('==200', 'VALIDATION', OPERATORS)).toEqual({ prefix: '==', rest: '200' });
    expect(splitValue('Not empty', 'VALIDATION', OPERATORS)).toEqual({ prefix: 'Not empty', rest: '' });
    expect(splitValue('Does not containerrorRedirect.htm', 'VALIDATION', OPERATORS)).toEqual({ prefix: 'Does not contain', rest: 'errorRedirect.htm' });
    expect(splitValue('=$.cart.id', 'ASSIGNMENT', OPERATORS)).toEqual({ prefix: '=', rest: '$.cart.id' });
    expect(splitValue('plain', 'NONE', OPERATORS)).toEqual({ prefix: '', rest: 'plain' });
  });

  it('labels scopes readably', () => {
    expect(['responseData', 'onFailure', 'request', 'host'].map(scopeLabel)).toEqual(['Response data', 'On failure', 'Request', 'Host']);
  });

  it('needs a name', () => {
    expect(filterProblems({ name: ' ' })).toEqual(['Name is required']);
    expect(filterProblems({ name: 'x'.repeat(256) })).toEqual(['Name can be at most 255 characters']);
    expect(filterProblems({ name: 'ok', actions: [] })).toEqual([]);
  });
});

import { describe, expect, it } from 'vitest';
import { encodeOnFail, encodeRules, parseOnFail, parseOperator, parseRules } from './responseRules';

describe('parseOperator', () => {
  it('matches prefixes in the server order, Equals any before Equals', () => {
    expect(parseOperator('==200')).toEqual({ operator: 'equals', rest: '200' });
    expect(parseOperator('==Anya,b')).toEqual({ operator: 'equalsany', rest: 'a,b' });
    expect(parseOperator('!=error')).toEqual({ operator: 'notequals', rest: 'error' });
    expect(parseOperator('Not empty')).toEqual({ operator: 'notempty', rest: '' });
    expect(parseOperator('Does not containfail')).toEqual({ operator: 'doesnotcontain', rest: 'fail' });
    expect(parseOperator('Greater Than@{min}')).toEqual({ operator: 'greaterthan', rest: '@{min}' });
    expect(parseOperator('plain')).toEqual({ operator: 'equals', rest: 'plain' });
  });
});

describe('parseRules and encodeRules', () => {
  const data = [
    { key: '$.status', value: '==ok', type: 'bodyValidation', phase: 'POST_REQUEST' },
    { key: 'X-Auth', value: 'Not empty', type: 'headerValidation', phase: 'PRE_REQUEST' },
    { key: 'code', value: '==200', type: 'statusValidation', phase: 'POST_REQUEST' },
    { key: 'cartId', value: '=$.cart.id', type: 'bodyAssignment', phase: 'POST_REQUEST' },
    { key: 'token', value: '=token', type: 'responseContent' },
  ];

  it('splits validations from assignments with their parts', () => {
    const { validations, assignments } = parseRules(data);
    expect(validations.map(({ original, ...v }) => v)).toEqual([
      { phase: 'POST_REQUEST', location: 'Body', key: '$.status', operator: 'equals', value: 'ok' },
      { phase: 'PRE_REQUEST', location: 'Header', key: 'X-Auth', operator: 'notempty', value: '' },
      { phase: 'POST_REQUEST', location: 'Body', key: 'code', operator: 'equals', value: '200' },
    ]);
    expect(assignments.map(({ original, ...a }) => a)).toEqual([
      { location: 'Body', variable: 'cartId', lookup: '$.cart.id' },
      { location: 'Body', variable: 'token', lookup: 'token' },
    ]);
  });

  it('writes unchanged entries back exactly, legacy types included', () => {
    const { validations, assignments } = parseRules(data);
    expect(encodeRules(validations, assignments)).toEqual([data[0], data[1], data[2], data[3], data[4]]);
  });

  it('encodes changed and new entries as the JSF editor does', () => {
    const { validations, assignments } = parseRules(data);
    validations[0] = { ...validations[0]!, operator: 'contains', value: 'ok' };
    validations.push({ phase: 'POST_REQUEST', location: 'Cookie', key: 'sid', operator: 'notempty', value: 'ignored' });
    assignments[0] = { ...assignments[0]!, location: 'Header', lookup: 'X-Cart' };
    const encoded = encodeRules(validations, assignments);

    expect(encoded[0]).toEqual({ key: '$.status', value: 'Containsok', type: 'bodyValidation', phase: 'POST_REQUEST' });
    expect(encoded[3]).toEqual({ key: 'sid', value: 'Not empty', type: 'cookieValidation', phase: 'POST_REQUEST' });
    expect(encoded[4]).toEqual({ key: 'cartId', value: '=X-Cart', type: 'headerAssignment', phase: 'POST_REQUEST' });
  });
});

describe('on failure', () => {
  it('keeps the whole goto group name', () => {
    expect(parseOnFail(undefined)).toEqual({ action: 'abort', group: '' });
    expect(parseOnFail('goto checkout flow')).toEqual({ action: 'goto', group: 'checkout flow' });
    expect(encodeOnFail('goto', ' checkout flow ')).toBe('goto checkout flow');
    expect(encodeOnFail('skip', 'ignored')).toBe('skip');
  });
});

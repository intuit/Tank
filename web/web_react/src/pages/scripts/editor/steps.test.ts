import { describe, expect, it } from 'vitest';
import { deleteSteps, hasAssignment, hasValidation, isAssignment, moveSteps, validateScript, type ScriptStep } from './steps';

const steps: ScriptStep[] = ['a', 'b', 'c', 'd', 'e'].map((uuid) => ({ uuid, type: 'request' }));
const order = (list: ScriptStep[]) => list.map((s) => s.uuid).join('');

describe('moveSteps', () => {
  it('moves a block, keeping its order, so it starts at the position', () => {
    expect(order(moveSteps(steps, new Set(['d', 'b']), 1))).toBe('bdace');
    expect(order(moveSteps(steps, new Set(['a']), 3))).toBe('bcade');
    expect(order(moveSteps(steps, new Set(['a', 'b']), 99))).toBe('cdeab');
    expect(order(moveSteps(steps, new Set(['c']), 0))).toBe('cabde');
  });
});

describe('deleteSteps', () => {
  it('removes the selected steps', () => {
    expect(order(deleteSteps(steps, new Set(['b', 'e'])))).toBe('acd');
  });
});

describe('validations and assignments', () => {
  it('tells them apart by type, and legacy entries by value', () => {
    expect(isAssignment({ type: 'bodyAssignment', value: '=x' })).toBe(true);
    expect(isAssignment({ type: 'headerValidation', value: '==x' })).toBe(false);
    expect(isAssignment({ type: 'responseContent', value: '=token' })).toBe(true);
    expect(isAssignment({ type: 'responseContent', value: '==200' })).toBe(false);
  });

  it('flags steps that have them', () => {
    const step: ScriptStep = { type: 'request', responseData: [{ type: 'bodyAssignment', value: '=id' }] };
    expect(hasAssignment(step)).toBe(true);
    expect(hasValidation(step)).toBe(false);
  });
});

describe('validateScript', () => {
  it('needs a name and step types', () => {
    expect(validateScript({ name: ' ', steps: [{ uuid: 'a' }] }).map((p) => p.message)).toEqual([
      'Name is required',
      'Step 1 needs a type',
    ]);
    expect(validateScript({ name: 'ok', comments: 'x'.repeat(1025), steps: [] })).toHaveLength(1);
  });
});

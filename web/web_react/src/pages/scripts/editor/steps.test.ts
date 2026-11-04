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

describe('labels', () => {
  it('match the server for each simple step type', async () => {
    const { createStep, withLabel } = await import('./steps');
    expect(createStep.variable('host', 'store.test').label).toBe('Variable definition host=>store.test');
    const think = createStep.thinkTime('1000', '3000');
    expect(think.label).toBe('Think time 1000-3000');
    expect(think.comments).toBe('ThinkTime 1000-3000');
    expect(createStep.sleep('500').label).toBe('Sleep for 500');
    expect(createStep.cookie({ name: 'sid', value: 'abc' }).label).toBe('Set Cookie: sid = abc');
    expect(createStep.authentication({ userName: 'bob', password: 'p', scheme: 'Basic', host: 'h' }).label).toBe(
      'Authentication Basic [host: h user: bob]',
    );
    expect(createStep.clear().label).toBe('Clear session');
    expect(
      withLabel({ type: 'request', protocol: 'https', hostname: 'h', simplePath: '/p', queryStrings: [{ key: 'q', value: '1' }, { key: 'r', value: '2' }] }).label,
    ).toBe('https://h/p?q=1&r=2');
  });
});

describe('insert and paste', () => {
  it('goes before the first selected step, else at the end', async () => {
    const { insertIndex, insertSteps } = await import('./steps');
    expect(insertIndex(steps, new Set(['d', 'b']))).toBe(1);
    expect(insertIndex(steps, new Set())).toBe(5);
    expect(order(insertSteps(steps, [{ uuid: 'x' }], 1))).toBe('axbcde');
  });

  it('gives copies new uuids and no response', async () => {
    const { copiesOf } = await import('./steps');
    const [copy] = copiesOf([{ uuid: 'a', type: 'request', response: 'html', stepIndex: 3, requestheaders: [{ key: 'k' }] }]);
    expect(copy!.uuid).not.toBe('a');
    expect(copy!.response).toBeUndefined();
    expect(copy!.requestheaders).toEqual([{ key: 'k' }]);
  });
});

describe('isTimeValue', () => {
  it('accepts numbers, variables, functions and expressions', async () => {
    const { isTimeValue } = await import('./steps');
    for (const ok of ['1000', '@delay', '#function.int.random.1.5', '#{x}']) expect(isTimeValue(ok)).toBe(true);
    for (const bad of ['', '1.5s', 'ten', '#{', '-1']) expect(isTimeValue(bad)).toBe(false);
  });
});

describe('timer groups', () => {
  it('needs two or more neighbouring steps', async () => {
    const { timerGroupProblem } = await import('./steps');
    expect(timerGroupProblem(steps, new Set(['b']))).toMatch(/two or more/);
    expect(timerGroupProblem(steps, new Set(['b', 'd']))).toMatch(/next to each other/);
    expect(timerGroupProblem(steps, new Set(['c', 'b']))).toBeUndefined();
  });

  it('wraps the selection in a paired start and stop', async () => {
    const { addTimerGroup, isTimerStart, timerPairId } = await import('./steps');
    const timed = addTimerGroup(steps, new Set(['b', 'c']), 'browse');
    expect(timed.map((s) => s.type === 'timer' ? (isTimerStart(s) ? 'S' : 'E') : s.uuid).join('')).toBe('aSbcEde');
    const [start, stop] = [timed[1]!, timed[4]!];
    expect(timerPairId(start)).toBe(stop.uuid);
    expect(timerPairId(stop)).toBe(start.uuid);
    expect(start.label).toBe('browse:START');
    expect(stop.label).toBe('browse:STOP');
  });

  it('renames and deletes both halves together', async () => {
    const { addTimerGroup, deleteSteps, renameTimer } = await import('./steps');
    const timed = addTimerGroup(steps, new Set(['b', 'c']), 'browse');
    const renamed = renameTimer(timed, timed[4]!.uuid!, 'catalog');
    expect([renamed[1]!.label, renamed[4]!.label]).toEqual(['catalog:START', 'catalog:STOP']);
    expect(order(deleteSteps(timed, new Set([timed[1]!.uuid!])))).toBe('abcde');
  });

  it('points copied halves at each other', async () => {
    const { addTimerGroup, copiesOf, timerPairId } = await import('./steps');
    const timed = addTimerGroup(steps, new Set(['b', 'c']), 'browse');
    const [start, , , stop] = copiesOf(timed.slice(1, 5));
    expect(timerPairId(start!)).toBe(stop!.uuid);
    expect(timerPairId(stop!)).toBe(start!.uuid);
  });
});

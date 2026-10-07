import { describe, expect, it } from 'vitest';
import { testPlanPercentageWarning, totalUsers, validateProject, type ProjectDetail } from './validation';

function project(overrides: Partial<ProjectDetail> = {}): ProjectDetail {
  return {
    id: 1,
    name: 'Checkout',
    owner: 'alice',
    settings: { incrementStrategy: 'increasing', terminationPolicy: 'time', userIntervalIncrement: 1 },
    regions: [{ region: 'US East (Ohio)', users: '10', percentage: '0' }],
    testPlans: [{ name: 'Main', userPercentage: 100, scriptGroups: [{ name: 'Login', loop: 1, scripts: [] }] }],
    variables: {},
    ...overrides,
  };
}

describe('validateProject', () => {
  it('accepts a valid project', () => {
    expect(validateProject(project())).toEqual([]);
  });

  it('requires a name and an owner', () => {
    expect(validateProject(project({ name: '  ', owner: '' })).map((p) => p.message)).toEqual([
      'Name is required',
      'Owner is required',
    ]);
  });

  it('checks every region the way the server does', () => {
    const problems = validateProject(project({ regions: [{ region: 'EU (Ireland)', users: '', percentage: '120' }] }));
    expect(problems).toEqual([
      { section: 'usersAndTimes', message: 'EU (Ireland) users are required (0 for none)' },
      { section: 'usersAndTimes', message: 'EU (Ireland) percentage must be a whole number from 0 to 100' },
    ]);
  });

  it('needs a test plan, named groups and loops of at least 1', () => {
    expect(validateProject(project({ testPlans: [] }))).toEqual([
      { section: 'scripts', message: 'At least one test plan is required' },
    ]);
    const problems = validateProject(
      project({
        testPlans: [
          {
            name: 'Main',
            userPercentage: 100,
            scriptGroups: [
              { name: '', loop: 1 },
              { name: 'Browse', loop: 0, scripts: [{ scriptId: 4, scriptName: 'search', loop: 0 }] },
            ],
          },
        ],
      }),
    );
    expect(problems.map((p) => p.message)).toEqual([
      'Main: every script group needs a name',
      'Main / Browse: loop must be at least 1',
      'Main / Browse / search: loop must be at least 1',
    ]);
  });

  it('only asks for a user increment on increasing workloads', () => {
    const standard = project({ settings: { incrementStrategy: 'standard', terminationPolicy: 'time', userIntervalIncrement: 0 } });
    expect(validateProject(standard)).toEqual([]);
    const increasing = project({ settings: { incrementStrategy: 'increasing', terminationPolicy: 'time', userIntervalIncrement: 0 } });
    expect(validateProject(increasing).map((p) => p.message)).toEqual(['User increment must be at least 1']);
  });
});

describe('totalUsers', () => {
  it('adds plain numbers', () => {
    expect(totalUsers(project({ regions: [{ users: '10' }, { users: '5' }] }))).toBe(15);
  });

  it('gives up on expressions', () => {
    expect(totalUsers(project({ regions: [{ users: '4 * RT / 1000' }] }))).toBeUndefined();
  });
});

describe('testPlanPercentageWarning', () => {
  it('warns when plans do not add up to 100%', () => {
    expect(testPlanPercentageWarning(project())).toBeUndefined();
    expect(
      testPlanPercentageWarning(project({ testPlans: [{ name: 'A', userPercentage: 60 }, { name: 'B', userPercentage: 30 }] })),
    ).toBe('Test plan percentages add up to 90%, not 100%');
  });
});

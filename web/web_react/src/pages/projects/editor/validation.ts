import type { Schemas } from '../../../api/client';

export type ProjectDetail = Schemas['ProjectDetail'];

/** Which tab a problem is on, so the editor can point at it */
export type Section = 'general' | 'usersAndTimes' | 'scripts' | 'dataFiles' | 'variables' | 'createJob';

export interface Problem {
  section: Section;
  message: string;
}

const MAX_NAME = 255;
const WHOLE_NUMBER = /^\d+$/;

/**
 * The checks ProjectDetailMapper.validate makes that can be made without the server, so most mistakes
 * show before Save. Time and user expressions are left to the server (TestParamUtil), apart from
 * being required.
 */
export function validateProject(detail: ProjectDetail): Problem[] {
  const problems: Problem[] = [];
  const add = (section: Section, message: string) => problems.push({ section, message });

  const name = detail.name?.trim() ?? '';
  if (!name) {
    add('general', 'Name is required');
  } else if (name.length > MAX_NAME) {
    add('general', `Name must be at most ${MAX_NAME} characters`);
  }
  if (!detail.owner) {
    add('general', 'Owner is required');
  }

  const settings = detail.settings ?? {};
  if ((settings.baselineVirtualUsers ?? 0) < 0) {
    add('usersAndTimes', 'Initial users must not be negative');
  }
  // the server only requires >= 0; the JSF editor required at least 1
  if (settings.incrementStrategy === 'increasing' && (settings.userIntervalIncrement ?? 0) < 1) {
    add('usersAndTimes', 'User increment must be at least 1');
  }
  if ((settings.targetRampRate ?? 0) < 0) {
    add('usersAndTimes', 'Target users per second must not be negative');
  }
  // agent sizing, set on the Create job tab
  if ((settings.numUsersPerAgent ?? 1) < 1) {
    add('createJob', 'Users per agent must be at least 1');
  }
  if ((settings.targetRatePerAgent ?? 1) <= 0) {
    add('createJob', 'Target rate per agent must be greater than 0');
  }
  // the server checks both values for every region, whichever the workload type uses
  for (const region of detail.regions ?? []) {
    if (!region.users?.trim()) {
      add('usersAndTimes', `${region.region} users are required (0 for none)`);
    }
    if (!WHOLE_NUMBER.test(region.percentage ?? '') || Number(region.percentage) > 100) {
      add('usersAndTimes', `${region.region} percentage must be a whole number from 0 to 100`);
    }
  }

  const plans = detail.testPlans ?? [];
  if (plans.length === 0) {
    add('scripts', 'At least one test plan is required');
  }
  for (const plan of plans) {
    const planName = plan.name?.trim();
    if (!planName) {
      add('scripts', 'Every test plan needs a name');
      continue;
    }
    if ((plan.userPercentage ?? 0) < 0 || (plan.userPercentage ?? 0) > 100) {
      add('scripts', `${planName}: user percentage must be from 0 to 100`);
    }
    for (const group of plan.scriptGroups ?? []) {
      if (!group.name?.trim()) {
        add('scripts', `${planName}: every script group needs a name`);
        continue;
      }
      if ((group.loop ?? 0) < 1) {
        add('scripts', `${planName} / ${group.name}: loop must be at least 1`);
      }
      for (const script of group.scripts ?? []) {
        if ((script.loop ?? 0) < 1) {
          add('scripts', `${planName} / ${group.name} / ${script.scriptName}: loop must be at least 1`);
        }
      }
    }
  }
  const keys = Object.keys(detail.variables ?? {});
  if (keys.some((key) => !key.trim())) {
    add('variables', 'Variable names must not be blank');
  }
  return problems;
}

/** Test plan percentages that don't add up to 100: allowed, but users get fewer or more than expected */
export function testPlanPercentageWarning(detail: ProjectDetail): string | undefined {
  const plans = detail.testPlans ?? [];
  const total = plans.reduce((sum, p) => sum + (p.userPercentage ?? 0), 0);
  return plans.length > 0 && total !== 100 ? `Test plan percentages add up to ${total}%, not 100%` : undefined;
}

/** Sum of region users when every value is a plain number; undefined when any is an expression */
export function totalUsers(detail: ProjectDetail): number | undefined {
  let total = 0;
  for (const region of detail.regions ?? []) {
    const users = region.users?.trim() ?? '0';
    if (!WHOLE_NUMBER.test(users)) {
      return undefined;
    }
    total += Number(users);
  }
  return total;
}

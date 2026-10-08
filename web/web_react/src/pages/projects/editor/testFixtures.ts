import type { Handlers } from '../../../test/renderApp';
import type { ProjectDetail } from './validation';

export const MODIFIED = '2026-10-05T12:00:00Z';

export function detail(overrides: Partial<ProjectDetail> = {}): ProjectDetail {
  return {
    id: 7,
    name: 'Checkout load',
    productName: 'Store',
    owner: 'alice',
    comments: 'Nightly',
    created: '2026-09-01T12:00:00Z',
    modified: MODIFIED,
    permissions: { edit: true, delete: true },
    settings: {
      incrementStrategy: 'increasing',
      terminationPolicy: 'time',
      simulationTime: '1h',
      rampTime: '10m',
      baselineVirtualUsers: 0,
      userIntervalIncrement: 1,
      numUsersPerAgent: 4000,
      targetRatePerAgent: 2.5,
      allowOverride: false,
    },
    regions: [
      { region: 'US East (Ohio)', users: '100', percentage: '0' },
      { region: 'US West (Oregon)', users: '50', percentage: '0' },
    ],
    testPlans: [
      {
        name: 'Main',
        userPercentage: 100,
        scriptGroups: [{ name: 'Login', loop: 1, scripts: [{ scriptId: 3, scriptName: 'login', loop: 1 }] }],
      },
    ],
    variables: { env: 'qa' },
    dataFileIds: [11],
    ...overrides,
  };
}

export function editorHandlers(project: ProjectDetail = detail(), overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/projects/7/full': () => ({ status: 200, body: project }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    'GET /v2/datafiles/names': () => ({ status: 200, body: { '11': 'users.csv', '12': 'cards.csv' } }),
    'GET /v2/datafiles': () => ({
      status: 200,
      body: { items: [{ id: 11, name: 'users.csv' }, { id: 12, name: 'cards.csv' }], total: 2, page: 0, size: 10 },
    }),
    'GET /v2/scripts': () => ({
      status: 200,
      body: { items: [{ id: 3, name: 'login' }, { id: 4, name: 'search' }], total: 2, page: 0, size: 10 },
    }),
    ...overrides,
  };
}

/** Records PUT bodies and answers with the saved project */
export function capturePut(bodies: ProjectDetail[]) {
  return async (request: Request) => {
    const body = (await request.json()) as ProjectDetail;
    bodies.push(body);
    return { status: 200, body: { ...body, modified: '2026-10-07T09:00:00Z' } };
  };
}


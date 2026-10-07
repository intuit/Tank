import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { ScriptDocument, ScriptStep } from './steps';
import { capturePut, handlers, open, rowOf } from './testFixtures';

type Body = Record<string, unknown> & { steps: ScriptStep[] };
const json = async (request: Request) => (await request.json()) as Body;

describe('search and replace', () => {
  it('finds steps anywhere in a value and replaces in the checked ones', async () => {
    const searches: Body[] = [];
    const replaces: Body[] = [];
    const bodies: ScriptDocument[] = [];
    await open(
      handlers(undefined, {
        'POST /v2/scripts/steps/search': async (request: Request) => {
          const body = await json(request);
          searches.push(body);
          // the second search runs on the replaced steps, where only u3 still matches
          return {
            status: 200,
            body:
              searches.length === 1
                ? [
                    { uuid: 'u1', position: 0, section: 'method', value: 'GET' },
                    { uuid: 'u3', position: 2, section: 'method', value: 'GET' },
                  ]
                : [{ uuid: 'u3', position: 2, section: 'method', value: 'GET' }],
          };
        },
        'POST /v2/scripts/steps/replace': async (request: Request) => {
          const body = await json(request);
          replaces.push(body);
          const steps = body.steps.map((s) => (s.uuid === 'u1' ? { ...s, method: 'POST', label: 'POST /' } : s));
          return { status: 200, body: { steps, changed: 1 } };
        },
        'PUT /v2/scripts/7/steps': capturePut(bodies),
      }),
    );

    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    const dialog = await screen.findByRole('dialog', { name: 'Search steps' });
    await userEvent.type(within(dialog).getByLabelText('Find'), 'get{Enter}');

    expect(await within(dialog).findByText('2 matching steps')).toBeInTheDocument();
    expect(searches[0]).toMatchObject({ query: '*get*', sections: ['search'] });
    expect(searches[0]!.steps).toHaveLength(4);

    await userEvent.click(within(within(dialog).getByText('GET /').closest('tr')!).getAllByRole('checkbox').at(-1)!);
    await userEvent.type(within(dialog).getByLabelText('Replace with'), 'POST');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Replace in 1 checked' }));

    expect(await within(dialog).findByText('1 matching step')).toBeInTheDocument();
    expect(replaces[0]).toMatchObject({ query: '*get*', sections: ['search'], replacement: 'POST', mode: 'VALUE', uuids: ['u1'] });
    expect(searches[1]!.steps[0]).toMatchObject({ method: 'POST' });

    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
    // the replacement is an unsaved change
    expect(screen.getByText('POST /')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]!.steps![0]).toMatchObject({ uuid: 'u1', method: 'POST' });
  });

  it('keeps a pattern as typed, and replaces keys in every match', async () => {
    const replaces: Body[] = [];
    await open(
      handlers(undefined, {
        'POST /v2/scripts/steps/search': () => ({
          status: 200,
          body: [{ uuid: 'u4', position: 3, section: 'variableKey', key: 'host', value: 'store.test' }],
        }),
        'POST /v2/scripts/steps/replace': async (request: Request) => {
          const body = await json(request);
          replaces.push(body);
          return { status: 200, body: { steps: body.steps, changed: 0 } };
        },
      }),
    );
    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.type(within(dialog).getByLabelText('Find'), 'host*{Enter}');

    expect(await within(dialog).findByText('host = store.test', { selector: 'li' })).toBeInTheDocument();
    await userEvent.type(within(dialog).getByLabelText('Replace with'), 'server');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Keys' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Replace in all 1' }));

    await waitFor(() => expect(replaces).toHaveLength(1));
    expect(replaces[0]).toMatchObject({ query: 'host*', mode: 'KEY' });
    expect(replaces[0]!.uuids).toBeUndefined();
    expect(await screen.findByText('Nothing was replaced')).toBeInTheDocument();
    expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument();
  });

  it('lists the first few matches of a step', async () => {
    const headers = Array.from({ length: 8 }, (_, i) => ({ uuid: 'u1', position: 0, section: 'requestHeaderValue', key: `X-${i}`, value: 'v' }));
    await open(handlers(undefined, { 'POST /v2/scripts/steps/search': () => ({ status: 200, body: headers }) }));
    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.type(within(dialog).getByLabelText('Find'), 'v{Enter}');

    expect(await within(dialog).findByText('and 3 more')).toBeInTheDocument();
    expect(within(dialog).getByText('X-4 = v')).toBeInTheDocument();
    expect(within(dialog).queryByText('X-5 = v')).not.toBeInTheDocument();
  });

  it('shows the server error', async () => {
    await open(handlers(undefined, { 'POST /v2/scripts/steps/search': () => ({ status: 400, body: { message: 'query is required' } }) }));
    await userEvent.click(screen.getByRole('button', { name: 'Search' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.type(within(dialog).getByLabelText('Find'), 'x{Enter}');
    expect(await within(dialog).findByText(/query is required/)).toBeInTheDocument();
  });
});

describe('apply filters', () => {
  it('applies the chosen filters, in list order, to the draft', async () => {
    let sent: Body | undefined;
    await open(
      handlers(undefined, {
        'GET /v2/filters': () => ({ status: 200, body: { filters: [{ id: 1, name: 'strip images' }, { id: 2, name: 'set host' }] } }),
        'GET /v2/filters/groups': () => ({ status: 200, body: { filterGroups: [{ id: 9, name: 'store', filterIds: [2, 1] }] } }),
        'POST /v2/scripts/steps/apply-filters': async (request: Request) => {
          sent = await json(request);
          return { status: 200, body: { steps: sent.steps.filter((s) => s.uuid !== 'u2'), changed: 1 } };
        },
      }),
    );
    await userEvent.click(within(rowOf('1000 - 3000')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Apply filters' }));
    const dialog = await screen.findByRole('dialog', { name: 'Apply filters' });
    await userEvent.click(await within(dialog).findByLabelText('store'));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Apply 2 filters' }));

    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
    expect(sent).toMatchObject({ filterIds: [1, 2] });
    expect(screen.queryByText('1000 - 3000')).not.toBeInTheDocument();
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();
    // the removed step isn't still selected
    expect(screen.getByText('3 steps')).toBeInTheDocument();
  });
});

describe('validate', () => {
  it('shows the expected time, warnings and data files', async () => {
    let sent: Body | undefined;
    await open(
      handlers(undefined, {
        'POST /v2/scripts/steps/validate': async (request: Request) => {
          sent = await json(request);
          return {
            status: 200,
            body: { durationMs: 65_000, warnings: ["Variable 'sku' is used but never set."], dataFiles: ['users.csv'] },
          };
        },
      }),
    );
    await userEvent.click(screen.getByRole('button', { name: 'Validate' }));
    const dialog = await screen.findByRole('dialog', { name: 'Validate script' });

    expect(await within(dialog).findByText('1 min 5 s')).toBeInTheDocument();
    expect(within(dialog).getByText("Variable 'sku' is used but never set.")).toBeInTheDocument();
    expect(within(dialog).getByText('users.csv')).toBeInTheDocument();
    expect(sent).toMatchObject({ name: 'checkout' });
    expect(sent!.steps).toHaveLength(4);
  });

  it('says when there is nothing to fix', async () => {
    await open(handlers(undefined, { 'POST /v2/scripts/steps/validate': () => ({ status: 200, body: { durationMs: 0, warnings: [] } }) }));
    await userEvent.click(screen.getByRole('button', { name: 'Validate' }));
    expect(await screen.findByText('No problems found.')).toBeInTheDocument();
  });
});

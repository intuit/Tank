import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp } from '../../../test/renderApp';
import { MASKED_PASSWORD, type ScriptDocument, type ScriptStep } from './steps';
import { capturePut, doc, handlers, open, rowOf, steps } from './testFixtures';

const AUTH: ScriptStep = {
  uuid: 'a1',
  type: 'authentication',
  label: 'Authentication Basic [host: store.test user: bob]',
  data: [
    { key: 'userName', value: 'bob', type: 'authentication' },
    { key: 'password', value: MASKED_PASSWORD, type: 'authentication' },
    { key: 'scheme', value: 'Basic', type: 'authentication' },
    { key: 'host', value: 'store.test', type: 'authentication' },
  ],
};

async function addStep(type: string) {
  await userEvent.click(screen.getByRole('button', { name: 'Add step' }));
  // the popup menu stays in its enter transition in jsdom, which hides it from role queries
  await userEvent.click(await screen.findByRole('menuitem', { name: type, hidden: true }));
  return screen.findByRole('dialog');
}

async function saved(bodies: ScriptDocument[]) {
  await userEvent.click(screen.getByRole('button', { name: 'Save' }));
  await waitFor(() => expect(bodies).toHaveLength(1));
  return bodies[0]!.steps ?? [];
}

describe('simple steps', () => {
  it('adds a variable before the first selected step', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.click(within(rowOf('GET /cart')).getAllByRole('checkbox').at(-1)!);
    const dialog = await addStep('Variable');
    await userEvent.type(within(dialog).getByLabelText('Name'), 'host');
    await userEvent.type(within(dialog).getByLabelText('Value'), 'store.test');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    expect(screen.getByText('Variable definition host=>store.test')).toBeInTheDocument();
    const sent = await saved(bodies);
    expect(sent.map((s) => s.type)).toEqual(['request', 'thinkTime', 'variable', 'request', 'variable']);
    expect(sent[2]!.data).toEqual([{ key: 'host', value: 'store.test' }]);
    expect(sent[2]!.uuid).toMatch(/^[0-9a-f-]{36}$/);
  });

  it('checks think times before adding them', async () => {
    await open(handlers());

    const dialog = await addStep('Think time');
    await userEvent.type(within(dialog).getByLabelText('Minimum'), '5000');
    await userEvent.type(within(dialog).getByLabelText('Maximum'), '1000');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(within(dialog).getByText('Minimum has to be no more than maximum')).toBeInTheDocument();

    await userEvent.clear(within(dialog).getByLabelText('Maximum'));
    await userEvent.type(within(dialog).getByLabelText('Maximum'), '@maxDelay');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(screen.getByText('Think time 5000-@maxDelay')).toBeInTheDocument();
  });

  it('edits a step in place, keeping its uuid', async () => {
    const bodies: ScriptDocument[] = [];
    const sleep: ScriptStep = { uuid: 's1', type: 'sleep', label: 'Sleep for 500', data: [{ key: 'time', value: '500', type: 'sleep' }] };
    await open(handlers(doc({ steps: [...steps(), sleep] }), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('button', { name: /Sleep for 500/ }));
    const dialog = await screen.findByRole('dialog', { name: 'Edit sleep time' });
    expect(within(dialog).getByLabelText('Sleep time')).toHaveValue('500');
    await userEvent.clear(within(dialog).getByLabelText('Sleep time'));
    await userEvent.type(within(dialog).getByLabelText('Sleep time'), '0');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
    expect(within(dialog).getByText('Sleep time has to be more than 0')).toBeInTheDocument();
    await userEvent.type(within(dialog).getByLabelText('Sleep time'), '{backspace}250');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    const sent = await saved(bodies);
    expect(sent.at(-1)).toMatchObject({ uuid: 's1', label: 'Sleep for 250', data: [{ key: 'time', value: '250', type: 'sleep' }] });
  });

  it('needs a cookie name', async () => {
    await open(handlers());

    const dialog = await addStep('Cookie');
    await userEvent.type(within(dialog).getByLabelText('Value'), 'abc');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    expect(within(dialog).getByText('Cookie name is required')).toBeInTheDocument();
  });

  it('keeps a stored password unless a new one is typed', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc({ steps: [...steps(), AUTH] }), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('button', { name: `Edit step ${steps().length + 1}` }));
    const dialog = await screen.findByRole('dialog', { name: 'Edit authentication' });
    expect(within(dialog).getByLabelText('Password')).toHaveValue('');
    expect(within(dialog).getByText('Leave empty to keep the stored password')).toBeInTheDocument();
    await userEvent.clear(within(dialog).getByLabelText('Realm'));
    await userEvent.type(within(dialog).getByLabelText('Realm'), 'shop');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    const sent = await saved(bodies);
    const data = sent.at(-1)!.data!;
    expect(data.find((d) => d.key === 'password')?.value).toBe(MASKED_PASSWORD);
    expect(data.find((d) => d.key === 'realm')?.value).toBe('shop');
  });

  it('needs a password for a new authentication step', async () => {
    await open(handlers());

    const dialog = await addStep('Authentication');
    await userEvent.type(within(dialog).getByLabelText('User name'), 'bob');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(within(dialog).getByText('Password is required')).toBeInTheDocument();

    await userEvent.type(within(dialog).getByLabelText('Password'), 's3cret');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(screen.getByText(/^Authentication ALL \[host:\s+user: bob\]$/)).toBeInTheDocument();
  });

  it('adds a clear session at the end when nothing is selected', async () => {
    await open(handlers());

    await userEvent.click(screen.getByRole('button', { name: 'Add step' }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Clear session', hidden: true }));

    const label = screen.getAllByText('Clear session').find((e) => e.closest('tbody'))!;
    expect(within(label.closest('tr')!).getByText('5')).toBeInTheDocument();
  });

  it('copies steps and pastes them into another script with new uuids', async () => {
    const bodies: ScriptDocument[] = [];
    const { router } = renderApp(
      '/scripts/7',
      handlers(doc(), {
        'GET /v2/scripts/8/steps': () => ({ status: 200, body: doc({ id: 8, name: 'other', steps: [] }) }),
        'PUT /v2/scripts/8/steps': capturePut(bodies),
      }),
    );
    await screen.findByText('GET /cart');

    await userEvent.click(within(rowOf('GET /')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(within(rowOf('1000 - 3000')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Copy' }));
    await router.navigate('/scripts/8');
    await screen.findByRole('heading', { name: 'other' });
    await userEvent.click(await screen.findByRole('button', { name: 'Paste 2 steps' }));

    const sent = await saved(bodies);
    expect(sent.map((s) => s.label)).toEqual(['GET /', '1000 - 3000']);
    expect(sent.map((s) => s.uuid)).not.toContain('u1');
  });

  it('shows steps read-only without the edit permission', async () => {
    await open(handlers(doc({ owner: 'bob', permissions: { edit: false, delete: false }, steps: [...steps(), AUTH] })));

    expect(screen.queryByRole('button', { name: 'Add step' })).not.toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: `View step ${steps().length + 1}` }));
    const dialog = await screen.findByRole('dialog');
    expect(within(dialog).getByLabelText('User name')).toBeDisabled();
    expect(within(dialog).queryByRole('button', { name: 'Done' })).not.toBeInTheDocument();
  });
});

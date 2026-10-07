import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { ScriptDocument } from './steps';
import { capturePut, doc, handlers, open, rowOf } from './testFixtures';

const select = (text: string) => userEvent.click(within(rowOf(text)).getAllByRole('checkbox').at(-1)!);
const labels = (body: ScriptDocument) => body.steps!.map((s) => s.label);

describe('timer groups', () => {
  it('wraps neighbouring selected steps in a named timer', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    expect(screen.queryByRole('button', { name: 'Timer group' })).not.toBeInTheDocument();
    await select('GET /');
    expect(screen.getByRole('button', { name: 'Timer group' })).toBeDisabled();
    await select('GET /cart');
    // steps 1 and 3 aren't next to each other
    expect(screen.getByRole('button', { name: 'Timer group' })).toBeDisabled();
    await select('1000 - 3000');
    await userEvent.click(screen.getByRole('button', { name: 'Timer group' }));

    const dialog = await screen.findByRole('dialog', { name: 'Time 3 steps' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(within(dialog).getByText('Name is required')).toBeInTheDocument();
    await userEvent.type(within(dialog).getByLabelText('Name'), 'browse');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(labels(bodies[0]!)).toEqual(['browse:START', 'GET /', '1000 - 3000', 'GET /cart', 'browse:STOP', 'host = store.test']);
    const [start, , , , stop] = bodies[0]!.steps!;
    expect(start!.data).toContainEqual({ key: 'aggregator-pair', value: stop!.uuid, type: 'timer' });
    expect(stop!.data).toContainEqual({ key: 'is-start', value: 'STOP', type: 'timer' });
  });

  it('renames and deletes both halves of a timer', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));
    await select('GET /');
    await select('1000 - 3000');
    await userEvent.click(screen.getByRole('button', { name: 'Timer group' }));
    await userEvent.type(within(await screen.findByRole('dialog')).getByLabelText('Name'), 'browse{Enter}');

    await userEvent.click(screen.getByRole('button', { name: 'Edit step 4' }));
    const dialog = await screen.findByRole('dialog', { name: 'Rename timer browse' });
    await userEvent.clear(within(dialog).getByLabelText('Name'));
    await userEvent.type(within(dialog).getByLabelText('Name'), 'landing');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
    expect(screen.getByText('landing:START')).toBeInTheDocument();
    expect(screen.getByText('landing:STOP')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Delete step 1' }));
    expect(screen.queryByText('landing:STOP')).not.toBeInTheDocument();
    // the timed steps were still selected; they stay selected
    expect(screen.getByText('4 steps, 2 selected')).toBeInTheDocument();
  });
});

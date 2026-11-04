import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

/** TableColumnDefaults.DATAFILES_COL_PREFS */
const COLUMNS = [
  { colName: 'selectColumn', displayName: 'Select', size: 20, visible: true, hideable: false },
  { colName: 'idColumn', displayName: 'ID', size: 75, visible: false, hideable: true },
  { colName: 'nameColumn', displayName: 'Name', size: 250, visible: true, hideable: true },
  { colName: 'createColumn', displayName: 'Create Time', size: 110, visible: true, hideable: true },
  { colName: 'modifiedColumn', displayName: 'Modified Time', size: 110, visible: true, hideable: true },
  { colName: 'ownerColumn', displayName: 'Owner', size: 100, visible: true, hideable: true },
  { colName: 'actionsColumn', displayName: 'Actions', size: 75, visible: true, hideable: false },
];
const FILES = [
  { id: 3, name: 'users.csv', owner: 'alice', modified: '2026-10-01T10:00:00Z' },
  { id: 4, name: 'skus.txt', owner: 'bob', modified: '2026-09-30T10:00:00Z' },
];

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_DATAFILE: true } } }),
    'GET /v2/me/preferences': () => ({ status: 200, body: { tables: { datafiles: COLUMNS } } }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    'GET /v2/datafiles': () => ({ status: 200, body: { items: FILES, total: 2, page: 0, size: 25 } }),
    ...overrides,
  };
}

const rowOf = (text: string) => screen.getByText(text).closest('tr')!;

describe('data files page', () => {
  it('lists data files with what each user may do', async () => {
    renderApp('/datafiles', handlers());
    await screen.findByText('users.csv');

    expect(within(rowOf('users.csv')).getByRole('link', { name: 'Download users.csv' })).toHaveAttribute(
      'href',
      expect.stringMatching(/\/v2\/datafiles\/download\/3$/),
    );
    expect(within(rowOf('users.csv')).getByRole('button', { name: 'Replace contents users.csv' })).toBeInTheDocument();
    expect(within(rowOf('users.csv')).getByRole('button', { name: 'Delete users.csv' })).toBeInTheDocument();
    expect(within(rowOf('skus.txt')).queryByRole('button', { name: 'Delete skus.txt' })).not.toBeInTheDocument();
    expect(within(rowOf('skus.txt')).queryByRole('button', { name: 'Replace contents skus.txt' })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Upload' })).toBeEnabled();
  });

  it('pages through a preview', async () => {
    const offsets: string[] = [];
    renderApp(
      '/datafiles',
      handlers({
        'GET /v2/datafiles/3/preview': (request) => {
          const offset = Number(new URL(request.url).searchParams.get('offset'));
          offsets.push(`${offset}:${new URL(request.url).searchParams.get('lines')}`);
          const count = offset === 100 ? 20 : 50;
          return {
            status: 200,
            body: { id: 3, name: 'users.csv', offset, totalLines: 120, lines: Array.from({ length: count }, (_, i) => `user${offset + i + 1},pw`) },
          };
        },
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'users.csv' }));
    const dialog = await screen.findByRole('dialog', { name: 'users.csv' });
    expect(await within(dialog).findByText('Lines 1 to 50 of 120')).toBeInTheDocument();
    expect(within(dialog).getByText('user1,pw')).toBeInTheDocument();

    await userEvent.click(within(dialog).getByRole('button', { name: 'Last Page' }));
    expect(await within(dialog).findByText('Lines 101 to 120 of 120')).toBeInTheDocument();
    expect(within(dialog).getByText('user120,pw')).toBeInTheDocument();
    expect(offsets).toEqual(['0:50', '100:50']);
  });

  it('says what an upload skipped', async () => {
    renderApp(
      '/datafiles',
      handlers({
        'POST /v2/datafiles/batch': () => ({ status: 201, body: { created: [{ id: 5, name: 'a.csv' }, { id: 6, name: 'b.csv' }], skipped: ['notes.pdf'] } }),
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Upload' }));
    const dialog = await screen.findByRole('dialog', { name: 'Upload data files' });
    await userEvent.upload(within(dialog).getByLabelText('Files'), new File(['zip'], 'bundle.zip'));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Upload' }));

    expect(await screen.findByText('Added 2 data files')).toBeInTheDocument();
    expect(within(dialog).getByText('notes.pdf')).toBeInTheDocument();
  });

  it('refuses files of the wrong type before uploading', async () => {
    renderApp('/datafiles', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Upload' }));
    const dialog = await screen.findByRole('dialog');
    // applyAccept off: a user can still pick "All files" in the browser's chooser
    await userEvent.setup({ applyAccept: false }).upload(within(dialog).getByLabelText('Files'), [new File([''], 'a.csv'), new File([''], 'b.xlsx')]);
    expect(within(dialog).getByText(/Only .csv, .txt, .xml and .zip files can be uploaded: b.xlsx/)).toBeInTheDocument();
    expect(within(dialog).getByRole('button', { name: 'Upload 2 files' })).toBeDisabled();
  });

  it('deletes the selected files', async () => {
    let ids: string | null = null;
    renderApp(
      '/datafiles',
      handlers({
        'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { DELETE_DATAFILE: true } } }),
        'DELETE /v2/datafiles': (request) => {
          ids = new URL(request.url).searchParams.getAll('ids').join(',');
          return { status: 200, body: { deleted: [3, 4], notFound: [] } };
        },
      }),
    );
    await screen.findByText('users.csv');
    for (const name of ['users.csv', 'skus.txt']) {
      await userEvent.click(within(rowOf(name)).getAllByRole('checkbox').at(-1)!);
    }
    await userEvent.click(screen.getByRole('button', { name: 'Delete 2 selected' }));
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }));
    await waitFor(() => expect(ids).toBe('3,4'));
    expect(await screen.findByText('2 data files deleted')).toBeInTheDocument();
  });
});

import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { DataTable, type DataTableSortEvent, type DataTableStateEvent } from 'primereact/datatable';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { PAGE_SIZES } from '../../components/EntityList';
import { RowAction } from '../../components/entityColumns';
import { formatDateTime } from '../../format';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { UserDialog } from './UserDialog';

export type AdminUser = Schemas['AdminUser'];

/** Sort fields the server accepts (AdminServiceV2Impl) */
const SORT_FIELDS = ['name', 'email', 'lastLoginTs', 'created', 'modified'];
const DEFAULT_SORT = 'name,asc';

/** The user list (UserAdmin and admin/users.xhtml) */
export function AdminUsersPage() {
  const { client, user: me } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const page = Math.max(0, Number(params.get('page') ?? 1) - 1) || 0;
  const size = PAGE_SIZES.includes(Number(params.get('size'))) ? Number(params.get('size')) : PAGE_SIZES[0]!;
  const sort = params.get('sort') ?? DEFAULT_SORT;
  const q = params.get('q') ?? '';
  const [search, setSearch] = useState(q);
  const [editing, setEditing] = useState<AdminUser | 'new'>();
  const update = (changes: Record<string, string | number | undefined>) =>
    setParams(
      (current) => {
        const next = new URLSearchParams(current);
        for (const [key, value] of Object.entries(changes)) {
          if (value === undefined || value === '') next.delete(key);
          else next.set(key, String(value));
        }
        return next;
      },
      { replace: true },
    );

  useEffect(() => {
    if (search.trim() === q) return;
    const timer = setTimeout(() => update({ q: search.trim(), page: undefined }), 300);
    return () => clearTimeout(timer);
  }, [search, q]);

  const users = useQuery({
    queryKey: ['admin-users', { page, size, sort, q }],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/admin/users', {
        params: { query: { page, size, sort, q: q || undefined } },
        signal,
      });
      if (!data) throw toApiError(error, response, 'load users');
      return data;
    },
    placeholderData: keepPreviousData,
  });
  const refresh = () => void queryClient.invalidateQueries({ queryKey: ['admin-users'] });

  const remove = useMutation({
    mutationFn: async (user: AdminUser) => {
      const { error, response } = await client.DELETE('/v2/admin/users/{userId}', { params: { path: { userId: user.id! } } });
      if (!response.ok) throw toApiError(error, response, 'delete the user');
      return user;
    },
    onSuccess: (user) => notify.success(`Deleted ${user.name}`),
    onError: (error) => notify.error('User not deleted', error.message),
    onSettled: refresh,
  });
  const resetPreferences = useMutation({
    mutationFn: async (user: AdminUser) => {
      const { error, response } = await client.DELETE('/v2/admin/users/{userId}/preferences', { params: { path: { userId: user.id! } } });
      if (!response.ok) throw toApiError(error, response, 'reset the preferences');
      return user;
    },
    onSuccess: (user) => notify.success(`Reset ${user.name}'s preferences`),
    onError: (error) => notify.error('Preferences not reset', error.message),
  });

  const confirmDelete = (user: AdminUser) =>
    confirmDialog({
      header: 'Delete user',
      message: `Delete ${user.name}? Their scripts, filters and data files are kept, with their name removed. A user who owns projects can't be deleted until the projects are given to someone else.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(user),
    });
  const confirmReset = (user: AdminUser) =>
    confirmDialog({
      header: 'Reset preferences',
      message: `Put every table back to its default columns and widths for ${user.name}?`,
      acceptLabel: 'Reset',
      rejectLabel: 'Cancel',
      defaultFocus: 'reject',
      accept: () => resetPreferences.mutate(user),
    });

  const [sortField, sortDirection] = sort.split(',');
  return (
    <>
      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search name or email"
            aria-label="Search users"
            className="list-search"
          />
        </IconField>
        <div className="list-toolbar-end">
          <Button label="New user" icon="pi pi-plus" onClick={() => setEditing('new')} />
        </div>
      </div>
      {users.error && <Message severity="error" text={users.error.message} />}
      <DataTable
        value={users.data?.items ?? []}
        dataKey="id"
        lazy
        paginator
        first={page * size}
        rows={size}
        totalRecords={users.data?.total ?? 0}
        rowsPerPageOptions={PAGE_SIZES}
        onPage={(e: DataTableStateEvent) => update({ page: e.page ? e.page + 1 : undefined, size: e.rows })}
        sortField={sortField}
        sortOrder={sortDirection === 'desc' ? -1 : 1}
        onSort={(e: DataTableSortEvent) =>
          SORT_FIELDS.includes(e.sortField) && update({ sort: `${e.sortField},${e.sortOrder === -1 ? 'desc' : 'asc'}`, page: undefined })
        }
        loading={users.isFetching}
        emptyMessage={q ? 'No users match.' : 'No users.'}
        size="small"
        stripedRows
        tableStyle={{ tableLayout: 'fixed', width: '100%' }}
        currentPageReportTemplate="{first}–{last} of {totalRecords}"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown CurrentPageReport"
        pt={{ table: { 'aria-label': 'Users' } }}
      >
        <Column
          header="Name"
          field="name"
          sortable
          body={(u: AdminUser) => (
            <button type="button" className="cell-link name-link" onClick={() => setEditing(u)}>
              {u.name}
              {u.name === me?.name && <span className="field-help"> (you)</span>}
            </button>
          )}
          bodyClassName="ellipsis"
        />
        <Column header="Email" field="email" sortable bodyClassName="ellipsis" />
        <Column header="Groups" body={(u: AdminUser) => u.groups?.join(', ')} bodyClassName="ellipsis" />
        <Column
          header="API token"
          body={(u: AdminUser) => (u.hasApiToken ? <code title="The token's last characters">{u.apiTokenHint ?? 'Yes'}</code> : null)}
          style={{ width: '9rem' }}
        />
        <Column
          header="Last signed in"
          field="lastLoginTs"
          sortable
          body={(u: AdminUser) => (u.lastLoginTs ? formatDateTime(u.lastLoginTs) : <span className="field-help">Never</span>)}
          style={{ width: '12rem' }}
        />
        <Column
          body={(u: AdminUser) => (
            <div className="row-actions">
              <RowAction icon="pi pi-pencil" label="Edit" name={u.name} onClick={() => setEditing(u)} />
              <RowAction icon="pi pi-refresh" label="Reset preferences" name={u.name} onClick={() => confirmReset(u)} />
              {u.name !== me?.name && <RowAction icon="pi pi-trash" label="Delete" name={u.name} severity="danger" onClick={() => confirmDelete(u)} />}
            </div>
          )}
          style={{ width: '8rem' }}
        />
      </DataTable>
      {editing && (
        <UserDialog
          user={editing === 'new' ? undefined : editing}
          onHide={() => setEditing(undefined)}
          onSaved={refresh}
        />
      )}
    </>
  );
}

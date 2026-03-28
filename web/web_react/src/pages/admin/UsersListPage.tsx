import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { Tag } from 'primereact/tag';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useUsers, useDeleteUser } from '../../hooks/useUsers';
import type { UserTO } from '../../types/user';

export function UsersListPage() {
  const { data: users, isLoading, error } = useUsers();
  const deleteUser = useDeleteUser();
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  if (isLoading) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load users." />;

  const handleDelete = (row: UserTO) => {
    confirmDialog({
      message: `Delete user "${row.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await deleteUser.mutateAsync(row.id);
          toast.current?.show({ severity: 'success', summary: `User "${row.name}" deleted` });
        } catch {
          toast.current?.show({ severity: 'error', summary: 'Failed to delete user' });
        }
      },
    });
  };

  const nameBody = (row: UserTO) => (
    <Button label={row.name} link onClick={() => navigate(`/admin/users/${row.id}/edit`)} />
  );

  const groupsBody = (row: UserTO) => (
    <div className="flex flex-wrap gap-1">
      {row.groups.map((g) => (
        <Tag key={g} value={g} severity="info" />
      ))}
    </div>
  );

  const tokenBody = (row: UserTO) => (
    row.apiToken ? (
      <span className="font-mono text-sm">{row.apiToken.substring(0, 12)}…</span>
    ) : (
      <span className="text-color-secondary">None</span>
    )
  );

  const actionsBody = (row: UserTO) => (
    <div className="flex gap-1">
      <Button
        icon="pi pi-pencil"
        size="small"
        text
        severity="secondary"
        tooltip="Edit"
        onClick={() => navigate(`/admin/users/${row.id}/edit`)}
      />
      <Button
        icon="pi pi-trash"
        size="small"
        text
        severity="danger"
        tooltip="Delete"
        onClick={() => handleDelete(row)}
      />
    </div>
  );

  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <span className="font-bold text-xl">Users</span>
      <Button
        label="New User"
        icon="pi pi-plus"
        size="small"
        onClick={() => navigate('/admin/users/new')}
      />
    </div>
  );

  const rightToolbar = (
    <InputText
      placeholder="Search…"
      value={globalFilter}
      onChange={(e) => setGlobalFilter(e.target.value)}
      className="p-inputtext-sm"
    />
  );

  return (
    <div>
      <Toast ref={toast} />
      <ConfirmDialog />
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3" />
      <DataTable
        value={users}
        dataKey="id"
        paginator
        rows={25}
        globalFilter={globalFilter}
        globalFilterFields={['name', 'email']}
        emptyMessage="No users found."
        sortField="name"
        sortOrder={1}
      >
        <Column field="name" header="Name" sortable body={nameBody} />
        <Column field="email" header="Email" sortable />
        <Column field="apiToken" header="API Token" body={tokenBody} />
        <Column field="groups" header="Groups" body={groupsBody} />
        <Column
          field="lastLoginTs"
          header="Last Login"
          sortable
          body={(row: UserTO) => row.lastLoginTs ? new Date(row.lastLoginTs).toLocaleString() : '—'}
        />
        <Column header="" body={actionsBody} style={{ width: '80px' }} />
      </DataTable>
    </div>
  );
}

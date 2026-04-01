import { useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Tag } from 'primereact/tag';
import { Toolbar } from 'primereact/toolbar';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { useAllGroups } from '../../hooks/useUsers';
import { useUsers } from '../../hooks/useUsers';
import { useNavigate } from 'react-router-dom';

export function UserGroupsPage() {
  const navigate = useNavigate();
  const toast = useRef<Toast>(null);
  const { data: groups, isLoading: loadingGroups, error } = useAllGroups();
  const { data: users, isLoading: loadingUsers } = useUsers();

  if (loadingGroups || loadingUsers) return <ProgressSpinner />;
  if (error) return <Message severity="error" text="Failed to load groups." />;

  // Build a map of group → users
  const groupUserMap: Record<string, string[]> = {};
  for (const group of groups ?? []) {
    groupUserMap[group] = [];
  }
  for (const user of users ?? []) {
    for (const g of user.groups) {
      if (!groupUserMap[g]) groupUserMap[g] = [];
      groupUserMap[g].push(user.name);
    }
  }

  const rows = Object.entries(groupUserMap).map(([name, members]) => ({ name, members }));

  const membersBody = (row: { name: string; members: string[] }) => (
    <div className="flex flex-wrap gap-1">
      {row.members.length === 0 ? (
        <span className="text-color-secondary text-sm">No members</span>
      ) : (
        row.members.map((m) => <Tag key={m} value={m} severity="secondary" />)
      )}
    </div>
  );

  const leftToolbar = (
    <div className="flex gap-2 align-items-center">
      <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/admin')} />
      <span className="font-bold text-xl">User Groups</span>
    </div>
  );

  const rightToolbar = (
    <Message
      severity="info"
      text="Groups are created automatically when assigned to users via User Edit."
    />
  );

  return (
    <div>
      <Toast ref={toast} />
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-3 ui-tank-theme" />
      <DataTable
        value={rows}
        dataKey="name"
        showGridlines
        stripedRows
        paginator
        rows={25}
        emptyMessage="No groups found."
        sortField="name"
        sortOrder={1}
      >
        <Column field="name" header="Group Name" sortable style={{ width: '200px' }} />
        <Column field="members" header="Members" body={membersBody} />
        <Column
          header="User Count"
          sortable
          sortField="members"
          body={(row: { members: string[] }) => row.members.length}
          style={{ width: '120px' }}
        />
      </DataTable>
    </div>
  );
}

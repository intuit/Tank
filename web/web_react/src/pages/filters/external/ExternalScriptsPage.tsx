import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { contextPath, type Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { RowAction, RowDownload } from '../../../components/entityColumns';
import { formatDateTime } from '../../../format';
import { useFillHeight } from '../../../hooks/useFillHeight';
import { useNotify } from '../../../notify';
import { hasRight, hasRightOrOwns } from '../../../rights';
import { useSession } from '../../../session';

export type ExternalScript = Schemas['ExternalScriptTO'];

/** The external scripts list: the programs external filters run, also uploaded by the Script Filter Editor tool */
export function ExternalScriptsPage() {
  const { client, user } = useSession();
  const navigate = useNavigate();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState('');
  const fill = useFillHeight<ExternalScript[]>();
  const canCreate = hasRight(user, 'CREATE_SCRIPT');
  const canDelete = (x: ExternalScript) => hasRightOrOwns(user, 'DELETE_SCRIPT', x.creator);

  const scripts = useQuery({
    queryKey: ['externalScripts'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/scripts/external', { signal });
      if (!data) throw toApiError(error, response, 'load the external scripts');
      return data.scripts ?? [];
    },
  });

  const remove = useMutation({
    mutationFn: async (x: ExternalScript) => {
      const { error, response } = await client.DELETE('/v2/scripts/external/{externalScriptId}', {
        params: { path: { externalScriptId: x.id! } },
      });
      if (!response.ok) throw toApiError(error, response, 'delete the external script');
      return x;
    },
    onSuccess: (x) => notify.success('External script deleted', x.name),
    onError: (error) => notify.error('The external script was not deleted', error.message),
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['externalScripts'] }),
  });

  const confirmDelete = (x: ExternalScript) =>
    confirmDialog({
      header: 'Delete external script',
      message: `Delete external script "${x.name}"? This can't be undone. Filters that run it will fail until they are changed.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(x),
    });

  if (scripts.error) {
    return <Message severity="error" text={scripts.error.message} />;
  }
  const rows = scripts.data ?? [];
  const q = search.trim().toLowerCase();
  const shown = rows.filter(
    (x) => !q || x.name?.toLowerCase().includes(q) || x.productName?.toLowerCase().includes(q) || String(x.id) === q,
  );

  return (
    <section className="list-page editor-wide">
      <p className="breadcrumb">
        <Link to="/filters">Filters</Link> / External filters
      </p>
      <div className="page-header">
        <h1>External filters</h1>
        <Button
          label="New external script"
          icon="pi pi-plus"
          onClick={() => void navigate('/filters/external/new')}
          disabled={!canCreate}
          tooltip={canCreate ? undefined : "You don't have permission to create scripts"}
          tooltipOptions={{ showOnDisabled: true, position: 'left' }}
        />
      </div>
      <p className="field-help">
        An external filter runs one of these JavaScript programs to change a script's steps. Open one to edit it and
        test it against a script.
      </p>
      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search name, product or ID"
            aria-label="Search external scripts"
            className="list-search"
          />
        </IconField>
        <span className="field-help">{shown.length === rows.length ? rows.length : `${shown.length} of ${rows.length}`}</span>
      </div>
      <DataTable
        value={shown}
        dataKey="id"
        loading={scripts.isPending}
        emptyMessage={rows.length ? 'No external scripts match.' : 'No external scripts yet.'}
        sortField="name"
        sortOrder={1}
        removableSort
        size="small"
        stripedRows
        tableStyle={{ tableLayout: 'fixed', width: '100%' }}
        scrollable
        ref={fill.ref}
        scrollHeight={fill.scrollHeight}
        pt={{ table: { 'aria-label': 'External scripts' } }}
      >
        <Column header="ID" field="id" sortable style={{ width: '4.5rem' }} />
        <Column
          header="Name"
          field="name"
          sortable
          body={(x: ExternalScript) => (
            <Link to={`/filters/external/${x.id}`} title={`${x.name} (id ${x.id})`}>
              {x.name}
            </Link>
          )}
          bodyClassName="ellipsis"
        />
        <Column header="Product" field="productName" sortable bodyClassName="ellipsis" style={{ width: '8rem' }} />
        <Column header="Owner" field="creator" sortable bodyClassName="ellipsis" style={{ width: '7rem' }} />
        <Column
          header="Modified"
          field="modified"
          sortable
          body={(x: ExternalScript) => formatDateTime(x.modified)}
          style={{ width: '11rem' }}
        />
        <Column
          body={(x: ExternalScript) => (
            <div className="row-actions">
              <RowAction icon="pi pi-pencil" label="Open" name={x.name} onClick={() => void navigate(`/filters/external/${x.id}`)} />
              <RowDownload href={`${contextPath()}/v2/scripts/external/download/${x.id}`} label="Download" name={x.name} />
              {canDelete(x) && (
                <RowAction icon="pi pi-trash" label="Delete" name={x.name} severity="danger" onClick={() => confirmDelete(x)} />
              )}
            </div>
          )}
          style={{ width: '8rem' }}
        />
      </DataTable>
    </section>
  );
}

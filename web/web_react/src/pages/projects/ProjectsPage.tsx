import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column, type ColumnBodyOptions } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { DataTable, type DataTableSortEvent, type DataTableStateEvent } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { MultiSelect } from 'primereact/multiselect';
import { useEffect, useMemo, useState, type ReactNode } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { formatDateTime } from '../../format';
import { useTablePreferences, type ColumnPreference } from '../../hooks/useTablePreferences';
import { useNotify } from '../../notify';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { CopyProjectDialog } from './CopyProjectDialog';
import { CreateProjectDialog } from './CreateProjectDialog';

type ProjectSummary = Schemas['ProjectSummary'];
type ProjectPage = Schemas['PageResponseProjectSummary'];

export const PAGE_SIZES = [25, 50, 75, 100];
const DEFAULT_SORT = 'modified,desc';
const SEARCH_DELAY_MS = 300;

/** Sort fields the server accepts (ProjectServiceV2Impl.SORTABLE_FIELDS), by column field */
const SORT_FIELDS = new Set(['id', 'name', 'productName', 'owner', 'created', 'modified']);

/** List state kept in the URL, so reloading or going back restores it */
function useListParams() {
  const [params, setParams] = useSearchParams();
  const page = Math.max(0, Number(params.get('page') ?? 1) - 1) || 0;
  const size = PAGE_SIZES.includes(Number(params.get('size'))) ? Number(params.get('size')) : PAGE_SIZES[0]!;
  const sort = params.get('sort') ?? DEFAULT_SORT;
  const q = params.get('q') ?? '';
  const owner = params.get('owner') ?? '';

  const update = (changes: Record<string, string | number | undefined>) =>
    setParams(
      (current) => {
        const next = new URLSearchParams(current);
        for (const [key, value] of Object.entries(changes)) {
          if (value === undefined || value === '') {
            next.delete(key);
          } else {
            next.set(key, String(value));
          }
        }
        return next;
      },
      { replace: true },
    );
  return { page, size, sort, q, owner, update };
}

export function ProjectsPage() {
  const { client, user } = useSession();
  const notify = useNotify();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { page, size, sort, q, owner, update } = useListParams();
  const preferences = useTablePreferences('projects');

  const [search, setSearch] = useState(q);
  const [selection, setSelection] = useState<ProjectSummary[]>([]);
  const [creating, setCreating] = useState(false);
  const [copying, setCopying] = useState<ProjectSummary>();

  // Follow the URL when it changes underneath the box (back, forward, a link)
  useEffect(() => {
    setSearch((current) => (current.trim() === q ? current : q));
  }, [q]);

  // Debounce typing into the URL; a new search starts at the first page
  useEffect(() => {
    if (search.trim() === q) {
      return;
    }
    const timer = setTimeout(() => update({ q: search.trim(), page: undefined }), SEARCH_DELAY_MS);
    return () => clearTimeout(timer);
  }, [search, q]);

  const projects = useQuery({
    queryKey: ['projects', { page, size, sort, q, owner }],
    queryFn: async ({ signal }): Promise<ProjectPage> => {
      const { data, error, response } = await client.GET('/v2/projects', {
        params: { query: { page, size, sort, q: q || undefined, owner: owner || undefined } },
        signal,
      });
      if (!data || !('items' in data)) {
        throw toApiError(error, response, 'load projects');
      }
      return data;
    },
    placeholderData: keepPreviousData,
  });

  const owners = useQuery({
    queryKey: ['users', 'names'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/users/names', { signal });
      if (!data) {
        throw toApiError(error, response, 'load users');
      }
      return data;
    },
    staleTime: 5 * 60_000,
  });

  const remove = useMutation({
    mutationFn: async (ids: number[]) => {
      const { data, error, response } = await client.DELETE('/v2/projects', { params: { query: { ids } } });
      if (!data) {
        throw toApiError(error, response, 'delete the projects');
      }
      return data;
    },
    onSuccess: (result) => {
      const deleted = result.deleted?.length ?? 0;
      notify.success(deleted === 1 ? 'Project deleted' : `${deleted} projects deleted`);
      setSelection([]);
    },
    onError: (error) => notify.error('Nothing was deleted', error.message),
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  });

  // After deleting the last rows of the last page, step back to a page that has rows
  const total = projects.data?.total ?? 0;
  useEffect(() => {
    if (projects.data && page > 0 && page * size >= total) {
      const last = Math.ceil(total / size);
      update({ page: last > 1 ? last : undefined });
    }
  }, [projects.data, page, size, total]);

  const canCreate = hasRight(user, 'CREATE_PROJECT');
  const canDelete = (project: ProjectSummary) => hasRightOrOwns(user, 'DELETE_PROJECT', project.owner);
  const selectionDeletable = selection.length > 0 && selection.every(canDelete);

  const confirmDelete = (targets: ProjectSummary[]) =>
    confirmDialog({
      header: targets.length === 1 ? 'Delete project' : `Delete ${targets.length} projects`,
      message:
        targets.length === 1
          ? `Delete project "${targets[0]!.name}"? This can't be undone.`
          : `Delete these ${targets.length} projects? This can't be undone.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(targets.map((p) => p.id!)),
    });

  const [sortField, sortDirection] = sort.split(',');
  const onSort = (event: DataTableSortEvent) => {
    const field = event.sortField;
    if (SORT_FIELDS.has(field)) {
      update({ sort: `${field},${event.sortOrder === -1 ? 'desc' : 'asc'}`, page: undefined });
    }
  };
  const onPage = (event: DataTableStateEvent) =>
    update({ page: event.page !== undefined && event.page > 0 ? event.page + 1 : undefined, size: event.rows });

  const actions = (project: ProjectSummary) => (
    <div className="row-actions">
      <Button
        icon="pi pi-pencil"
        rounded
        text
        aria-label={`Open ${project.name}`}
        tooltip="Open"
        tooltipOptions={{ position: 'top' }}
        onClick={() => void navigate(`/projects/${project.id}`)}
      />
      {canCreate && (
        <Button
          icon="pi pi-copy"
          rounded
          text
          aria-label={`Copy ${project.name}`}
          tooltip="Copy"
          tooltipOptions={{ position: 'top' }}
          onClick={() => setCopying(project)}
        />
      )}
      {canDelete(project) && (
        <Button
          icon="pi pi-trash"
          rounded
          text
          severity="danger"
          aria-label={`Delete ${project.name}`}
          tooltip="Delete"
          tooltipOptions={{ position: 'top' }}
          onClick={() => confirmDelete([project])}
        />
      )}
    </div>
  );

  const columns = buildColumns(actions);
  const visibleColumns = preferences.columns.filter((c) => c.visible && columns[c.colName ?? '']);
  const hideable = preferences.columns.filter((c) => c.hideable && columns[c.colName ?? '']);

  const ownerOptions = useMemo(
    () => [
      ...(user?.name ? [{ label: 'My projects', value: user.name }] : []),
      ...(owners.data ?? []).filter((name) => name !== user?.name).map((name) => ({ label: name, value: name })),
    ],
    [owners.data, user?.name],
  );

  return (
    <section className="list-page">
      <div className="page-header">
        <h1>Projects</h1>
        <Button
          label="New project"
          icon="pi pi-plus"
          onClick={() => setCreating(true)}
          disabled={!canCreate}
          tooltip={canCreate ? undefined : "You don't have permission to create projects"}
          tooltipOptions={{ showOnDisabled: true, position: 'left' }}
        />
      </div>

      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search name, product, comments"
            aria-label="Search projects"
            className="list-search"
          />
        </IconField>
        <Dropdown
          value={owner || null}
          options={ownerOptions}
          onChange={(e) => update({ owner: (e.value as string | null) ?? undefined, page: undefined })}
          placeholder="All owners"
          showClear
          aria-label="Owner"
          filter={ownerOptions.length > 8}
          className="list-filter"
        />
        <div className="list-toolbar-end">
          {selection.length > 0 && (
            <Button
              label={`Delete ${selection.length} selected`}
              icon="pi pi-trash"
              severity="danger"
              outlined
              disabled={!selectionDeletable || remove.isPending}
              tooltip={selectionDeletable ? undefined : "You can't delete every selected project"}
              tooltipOptions={{ showOnDisabled: true, position: 'top' }}
              onClick={() => confirmDelete(selection)}
            />
          )}
          <MultiSelect
            value={hideable.filter((c) => c.visible).map((c) => c.colName)}
            options={hideable.map((c) => ({ label: c.displayName, value: c.colName }))}
            onChange={(e) => preferences.setVisible(e.value as string[])}
            placeholder="Columns"
            selectedItemsLabel="Columns"
            maxSelectedLabels={0}
            aria-label="Visible columns"
            dropdownIcon="pi pi-table"
            className="list-columns"
          />
        </div>
      </div>

      {projects.error && !projects.data ? (
        <Message severity="error" text={projects.error.message} className="list-error" />
      ) : (
        <DataTable
          value={projects.data?.items ?? []}
          dataKey="id"
          lazy
          paginator
          first={page * size}
          rows={size}
          rowsPerPageOptions={PAGE_SIZES}
          totalRecords={total}
          onPage={onPage}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown CurrentPageReport"
          currentPageReportTemplate="{first}–{last} of {totalRecords}"
          sortField={sortField}
          sortOrder={sortDirection === 'desc' ? -1 : 1}
          onSort={onSort}
          loading={projects.isFetching || preferences.isLoading}
          selectionMode="checkbox"
          selection={selection}
          onSelectionChange={(e) => setSelection(e.value as ProjectSummary[])}
          resizableColumns
          columnResizeMode="expand"
          onColumnResizeEnd={(e) => {
            const key = (e.column.props.columnKey ?? '') as string;
            if (key) {
              preferences.setWidth(key, e.element.offsetWidth);
            }
          }}
          emptyMessage={q || owner ? 'No projects match your search' : 'No projects yet'}
          stripedRows
          size="small"
          tableStyle={{ minWidth: '40rem' }}
        >
          {visibleColumns.map((pref) => columnFor(pref, columns))}
        </DataTable>
      )}

      {creating && <CreateProjectDialog onHide={() => setCreating(false)} />}
      {copying && (
        <CopyProjectDialog
          project={copying}
          onHide={() => setCopying(undefined)}
          onCopied={() => void queryClient.invalidateQueries({ queryKey: ['projects'] })}
        />
      )}
    </section>
  );
}

interface ColumnDef {
  header?: string;
  field?: keyof ProjectSummary;
  sortable?: boolean;
  body?: (project: ProjectSummary, options: ColumnBodyOptions) => ReactNode;
  selection?: boolean;
  resizable?: boolean;
}

/** Columns by their preference key (TableColumnDefaults.PROJECT_COL_PREFS) */
function buildColumns(actions: (project: ProjectSummary) => ReactNode): Record<string, ColumnDef> {
  return {
    selectColumn: { selection: true, resizable: false },
    idColumn: { field: 'id', sortable: true },
    nameColumn: {
      field: 'name',
      sortable: true,
      body: (p) => (
        <Link to={`/projects/${p.id}`} title={`${p.name} (id ${p.id})`}>
          {p.name}
        </Link>
      ),
    },
    productColumn: { field: 'productName', sortable: true },
    commentsColumn: { field: 'comments', body: (p) => <span title={p.comments}>{p.comments}</span> },
    createColumn: { field: 'created', sortable: true, body: (p) => formatDateTime(p.created) },
    modifiedColumn: { field: 'modified', sortable: true, body: (p) => formatDateTime(p.modified) },
    ownerColumn: { field: 'owner', sortable: true },
    actionsColumn: { header: '', body: actions, resizable: false },
  };
}

function columnFor(pref: ColumnPreference, columns: Record<string, ColumnDef>) {
  const key = pref.colName!;
  const def = columns[key]!;
  const width = pref.size ? `${pref.size}px` : undefined;
  if (def.selection) {
    return <Column key={key} columnKey={key} selectionMode="multiple" headerStyle={{ width: '3rem' }} />;
  }
  return (
    <Column
      key={key}
      columnKey={key}
      field={def.field}
      header={def.header ?? pref.displayName}
      sortable={def.sortable}
      body={def.body}
      style={{ width }}
      bodyClassName="ellipsis"
      resizeable={def.resizable !== false}
    />
  );
}

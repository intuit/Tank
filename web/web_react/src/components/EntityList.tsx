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
import { useSearchParams } from 'react-router';
import type { Schemas } from '../api/client';
import { toApiError } from '../api/errors';
import { useTablePreferences, type ColumnPreference, type TableName } from '../hooks/useTablePreferences';
import { useNotify } from '../notify';
import { useSession } from '../session';

export const PAGE_SIZES = [25, 50, 75, 100];
const DEFAULT_SORT = 'modified,desc';
const SEARCH_DELAY_MS = 300;

export interface ListQuery {
  page: number;
  size: number;
  sort: string;
  q?: string;
  owner?: string;
}

export interface PageOf<T> {
  items?: T[];
  total?: number;
}

export interface ColumnDef<T> {
  header?: string;
  field?: keyof T & string;
  sortable?: boolean;
  body?: (row: T, options: ColumnBodyOptions) => ReactNode;
  selection?: boolean;
  resizable?: boolean;
}

export interface Noun {
  one: string;
  many: string;
}

/** List state kept in the URL, so reloading or going back restores it */
export function useListParams() {
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

/**
 * A paged, searchable list of things users own (the JSF SelectableBean tables): search, owner filter,
 * server-side sort and paging kept in the URL, saved column visibility and widths, selection and
 * bulk delete. The page supplies the calls, its columns and its header.
 */
export function EntityList<T extends { id?: number; name?: string; owner?: string }>({
  title,
  noun,
  table,
  queryKey,
  searchPlaceholder,
  sortFields,
  fetchPage,
  deleteMany,
  canDelete,
  columns,
  headerActions,
}: {
  title: string;
  noun: Noun;
  table: TableName;
  /** First element of the TanStack Query key; mutations elsewhere invalidate it */
  queryKey: string;
  searchPlaceholder: string;
  /** Fields the server sorts by */
  sortFields: string[];
  fetchPage: (query: ListQuery, signal: AbortSignal) => Promise<PageOf<T>>;
  deleteMany: (ids: number[]) => Promise<Schemas['BulkDeleteResult']>;
  canDelete: (row: T) => boolean;
  /** Columns by preference key; `confirmDelete` asks before deleting one row */
  columns: (helpers: { confirmDelete: (rows: T[]) => void }) => Record<string, ColumnDef<T>>;
  headerActions?: ReactNode;
}) {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const { page, size, sort, q, owner, update } = useListParams();
  const preferences = useTablePreferences(table);
  const [search, setSearch] = useState(q);
  const [selection, setSelection] = useState<T[]>([]);

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

  const rows = useQuery({
    queryKey: [queryKey, { page, size, sort, q, owner }],
    queryFn: ({ signal }) => fetchPage({ page, size, sort, q: q || undefined, owner: owner || undefined }, signal),
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
    mutationFn: deleteMany,
    onSuccess: (result) => {
      const deleted = result.deleted?.length ?? 0;
      notify.success(deleted === 1 ? `${capitalize(noun.one)} deleted` : `${deleted} ${noun.many} deleted`);
      setSelection([]);
    },
    onError: (error) => notify.error('Nothing was deleted', error.message),
    onSettled: () => queryClient.invalidateQueries({ queryKey: [queryKey] }),
  });

  // After deleting the last rows of the last page, step back to a page that has rows
  const total = rows.data?.total ?? 0;
  useEffect(() => {
    if (rows.data && page > 0 && page * size >= total) {
      const last = Math.ceil(total / size);
      update({ page: last > 1 ? last : undefined });
    }
  }, [rows.data, page, size, total]);

  const confirmDelete = (targets: T[]) =>
    confirmDialog({
      header: targets.length === 1 ? `Delete ${noun.one}` : `Delete ${targets.length} ${noun.many}`,
      message:
        targets.length === 1
          ? `Delete ${noun.one} "${targets[0]!.name}"? This can't be undone.`
          : `Delete these ${targets.length} ${noun.many}? This can't be undone.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(targets.map((t) => t.id!)),
    });
  const selectionDeletable = selection.length > 0 && selection.every(canDelete);

  const [sortField, sortDirection] = sort.split(',');
  const onSort = (event: DataTableSortEvent) => {
    if (sortFields.includes(event.sortField)) {
      update({ sort: `${event.sortField},${event.sortOrder === -1 ? 'desc' : 'asc'}`, page: undefined });
    }
  };
  const onPage = (event: DataTableStateEvent) =>
    update({ page: event.page !== undefined && event.page > 0 ? event.page + 1 : undefined, size: event.rows });

  const defs = columns({ confirmDelete });
  const visibleColumns = preferences.columns.filter((c) => c.visible && defs[c.colName ?? '']);
  const hideable = preferences.columns.filter((c) => c.hideable && defs[c.colName ?? '']);
  const ownerOptions = useMemo(
    () => [
      ...(user?.name ? [{ label: `My ${noun.many}`, value: user.name }] : []),
      ...(owners.data ?? []).filter((name) => name !== user?.name).map((name) => ({ label: name, value: name })),
    ],
    [owners.data, user?.name, noun.many],
  );

  return (
    <section className="list-page">
      <div className="page-header">
        <h1>{title}</h1>
        <div className="editor-actions">{headerActions}</div>
      </div>

      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder={searchPlaceholder}
            aria-label={`Search ${noun.many}`}
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
              tooltip={selectionDeletable ? undefined : `You can't delete every selected ${noun.one}`}
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

      {rows.error && !rows.data ? (
        <Message severity="error" text={rows.error.message} className="list-error" />
      ) : (
        <DataTable
          value={rows.data?.items ?? []}
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
          loading={rows.isFetching || preferences.isLoading}
          selectionMode="checkbox"
          selection={selection}
          onSelectionChange={(e) => setSelection(e.value as T[])}
          resizableColumns
          columnResizeMode="expand"
          onColumnResizeEnd={(e) => {
            const key = (e.column.props.columnKey ?? '') as string;
            if (key) {
              preferences.setWidth(key, e.element.offsetWidth);
            }
          }}
          emptyMessage={q || owner ? `No ${noun.many} match your search` : `No ${noun.many} yet`}
          stripedRows
          size="small"
          tableStyle={{ minWidth: '40rem' }}
        >
          {visibleColumns.map((pref) => columnFor(pref, defs))}
        </DataTable>
      )}
    </section>
  );
}

function columnFor<T>(pref: ColumnPreference, columns: Record<string, ColumnDef<T>>) {
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

function capitalize(word: string): string {
  return word.charAt(0).toUpperCase() + word.slice(1);
}

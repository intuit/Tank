import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { confirmDialog } from 'primereact/confirmdialog';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useState, type ReactNode } from 'react';
import { Link, useNavigate } from 'react-router';
import { toApiError } from '../../api/errors';
import { CopyDialog } from '../../components/CopyDialog';
import { RowAction } from '../../components/entityColumns';
import { useNotify } from '../../notify';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { useFilters, type Filter, type FilterGroup } from './useFilters';

type Kind = 'filter' | 'group';
type Row = Filter | FilterGroup;

const NOUNS: Record<Kind, { one: string; many: string }> = {
  filter: { one: 'filter', many: 'filters' },
  group: { one: 'filter group', many: 'filter groups' },
};

/** Where a filter or group is edited */
export const filterHref = (kind: Kind, id: number | 'new' | undefined) => (kind === 'group' ? `/filters/groups/${id}` : `/filters/${id}`);

/** The filter groups and filters (FilterGroupBean, FilterBean and filters/index.xhtml) */
export function FiltersPage() {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const all = useFilters();
  const [copying, setCopying] = useState<{ kind: Kind; row: Row }>();
  /** Show only the filters in this group */
  const [inGroup, setInGroup] = useState<number | null>(null);

  const remove = useMutation({
    mutationFn: async ({ kind, rows }: { kind: Kind; rows: Row[] }) => {
      // there's no bulk delete; one at a time, so a failure says which
      const failed: string[] = [];
      for (const row of rows) {
        const { error, response } =
          kind === 'group'
            ? await client.DELETE('/v2/filters/groups/{filterGroupId}', { params: { path: { filterGroupId: row.id! } } })
            : await client.DELETE('/v2/filters/{filterId}', { params: { path: { filterId: row.id! } } });
        if (!response.ok) {
          failed.push(`${row.name}: ${toApiError(error, response, 'delete it').message}`);
        }
      }
      return { kind, deleted: rows.length - failed.length, failed };
    },
    onSuccess: ({ kind, deleted, failed }) => {
      const noun = NOUNS[kind];
      if (deleted) {
        notify.success(deleted === 1 ? `${capitalize(noun.one)} deleted` : `${deleted} ${noun.many} deleted`);
      }
      if (failed.length) {
        notify.error(`${failed.length} ${failed.length === 1 ? noun.one : noun.many} not deleted`, failed.join('\n'));
      }
    },
    onError: (error) => notify.error('Nothing was deleted', error.message),
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['filters'] }),
  });

  const confirmDelete = (kind: Kind, rows: Row[]) => {
    const noun = NOUNS[kind];
    confirmDialog({
      header: rows.length === 1 ? `Delete ${noun.one}` : `Delete ${rows.length} ${noun.many}`,
      message:
        rows.length === 1
          ? `Delete ${noun.one} "${rows[0]!.name}"? This can't be undone.${kind === 'filter' ? ' It is also removed from its groups.' : ''}`
          : `Delete these ${rows.length} ${noun.many}? This can't be undone.${kind === 'filter' ? ' They are also removed from their groups.' : ''}`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate({ kind, rows }),
    });
  };

  if (all.error) {
    return <Message severity="error" text={all.error.message} />;
  }
  const groups = all.data?.groups ?? [];
  const filters = all.data?.filters ?? [];
  const group = groups.find((g) => g.id === inGroup);
  const shownFilters = group ? filters.filter((f) => group.filterIds?.includes(f.id!)) : filters;

  return (
    <section className="list-page editor-wide">
      <div className="page-header">
        <h1>Filters</h1>
      </div>
      <p className="field-help">
        Filters change or remove a recording's steps when it becomes a script, and can be applied to a script later. A
        group applies several filters together.
      </p>
      <div className="filter-lists">
        <LocalList
          kind="group"
          rows={groups}
          loading={all.isPending}
          busy={remove.isPending}
          canCreate={hasRight(user, 'CREATE_FILTER')}
          canDelete={(g) => hasRightOrOwns(user, 'DELETE_FILTER', g.creator)}
          onCopy={(row) => setCopying({ kind: 'group', row })}
          onDelete={(rows) => confirmDelete('group', rows)}
          extraColumns={
            <Column
              header="Filters"
              field="filterIds"
              body={(g: FilterGroup) => (
                <Button
                  label={String(g.filterIds?.length ?? 0)}
                  link
                  size="small"
                  className="count-link"
                  aria-label={`Show the filters in ${g.name}`}
                  tooltip="Show these filters"
                  tooltipOptions={{ position: 'top' }}
                  onClick={() => setInGroup(g.id!)}
                />
              )}
              style={{ width: '5rem' }}
            />
          }
        />
        <LocalList
          kind="filter"
          rows={shownFilters}
          loading={all.isPending}
          busy={remove.isPending}
          canCreate={hasRight(user, 'CREATE_FILTER')}
          canDelete={(f) => hasRightOrOwns(user, 'DELETE_FILTER', f.creator)}
          onCopy={(row) => setCopying({ kind: 'filter', row })}
          onDelete={(rows) => confirmDelete('filter', rows)}
          toolbar={
            <Dropdown
              value={inGroup}
              options={groups.map((g) => ({ label: g.name, value: g.id }))}
              onChange={(e) => setInGroup((e.value as number | null) ?? null)}
              placeholder="In any group"
              showClear
              filter={groups.length > 8}
              aria-label="Group"
              className="list-filter"
            />
          }
        />
      </div>

      {copying && (
        <CopyDialog
          noun={NOUNS[copying.kind].one}
          name={copying.row.name}
          copy={async (name) => {
            const id = copying.row.id!;
            const result =
              copying.kind === 'group'
                ? await client.POST('/v2/filters/groups/{filterGroupId}/copy', { params: { path: { filterGroupId: id } }, body: { name } })
                : await client.POST('/v2/filters/{filterId}/copy', { params: { path: { filterId: id } }, body: { name } });
            if (!result.data) {
              throw toApiError(result.error, result.response, `copy the ${NOUNS[copying.kind].one}`);
            }
            return result.data;
          }}
          onHide={() => setCopying(undefined)}
          onCopied={() => void queryClient.invalidateQueries({ queryKey: ['filters'] })}
        />
      )}
    </section>
  );
}

/** One of the page's two tables, searched, filtered and sorted in the browser */
function LocalList<T extends Row>({
  kind,
  rows,
  loading,
  busy,
  canCreate,
  canDelete,
  onCopy,
  onDelete,
  toolbar,
  extraColumns,
}: {
  kind: Kind;
  rows: T[];
  loading: boolean;
  busy: boolean;
  canCreate: boolean;
  canDelete: (row: T) => boolean;
  onCopy: (row: T) => void;
  onDelete: (rows: T[]) => void;
  toolbar?: ReactNode;
  extraColumns?: ReactNode;
}) {
  const navigate = useNavigate();
  const noun = NOUNS[kind];
  const [search, setSearch] = useState('');
  const [product, setProduct] = useState<string | null>(null);
  const [selection, setSelection] = useState<T[]>([]);

  const q = search.trim().toLowerCase();
  const products = [...new Set(rows.map((r) => r.productName).filter((p): p is string => !!p))].sort();
  const shown = rows.filter(
    (r) =>
      (!q || r.name?.toLowerCase().includes(q) || String(r.id) === q) && (!product || r.productName === product),
  );
  // a deleted or filtered-out row isn't still selected
  const selected = selection.filter((s) => shown.some((r) => r.id === s.id));
  const deletable = selected.length > 0 && selected.every(canDelete);
  const title = capitalize(noun.many);

  return (
    <div className="filter-list">
      <div className="list-toolbar">
        <h2 className="list-title">{title}</h2>
        <span className="field-help">
          {shown.length === rows.length ? rows.length : `${shown.length} of ${rows.length}`}
        </span>
        <div className="list-toolbar-end">
          <Button
            label={kind === 'group' ? 'New group' : 'New filter'}
            icon="pi pi-plus"
            size="small"
            disabled={!canCreate}
            title={canCreate ? undefined : `You don't have permission to create ${noun.many}`}
            onClick={() => void navigate(filterHref(kind, 'new'))}
          />
        </div>
      </div>
      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search name or ID"
            aria-label={`Search ${noun.many}`}
            className="list-search"
          />
        </IconField>
        <Dropdown
          value={product}
          options={products}
          onChange={(e) => setProduct((e.value as string | null) ?? null)}
          placeholder="All products"
          showClear
          aria-label={`${capitalize(noun.one)} product`}
          className="list-filter"
        />
        {toolbar}
        {selected.length > 0 && (
          // a span of our own carries the hint: showOnDisabled wraps the button in DOM React can't remove
          <span title={deletable ? undefined : `You can't delete every selected ${noun.one}`}>
            <Button
              label={`Delete ${selected.length} selected`}
              icon="pi pi-trash"
              severity="danger"
              outlined
              size="small"
              disabled={!deletable || busy}
              onClick={() => onDelete(selected)}
            />
          </span>
        )}
      </div>
      <DataTable
        value={shown}
        dataKey="id"
        selectionMode="checkbox"
        selection={selected}
        onSelectionChange={(e) => setSelection(e.value as T[])}
        loading={loading}
        emptyMessage={rows.length ? `No ${noun.many} match.` : `No ${noun.many} yet.`}
        sortField="name"
        sortOrder={1}
        removableSort
        size="small"
        stripedRows
        // fixed, so a long filter name is cut short rather than widening the table
        tableStyle={{ tableLayout: 'fixed', width: '100%' }}
        scrollable
        scrollHeight="60vh"
        // names the <table> itself, for screen readers moving between the two lists
        pt={{ table: { 'aria-label': title } }}
      >
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
        <Column header="ID" field="id" sortable style={{ width: '4.5rem' }} />
        <Column
          header="Name"
          field="name"
          sortable
          sortFunction={(e) => [...e.data].sort((a: T, b: T) => e.order! * (a.name ?? '').localeCompare(b.name ?? '', undefined, { sensitivity: 'base' }))}
          body={(r: T) => (
            <Link to={filterHref(kind, r.id)} title={`${r.name} (id ${r.id})`}>
              {r.name}
            </Link>
          )}
          bodyClassName="ellipsis"
        />
        <Column header="Product" field="productName" sortable bodyClassName="ellipsis" style={{ width: '8rem' }} />
        {extraColumns}
        <Column header="Owner" field="creator" sortable bodyClassName="ellipsis" style={{ width: '7rem' }} />
        <Column
          body={(r: T) => (
            <div className="row-actions">
              <RowAction icon="pi pi-pencil" label="Open" name={r.name} onClick={() => void navigate(filterHref(kind, r.id))} />
              {canCreate && <RowAction icon="pi pi-copy" label="Copy" name={r.name} onClick={() => onCopy(r)} />}
              {canDelete(r) && (
                <RowAction icon="pi pi-trash" label="Delete" name={r.name} severity="danger" onClick={() => onDelete([r])} />
              )}
            </div>
          )}
          style={{ width: canCreate ? '8rem' : '6rem' }}
        />
      </DataTable>
    </div>
  );
}

function capitalize(text: string) {
  return text.charAt(0).toUpperCase() + text.slice(1);
}

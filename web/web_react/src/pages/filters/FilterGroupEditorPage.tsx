import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useCallback, useRef, useState } from 'react';
import { Link, useParams } from 'react-router';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { CopyDialog } from '../../components/CopyDialog';
import { ConflictDialog, useUnsavedGuard } from '../../components/editorGuards';
import { Field } from '../../components/Field';
import { formatDateTime } from '../../format';
import { useConfigOptions } from '../../hooks/useConfigOptions';
import { useDocumentDraft } from '../../hooks/useDocumentDraft';
import { useNotify } from '../../notify';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { MAX_NAME_LENGTH } from './filterRules';
import { useFilters, type Filter, type FilterGroup } from './useFilters';

/** The group as edited: its fields and member IDs, without the member filters the server also sends */
function asGroup(detail: Schemas['FilterGroupDetailTO']): FilterGroup {
  const { filters: _filters, ...group } = detail;
  return { ...group, filterIds: [...(group.filterIds ?? [])].sort((a, b) => a - b) };
}

/** The fields a create or Save as sends: the server picks the ID, owner and times */
function asNew(group: FilterGroup, name = group.name): FilterGroup {
  return { name: name?.trim(), productName: group.productName, filterIds: group.filterIds };
}

function groupProblems(group: FilterGroup): string[] {
  const name = group.name?.trim() ?? '';
  if (!name) return ['Name is required'];
  if (name.length > MAX_NAME_LENGTH) return [`Name can be at most ${MAX_NAME_LENGTH} characters`];
  return [];
}

/** The filter group editor (FilterGroupCreationBean and createNewFilterGroup.xhtml), fresh for each group */
export function FilterGroupEditorPage() {
  const param = useParams().groupId;
  const groupId = param === 'new' ? 'new' : Number(param);
  return <FilterGroupEditor key={String(groupId)} groupId={groupId} />;
}

function FilterGroupEditor({ groupId }: { groupId: number | 'new' }) {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const options = useConfigOptions();
  const all = useFilters();
  const isNew = groupId === 'new';
  const [savingAs, setSavingAs] = useState(false);
  const [showProblems, setShowProblems] = useState(false);

  const group = useDocumentDraft<FilterGroup, string>({
    queryKey: ['filter-group', groupId],
    enabled: isNew || Number.isInteger(groupId),
    load: async (signal) => {
      if (isNew) return { name: '', filterIds: [] };
      const { data, error, response } = await client.GET('/v2/filters/groups/{filterGroupId}', {
        params: { path: { filterGroupId: groupId } },
        signal,
      });
      if (!data) throw toApiError(error, response, 'load the filter group');
      return asGroup(data);
    },
    store: async (doc) => {
      const body = { ...doc, name: doc.name?.trim() };
      const { data, error, response } = isNew
        ? await client.POST('/v2/filters/groups', { body: asNew(doc) })
        : await client.PUT('/v2/filters/groups/{filterGroupId}', { params: { path: { filterGroupId: groupId } }, body });
      if (!data) throw toApiError(error, response, 'save the filter group');
      return asGroup(data);
    },
    validate: useCallback(groupProblems, []),
    invalidate: [['filters']],
  });
  const { draft, saved, dirty, problems, update } = group;
  const { leaveTo } = useUnsavedGuard(dirty, draft?.name);

  const users = useQuery({
    queryKey: ['users', 'names'],
    enabled: !isNew,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/users/names', { signal });
      if (!data) throw toApiError(error, response, 'load users');
      return data;
    },
    staleTime: 5 * 60_000,
  });

  if (!isNew && !Number.isInteger(groupId)) {
    return <Message severity="error" text="That isn't a filter group ID" />;
  }
  if (group.isLoading || (!draft && !group.loadError)) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (group.loadError || !draft || !saved) {
    return (
      <section>
        <Message severity="error" text={group.loadError?.message ?? 'Filter group not found'} />
        <p>
          <Link to="/filters">Back to filters</Link>
        </p>
      </section>
    );
  }

  const readOnly = isNew ? !hasRight(user, 'CREATE_FILTER') : !hasRightOrOwns(user, 'EDIT_FILTER', saved.creator);
  const canChangeOwner = !isNew && !readOnly && (!!user?.admin || user?.name === saved.creator);
  const products = options.data?.products ?? [];
  const productOptions =
    draft.productName && !products.some((p) => p.value === draft.productName)
      ? [...products, { label: draft.productName, value: draft.productName }]
      : products;

  const save = async () => {
    if (problems.length) {
      setShowProblems(true);
      return;
    }
    setShowProblems(false);
    if (!isNew) {
      group.save();
      return;
    }
    try {
      const created = await group.saveAsync(draft);
      notify.success('Filter group created', created.name);
      queryClient.removeQueries({ queryKey: ['filter-group', 'new'] });
      leaveTo(`/filters/groups/${created.id}`);
    } catch {
      // shown below the header
    }
  };

  return (
    <section className="editor editor-wide">
      <p className="breadcrumb">
        <Link to="/filters">Filters</Link> / {isNew ? 'New filter group' : saved.name}
      </p>
      <div className="page-header">
        <h1>
          {draft.name || (isNew ? 'New filter group' : 'Unnamed filter group')}
          {dirty && !isNew && <span className="unsaved"> (unsaved)</span>}
        </h1>
        <div className="editor-actions">
          {!readOnly && (
            <>
              {!isNew && <Button label="Revert" icon="pi pi-undo" text disabled={!dirty || group.saving} onClick={group.revert} />}
              {!isNew && hasRight(user, 'CREATE_FILTER') && (
                <Button label="Save as…" icon="pi pi-copy" outlined disabled={group.saving} onClick={() => setSavingAs(true)} />
              )}
              <Button
                label={isNew ? 'Create' : 'Save'}
                icon="pi pi-save"
                disabled={!isNew && !dirty}
                loading={group.saving}
                onClick={() => void save()}
              />
            </>
          )}
        </div>
      </div>

      {readOnly && (
        <Message
          severity="info"
          text={isNew ? "You don't have permission to create filter groups." : 'You can view this filter group but not change it.'}
          className="editor-message"
        />
      )}
      {showProblems && problems.length > 0 && <Message severity="error" text={problems.join('. ')} className="editor-message" />}
      {group.saveError && <Message severity="error" text={group.saveError.message} className="editor-message" />}

      <div className="form-columns editor-general">
        <Field label="Name" htmlFor="group-name">
          <InputText
            id="group-name"
            value={draft.name ?? ''}
            onChange={(e) => update((d) => void (d.name = e.target.value))}
            maxLength={MAX_NAME_LENGTH}
            disabled={readOnly}
            autoFocus={isNew}
          />
        </Field>
        <Field label="Product" htmlFor="group-product">
          <Dropdown
            inputId="group-product"
            value={draft.productName || null}
            options={productOptions}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => update((d) => void (d.productName = (e.value as string | null) ?? undefined))}
            placeholder="None"
            showClear={!readOnly}
            disabled={readOnly}
          />
        </Field>
        {!isNew && (
          <Field label="Owner" htmlFor="group-owner">
            {canChangeOwner ? (
              <Dropdown
                inputId="group-owner"
                value={draft.creator ?? null}
                options={[...new Set([...(users.data ?? []), saved.creator ?? ''])].filter(Boolean)}
                onChange={(e) => update((d) => void (d.creator = e.value as string))}
                filter
              />
            ) : (
              <InputText id="group-owner" value={saved.creator ?? ''} disabled />
            )}
          </Field>
        )}
      </div>

      <h2 className="form-section">Filters</h2>
      <p className="field-help">A group applies its filters together, in the order the filters list shows them.</p>
      {all.error ? (
        <Message severity="error" text={all.error.message} />
      ) : (
        <Members
          filters={all.data?.filters ?? []}
          loading={all.isPending}
          memberIds={draft.filterIds ?? []}
          onChange={(ids) => update((d) => void (d.filterIds = [...ids].sort((a, b) => a - b)))}
          readOnly={readOnly}
        />
      )}

      {!isNew && (
        <p className="field-help">
          {[saved.created && `Created ${formatDateTime(saved.created)}`, saved.modified && `Last saved ${formatDateTime(saved.modified)}`]
            .filter(Boolean)
            .join(' · ')}
        </p>
      )}

      <ConflictDialog
        visible={group.conflict}
        noun="filter group"
        name={saved.name}
        onReload={() => void group.reload().then(() => notify.success('Filter group reloaded'))}
      />
      {savingAs && (
        <CopyDialog
          noun="filter group"
          name={draft.name}
          copy={async (name) => {
            // a copy of the group as edited, leaving this one as it was saved
            const { data, error, response } = await client.POST('/v2/filters/groups', { body: asNew(draft, name) });
            if (!data) throw toApiError(error, response, 'save the copy');
            void queryClient.invalidateQueries({ queryKey: ['filters'] });
            leaveTo(`/filters/groups/${data.id}`);
            return data;
          }}
          onHide={() => setSavingAs(false)}
          onCopied={() => undefined}
        />
      )}
    </section>
  );
}

/** Every filter, ticked when it's in the group (the JSF member table) */
function Members({
  filters,
  loading,
  memberIds,
  onChange,
  readOnly,
}: {
  filters: Filter[];
  loading: boolean;
  memberIds: number[];
  onChange: (ids: number[]) => void;
  readOnly: boolean;
}) {
  const [search, setSearch] = useState('');
  const [onlyMembers, setOnlyMembers] = useState(readOnly);
  const members = new Set(memberIds);
  const q = search.trim().toLowerCase();
  const shown = filters.filter(
    (f) => (!onlyMembers || members.has(f.id!)) && (!q || f.name?.toLowerCase().includes(q) || f.productName?.toLowerCase().includes(q)),
  );

  const latest = useRef({ shown, memberIds, onChange });
  latest.current = { shown, memberIds, onChange };

  return (
    <div className="filter-list">
      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search name or product"
            aria-label="Search filters"
            className="list-search"
          />
        </IconField>
        <div className="field-inline">
          <Checkbox inputId="only-members" checked={onlyMembers} onChange={(e) => setOnlyMembers(!!e.checked)} />
          <label htmlFor="only-members">Only filters in this group</label>
        </div>
        <span className="field-help">
          {memberIds.length} of {filters.length} filters in this group
        </span>
      </div>
      <DataTable
        value={shown}
        dataKey="id"
        selectionMode="checkbox"
        selection={shown.filter((f) => members.has(f.id!))}
        onSelectionChange={(e) => {
          if (readOnly) return;
          // the table's rows keep the handler from an earlier render, so read this render's state
          const { shown: rows, memberIds: ids, onChange: change } = latest.current;
          // the table knows only the rows shown; members hidden by the search stay members
          const picked = (e.value as Filter[]).map((f) => f.id!);
          const hidden = ids.filter((id) => !rows.some((f) => f.id === id));
          change([...new Set([...hidden, ...picked])]);
        }}
        isDataSelectable={() => !readOnly}
        loading={loading}
        emptyMessage={onlyMembers ? 'No filters in this group yet.' : 'No filters match.'}
        sortField="name"
        sortOrder={1}
        size="small"
        stripedRows
        tableStyle={{ tableLayout: 'fixed', width: '100%' }}
        scrollable
        scrollHeight="50vh"
        pt={{ table: { 'aria-label': 'Filters in the group' } }}
      >
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
        <Column
          header="Name"
          field="name"
          sortable
          sortFunction={(e) =>
            [...e.data].sort((a: Filter, b: Filter) => e.order! * (a.name ?? '').trim().localeCompare((b.name ?? '').trim(), undefined, { sensitivity: 'base' }))
          }
          body={(f: Filter) => (
            <Link to={`/filters/${f.id}`} title={`${f.name} (id ${f.id})`}>
              {f.name}
            </Link>
          )}
          bodyClassName="ellipsis"
        />
        <Column header="Product" field="productName" sortable bodyClassName="ellipsis" style={{ width: '10rem' }} />
        <Column header="Owner" field="creator" sortable bodyClassName="ellipsis" style={{ width: '9rem' }} />
      </DataTable>
    </div>
  );
}

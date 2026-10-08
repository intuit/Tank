import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { SelectButton } from 'primereact/selectbutton';
import { useCallback, useState } from 'react';
import { Link, useParams } from 'react-router';
import { toApiError } from '../../api/errors';
import { classicUrl } from '../../classicPages';
import { CopyDialog } from '../../components/CopyDialog';
import { ConflictDialog, useUnsavedGuard } from '../../components/editorGuards';
import { Field } from '../../components/Field';
import { formatDateTime } from '../../format';
import { useConfigOptions } from '../../hooks/useConfigOptions';
import { useDocumentDraft } from '../../hooks/useDocumentDraft';
import { useNotify } from '../../notify';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import {
  ACTION_TYPES,
  blankFilter,
  changeAction,
  fieldFor,
  filterProblems,
  MAX_NAME_LENGTH,
  newAction,
  newCondition,
  scopesFor,
  splitValue,
  type Action,
  type ActionField,
  type Condition,
} from './filterRules';
import type { Filter } from './useFilters';

const MATCH_ALL = [
  { label: 'All', value: true },
  { label: 'Any', value: false },
];

/** The filter editor (ScriptFilterCreationBean and addFilter.xhtml), fresh for each filter */
export function FilterEditorPage() {
  const param = useParams().filterId;
  const filterId = param === 'new' ? 'new' : Number(param);
  return <FilterEditor key={String(filterId)} filterId={filterId} />;
}

/** The fields a create or Save as sends: the server picks the ID, owner and times */
function asNew(filter: Filter, name = filter.name): Filter {
  const { id: _id, created: _created, modified: _modified, creator: _creator, ...rest } = filter;
  return { ...rest, name: name?.trim() };
}

function FilterEditor({ filterId }: { filterId: number | 'new' }) {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const options = useConfigOptions();
  const isNew = filterId === 'new';
  const [savingAs, setSavingAs] = useState(false);
  const [showProblems, setShowProblems] = useState(false);

  const filter = useDocumentDraft<Filter, string>({
    queryKey: ['filter', filterId],
    enabled: isNew || Number.isInteger(filterId),
    load: async (signal) => {
      if (isNew) return blankFilter();
      const { data, error, response } = await client.GET('/v2/filters/{filterId}', { params: { path: { filterId } }, signal });
      if (!data) throw toApiError(error, response, 'load the filter');
      return data;
    },
    store: async (doc) => {
      const { data, error, response } = isNew
        ? await client.POST('/v2/filters', { body: asNew(doc) })
        : await client.PUT('/v2/filters/{filterId}', { params: { path: { filterId } }, body: { ...doc, name: doc.name?.trim() } });
      if (!data) throw toApiError(error, response, 'save the filter');
      return data;
    },
    validate: useCallback(filterProblems, []),
    invalidate: [['filters']],
  });
  const { draft, saved, dirty, problems, update } = filter;
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

  if (!isNew && !Number.isInteger(filterId)) {
    return <Message severity="error" text="That isn't a filter ID" />;
  }
  if (filter.isLoading || (!draft && !filter.loadError)) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (filter.loadError || !draft || !saved) {
    return (
      <section>
        <Message severity="error" text={filter.loadError?.message ?? 'Filter not found'} />
        <p>
          <Link to="/filters">Back to filters</Link>
        </p>
      </section>
    );
  }
  if (draft.filterType && draft.filterType !== 'INTERNAL') {
    return (
      <section>
        <Message
          severity="info"
          text={`"${saved.name}" runs an external script. Only internal filters can be edited here.`}
        />
        <p>
          <a href={classicUrl('/filters/')}>Edit it in the classic Filters page</a> · <Link to="/filters">Back to filters</Link>
        </p>
      </section>
    );
  }

  const readOnly = isNew ? !hasRight(user, 'CREATE_FILTER') : !hasRightOrOwns(user, 'EDIT_FILTER', saved.creator);
  const canChangeOwner = !isNew && !readOnly && (!!user?.admin || user?.name === saved.creator);
  const filterOptions = options.data?.filterOptions;
  const fields = options.data?.filterActionFields;
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
      filter.save();
      return;
    }
    try {
      const created = await filter.saveAsync(draft);
      notify.success('Filter created', created.name);
      // the "new" draft starts blank next time
      queryClient.removeQueries({ queryKey: ['filter', 'new'] });
      leaveTo(`/filters/${created.id}`);
    } catch {
      // shown below the header
    }
  };
  const setConditions = (conditions: Condition[]) => update((d) => void (d.conditions = conditions));
  const setActions = (actions: Action[]) => update((d) => void (d.actions = actions));

  return (
    <section className="editor editor-wide">
      <p className="breadcrumb">
        <Link to="/filters">Filters</Link> / {isNew ? 'New filter' : saved.name}
      </p>
      <div className="page-header">
        <h1>
          {draft.name || (isNew ? 'New filter' : 'Unnamed filter')}
          {dirty && !isNew && <span className="unsaved"> (unsaved)</span>}
        </h1>
        <div className="editor-actions">
          {!readOnly && (
            <>
              {!isNew && <Button label="Revert" icon="pi pi-undo" text disabled={!dirty || filter.saving} onClick={filter.revert} />}
              {!isNew && hasRight(user, 'CREATE_FILTER') && (
                <Button label="Save as…" icon="pi pi-copy" outlined disabled={filter.saving} onClick={() => setSavingAs(true)} />
              )}
              <Button
                label={isNew ? 'Create' : 'Save'}
                icon="pi pi-save"
                disabled={!isNew && !dirty}
                loading={filter.saving}
                onClick={() => void save()}
              />
            </>
          )}
        </div>
      </div>

      {readOnly && (
        <Message
          severity="info"
          text={isNew ? "You don't have permission to create filters." : 'You can view this filter but not change it.'}
          className="editor-message"
        />
      )}
      {showProblems && problems.length > 0 && (
        <Message
          severity="error"
          className="editor-message"
          content={
            <div>
              <strong>Fix these before saving:</strong>
              <ul className="problem-list">
                {problems.map((p) => (
                  <li key={p}>{p}</li>
                ))}
              </ul>
            </div>
          }
        />
      )}
      {filter.saveError && <Message severity="error" text={filter.saveError.message} className="editor-message" />}

      <div className="form-columns editor-general">
        <Field label="Name" htmlFor="filter-name">
          <InputText
            id="filter-name"
            value={draft.name ?? ''}
            onChange={(e) => update((d) => void (d.name = e.target.value))}
            maxLength={MAX_NAME_LENGTH}
            disabled={readOnly}
            autoFocus={isNew}
          />
        </Field>
        <Field label="Product" htmlFor="filter-product">
          <Dropdown
            inputId="filter-product"
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
          <Field label="Owner" htmlFor="filter-owner">
            {canChangeOwner ? (
              <Dropdown
                inputId="filter-owner"
                value={draft.creator ?? null}
                options={[...new Set([...(users.data ?? []), saved.creator ?? ''])].filter(Boolean)}
                onChange={(e) => update((d) => void (d.creator = e.value as string))}
                filter
              />
            ) : (
              <InputText id="filter-owner" value={saved.creator ?? ''} disabled />
            )}
          </Field>
        )}
      </div>

      <div className="tab-toolbar filter-if">
        <h2 className="form-section">If</h2>
        <SelectButton
          value={draft.allConditionsMustPass ?? true}
          options={MATCH_ALL}
          onChange={(e) => e.value !== null && update((d) => void (d.allConditionsMustPass = e.value as boolean))}
          allowEmpty={false}
          disabled={readOnly}
          aria-label="Conditions that must match"
        />
        <span className="field-help">of these conditions match a request</span>
      </div>
      <ConditionsTable
        conditions={draft.conditions ?? []}
        onChange={setConditions}
        scopes={filterOptions?.['conditionScopes'] ?? []}
        matches={filterOptions?.['conditionMatches'] ?? []}
        readOnly={readOnly}
      />

      <h2 className="form-section">Then</h2>
      <ActionsTable
        actions={draft.actions ?? []}
        onChange={setActions}
        options={filterOptions}
        fields={fields}
        readOnly={readOnly}
      />

      {!isNew && (
        <p className="field-help">
          {[saved.created && `Created ${formatDateTime(saved.created)}`, saved.modified && `Last saved ${formatDateTime(saved.modified)}`]
            .filter(Boolean)
            .join(' · ')}
        </p>
      )}

      <ConflictDialog
        visible={filter.conflict}
        noun="filter"
        name={saved.name}
        onReload={() => void filter.reload().then(() => notify.success('Filter reloaded'))}
      />
      {savingAs && (
        <CopyDialog
          noun="filter"
          name={draft.name}
          copy={async (name) => {
            // a copy of the filter as edited, leaving this one as it was saved
            const { data, error, response } = await client.POST('/v2/filters', { body: asNew(draft, name) });
            if (!data) throw toApiError(error, response, 'save the copy');
            void queryClient.invalidateQueries({ queryKey: ['filters'] });
            leaveTo(`/filters/${data.id}`);
            return data;
          }}
          onHide={() => setSavingAs(false)}
          onCopied={() => undefined}
        />
      )}
    </section>
  );
}

type Option = { label?: string; value?: string };

function ConditionsTable({
  conditions,
  onChange,
  scopes,
  matches,
  readOnly,
}: {
  conditions: Condition[];
  onChange: (conditions: Condition[]) => void;
  scopes: Option[];
  matches: Option[];
  readOnly: boolean;
}) {
  const set = (i: number, change: Partial<Condition>) => onChange(conditions.map((c, j) => (j === i ? { ...c, ...change } : c)));
  return (
    <div>
      <table className="kv-table kv-table-wide rules-table">
        <thead>
          <tr>
            <th>Part of the request</th>
            <th>Test</th>
            <th>Value</th>
            {!readOnly && <th aria-label="Actions" />}
          </tr>
        </thead>
        <tbody>
          {conditions.map((c, i) => (
            <tr key={i}>
              <td>
                <Dropdown
                  value={sameIgnoringCase(scopes, c.scope)}
                  options={withCurrent(scopes, c.scope)}
                  onChange={(e) => set(i, { scope: e.value as string })}
                  disabled={readOnly}
                  aria-label={`Condition ${i + 1} part`}
                />
              </td>
              <td>
                <Dropdown
                  value={sameIgnoringCase(matches, c.condition)}
                  options={withCurrent(matches, c.condition)}
                  onChange={(e) => set(i, { condition: e.value as string })}
                  disabled={readOnly}
                  aria-label={`Condition ${i + 1} test`}
                />
              </td>
              <td>
                <InputText
                  value={c.value ?? ''}
                  onChange={(e) => set(i, { value: e.target.value })}
                  disabled={readOnly}
                  aria-label={`Condition ${i + 1} value`}
                  placeholder={c.condition === 'Matches' ? 'a regular expression' : undefined}
                />
              </td>
              {!readOnly && (
                <td>
                  <Button
                    type="button"
                    icon="pi pi-times"
                    rounded
                    text
                    severity="danger"
                    aria-label={`Remove condition ${i + 1}`}
                    onClick={() => onChange(conditions.filter((_, j) => j !== i))}
                  />
                </td>
              )}
            </tr>
          ))}
          {conditions.length === 0 && (
            <tr>
              <td colSpan={4} className="field-help">
                No conditions: the actions apply to every request.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && (
        <Button
          type="button"
          label="Add condition"
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => onChange([...conditions, newCondition()])}
        />
      )}
    </div>
  );
}

function ActionsTable({
  actions,
  onChange,
  options,
  fields,
  readOnly,
}: {
  actions: Action[];
  onChange: (actions: Action[]) => void;
  options: Record<string, Option[]> | undefined;
  fields: ActionField[] | undefined;
  readOnly: boolean;
}) {
  const replace = (i: number, action: Action) => onChange(actions.map((a, j) => (j === i ? action : a)));
  const operators = (options?.['validationTypes'] ?? []).map((o) => o.value!).filter(Boolean);
  return (
    <div>
      <table className="kv-table kv-table-wide rules-table">
        <thead>
          <tr>
            <th>Action</th>
            <th>On</th>
            <th>Key</th>
            <th>Value</th>
            {!readOnly && <th aria-label="Actions" />}
          </tr>
        </thead>
        <tbody>
          {actions.map((a, i) => {
            const field = fieldFor(fields, a);
            const { prefix, rest } = splitValue(a.value, field.prefix, operators);
            const setValue = (p: string, r: string) => replace(i, { ...a, value: field.prefix === 'NONE' ? r : p + r });
            return (
              <tr key={i}>
                <td>
                  <Dropdown
                    value={a.action}
                    options={ACTION_TYPES}
                    onChange={(e) => replace(i, changeAction(a, { action: e.value as Action['action'] }, options, fields))}
                    disabled={readOnly}
                    aria-label={`Action ${i + 1} type`}
                  />
                </td>
                <td>
                  <Dropdown
                    value={a.scope}
                    options={withCurrent(scopesFor(a.action, options), a.scope)}
                    optionLabel="label"
                    optionValue="value"
                    onChange={(e) => replace(i, changeAction(a, { scope: e.value as string }, options, fields))}
                    disabled={readOnly}
                    aria-label={`Action ${i + 1} part`}
                  />
                </td>
                <td>
                  {field.key && (
                    <InputText
                      value={a.key ?? ''}
                      onChange={(e) => replace(i, { ...a, key: e.target.value })}
                      disabled={readOnly}
                      aria-label={`Action ${i + 1} key`}
                    />
                  )}
                </td>
                <td>
                  {field.onFail ? (
                    <Dropdown
                      value={a.value || null}
                      options={options?.['onFailOptions'] ?? []}
                      onChange={(e) => replace(i, { ...a, value: e.value as string })}
                      placeholder="Choose what happens"
                      disabled={readOnly}
                      aria-label={`Action ${i + 1} on failure`}
                    />
                  ) : field.value ? (
                    <div className="action-value">
                      {field.prefix === 'ASSIGNMENT' && <span className="action-prefix">=</span>}
                      {field.prefix === 'VALIDATION' && (
                        <Dropdown
                          value={prefix}
                          className="action-check"
                          options={options?.['validationTypes'] ?? []}
                          onChange={(e) => setValue(e.value as string, rest)}
                          disabled={readOnly}
                          aria-label={`Action ${i + 1} check`}
                        />
                      )}
                      <InputText
                        value={rest}
                        onChange={(e) => setValue(prefix, e.target.value)}
                        disabled={readOnly}
                        aria-label={`Action ${i + 1} value`}
                      />
                    </div>
                  ) : null}
                </td>
                {!readOnly && (
                  <td>
                    <Button
                      type="button"
                      icon="pi pi-times"
                      rounded
                      text
                      severity="danger"
                      aria-label={`Remove action ${i + 1}`}
                      onClick={() => onChange(actions.filter((_, j) => j !== i))}
                    />
                  </td>
                )}
              </tr>
            );
          })}
          {actions.length === 0 && (
            <tr>
              <td colSpan={5} className="field-help">
                No actions yet: a filter changes nothing until it has one.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && (
        <Button
          type="button"
          label="Add action"
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => onChange([...actions, newAction()])}
        />
      )}
    </div>
  );
}

/**
 * The option a stored condition value means. The filter engine ignores case (ScriptFilterUtil), and
 * older filters hold "Post Data" for "Post data", so a row shows the option without being changed.
 */
function sameIgnoringCase(options: Option[], current: string | undefined): string | undefined {
  return options.find((o) => o.value?.toLowerCase() === current?.toLowerCase())?.value ?? current;
}

/** The options, plus a stored value they don't have in any case, so an old filter shows what it holds */
function withCurrent(options: Option[], current: string | undefined): Option[] {
  return current && !options.some((o) => o.value?.toLowerCase() === current.toLowerCase())
    ? [...options, { label: current, value: current }]
    : options;
}

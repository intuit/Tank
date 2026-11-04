import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useCallback, useState } from 'react';
import { Link, useParams } from 'react-router';
import type { Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { useUnsavedGuard } from '../../../components/editorGuards';
import { Field } from '../../../components/Field';
import { LazyCodeEditor } from '../../../components/LazyCodeEditor';
import { formatDateTime } from '../../../format';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { useDocumentDraft } from '../../../hooks/useDocumentDraft';
import { useNotify } from '../../../notify';
import { hasRight, hasRightOrOwns } from '../../../rights';
import { useSession } from '../../../session';
import type { ExternalScript } from './ExternalScriptsPage';

type TestResult = Schemas['ExternalScriptTestResult'];

/** ExternalScript.name's column length */
export const MAX_NAME_LENGTH = 255;

/** What a new external script starts with: how a filter hands it the script */
export const TEMPLATE = `// A filter runs this with the script being filtered as ioBean.getInput('script').
// Its step list can't be changed in place: copy it, change the copy and set it back.
var script = ioBean.getInput('script');
var steps = new (Java.type('java.util.ArrayList'))(script.getSteps());

ioBean.println(script.getName() + ' has ' + steps.size() + ' steps');

script.setSteps(steps);
`;

/** The controller runs external scripts with its JavaScript engine, chosen by the name's extension */
export const isJavaScript = (name: string | undefined) => !!name && name.trim().toLowerCase().endsWith('.js');

export function externalScriptProblems(x: ExternalScript): string[] {
  const name = x.name?.trim() ?? '';
  const problems: string[] = [];
  if (!name) problems.push('Name is required.');
  if (name.length > MAX_NAME_LENGTH) problems.push(`Name must be at most ${MAX_NAME_LENGTH} characters.`);
  return problems;
}

/** An external script's editor and tester, fresh for each external script */
export function ExternalScriptEditorPage() {
  const param = useParams().externalScriptId;
  const externalScriptId = param === 'new' ? 'new' : Number(param);
  return <ExternalScriptEditor key={String(externalScriptId)} externalScriptId={externalScriptId} />;
}

function ExternalScriptEditor({ externalScriptId }: { externalScriptId: number | 'new' }) {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const options = useConfigOptions();
  const isNew = externalScriptId === 'new';
  const [showProblems, setShowProblems] = useState(false);

  const doc = useDocumentDraft<ExternalScript, string>({
    queryKey: ['externalScript', externalScriptId],
    enabled: isNew || Number.isInteger(externalScriptId),
    load: async (signal) => {
      if (isNew) return { name: '', script: TEMPLATE };
      const { data, error, response } = await client.GET('/v2/scripts/external/{externalScriptId}', {
        params: { path: { externalScriptId } },
        signal,
      });
      if (!data) throw toApiError(error, response, 'load the external script');
      return data;
    },
    // the same call creates and, given an id, updates
    store: async (x) => {
      const body = isNew ? { name: x.name?.trim(), productName: x.productName, script: x.script } : { ...x, name: x.name?.trim() };
      const { data, error, response } = await client.POST('/v2/scripts/external', { body });
      if (!data) throw toApiError(error, response, 'save the external script');
      return data;
    },
    validate: useCallback(externalScriptProblems, []),
    invalidate: [['externalScripts']],
  });
  const { draft, saved, dirty, problems, update } = doc;
  const { leaveTo } = useUnsavedGuard(dirty, draft?.name);

  const remove = useMutation({
    mutationFn: async () => {
      const { error, response } = await client.DELETE('/v2/scripts/external/{externalScriptId}', {
        params: { path: { externalScriptId: externalScriptId as number } },
      });
      if (!response.ok) throw toApiError(error, response, 'delete the external script');
    },
    onSuccess: () => {
      notify.success('External script deleted', saved?.name);
      void queryClient.invalidateQueries({ queryKey: ['externalScripts'] });
      leaveTo('/filters/external');
    },
    onError: (error) => notify.error('The external script was not deleted', error.message),
  });

  if (!isNew && !Number.isInteger(externalScriptId)) {
    return <Message severity="error" text="That isn't an external script ID" />;
  }
  if (doc.isLoading || (!draft && !doc.loadError)) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (doc.loadError || !draft || !saved) {
    return (
      <section>
        <Message severity="error" text={doc.loadError?.message ?? 'External script not found'} />
        <p>
          <Link to="/filters/external">Back to external filters</Link>
        </p>
      </section>
    );
  }
  // the server's rules (ScriptServiceV2Impl.createExternalScript and deleteExternalScript)
  const readOnly = isNew ? !hasRight(user, 'CREATE_SCRIPT') : !hasRightOrOwns(user, 'EDIT_SCRIPT', saved.creator);
  const canDelete = !isNew && hasRightOrOwns(user, 'DELETE_SCRIPT', saved.creator);
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
      doc.save();
      return;
    }
    try {
      const created = await doc.saveAsync(draft);
      notify.success('External script created', created.name);
      // the "new" draft starts blank next time
      queryClient.removeQueries({ queryKey: ['externalScript', 'new'] });
      leaveTo(`/filters/external/${created.id}`);
    } catch {
      // shown below the header
    }
  };

  const confirmDelete = () =>
    confirmDialog({
      header: 'Delete external script',
      message: `Delete external script "${saved.name}"? This can't be undone. Filters that run it will fail until they are changed.`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(),
    });

  return (
    <section className="editor editor-wide">
      <p className="breadcrumb">
        <Link to="/filters">Filters</Link> / <Link to="/filters/external">External filters</Link> /{' '}
        {isNew ? 'New external script' : saved.name}
      </p>
      <div className="page-header">
        <h1>
          {draft.name || (isNew ? 'New external script' : 'Unnamed external script')}
          {dirty && !isNew && <span className="unsaved"> (unsaved)</span>}
        </h1>
        <div className="editor-actions">
          {canDelete && (
            <Button label="Delete" icon="pi pi-trash" severity="danger" text loading={remove.isPending} onClick={confirmDelete} />
          )}
          {!readOnly && (
            <>
              {!isNew && <Button label="Revert" icon="pi pi-undo" text disabled={!dirty || doc.saving} onClick={doc.revert} />}
              <Button
                label={isNew ? 'Create' : 'Save'}
                icon="pi pi-save"
                disabled={!isNew && !dirty}
                loading={doc.saving}
                onClick={() => void save()}
              />
            </>
          )}
        </div>
      </div>

      {readOnly && (
        <Message
          severity="info"
          text={isNew ? "You don't have permission to create scripts." : 'You can view this external script but not change it.'}
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
      {doc.saveError && <Message severity="error" text={doc.saveError.message} className="editor-message" />}

      <div className="form-columns editor-general">
        <Field
          label="Name"
          htmlFor="external-name"
          help={draft.name && !isJavaScript(draft.name) ? 'Only names ending in .js run on this Tank: it runs JavaScript.' : undefined}
        >
          <InputText
            id="external-name"
            value={draft.name ?? ''}
            onChange={(e) => update((d) => void (d.name = e.target.value))}
            maxLength={MAX_NAME_LENGTH}
            placeholder="my-filter.js"
            disabled={readOnly}
            autoFocus={isNew}
          />
        </Field>
        <Field label="Product" htmlFor="external-product">
          <Dropdown
            inputId="external-product"
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
          <Field label="Owner" htmlFor="external-owner">
            <InputText id="external-owner" value={saved.creator ?? ''} disabled />
          </Field>
        )}
      </div>

      <h2 className="form-section">Script</h2>
      <LazyCodeEditor
        value={draft.script ?? ''}
        onChange={(script) => update((d) => void (d.script = script))}
        readOnly={readOnly}
        label="External script"
        minHeight="20rem"
      />

      <TestPanel externalScriptId={isNew ? undefined : externalScriptId} draft={draft} canTest={!readOnly} />

      {!isNew && (
        <p className="field-help">
          {[saved.created && `Created ${formatDateTime(saved.created)}`, saved.modified && `Last saved ${formatDateTime(saved.modified)}`]
            .filter(Boolean)
            .join(' · ')}
        </p>
      )}
    </section>
  );
}

/** Runs the external script, as edited, against a script; nothing is saved */
function TestPanel({
  externalScriptId,
  draft,
  canTest,
}: {
  externalScriptId: number | undefined;
  draft: ExternalScript;
  canTest: boolean;
}) {
  const { client } = useSession();
  const [scriptId, setScriptId] = useState<number | null>(null);

  const scripts = useQuery({
    queryKey: ['scripts', 'names'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/scripts/names', { signal });
      if (!data) throw toApiError(error, response, 'load the scripts');
      return Object.entries(data)
        .map(([id, name]) => ({ label: name, value: Number(id) }))
        .sort((a, b) => a.label.localeCompare(b.label, undefined, { sensitivity: 'base' }));
    },
  });

  const test = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.POST('/v2/scripts/external/test', {
        body: { externalScriptId, scriptId: scriptId!, name: draft.name?.trim(), script: draft.script },
      });
      if (!data) {
        throw response.status === 429
          ? new Error('Too many tests are running on the controller; try again in a moment')
          : toApiError(error, response, 'run the test');
      }
      return data;
    },
  });

  const blocked = !canTest
    ? "You don't have permission to test this external script"
    : !draft.script?.trim()
      ? 'Write the script first'
      : !isJavaScript(draft.name)
        ? 'Give it a name ending in .js to test it'
        : scriptId == null
          ? 'Choose a script to test against'
          : undefined;

  return (
    <>
      <h2 className="form-section">Test it</h2>
      <p className="field-help">
        Runs the script as edited against a copy of a script's steps, and shows what it printed and which steps it
        changed. Nothing is saved. Tests run for at most 1 minute and can use only the script classes
        (com.intuit.tank.script.models) and Java collections.
      </p>
      <div className="tab-toolbar">
        <Dropdown
          inputId="external-test-script"
          value={scriptId}
          options={scripts.data ?? []}
          onChange={(e) => setScriptId((e.value as number | null) ?? null)}
          placeholder={scripts.isPending ? 'Loading…' : 'Choose a script'}
          emptyMessage="There are no scripts"
          filter
          aria-label="Script to test against"
          className="list-filter"
        />
        <span title={blocked}>
          <Button
            type="button"
            label="Run test"
            icon="pi pi-play"
            outlined
            loading={test.isPending}
            disabled={!!blocked}
            onClick={() => test.mutate()}
          />
        </span>
      </div>
      {scripts.error && <Message severity="error" text={scripts.error.message} className="editor-message" />}
      {test.error && <Message severity="error" text={test.error.message} className="editor-message" />}
      {test.data && <TestOutput result={test.data} />}
    </>
  );
}

function TestOutput({ result }: { result: TestResult }) {
  const changes = result.changes ?? [];
  const total = (result.added ?? 0) + (result.removed ?? 0) + (result.changed ?? 0);
  return (
    <div className="logic-output" role="status">
      {result.timedOut ? (
        <Message
          severity="warn"
          text="The script didn't finish within the time limit, so its changes to the steps aren't shown"
          className="editor-message"
        />
      ) : (
        <p>
          {total === 0
            ? `The script didn't change any of the ${result.stepsBefore ?? 0} steps.`
            : `${result.stepsBefore ?? 0} steps before, ${result.stepsAfter ?? 0} after: ` +
              `${result.added ?? 0} added, ${result.removed ?? 0} removed, ${result.changed ?? 0} changed.`}{' '}
          <span className="field-help">Finished in {result.durationMs ?? 0} ms</span>
        </p>
      )}
      {changes.length > 0 && (
        <>
          <table className="kv-table kv-table-wide" aria-label="Step changes">
            <thead>
              <tr>
                <th>Change</th>
                <th>Step</th>
                <th>Type</th>
                <th>Label</th>
              </tr>
            </thead>
            <tbody>
              {changes.map((c, i) => (
                <tr key={i}>
                  <td>{c.kind}</td>
                  <td>{(c.position ?? 0) + 1}</td>
                  <td>{c.type}</td>
                  <td className="ellipsis">{c.label}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {changes.length < total && <p className="field-help">Showing the first {changes.length} of {total} changes.</p>}
        </>
      )}
      <h3 className="form-section">Output</h3>
      <pre className="file-preview response-body">{result.output || '(no output)'}</pre>
    </div>
  );
}

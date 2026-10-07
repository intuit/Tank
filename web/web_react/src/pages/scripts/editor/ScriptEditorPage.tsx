import { useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useState } from 'react';
import { Link, useParams } from 'react-router';
import { contextPath } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { CopyDialog } from '../../../components/CopyDialog';
import { ConflictDialog, useUnsavedGuard } from '../../../components/editorGuards';
import { formatDateTime } from '../../../format';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { useNotify } from '../../../notify';
import { hasRight } from '../../../rights';
import { useSession } from '../../../session';
import { Field } from '../../../components/Field';
import { StepTable } from './StepTable';
import type { ScriptStep } from './steps';
import { useScriptDraft } from './useScriptDraft';

/** The script editor (ScriptEditor and script-edit-view.xhtml), fresh for each script */
export function ScriptEditorPage() {
  const scriptId = Number(useParams().scriptId);
  // opening another script (Save as, a link) must not carry over this one's selection or dialogs
  return <ScriptEditor key={scriptId} scriptId={scriptId} />;
}

function ScriptEditor({ scriptId }: { scriptId: number }) {
  const { client, user } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const script = useScriptDraft(scriptId);
  const options = useConfigOptions();
  const [selection, setSelection] = useState<ScriptStep[]>([]);
  const [savingAs, setSavingAs] = useState(false);
  const [showProblems, setShowProblems] = useState(false);

  const { draft, saved, dirty, problems } = script;
  const readOnly = !saved?.permissions?.edit;
  const { leaveTo } = useUnsavedGuard(dirty, draft?.name);

  if (!Number.isInteger(scriptId)) {
    return <Message severity="error" text="That isn't a script ID" />;
  }
  if (script.isLoading || (!draft && !script.loadError)) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (script.loadError || !draft || !saved) {
    return (
      <section>
        <Message severity="error" text={script.loadError?.message ?? 'Script not found'} />
        <p>
          <Link to="/scripts">Back to scripts</Link>
        </p>
      </section>
    );
  }

  const save = () => {
    if (problems.length) {
      setShowProblems(true);
      return;
    }
    setShowProblems(false);
    script.save();
  };
  const products = options.data?.products ?? [];
  const productOptions =
    draft.productName && !products.some((p) => p.value === draft.productName)
      ? [...products, { label: draft.productName, value: draft.productName }]
      : products;

  return (
    <section className="editor editor-wide">
      <p className="breadcrumb">
        <Link to="/scripts">Scripts</Link> / {saved.name}
      </p>
      <div className="page-header">
        <h1>
          {draft.name || 'Unnamed script'}
          {dirty && <span className="unsaved"> (unsaved)</span>}
        </h1>
        <div className="editor-actions">
          <a className="p-button p-button-text plain-link" href={`${contextPath()}/v2/scripts/download/${scriptId}`}>
            <i className="pi pi-download" aria-hidden />
            &nbsp;Tank XML
          </a>
          <a className="p-button p-button-text plain-link" href={`${contextPath()}/v2/scripts/harness/download/${scriptId}`}>
            <i className="pi pi-download" aria-hidden />
            &nbsp;Harness XML
          </a>
          {!readOnly && (
            <>
              <Button label="Revert" icon="pi pi-undo" text disabled={!dirty || script.saving} onClick={script.revert} />
              {hasRight(user, 'CREATE_SCRIPT') && (
                <Button
                  label="Save as…"
                  icon="pi pi-copy"
                  outlined
                  // the copy's steps get new uuids, so unsaved edits couldn't keep recorded responses or passwords
                  disabled={dirty || script.saving}
                  tooltip={dirty ? 'Save or revert your changes first' : undefined}
                  tooltipOptions={{ showOnDisabled: true, position: 'bottom' }}
                  onClick={() => setSavingAs(true)}
                />
              )}
              <Button label="Save" icon="pi pi-save" disabled={!dirty} loading={script.saving} onClick={save} />
            </>
          )}
        </div>
      </div>

      {readOnly && <Message severity="info" text="You can view this script but not change it." className="editor-message" />}
      {showProblems && problems.length > 0 && (
        <Message
          severity="error"
          className="editor-message"
          content={
            <div>
              <strong>Fix these before saving:</strong>
              <ul className="problem-list">
                {problems.map((p) => (
                  <li key={p.message}>{p.message}</li>
                ))}
              </ul>
            </div>
          }
        />
      )}
      {script.saveError && <Message severity="error" text={script.saveError.message} className="editor-message" />}

      <div className="form-columns editor-general">
        <Field label="Name" htmlFor="script-name">
          <InputText
            id="script-name"
            value={draft.name ?? ''}
            onChange={(e) => script.update((d) => void (d.name = e.target.value))}
            maxLength={255}
            disabled={readOnly}
          />
        </Field>
        <Field label="Product" htmlFor="script-product">
          <Dropdown
            inputId="script-product"
            value={draft.productName ?? null}
            options={productOptions}
            optionLabel="label"
            optionValue="value"
            onChange={(e) => script.update((d) => void (d.productName = (e.value as string | null) ?? undefined))}
            placeholder="None"
            showClear={!readOnly}
            disabled={readOnly}
          />
        </Field>
        <Field label="Comments" htmlFor="script-comments">
          <InputTextarea
            id="script-comments"
            value={draft.comments ?? ''}
            onChange={(e) => script.update((d) => void (d.comments = e.target.value))}
            rows={2}
            autoResize
            maxLength={1024}
            disabled={readOnly}
          />
        </Field>
      </div>

      <StepTable
        scriptId={scriptId}
        steps={draft.steps ?? []}
        update={script.update}
        readOnly={readOnly}
        selection={selection}
        onSelectionChange={setSelection}
      />

      <p className="field-help">
        {[
          `Owned by ${saved.owner}`,
          saved.created && `Created ${formatDateTime(saved.created)}`,
          saved.modified && `Last saved ${formatDateTime(saved.modified)}`,
        ]
          .filter(Boolean)
          .join(' · ')}
      </p>

      <ConflictDialog
        visible={script.conflict}
        noun="script"
        name={saved.name}
        onReload={() =>
          void script.reload().then(() => {
            setSelection([]);
            notify.success('Script reloaded');
          })
        }
      />
      {savingAs && (
        <CopyDialog
          noun="script"
          name={saved.name}
          copy={async (name) => {
            const { data, error, response } = await client.POST('/v2/scripts/{scriptId}/copy', {
              params: { path: { scriptId } },
              body: { name },
            });
            if (!data) {
              throw toApiError(error, response, 'save the copy');
            }
            void queryClient.invalidateQueries({ queryKey: ['scripts'] });
            leaveTo(`/scripts/${data.id}`);
            return data;
          }}
          onHide={() => setSavingAs(false)}
          onCopied={() => undefined}
        />
      )}
    </section>
  );
}

import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { SelectButton } from 'primereact/selectbutton';
import { TabPanel, TabView } from 'primereact/tabview';
import { useRef, useState, type FormEvent } from 'react';
import { toApiError } from '../../../api/errors';
import { Field } from '../../../components/Field';
import { KeyValueTable } from '../../../components/KeyValueTable';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { useSession } from '../../../session';
import { AssignmentsEditor, ResponseFormat, rulesProblem, ValidationsEditor } from './ResponseRulesEditor';
import { encodeOnFail, encodeRules, parseOnFail, parseRules, type Assignment, type Validation } from './responseRules';
import { ENTRY_TYPES, METHODS, newRequest, PROTOCOLS, withLabel, type ScriptStep } from './steps';
import { focusOnShow } from '../../../components/focusOnShow';

/** ScriptRequestEditor.getRequestFormats, if the server doesn't send them */
const DEFAULT_FORMATS = [
  { label: 'Key-Value', value: 'nvp' },
  { label: 'XML', value: 'xml' },
  { label: 'JSON', value: 'json' },
  { label: 'Plain Text', value: 'plain' },
  { label: 'Multi-Part', value: 'multipart' },
];

/**
 * Adds or edits a request (ScriptRequestEditor and request-editor.xhtml): its address and method,
 * headers, query string and post data, validations, assignments and what happens on failure, with
 * the recorded cookies and response to look at.
 */
export function RequestDialog({
  scriptId,
  groups,
  step,
  readOnly,
  onHide,
  onSave,
}: {
  scriptId: number;
  /** The script's group names, for "go to group" */
  groups: string[];
  /** The request to edit; a new one is added when absent */
  step?: ScriptStep;
  readOnly: boolean;
  onHide: () => void;
  onSave: (step: ScriptStep) => void;
}) {
  const [draft, setDraft] = useState<ScriptStep>(() => structuredClone(step ?? newRequest()));
  const options = useConfigOptions();
  const first = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<string>();
  const set = (change: Partial<ScriptStep>) => {
    setError(undefined);
    setDraft((d) => ({ ...d, ...change }));
  };
  const formats = options.data?.stepOptions?.['requestFormats'] ?? DEFAULT_FORMATS;
  const failureTypes = options.data?.stepOptions?.['failureTypes'] ?? [{ label: 'Abort script, goto next script', value: 'abort' }];
  const [rules, setRules] = useState(() => parseRules(draft.responseData));
  const [onFail, setOnFail] = useState(() => parseOnFail(draft.onFail));
  const setValidations = (validations: Validation[]) => {
    setError(undefined);
    setRules((r) => ({ ...r, validations }));
  };
  const setAssignments = (assignments: Assignment[]) => {
    setError(undefined);
    setRules((r) => ({ ...r, assignments }));
  };
  const format = draft.reqFormat ?? 'nvp';

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (!draft.hostname?.trim()) {
      setError('Host is required');
      return;
    }
    const problem =
      rulesProblem(rules.validations, rules.assignments) ??
      (onFail.action === 'goto' && !onFail.group.trim() ? 'Choose the group to go to on failure' : undefined);
    if (problem) {
      setError(problem);
      return;
    }
    onSave(
      withLabel({
        ...draft,
        hostname: draft.hostname.trim(),
        simplePath: draft.simplePath?.trim() || '/',
        // blank rows added and left empty aren't kept
        requestheaders: (draft.requestheaders ?? []).filter((e) => e.key?.trim()),
        queryStrings: (draft.queryStrings ?? []).filter((e) => e.key?.trim()),
        postDatas: (draft.postDatas ?? []).filter((e) => e.key?.trim()),
        responseData: encodeRules(rules.validations, rules.assignments),
        onFail: encodeOnFail(onFail.action, onFail.group),
      }),
    );
  };

  return (
    <Dialog
      header={step ? `${readOnly ? '' : 'Edit '}request ${step.name ?? ''}`.trim().replace(/^./, (c) => c.toUpperCase()) : 'Add request'}
      visible
      onHide={onHide}
      className="request-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(first)}
    >
      <form onSubmit={submit}>
        <div className="form-columns request-general">
          <Field label="Name" htmlFor="req-name">
            <InputText id="req-name" ref={first} value={draft.name ?? ''} onChange={(e) => set({ name: e.target.value })} disabled={readOnly} />
          </Field>
          <Field label="Group" htmlFor="req-group" help="Steps with the same group name form a group the on-failure actions can skip or jump to">
            <InputText id="req-group" value={draft.scriptGroupName ?? ''} onChange={(e) => set({ scriptGroupName: e.target.value })} disabled={readOnly} />
          </Field>
          <Field label="Method" htmlFor="req-method">
            <Dropdown inputId="req-method" value={draft.method} options={METHODS} onChange={(e) => set({ method: e.value as string })} disabled={readOnly} />
          </Field>
          <Field label="Protocol" htmlFor="req-protocol">
            <Dropdown inputId="req-protocol" value={draft.protocol} options={PROTOCOLS} onChange={(e) => set({ protocol: e.value as string })} disabled={readOnly} />
          </Field>
          <Field label="Host" htmlFor="req-host" help="May use variables, e.g. @{host}">
            <InputText id="req-host" value={draft.hostname ?? ''} onChange={(e) => set({ hostname: e.target.value })} disabled={readOnly} />
          </Field>
          <Field label="Path" htmlFor="req-path">
            <InputText id="req-path" value={draft.simplePath ?? ''} onChange={(e) => set({ simplePath: e.target.value })} placeholder="/" disabled={readOnly} />
          </Field>
          <Field label="On failure" htmlFor="req-onfail" help="What a user does when a validation fails">
            <Dropdown
              inputId="req-onfail"
              value={onFail.action}
              options={failureTypes}
              optionLabel="label"
              optionValue="value"
              onChange={(e) => setOnFail((f) => ({ ...f, action: e.value as string }))}
              disabled={readOnly}
            />
          </Field>
          {onFail.action === 'goto' && (
            <Field label="Go to group" htmlFor="req-goto">
              <Dropdown
                inputId="req-goto"
                value={onFail.group || null}
                options={[...new Set([...groups, ...(onFail.group ? [onFail.group] : [])])]}
                onChange={(e) => setOnFail((f) => ({ ...f, group: (e.value as string) ?? '' }))}
                editable
                placeholder="Group name"
                disabled={readOnly}
              />
            </Field>
          )}
          <Field label="Logging key" htmlFor="req-logging" help="Groups this request's timings in the results">
            <InputText id="req-logging" value={draft.loggingKey ?? ''} onChange={(e) => set({ loggingKey: e.target.value })} disabled={readOnly} />
          </Field>
        </div>

        <TabView className="request-tabs">
          <TabPanel header={`Headers (${draft.requestheaders?.length ?? 0})`}>
            <KeyValueTable
              entries={draft.requestheaders ?? []}
              onChange={(requestheaders) => set({ requestheaders })}
              newType={ENTRY_TYPES.header}
              keyLabel="Header"
              valueLabel="Value"
              addLabel="Add header"
              readOnly={readOnly}
              emptyMessage="No request headers."
            />
          </TabPanel>
          <TabPanel header={`Query string (${draft.queryStrings?.length ?? 0})`}>
            <KeyValueTable
              entries={draft.queryStrings ?? []}
              onChange={(queryStrings) => set({ queryStrings })}
              newType={ENTRY_TYPES.queryString}
              keyLabel="Parameter"
              valueLabel="Value"
              addLabel="Add parameter"
              readOnly={readOnly}
              emptyMessage="No query string parameters."
            />
          </TabPanel>
          <TabPanel header="Post data">
            <div className="tab-toolbar">
              <SelectButton
                value={format}
                options={formats}
                optionLabel="label"
                optionValue="value"
                onChange={(e) => e.value && set({ reqFormat: e.value as string })}
                allowEmpty={false}
                disabled={readOnly}
                aria-label="Post data format"
              />
              {format === 'json' && !readOnly && (
                <Button
                  type="button"
                  label="Format JSON"
                  icon="pi pi-align-left"
                  text
                  size="small"
                  onClick={() => {
                    try {
                      set({ payload: JSON.stringify(JSON.parse(draft.payload ?? ''), null, 2) });
                    } catch {
                      setError("The body isn't valid JSON (variables such as @{id} may need quotes)");
                    }
                  }}
                />
              )}
            </div>
            {format === 'nvp' ? (
              <KeyValueTable
                entries={draft.postDatas ?? []}
                onChange={(postDatas) => set({ postDatas })}
                newType={ENTRY_TYPES.postData}
                keyLabel="Parameter"
                valueLabel="Value"
                addLabel="Add parameter"
                readOnly={readOnly}
                emptyMessage="No post data parameters."
              />
            ) : (
              <>
                <InputTextarea
                  value={draft.payload ?? ''}
                  onChange={(e) => set({ payload: e.target.value })}
                  rows={14}
                  className="payload"
                  aria-label="Request body"
                  // a recorded multipart body can't be edited safely as text (as in the JSF editor)
                  readOnly={readOnly || format === 'multipart'}
                  spellCheck={false}
                />
                {format === 'multipart' && <small className="field-help">A multipart body is shown as recorded and can't be edited here.</small>}
              </>
            )}
          </TabPanel>
          <TabPanel header={`Validations (${rules.validations.length})`}>
            <ResponseFormat value={draft.respFormat} onChange={(respFormat) => set({ respFormat })} readOnly={readOnly} />
            <ValidationsEditor rules={rules.validations} onChange={setValidations} readOnly={readOnly} />
          </TabPanel>
          <TabPanel header={`Assignments (${rules.assignments.length})`}>
            <ResponseFormat value={draft.respFormat} onChange={(respFormat) => set({ respFormat })} readOnly={readOnly} />
            <AssignmentsEditor rules={rules.assignments} onChange={setAssignments} readOnly={readOnly} />
          </TabPanel>
          <TabPanel header="Recorded">
            <RecordedResponse scriptId={scriptId} step={draft} isNew={!step} />
          </TabPanel>
        </TabView>

        {error && <Message severity="error" text={error} className="editor-message" />}
        <div className="form-actions">
          <Button type="button" label={readOnly ? 'Close' : 'Cancel'} text onClick={onHide} />
          {!readOnly && <Button type="submit" label={step ? 'Done' : 'Add'} />}
        </div>
      </form>
    </Dialog>
  );
}

/**
 * What the recording captured: the cookies sent, the response headers and cookies, and (loaded on
 * request, since steps arrive without it) the response body (the JSF Raw Response viewer).
 */
function RecordedResponse({ scriptId, step, isNew }: { scriptId: number; step: ScriptStep; isNew: boolean }) {
  const { client } = useSession();
  const [show, setShow] = useState(false);
  const response = useQuery({
    queryKey: ['script', scriptId, 'response', step.uuid],
    enabled: show,
    retry: false,
    queryFn: async ({ signal }) => {
      const { data, error, response: res } = await client.GET('/v2/scripts/{scriptId}/steps/{stepUuid}/response', {
        params: { path: { scriptId, stepUuid: step.uuid! } },
        parseAs: 'text',
        signal,
      });
      if (res.status === 404) {
        // no recorded response; null because a query can't return undefined
        return null;
      }
      if (data === undefined) {
        throw toApiError(error, res, 'load the recorded response');
      }
      return data;
    },
  });
  const body = response.data;
  const pretty = body && /json/i.test(step.mimetype ?? '') ? tryFormatJson(body) : body;

  return (
    <div className="recorded">
      <h3 className="form-section">Cookies sent</h3>
      <KeyValueTable entries={step.requestCookies ?? []} onChange={() => undefined} keyLabel="Cookie" valueLabel="Value" readOnly emptyMessage="None recorded." />
      <h3 className="form-section">Response headers</h3>
      <KeyValueTable entries={step.responseheaders ?? []} onChange={() => undefined} keyLabel="Header" valueLabel="Value" readOnly emptyMessage="None recorded." />
      <h3 className="form-section">Response cookies</h3>
      <KeyValueTable entries={step.responseCookies ?? []} onChange={() => undefined} keyLabel="Cookie" valueLabel="Value" readOnly emptyMessage="None recorded." />
      <h3 className="form-section">Response body</h3>
      {isNew ? (
        <p className="field-help">A request added here has no recording.</p>
      ) : !show ? (
        <Button type="button" label="Show recorded response" icon="pi pi-eye" outlined size="small" onClick={() => setShow(true)} />
      ) : response.isPending ? (
        <p className="field-help">Loading…</p>
      ) : response.error ? (
        <Message severity="error" text={response.error.message} />
      ) : body === null || body === undefined ? (
        <p className="field-help">This step has no recorded response.</p>
      ) : (
        <pre className="file-preview response-body">{pretty}</pre>
      )}
    </div>
  );
}

function tryFormatJson(text: string): string {
  try {
    return JSON.stringify(JSON.parse(text), null, 2);
  } catch {
    return text;
  }
}

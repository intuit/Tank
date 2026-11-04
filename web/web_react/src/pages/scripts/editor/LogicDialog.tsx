import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { TabPanel, TabView } from 'primereact/tabview';
import { useRef, useState, type FormEvent } from 'react';
import type { Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { Field } from '../../../components/Field';
import { focusOnShow } from '../../../components/focusOnShow';
import { KeyValueTable } from '../../../components/KeyValueTable';
import { LazyCodeEditor } from '../../../components/LazyCodeEditor';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { useSession } from '../../../session';
import { logicScript, logicStep, readTestData, testDataFrom, type LogicTestData, type ScriptStep } from './steps';

type Entry = Schemas['StepDataTO'];
const toEntries = (map: Record<string, string>): Entry[] => Object.entries(map).map(([key, value]) => ({ key, value }));
type Lists = Omit<LogicTestData, 'variables' | 'requestHeaders' | 'responseHeaders'> & {
  variables: Entry[];
  requestHeaders: Entry[];
  responseHeaders: Entry[];
};
const asLists = (d: LogicTestData): Lists => ({
  ...d,
  variables: toEntries(d.variables),
  requestHeaders: toEntries(d.requestHeaders),
  responseHeaders: toEntries(d.responseHeaders),
});
const toMap = (entries: Entry[]): Record<string, string> =>
  Object.fromEntries(entries.filter((e) => e.key?.trim()).map((e) => [e.key!, e.value ?? '']));

/**
 * Adds or edits a logic step (LogicStepEditor and logic-editor.xhtml): JavaScript that runs between
 * requests, with test data to try it against on the server.
 */
export function LogicDialog({
  scriptId,
  step,
  steps,
  position,
  readOnly,
  onHide,
  onSave,
}: {
  scriptId: number;
  step?: ScriptStep;
  /** The script's steps, for its variables and the request before the logic step */
  steps: ScriptStep[];
  /** Where the step is or will be, so "previous request" means the one before it */
  position: number;
  readOnly: boolean;
  onHide: () => void;
  onSave: (step: ScriptStep) => void;
}) {
  const { client } = useSession();
  const options = useConfigOptions();
  const first = useRef<HTMLInputElement>(null);
  const [name, setName] = useState(step?.name ?? '');
  const [group, setGroup] = useState(step?.scriptGroupName ?? '');
  const [script, setScript] = useState(() => (step ? logicScript(step) : ''));
  const [showHelp, setShowHelp] = useState(false);
  const [error, setError] = useState<string>();
  // a saved step keeps its own test data; a new one starts from the request before it
  const previous = () => steps.slice(0, position).reverse().find((s) => s.type === 'request');
  // rows stay as lists while edited, so a new row with no name yet doesn't vanish
  const [testData, setTestData] = useState(() => asLists(step ? readTestData(step) : testDataFrom(previous(), steps)));
  const setData = (change: Partial<Lists>) => setTestData((d) => ({ ...d, ...change }));
  const asData = (): LogicTestData => ({
    ...testData,
    variables: toMap(testData.variables),
    requestHeaders: toMap(testData.requestHeaders),
    responseHeaders: toMap(testData.responseHeaders),
  });

  const test = useMutation({
    mutationFn: async () => {
      const { data, error: failed, response } = await client.POST('/v2/scripts/logic/test', {
        body: { scriptId, script, ...asData() },
      });
      if (!data) {
        throw response.status === 429
          ? new Error('Too many tests are running on the controller; try again in a moment')
          : toApiError(failed, response, 'run the test');
      }
      return data;
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (!name.trim()) {
      setError('Name is required');
      return;
    }
    onSave(logicStep(step, { name: name.trim(), group: group.trim(), script, testData: asData() }));
  };

  return (
    <Dialog
      header={step ? `${readOnly ? 'Logic step' : 'Edit logic step'} ${step.name ?? ''}`.trim() : 'Add logic step'}
      visible
      onHide={onHide}
      className="request-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(first)}
    >
      <form onSubmit={submit}>
        <div className="form-columns request-general">
          <Field label="Name" htmlFor="logic-name">
            <InputText
              id="logic-name"
              ref={first}
              value={name}
              onChange={(e) => {
                setError(undefined);
                setName(e.target.value);
              }}
              disabled={readOnly}
            />
          </Field>
          <Field label="Group" htmlFor="logic-group">
            <InputText id="logic-group" value={group} onChange={(e) => setGroup(e.target.value)} disabled={readOnly} />
          </Field>
        </div>

        <div className="tab-toolbar">
          <h3 className="form-section logic-heading">Script</h3>
          <Button
            type="button"
            label={showHelp ? 'Hide help' : 'What can a script use?'}
            icon="pi pi-question-circle"
            text
            size="small"
            onClick={() => setShowHelp(!showHelp)}
          />
        </div>
        {showHelp && (
          <div className="logic-help">
            <p className="field-help">
              Tank runs this before your script, so a script can call these. Print with <code>logger.info(...)</code> to
              see values in a test.
            </p>
            <LazyCodeEditor value={options.data?.logicStep?.insertBefore ?? ''} readOnly label="Code that runs before the script" minHeight="8rem" />
          </div>
        )}
        <LazyCodeEditor value={script} onChange={setScript} readOnly={readOnly} label="Logic step script" minHeight="14rem" />

        <h3 className="form-section">Test it</h3>
        <TabView className="request-tabs">
          <TabPanel header="Variables">
            <KeyValueTable
              entries={testData.variables}
              onChange={(variables) => setData({ variables })}
              keyLabel="Variable"
              valueLabel="Value"
              addLabel="Add variable"
              readOnly={readOnly}
              emptyMessage="No test variables."
            />
          </TabPanel>
          <TabPanel header="Request">
            <h4 className="form-section">Headers</h4>
            <KeyValueTable
              entries={testData.requestHeaders}
              onChange={(requestHeaders) => setData({ requestHeaders })}
              keyLabel="Header"
              valueLabel="Value"
              addLabel="Add header"
              readOnly={readOnly}
              emptyMessage="No request headers."
            />
            <h4 className="form-section">Body</h4>
            <InputTextarea
              value={testData.requestBody}
              onChange={(e) => setData({ requestBody: e.target.value })}
              rows={5}
              className="payload"
              aria-label="Test request body"
              disabled={readOnly}
            />
          </TabPanel>
          <TabPanel header="Response">
            <h4 className="form-section">Headers</h4>
            <KeyValueTable
              entries={testData.responseHeaders}
              onChange={(responseHeaders) => setData({ responseHeaders })}
              keyLabel="Header"
              valueLabel="Value"
              addLabel="Add header"
              readOnly={readOnly}
              emptyMessage="No response headers."
            />
            <h4 className="form-section">Body</h4>
            <InputTextarea
              value={testData.responseBody}
              onChange={(e) => setData({ responseBody: e.target.value })}
              rows={5}
              className="payload"
              aria-label="Test response body"
              disabled={readOnly}
            />
            <small className="field-help">Used for tests only; it isn't saved with the step.</small>
          </TabPanel>
        </TabView>
        <div className="tab-toolbar">
          <Button type="button" label="Run test" icon="pi pi-play" outlined loading={test.isPending} disabled={!script.trim()} onClick={() => test.mutate()} />
          {!readOnly && (
            <Button
              type="button"
              label="Use the previous request"
              icon="pi pi-replay"
              text
              size="small"
              disabled={!previous()}
              tooltip={previous() ? 'Fill the test data from the request before this step' : 'There is no request before this step'}
              tooltipOptions={{ showOnDisabled: true, position: 'top' }}
              onClick={() => setTestData(asLists(testDataFrom(previous(), steps)))}
            />
          )}
        </div>
        {test.error && <Message severity="error" text={test.error.message} className="editor-message" />}
        {test.data && (
          <div className="logic-output" role="status">
            {test.data.timedOut ? (
              <Message severity="warn" text="The script didn't finish within the time limit" className="editor-message" />
            ) : (
              <span className="field-help">Finished in {test.data.durationMs ?? 0} ms</span>
            )}
            <pre className="file-preview response-body">{test.data.output || '(no output)'}</pre>
          </div>
        )}

        {error && <Message severity="error" text={error} className="editor-message" />}
        <div className="form-actions">
          <Button type="button" label={readOnly ? 'Close' : 'Cancel'} text onClick={onHide} />
          {!readOnly && <Button type="submit" label={step ? 'Done' : 'Add'} />}
        </div>
      </form>
    </Dialog>
  );
}

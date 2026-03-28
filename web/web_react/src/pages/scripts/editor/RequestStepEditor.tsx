import { TabView, TabPanel } from 'primereact/tabview';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { InputTextarea } from 'primereact/inputtextarea';
import { StepDataTable } from './StepDataTable';
import type { ScriptStep } from '../../../types/scriptEditor';

const HTTP_METHODS = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD'];
const PROTOCOLS   = ['https', 'http'];
const ON_FAIL_OPTS = ['continue', 'abort', 'skip', 'terminate'];
const REQ_FORMATS  = ['nvp', 'json', 'xml', 'plain', 'multipart'];
const RESP_FORMATS = ['json', 'xml', 'raw'];

interface Props {
  step: ScriptStep;
  onChange: (updated: ScriptStep) => void;
}

function field(step: ScriptStep, key: keyof ScriptStep, val: unknown): ScriptStep {
  return { ...step, [key]: val };
}

export function RequestStepEditor({ step, onChange }: Props) {
  const set = (key: keyof ScriptStep, val: unknown) => onChange(field(step, key, val));

  return (
    <div className="flex flex-column gap-3">
      {/* URL row */}
      <div className="flex gap-2 align-items-end flex-wrap">
        <div className="flex flex-column gap-1">
          <label className="text-sm font-semibold">Method</label>
          <Dropdown
            value={step.method ?? 'GET'}
            options={HTTP_METHODS}
            onChange={e => set('method', e.value)}
            style={{ width: '110px' }}
          />
        </div>
        <div className="flex flex-column gap-1">
          <label className="text-sm font-semibold">Protocol</label>
          <Dropdown
            value={step.protocol ?? 'https'}
            options={PROTOCOLS}
            onChange={e => set('protocol', e.value)}
            style={{ width: '100px' }}
          />
        </div>
        <div className="flex flex-column gap-1 flex-1" style={{ minWidth: '180px' }}>
          <label className="text-sm font-semibold">Host</label>
          <InputText
            value={step.hostname ?? ''}
            onChange={e => set('hostname', e.target.value)}
            placeholder="example.com"
            className="p-inputtext-sm"
          />
        </div>
        <div className="flex flex-column gap-1 flex-1" style={{ minWidth: '240px' }}>
          <label className="text-sm font-semibold">Path</label>
          <InputText
            value={step.simplePath ?? ''}
            onChange={e => set('simplePath', e.target.value)}
            placeholder="/api/endpoint"
            className="p-inputtext-sm"
          />
        </div>
      </div>

      {/* Metadata row */}
      <div className="flex gap-2 align-items-end flex-wrap">
        <div className="flex flex-column gap-1 flex-1">
          <label className="text-sm font-semibold">Name</label>
          <InputText
            value={step.name ?? ''}
            onChange={e => set('name', e.target.value)}
            className="p-inputtext-sm"
          />
        </div>
        <div className="flex flex-column gap-1 flex-1">
          <label className="text-sm font-semibold">Group</label>
          <InputText
            value={step.scriptGroupName ?? ''}
            onChange={e => set('scriptGroupName', e.target.value)}
            className="p-inputtext-sm"
          />
        </div>
        <div className="flex flex-column gap-1 flex-1">
          <label className="text-sm font-semibold">Logging Key</label>
          <InputText
            value={step.loggingKey ?? ''}
            onChange={e => set('loggingKey', e.target.value)}
            className="p-inputtext-sm"
          />
        </div>
        <div className="flex flex-column gap-1">
          <label className="text-sm font-semibold">On Failure</label>
          <Dropdown
            value={step.onFail ?? 'continue'}
            options={ON_FAIL_OPTS}
            onChange={e => set('onFail', e.value)}
            style={{ width: '130px' }}
          />
        </div>
      </div>

      {/* Tabbed sub-sections */}
      <TabView>
        <TabPanel header="Query String">
          <StepDataTable
            items={step.queryStrings}
            onChange={v => set('queryStrings', v)}
            defaultType="queryString"
            defaultPhase="POST_REQUEST"
            keyHeader="Parameter"
            valueHeader="Value"
          />
        </TabPanel>

        <TabPanel header="Post Data">
          <div className="flex flex-column gap-2">
            <div className="flex gap-2 align-items-end">
              <div className="flex flex-column gap-1">
                <label className="text-sm font-semibold">Request Format</label>
                <Dropdown
                  value={step.reqFormat ?? 'nvp'}
                  options={REQ_FORMATS}
                  onChange={e => set('reqFormat', e.value)}
                  style={{ width: '130px' }}
                />
              </div>
              <div className="flex flex-column gap-1">
                <label className="text-sm font-semibold">Response Format</label>
                <Dropdown
                  value={step.respFormat ?? 'json'}
                  options={RESP_FORMATS}
                  onChange={e => set('respFormat', e.value)}
                  style={{ width: '130px' }}
                />
              </div>
            </div>
            {(step.reqFormat === 'nvp' || !step.reqFormat) ? (
              <StepDataTable
                items={step.postDatas}
                onChange={v => set('postDatas', v)}
                defaultType="postData"
                defaultPhase="POST_REQUEST"
                keyHeader="Name"
                valueHeader="Value"
              />
            ) : (
              <div className="flex flex-column gap-1">
                <label className="text-sm font-semibold">Body</label>
                <InputTextarea
                  value={step.payload ?? ''}
                  onChange={e => set('payload', e.target.value)}
                  rows={6}
                  className="w-full font-monospace"
                  style={{ fontFamily: 'monospace' }}
                />
              </div>
            )}
          </div>
        </TabPanel>

        <TabPanel header="Request Headers">
          <StepDataTable
            items={step.requestheaders}
            onChange={v => set('requestheaders', v)}
            defaultType="requestHeader"
            defaultPhase="POST_REQUEST"
            keyHeader="Header"
            valueHeader="Value"
          />
        </TabPanel>

        <TabPanel header="Response Headers">
          <StepDataTable
            items={step.responseheaders}
            onChange={v => set('responseheaders', v)}
            defaultType="responseHeader"
            defaultPhase="POST_RESPONSE"
            keyHeader="Header"
            valueHeader="Expected Value"
          />
        </TabPanel>

        <TabPanel header="Req Cookies">
          <StepDataTable
            items={step.requestCookies}
            onChange={v => set('requestCookies', v)}
            defaultType="requestCookie"
            defaultPhase="POST_REQUEST"
            keyHeader="Cookie Name"
            valueHeader="Value"
          />
        </TabPanel>

        <TabPanel header="Resp Cookies">
          <StepDataTable
            items={step.responseCookies}
            onChange={v => set('responseCookies', v)}
            defaultType="responseCookie"
            defaultPhase="POST_RESPONSE"
            keyHeader="Cookie Name"
            valueHeader="Expected Value"
          />
        </TabPanel>

        <TabPanel header="Validation">
          <StepDataTable
            items={step.data}
            onChange={v => set('data', v)}
            defaultType="bodyValidation"
            defaultPhase="POST_RESPONSE"
            keyHeader="Condition"
            valueHeader="Expected"
          />
        </TabPanel>

        <TabPanel header="Assignments">
          <StepDataTable
            items={step.responseData}
            onChange={v => set('responseData', v)}
            defaultType="bodyAssignment"
            defaultPhase="POST_RESPONSE"
            keyHeader="Variable"
            valueHeader="XPath / JSONPath"
          />
        </TabPanel>

        <TabPanel header="Comments">
          <InputTextarea
            value={step.comments ?? ''}
            onChange={e => set('comments', e.target.value)}
            rows={4}
            className="w-full"
          />
        </TabPanel>
      </TabView>
    </div>
  );
}

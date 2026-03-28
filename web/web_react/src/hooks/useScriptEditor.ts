/**
 * Fetches a Tank script XML, parses it into an editable in-memory model,
 * and provides a save function that serialises back to XML and uploads.
 */
import { useState, useCallback } from 'react';
import apiClient from '../api/client';
import type { EditableScript, ScriptStep, StepData, StepType } from '../types/scriptEditor';

// ── XML namespace used by Tank ────────────────────────────────────────────────
const NS = 'urn:wats/domain/script/v1';

function text(el: Element, tag: string): string {
  const child = el.getElementsByTagNameNS(NS, tag)[0]
    ?? el.getElementsByTagName(tag)[0];
  return child?.textContent?.trim() ?? '';
}

function parseDataSet(parent: Element, tag: string): StepData[] {
  const results: StepData[] = [];
  const items = parent.getElementsByTagNameNS(NS, tag);
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    // Only direct children of parent to avoid deep nesting
    if (item.parentElement !== parent) continue;
    results.push({
      key:   text(item, 'key'),
      value: text(item, 'value'),
      type:  text(item, 'type'),
      phase: text(item, 'phase'),
    });
  }
  return results;
}

function parseStep(el: Element, index: number): ScriptStep {
  const type = (text(el, 'type') || 'request') as StepType;

  // For think-time, min/max are encoded in the payload or name fields
  // Tank stores minThinkTime in loggingKey and maxThinkTime in name for thinkTime steps
  const payload = text(el, 'payload');
  const name    = text(el, 'name');
  const loggingKey = text(el, 'loggingKey');

  let minThinkTime: string | undefined;
  let maxThinkTime: string | undefined;
  let varKey: string | undefined;
  let varValue: string | undefined;
  let cookieName: string | undefined, cookieValue: string | undefined;
  let cookiePath: string | undefined, cookieDomain: string | undefined;
  let authScheme: string | undefined, authUser: string | undefined;
  let authPassword: string | undefined, authHost: string | undefined;
  let authPort: string | undefined, authRealm: string | undefined;

  if (type === 'thinkTime') {
    // Tank encodes think time as "min,max" in the payload
    const parts = payload?.split(',') ?? [];
    minThinkTime = parts[0] ?? '0';
    maxThinkTime = parts[1] ?? '0';
  }
  if (type === 'variable') {
    varKey   = text(el, 'loggingKey') || name;
    varValue = payload;
  }
  if (type === 'cookie') {
    cookieName   = name;
    cookieValue  = payload;
    cookiePath   = text(el, 'simplePath');
    cookieDomain = text(el, 'hostname');
  }
  if (type === 'authentication') {
    authScheme   = text(el, 'method');
    authUser     = name;
    authPassword = payload;
    authHost     = text(el, 'hostname');
    authPort     = text(el, 'result');
    authRealm    = text(el, 'mimetype');
  }

  return {
    uuid:            text(el, 'uuid'),
    stepIndex:       index,
    type,
    scriptGroupName: text(el, 'scriptGroupName') || undefined,
    name:            name || undefined,
    label:           text(el, 'label') || undefined,
    method:          text(el, 'method') || undefined,
    protocol:        text(el, 'protocol') || undefined,
    hostname:        text(el, 'hostname') || undefined,
    simplePath:      text(el, 'simplePath') || undefined,
    url:             text(el, 'url') || undefined,
    loggingKey:      loggingKey || undefined,
    onFail:          text(el, 'onFail') || undefined,
    comments:        text(el, 'comments') || undefined,
    payload:         payload || undefined,
    response:        text(el, 'response') || undefined,
    respFormat:      text(el, 'respFormat') || undefined,
    reqFormat:       text(el, 'reqFormat') || undefined,
    mimetype:        text(el, 'mimetype') || undefined,
    result:          text(el, 'result') || undefined,
    minThinkTime, maxThinkTime,
    varKey, varValue,
    cookieName, cookieValue, cookiePath, cookieDomain,
    authScheme, authUser, authPassword, authHost, authPort, authRealm,
    script:          type === 'logic' ? payload : undefined,
    requestheaders:  parseDataSet(el, 'requestheaders'),
    responseheaders: parseDataSet(el, 'responseheaders'),
    requestCookies:  parseDataSet(el, 'requestCookies'),
    responseCookies: parseDataSet(el, 'responseCookies'),
    postDatas:       parseDataSet(el, 'postDatas'),
    queryStrings:    parseDataSet(el, 'queryStrings'),
    data:            parseDataSet(el, 'data'),
    responseData:    parseDataSet(el, 'responseData'),
  };
}

function parseScriptXml(xmlText: string, scriptId: number): EditableScript {
  const parser = new DOMParser();
  const doc = parser.parseFromString(xmlText, 'application/xml');
  const root = doc.documentElement;

  const stepEls = doc.getElementsByTagNameNS(NS, 'step');
  const steps: ScriptStep[] = [];
  for (let i = 0; i < stepEls.length; i++) {
    steps.push(parseStep(stepEls[i], i));
  }

  return {
    id:          scriptId,
    name:        text(root, 'name'),
    productName: text(root, 'productName') || undefined,
    comments:    text(root, 'comments') || undefined,
    creator:     text(root, 'creator') || undefined,
    steps,
  };
}

// ── XML serialisation ─────────────────────────────────────────────────────────

function xmlEsc(s: string | undefined): string {
  if (!s) return '';
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function dataSetXml(items: StepData[], tag: string): string {
  return items
    .map(d =>
      `<${tag} xmlns="${NS}"><key>${xmlEsc(d.key)}</key><value>${xmlEsc(d.value)}</value><type>${xmlEsc(d.type)}</type><phase>${xmlEsc(d.phase)}</phase></${tag}>`)
    .join('');
}

function stepToXml(s: ScriptStep): string {
  // Reconstruct payload / name / loggingKey based on type
  let payload = s.payload ?? '';
  let name    = s.name ?? '';
  let loggingKey = s.loggingKey ?? '';
  let hostname = s.hostname ?? '';
  let simplePath = s.simplePath ?? '';
  let method = s.method ?? '';
  let result = s.result ?? '';
  let mimetype = s.mimetype ?? '';

  if (s.type === 'thinkTime') {
    payload = `${s.minThinkTime ?? 0},${s.maxThinkTime ?? 0}`;
  }
  if (s.type === 'variable') {
    loggingKey = s.varKey ?? '';
    name       = s.varKey ?? '';
    payload    = s.varValue ?? '';
  }
  if (s.type === 'cookie') {
    name       = s.cookieName ?? '';
    payload    = s.cookieValue ?? '';
    simplePath = s.cookiePath ?? '';
    hostname   = s.cookieDomain ?? '';
  }
  if (s.type === 'authentication') {
    method   = s.authScheme ?? '';
    name     = s.authUser ?? '';
    payload  = s.authPassword ?? '';
    hostname = s.authHost ?? '';
    result   = s.authPort ?? '';
    mimetype = s.authRealm ?? '';
  }
  if (s.type === 'logic') {
    payload = s.script ?? '';
    name    = s.name ?? '';
  }

  return `<step xmlns="${NS}">
<uuid>${xmlEsc(s.uuid)}</uuid>
<scriptGroupName>${xmlEsc(s.scriptGroupName)}</scriptGroupName>
<method>${xmlEsc(method)}</method>
<type>${xmlEsc(s.type)}</type>
<label>${xmlEsc(s.label)}</label>
<url>${xmlEsc(s.url)}</url>
<loggingKey>${xmlEsc(loggingKey)}</loggingKey>
<result>${xmlEsc(result)}</result>
<mimetype>${xmlEsc(mimetype)}</mimetype>
<name>${xmlEsc(name)}</name>
<onFail>${xmlEsc(s.onFail)}</onFail>
<stepIndex>${s.stepIndex}</stepIndex>
<simplePath>${xmlEsc(simplePath)}</simplePath>
<hostname>${xmlEsc(hostname)}</hostname>
<protocol>${xmlEsc(s.protocol)}</protocol>
<payload>${xmlEsc(payload)}</payload>
<comments>${xmlEsc(s.comments)}</comments>
<respFormat>${xmlEsc(s.respFormat)}</respFormat>
<reqFormat>${xmlEsc(s.reqFormat)}</reqFormat>
${dataSetXml(s.requestheaders, 'requestheaders')}
${dataSetXml(s.responseheaders, 'responseheaders')}
${dataSetXml(s.requestCookies, 'requestCookies')}
${dataSetXml(s.responseCookies, 'responseCookies')}
${dataSetXml(s.postDatas, 'postDatas')}
${dataSetXml(s.queryStrings, 'queryStrings')}
<response>${xmlEsc(s.response)}</response>
${dataSetXml(s.data, 'data')}
${dataSetXml(s.responseData, 'responseData')}
</step>`;
}

function scriptToXml(script: EditableScript): string {
  const stepsXml = script.steps
    .map((s, i) => stepToXml({ ...s, stepIndex: i }))
    .join('\n');

  return `<?xml version="1.0" encoding="UTF-8"?>
<script xmlns="${NS}">
<id>${script.id}</id>
<name>${xmlEsc(script.name)}</name>
<productName>${xmlEsc(script.productName)}</productName>
<comments>${xmlEsc(script.comments)}</comments>
<creator>${xmlEsc(script.creator)}</creator>
<steps>
${stepsXml}
</steps>
</script>`;
}

// ── Hook ──────────────────────────────────────────────────────────────────────

export type SaveState = 'idle' | 'saving' | 'saved' | 'error';

export function useScriptEditor(scriptId: number) {
  const [script, setScript] = useState<EditableScript | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [saveState, setSaveState] = useState<SaveState>('idle');
  const [dirty, setDirty] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      const res = await apiClient.get<string>(`/v2/scripts/download/${scriptId}`, {
        transformResponse: [(d) => d],
      });
      setScript(parseScriptXml(res.data, scriptId));
      setDirty(false);
    } catch (e: unknown) {
      setLoadError(e instanceof Error ? e.message : 'Failed to load script');
    } finally {
      setLoading(false);
    }
  }, [scriptId]);

  const update = useCallback((updater: (prev: EditableScript) => EditableScript) => {
    setScript(prev => {
      if (!prev) return prev;
      const next = updater(prev);
      setDirty(true);
      return next;
    });
  }, []);

  const save = useCallback(async () => {
    if (!script) return;
    setSaveState('saving');
    try {
      const xml = scriptToXml(script);
      const blob = new Blob([xml], { type: 'application/xml' });
      const formData = new FormData();
      formData.append('file', blob, `${script.name}.xml`);
      await apiClient.post(`/v2/scripts?id=${script.id}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setSaveState('saved');
      setDirty(false);
      setTimeout(() => setSaveState('idle'), 2000);
    } catch {
      setSaveState('error');
      setTimeout(() => setSaveState('idle'), 3000);
    }
  }, [script]);

  return { script, loading, loadError, saveState, dirty, load, update, save };
}

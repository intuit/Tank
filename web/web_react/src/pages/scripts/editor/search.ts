import type { Schemas } from '../../../api/client';

export type StepMatch = Schemas['StepMatch'];

/**
 * The parts of a step search can look in, by the server's section names (RequestStepSection,
 * ThinkTimeSection, SleepTimeSection, VariableSection), grouped as the JSF search options are.
 */
export const SECTION_GROUPS = [
  {
    label: 'Requests',
    items: [
      ['searchRequest', 'Any request field'],
      ['url', 'URL'],
      ['protocol', 'Protocol'],
      ['host', 'Host'],
      ['simplePath', 'Path'],
      ['method', 'Method'],
      ['name', 'Request name'],
      ['scriptGroupName', 'Group'],
      ['logginKey', 'Logging key'],
      ['mimeType', 'Mime type'],
      ['queryStringKey', 'Query string key'],
      ['queryStringValue', 'Query string value'],
      ['postDataKey', 'Post data key'],
      ['postDataValue', 'Post data value'],
      ['requestHeaderKey', 'Request header key'],
      ['requestHeaderValue', 'Request header value'],
      ['requestCookieKey', 'Request cookie key'],
      ['requestCookieValue', 'Request cookie value'],
      ['responseHeaderKey', 'Response header key'],
      ['responseHeaderValue', 'Response header value'],
      ['responseCookieKey', 'Response cookie key'],
      ['responseCookieValue', 'Response cookie value'],
      ['responseContent', 'Response content'],
    ],
  },
  { label: 'Think time', items: [['minTime', 'Minimum'], ['maxTime', 'Maximum']] },
  { label: 'Sleep time', items: [['sleepTime', 'Sleep time']] },
  { label: 'Variables', items: [['variableKey', 'Variable name'], ['variableValue', 'Variable value']] },
].map((g) => ({ label: g.label, items: g.items.map(([value, label]) => ({ value: value!, label: label! })) }));

/** Every section, CommonSection.search: what an empty choice searches */
export const EVERYTHING = 'search';

const LABELS = new Map(SECTION_GROUPS.flatMap((g) => g.items.map((i) => [i.value, i.label] as const)));

export function sectionLabel(section: string | undefined): string {
  return LABELS.get(section ?? '') ?? section ?? '';
}

/**
 * The query as the server matches it: the whole value, ignoring case, with * matching any text and
 * everything else, ? included, literal (RegexUtil.wildcardToRegexp). To find text anywhere in a
 * value, it's wrapped in *.
 */
export function searchQuery(text: string, anywhere: boolean): string {
  const q = text.trim();
  return anywhere && !q.includes('*') ? `*${q}*` : q;
}

export interface StepMatches {
  uuid: string;
  /** 1-based, as the step table numbers steps */
  position: number;
  matches: StepMatch[];
}

/** The matches, one entry per step in script order */
export function byStep(matches: StepMatch[]): StepMatches[] {
  const steps = new Map<string, StepMatches>();
  for (const m of matches) {
    const uuid = m.uuid ?? '';
    const entry = steps.get(uuid) ?? { uuid, position: (m.position ?? 0) + 1, matches: [] };
    // a step matches once per part, though "any request field" and a named part can both find it
    if (!entry.matches.some((e) => e.section === m.section && e.key === m.key && e.value === m.value)) {
      entry.matches.push(m);
    }
    steps.set(uuid, entry);
  }
  return [...steps.values()].sort((a, b) => a.position - b.position);
}

/** "1 min 5 s", for the expected duration of a pass through the script */
export function formatDuration(ms: number): string {
  const seconds = Math.round(ms / 1000);
  if (seconds < 60) return seconds === 0 && ms > 0 ? `${ms} ms` : `${seconds} s`;
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  return [h && `${h} h`, m && `${m} min`, s && `${s} s`].filter(Boolean).join(' ');
}

import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { LOCATIONS, NO_VALUE, OPERATORS, type Assignment, type Location, type OperatorName, type Validation } from './responseRules';

const PHASES = [
  { label: 'After the request', value: 'POST_REQUEST' },
  { label: 'Before the request', value: 'PRE_REQUEST' },
];
const OPERATOR_OPTIONS = OPERATORS.map((o) => ({ label: o.label, value: o.name }));
/** ScriptRequestEditor.getResponseFormats, if the server doesn't send them */
const DEFAULT_RESPONSE_FORMATS = [
  { label: 'JSON', value: 'json' },
  { label: 'RAW', value: 'raw' },
  { label: 'XML', value: 'xml' },
];

/** How body lookups are read: JSON path, XPath, or the raw text (Request.respFormat) */
export function ResponseFormat({
  value,
  onChange,
  readOnly,
}: {
  value: string | undefined;
  onChange: (value: string) => void;
  readOnly: boolean;
}) {
  const options = useConfigOptions();
  return (
    <div className="field-inline">
      <label htmlFor="resp-format">Response format</label>
      <Dropdown
        inputId="resp-format"
        value={value ?? null}
        options={options.data?.stepOptions?.['responseFormats'] ?? DEFAULT_RESPONSE_FORMATS}
        optionLabel="label"
        optionValue="value"
        onChange={(e) => onChange(e.value as string)}
        placeholder="Not set"
        disabled={readOnly}
      />
      <small className="field-help">Body lookups are JSON paths for JSON, XPath for XML</small>
    </div>
  );
}

/** A request's response checks (validation-editor.xhtml) */
export function ValidationsEditor({
  rules,
  onChange,
  readOnly,
}: {
  rules: Validation[];
  onChange: (rules: Validation[]) => void;
  readOnly: boolean;
}) {
  const set = (i: number, change: Partial<Validation>) => onChange(rules.map((r, j) => (j === i ? { ...r, ...change } : r)));
  return (
    <div>
      <table className="kv-table kv-table-wide rules-table">
        <thead>
          <tr>
            <th>When</th>
            <th>Location</th>
            <th>Lookup</th>
            <th>Operator</th>
            <th>Value or variable</th>
            {!readOnly && <th aria-label="Actions" />}
          </tr>
        </thead>
        <tbody>
          {rules.map((rule, i) => (
            <tr key={i}>
              <td>
                <Dropdown value={rule.phase} options={PHASES} onChange={(e) => set(i, { phase: e.value })} disabled={readOnly} aria-label={`Validation ${i + 1} when`} />
              </td>
              <td>
                <LocationPicker value={rule.location} onChange={(location) => set(i, { location })} readOnly={readOnly} label={`Validation ${i + 1} location`} />
              </td>
              <td>
                <InputText value={rule.key} onChange={(e) => set(i, { key: e.target.value })} disabled={readOnly} aria-label={`Validation ${i + 1} lookup`} placeholder={placeholderFor(rule.location)} />
              </td>
              <td>
                <Dropdown
                  value={rule.operator}
                  options={OPERATOR_OPTIONS}
                  onChange={(e) => set(i, { operator: e.value as OperatorName })}
                  disabled={readOnly}
                  aria-label={`Validation ${i + 1} operator`}
                />
              </td>
              <td>
                <InputText
                  value={NO_VALUE.has(rule.operator) ? '' : rule.value}
                  onChange={(e) => set(i, { value: e.target.value })}
                  disabled={readOnly || NO_VALUE.has(rule.operator)}
                  aria-label={`Validation ${i + 1} value`}
                />
              </td>
              {!readOnly && (
                <td>
                  <Button type="button" icon="pi pi-times" rounded text severity="danger" aria-label={`Remove validation ${i + 1}`} onClick={() => onChange(rules.filter((_, j) => j !== i))} />
                </td>
              )}
            </tr>
          ))}
          {rules.length === 0 && (
            <tr>
              <td colSpan={6} className="field-help">
                No validations: the request passes whatever it gets back.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && (
        <Button
          type="button"
          label="Add validation"
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => onChange([...rules, { phase: 'POST_REQUEST', location: 'Body', key: '', operator: 'equals', value: '' }])}
        />
      )}
    </div>
  );
}

/** Values a request takes from its response into variables (assignment-editor.xhtml) */
export function AssignmentsEditor({
  rules,
  onChange,
  readOnly,
}: {
  rules: Assignment[];
  onChange: (rules: Assignment[]) => void;
  readOnly: boolean;
}) {
  const set = (i: number, change: Partial<Assignment>) => onChange(rules.map((r, j) => (j === i ? { ...r, ...change } : r)));
  return (
    <div>
      <table className="kv-table kv-table-wide rules-table">
        <thead>
          <tr>
            <th>Location</th>
            <th>Variable</th>
            <th>Lookup</th>
            {!readOnly && <th aria-label="Actions" />}
          </tr>
        </thead>
        <tbody>
          {rules.map((rule, i) => (
            <tr key={i}>
              <td>
                <LocationPicker value={rule.location} onChange={(location) => set(i, { location })} readOnly={readOnly} label={`Assignment ${i + 1} location`} />
              </td>
              <td>
                <InputText value={rule.variable} onChange={(e) => set(i, { variable: e.target.value })} disabled={readOnly} aria-label={`Assignment ${i + 1} variable`} placeholder="cartId" />
              </td>
              <td>
                <InputText value={rule.lookup} onChange={(e) => set(i, { lookup: e.target.value })} disabled={readOnly} aria-label={`Assignment ${i + 1} lookup`} placeholder={placeholderFor(rule.location)} />
              </td>
              {!readOnly && (
                <td>
                  <Button type="button" icon="pi pi-times" rounded text severity="danger" aria-label={`Remove assignment ${i + 1}`} onClick={() => onChange(rules.filter((_, j) => j !== i))} />
                </td>
              )}
            </tr>
          ))}
          {rules.length === 0 && (
            <tr>
              <td colSpan={4} className="field-help">
                No assignments. Later steps can use an assigned variable as @name.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {!readOnly && (
        <Button
          type="button"
          label="Add assignment"
          icon="pi pi-plus"
          outlined
          size="small"
          onClick={() => onChange([...rules, { location: 'Body', variable: '', lookup: '' }])}
        />
      )}
    </div>
  );
}

function LocationPicker({
  value,
  onChange,
  readOnly,
  label,
}: {
  value: Location;
  onChange: (value: Location) => void;
  readOnly: boolean;
  label: string;
}) {
  return <Dropdown value={value} options={[...LOCATIONS]} onChange={(e) => onChange(e.value as Location)} disabled={readOnly} aria-label={label} />;
}

function placeholderFor(location: Location): string {
  return location === 'Body' ? '$.status or /cart/id' : location === 'Header' ? 'X-Request-Id' : 'sessionId';
}

/** Problems that would make the agent skip a rule */
export function rulesProblem(validations: Validation[], assignments: Assignment[]): string | undefined {
  const v = validations.findIndex((r) => !r.key.trim());
  if (v >= 0) return `Validation ${v + 1} needs a lookup`;
  const a = assignments.findIndex((r) => !r.variable.trim() || !r.lookup.trim());
  if (a >= 0) return `Assignment ${a + 1} needs a variable and a lookup`;
  return undefined;
}

import type { ReactNode } from 'react';

/** A labelled form row: label left, input and optional help right (stacked on narrow screens) */
export function Field({
  label,
  htmlFor,
  help,
  children,
}: {
  label: string;
  htmlFor?: string;
  help?: string;
  children: ReactNode;
}) {
  return (
    <div className="field">
      <label htmlFor={htmlFor}>{label}</label>
      <div className="field-input">
        {children}
        {help && <small className="field-help">{help}</small>}
      </div>
    </div>
  );
}

import { Button } from 'primereact/button';
import type { ReactNode } from 'react';
import { Link } from 'react-router';
import { formatDateTime } from '../format';
import type { ColumnDef } from './EntityList';

export interface OwnedSummary {
  id?: number;
  name?: string;
  productName?: string;
  comments?: string;
  created?: string;
  modified?: string;
  owner?: string;
}

/**
 * The columns projects and scripts share, by preference key (TableColumnDefaults): select, ID, name
 * (a link to `href`), product, comments, created, modified, owner and the row actions.
 */
export function ownedColumns<T extends OwnedSummary>({
  href,
  actions,
}: {
  href: (row: T) => string;
  actions: (row: T) => ReactNode;
}): Record<string, ColumnDef<T>> {
  return {
    selectColumn: { selection: true, resizable: false },
    idColumn: { field: 'id' as keyof T & string, sortable: true },
    nameColumn: {
      field: 'name' as keyof T & string,
      sortable: true,
      body: (row) => (
        <Link to={href(row)} title={`${row.name} (id ${row.id})`}>
          {row.name}
        </Link>
      ),
    },
    productColumn: { field: 'productName' as keyof T & string, sortable: true },
    commentsColumn: { field: 'comments' as keyof T & string, body: (row) => <span title={row.comments}>{row.comments}</span> },
    createColumn: { field: 'created' as keyof T & string, sortable: true, body: (row) => formatDateTime(row.created) },
    modifiedColumn: { field: 'modified' as keyof T & string, sortable: true, body: (row) => formatDateTime(row.modified) },
    ownerColumn: { field: 'owner' as keyof T & string, sortable: true },
    actionsColumn: { header: '', body: (row) => <div className="row-actions">{actions(row)}</div>, resizable: false },
  };
}

/** An icon button in a row's actions, labelled for screen readers and with a tooltip */
export function RowAction({
  icon,
  label,
  name,
  onClick,
  severity,
}: {
  icon: string;
  label: string;
  /** What it acts on, for the accessible name: "Copy Checkout load" */
  name: string | undefined;
  onClick: () => void;
  severity?: 'danger';
}) {
  return (
    <Button
      icon={icon}
      rounded
      text
      severity={severity}
      aria-label={`${label} ${name ?? ''}`.trim()}
      tooltip={label}
      tooltipOptions={{ position: 'top' }}
      onClick={onClick}
    />
  );
}

/** A download in a row's actions; a plain link so the browser handles the file */
export function RowDownload({ href, label, name }: { href: string; label: string; name: string | undefined }) {
  return (
    <a
      className="p-button p-button-icon-only p-button-text p-button-rounded plain-link"
      href={href}
      aria-label={`${label} ${name ?? ''}`.trim()}
      title={label}
    >
      <i className="pi pi-download" aria-hidden />
    </a>
  );
}

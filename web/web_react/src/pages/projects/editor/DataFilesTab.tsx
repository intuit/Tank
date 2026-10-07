import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useEffect, useState } from 'react';
import type { Schemas } from '../../../api/client';
import { toApiError } from '../../../api/errors';
import { formatDateTime } from '../../../format';
import { useSession } from '../../../session';
import type { Update } from './useProjectDraft';
import type { ProjectDetail } from './validation';

type DataFileSummary = Schemas['DataFileSummary'];
const PICKER_SIZE = 10;

/** The data files the project's scripts can read (AssociateDataFileBean) */
export function DataFilesTab({ detail, update, readOnly }: { detail: ProjectDetail; update: Update; readOnly: boolean }) {
  const { client } = useSession();
  const ids = detail.dataFileIds ?? [];
  const [previewing, setPreviewing] = useState<{ id: number; name: string }>();

  const names = useQuery({
    queryKey: ['datafiles', 'names'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/datafiles/names', { signal });
      if (!data) {
        throw toApiError(error, response, 'load data file names');
      }
      return data;
    },
  });
  const nameOf = (id: number) => names.data?.[String(id)] ?? `Data file ${id}`;
  const associated = ids.map((id) => ({ id, name: nameOf(id) }));

  return (
    <div>
      <h3 className="form-section">Data files used by this project</h3>
      <DataTable
        value={associated}
        size="small"
        loading={names.isPending}
        emptyMessage="No data files. Find files below to add them."
      >
        <Column field="name" header="Name" bodyClassName="ellipsis" />
        <Column
          style={{ width: '8rem' }}
          body={(file: { id: number; name: string }) => (
            <div className="row-actions">
              <Button
                icon="pi pi-eye"
                rounded
                text
                aria-label={`Preview ${file.name}`}
                tooltip="Preview"
                tooltipOptions={{ position: 'top' }}
                onClick={() => setPreviewing(file)}
              />
              {!readOnly && (
                <Button
                  icon="pi pi-times"
                  rounded
                  text
                  severity="danger"
                  aria-label={`Remove ${file.name}`}
                  onClick={() => update((d) => void (d.dataFileIds = (d.dataFileIds ?? []).filter((x) => x !== file.id)))}
                />
              )}
            </div>
          )}
        />
      </DataTable>

      {!readOnly && (
        <DataFilePicker
          exclude={ids}
          onAdd={(file) => update((d) => void (d.dataFileIds = [...(d.dataFileIds ?? []), file.id!]))}
          onPreview={(file) => setPreviewing({ id: file.id!, name: file.name ?? '' })}
        />
      )}
      {previewing && <DataFilePreview file={previewing} onHide={() => setPreviewing(undefined)} />}
    </div>
  );
}

function DataFilePicker({
  exclude,
  onAdd,
  onPreview,
}: {
  exclude: number[];
  onAdd: (file: DataFileSummary) => void;
  onPreview: (file: DataFileSummary) => void;
}) {
  const { client } = useSession();
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  useEffect(() => {
    const timer = setTimeout(() => setQ(search.trim()), 300);
    return () => clearTimeout(timer);
  }, [search]);

  const results = useQuery({
    queryKey: ['datafiles', { page: 0, size: PICKER_SIZE, q, sort: 'name,asc' }],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/datafiles', {
        params: { query: { page: 0, size: PICKER_SIZE, sort: 'name,asc', q: q || undefined } },
        signal,
      });
      if (!data || !('items' in data)) {
        throw toApiError(error, response, 'find data files');
      }
      return data;
    },
  });

  return (
    <div className="picker">
      <h3 className="form-section">Add data files</h3>
      <IconField iconPosition="left">
        <InputIcon className="pi pi-search" />
        <InputText
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Find data files by name or comments"
          aria-label="Find data files"
          className="picker-search"
        />
      </IconField>
      {results.error ? (
        <Message severity="error" text={results.error.message} />
      ) : (
        <DataTable value={results.data?.items ?? []} size="small" loading={results.isFetching} emptyMessage="No data files found">
          <Column field="name" header="Name" bodyClassName="ellipsis" />
          <Column field="owner" header="Owner" style={{ width: '9rem' }} />
          <Column header="Modified" style={{ width: '12rem' }} body={(f: DataFileSummary) => formatDateTime(f.modified)} />
          <Column
            style={{ width: '9rem' }}
            body={(file: DataFileSummary) => (
              <div className="row-actions">
                <Button icon="pi pi-eye" rounded text aria-label={`Preview ${file.name}`} onClick={() => onPreview(file)} />
                <Button
                  label={exclude.includes(file.id!) ? 'Added' : 'Add'}
                  size="small"
                  text
                  aria-label={`Add ${file.name}`}
                  disabled={exclude.includes(file.id!)}
                  onClick={() => onAdd(file)}
                />
              </div>
            )}
          />
        </DataTable>
      )}
      {results.data && (results.data.total ?? 0) > PICKER_SIZE && (
        <small className="field-help">
          Showing {PICKER_SIZE} of {results.data.total}. Narrow the search to find others.
        </small>
      )}
    </div>
  );
}

const PREVIEW_LINES = 25;

/** A page of a data file's lines (DataFileBrowser), using the X-Total-Lines header for paging */
function DataFilePreview({ file, onHide }: { file: { id: number; name: string }; onHide: () => void }) {
  const { client } = useSession();
  const [offset, setOffset] = useState(0);
  const content = useQuery({
    queryKey: ['datafiles', file.id, 'content', offset],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/datafiles/content', {
        params: { query: { id: file.id, offset, lines: PREVIEW_LINES } },
        parseAs: 'text',
        signal,
      });
      if (data === undefined) {
        throw toApiError(error, response, 'preview the data file');
      }
      const total = Number(response.headers.get('X-Total-Lines'));
      return { text: data, total: Number.isFinite(total) ? total : undefined };
    },
  });
  const total = content.data?.total;
  return (
    <Dialog header={file.name} visible onHide={onHide} className="wide-dialog" modal draggable={false}>
      {content.error ? (
        <Message severity="error" text={content.error.message} />
      ) : (
        <pre className="file-preview" aria-busy={content.isFetching}>
          {content.data?.text ?? 'Loading…'}
        </pre>
      )}
      <div className="form-actions">
        <span className="field-help">
          {total !== undefined
            ? `Lines ${Math.min(offset + 1, total)}–${Math.min(offset + PREVIEW_LINES, total)} of ${total}`
            : ''}
        </span>
        <Button
          icon="pi pi-angle-left"
          text
          aria-label="Previous lines"
          disabled={offset === 0}
          onClick={() => setOffset(Math.max(0, offset - PREVIEW_LINES))}
        />
        <Button
          icon="pi pi-angle-right"
          text
          aria-label="Next lines"
          disabled={total === undefined || offset + PREVIEW_LINES >= total}
          onClick={() => setOffset(offset + PREVIEW_LINES)}
        />
      </div>
    </Dialog>
  );
}

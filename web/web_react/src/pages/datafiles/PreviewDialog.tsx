import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { Paginator } from 'primereact/paginator';
import { useState } from 'react';
import { contextPath, type Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { useSession } from '../../session';

/** Lines a page shows, as in the JSF viewer */
export const LINES_PER_PAGE = 50;

/** A data file's lines, a page at a time (the JSF "Data File" view dialog) */
export function PreviewDialog({ dataFile, onHide }: { dataFile: Schemas['DataFileSummary']; onHide: () => void }) {
  const { client } = useSession();
  const [offset, setOffset] = useState(0);
  const preview = useQuery({
    queryKey: ['datafile-preview', dataFile.id, offset],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/datafiles/{datafileId}/preview', {
        params: { path: { datafileId: dataFile.id! }, query: { offset, lines: LINES_PER_PAGE } },
        signal,
      });
      if (!data) throw toApiError(error, response, 'load the file');
      return data;
    },
    placeholderData: keepPreviousData,
  });
  const total = preview.data?.totalLines ?? 0;
  const lines = preview.data?.lines ?? [];
  const width = String(offset + lines.length).length;

  return (
    <Dialog
      header={dataFile.name}
      visible
      onHide={onHide}
      className="request-dialog"
      modal
      draggable={false}
      footer={
        <div className="preview-footer">
          <a className="p-button p-button-text plain-link" href={`${contextPath()}/v2/datafiles/download/${dataFile.id}`}>
            <i className="pi pi-download" aria-hidden />
            &nbsp;Download
          </a>
          <Button label="Done" onClick={onHide} />
        </div>
      }
    >
      {preview.error && <Message severity="error" text={preview.error.message} />}
      {preview.data && (
        <>
          <p className="field-help" role="status">
            {total === 0
              ? 'The file is empty.'
              : `Lines ${offset + 1} to ${offset + lines.length} of ${total.toLocaleString()}`}
          </p>
          <pre className="file-preview datafile-lines" aria-label={`Lines of ${dataFile.name}`}>
            {lines.map((line, i) => (
              <span key={offset + i} className="datafile-line">
                <span className="line-number" aria-hidden>
                  {String(offset + i + 1).padStart(width)}
                </span>
                {line}
                {'\n'}
              </span>
            ))}
          </pre>
          {total > LINES_PER_PAGE && (
            <Paginator
              first={offset}
              rows={LINES_PER_PAGE}
              totalRecords={total}
              onPageChange={(e) => setOffset(e.first)}
              template="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink JumpToPageInput"
            />
          )}
        </>
      )}
    </Dialog>
  );
}

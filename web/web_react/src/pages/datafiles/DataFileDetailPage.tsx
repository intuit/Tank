import { useParams, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Paginator } from 'primereact/paginator';
import { useDataFile, useDataFileContent } from '../../hooks/useDataFiles';

const LINES_PER_PAGE = 50;

export function DataFileDetailPage() {
  const { id } = useParams<{ id: string }>();
  const fileId = Number(id);
  const navigate = useNavigate();
  const [offset, setOffset] = useState(0);
  const { data: file, isLoading, error } = useDataFile(fileId);
  const { data: content, isLoading: loadingContent } = useDataFileContent(fileId, offset, LINES_PER_PAGE);

  if (isLoading) return <ProgressSpinner />;
  if (error || !file) return <Message severity="error" text="Data file not found." />;

  return (
    <div className="flex flex-column gap-3">
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/datafiles')} />
        <h2 className="m-0">{file.name}</h2>
      </div>

      <Card title="Details">
        <div className="grid">
          <div className="col-3 font-bold">Path</div>
          <div className="col-9">{file.path ?? '—'}</div>
          <div className="col-3 font-bold">Owner</div>
          <div className="col-9">{file.creator ?? '—'}</div>
          <div className="col-3 font-bold">Comments</div>
          <div className="col-9">{file.comments ?? '—'}</div>
        </div>
      </Card>

      <Card title="Content Preview">
        {loadingContent ? (
          <ProgressSpinner style={{ width: '32px', height: '32px' }} />
        ) : (
          <pre
            style={{
              fontFamily: 'monospace',
              fontSize: '0.85rem',
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-all',
              background: '#f8f8f8',
              padding: '1rem',
              borderRadius: '4px',
              maxHeight: '400px',
              overflow: 'auto',
            }}
          >
            {content ?? '(empty)'}
          </pre>
        )}
        <Paginator
          first={offset}
          rows={LINES_PER_PAGE}
          totalRecords={offset + LINES_PER_PAGE + 1}
          onPageChange={(e) => setOffset(e.first)}
          template="FirstPageLink PrevPageLink NextPageLink"
          className="mt-2"
        />
      </Card>
    </div>
  );
}

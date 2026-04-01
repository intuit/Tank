import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { logsApi } from '../../api/logs';

const QUICK_FILES = ['tank.log', 'tank-agent.log', 'catalina.out'];

export function ServerLogsPage() {
  const navigate = useNavigate();
  const [filename, setFilename] = useState('tank.log');
  const [content, setContent] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const loadedFilename = useRef<string>('');

  async function handleLoad() {
    if (!filename.trim()) return;
    setLoading(true);
    setError(null);
    setContent(null);
    try {
      const res = await logsApi.getFile(filename.trim());
      setContent(res.data as string);
      loadedFilename.current = filename.trim();
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : 'Failed to load log file.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  }

  function handleDownload() {
    if (content == null) return;
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = loadedFilename.current || 'log.txt';
    a.click();
    URL.revokeObjectURL(url);
  }

  return (
    <div>
      <div className="flex align-items-center gap-2 mb-3">
        <Button
          icon="pi pi-arrow-left"
          severity="secondary"
          text
          onClick={() => navigate('/admin')}
          aria-label="Back to Admin"
        />
        <h2 className="m-0">Server Logs</h2>
      </div>

      <Card>
        <div className="flex flex-column gap-3">
          <div className="flex align-items-center gap-2 flex-wrap">
            <label htmlFor="log-filename" className="font-semibold white-space-nowrap">
              Log filename
            </label>
            <InputText
              id="log-filename"
              value={filename}
              onChange={(e) => setFilename(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleLoad()}
              placeholder="e.g. tank.log"
              style={{ minWidth: '220px' }}
            />
            <Button label="Load" icon="pi pi-search" onClick={handleLoad} disabled={loading} />
            {content != null && (
              <Button
                label="Download"
                icon="pi pi-download"
                severity="secondary"
                outlined
                onClick={handleDownload}
              />
            )}
          </div>

          <div className="flex gap-2 flex-wrap">
            <span className="font-semibold align-self-center">Quick select:</span>
            {QUICK_FILES.map((f) => (
              <Button
                key={f}
                label={f}
                size="small"
                severity="secondary"
                outlined
                onClick={() => setFilename(f)}
              />
            ))}
          </div>
        </div>
      </Card>

      {loading && (
        <div className="flex justify-content-center mt-4">
          <ProgressSpinner style={{ width: '48px', height: '48px' }} />
        </div>
      )}

      {error && (
        <div className="mt-3">
          <Message severity="error" text={error} style={{ width: '100%' }} />
        </div>
      )}

      {content != null && !loading && (
        <Card className="mt-3">
          <pre
            style={{
              fontFamily: 'monospace',
              fontSize: '0.85rem',
              maxHeight: '60vh',
              overflowY: 'auto',
              overflowX: 'auto',
              whiteSpace: 'pre',
              margin: 0,
              padding: '0.5rem',
              background: 'var(--surface-ground)',
              borderRadius: 'var(--border-radius)',
            }}
          >
            {content}
          </pre>
        </Card>
      )}
    </div>
  );
}

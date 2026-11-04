import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { useState, type FormEvent } from 'react';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { IMPORT_FILE_TYPES, scriptsToImport, uploadScript } from './scriptUpload';

interface Imported {
  name: string;
  message?: string;
  error?: string;
}

/**
 * Imports Tank script XML files (TankXmlUploadBean): each replaces the script whose ID and name it
 * carries, or creates a script when its ID is 0. Takes several files, gzipped files, and zips of them.
 */
export function ImportScriptDialog({ onHide }: { onHide: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [files, setFiles] = useState<File[]>([]);
  const [results, setResults] = useState<Imported[]>();

  const upload = useMutation({
    mutationFn: async () => {
      const scripts = await scriptsToImport(files);
      const imported: Imported[] = [];
      // one at a time, like the JSF upload, so a bad file doesn't stop the rest
      for (const script of scripts) {
        try {
          const result = await uploadScript(client, script);
          imported.push({ name: script.name, message: result.message });
        } catch (e) {
          imported.push({ name: script.name, error: e instanceof Error ? e.message : String(e) });
        }
      }
      return imported;
    },
    onSuccess: (imported) => {
      const ok = imported.filter((r) => !r.error);
      if (ok.length) void queryClient.invalidateQueries({ queryKey: ['scripts'] });
      if (ok.length === imported.length && imported.length === 1) {
        notify.success('Script imported', imported[0]!.message);
        onHide();
      } else if (ok.length === imported.length) {
        notify.success(`${ok.length} scripts imported`);
        onHide();
      } else {
        // keep the dialog open to show which failed
        setResults(imported);
      }
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (files.length) {
      setResults(undefined);
      upload.mutate();
    }
  };

  return (
    <Dialog header="Import Tank XML" visible onHide={onHide} className="form-dialog" modal draggable={false}>
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="import-file">Script files</label>
        <input
          id="import-file"
          type="file"
          multiple
          accept={IMPORT_FILE_TYPES}
          onChange={(e) => setFiles([...(e.target.files ?? [])])}
          className="file-input"
        />
        <small className="field-help">
          Scripts exported from Tank (.xml, gzipped .gz, or a .zip of them). Each replaces the script with the ID and name in
          the file; an ID of 0 creates a new script.
        </small>
        {upload.error && <Message severity="error" text={upload.error.message} />}
        {results && (
          <Message
            severity={results.some((r) => !r.error) ? 'warn' : 'error'}
            content={
              <ul className="problem-list">
                {results.map((r, i) => (
                  <li key={i}>
                    <strong>{r.name}</strong>: {r.error ?? r.message ?? 'imported'}
                  </li>
                ))}
              </ul>
            }
          />
        )}
        <div className="form-actions">
          <Button type="button" label={results ? 'Close' : 'Cancel'} text onClick={onHide} />
          <Button type="submit" label="Import" icon="pi pi-upload" loading={upload.isPending} disabled={!files.length} />
        </div>
      </form>
    </Dialog>
  );
}

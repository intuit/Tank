import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { useState, type FormEvent } from 'react';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { SCRIPT_FILE_TYPES, uploadScript } from './scriptUpload';

/**
 * Imports a Tank script XML (TankXmlUploadBean): it replaces the script whose ID and name it carries,
 * or creates a script when its ID is 0.
 */
export function ImportScriptDialog({ onHide }: { onHide: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [file, setFile] = useState<File>();

  const upload = useMutation({
    mutationFn: () => uploadScript(client, file!),
    onSuccess: (result) => {
      notify.success('Script imported', result.message);
      void queryClient.invalidateQueries({ queryKey: ['scripts'] });
      onHide();
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (file) {
      upload.mutate();
    }
  };

  return (
    <Dialog header="Import Tank XML" visible onHide={onHide} className="form-dialog" modal draggable={false}>
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="import-file">Script file</label>
        <input id="import-file" type="file" accept={SCRIPT_FILE_TYPES} onChange={(e) => setFile(e.target.files?.[0])} className="file-input" />
        <small className="field-help">
          A script exported from Tank. It replaces the script with the ID and name in the file; an ID of 0 creates a new script.
        </small>
        {upload.error && <Message severity="error" text={upload.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Import" icon="pi pi-upload" loading={upload.isPending} disabled={!file} />
        </div>
      </form>
    </Dialog>
  );
}

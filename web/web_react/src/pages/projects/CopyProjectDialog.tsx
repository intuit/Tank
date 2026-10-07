import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useRef, useState, type FormEvent } from 'react';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { useNotify } from '../../notify';
import { useSession } from '../../session';

/** Copies a project under a new name (ProjectBean.saveAs). */
export function CopyProjectDialog({
  project,
  onHide,
  onCopied,
}: {
  project: Schemas['ProjectSummary'];
  onHide: () => void;
  onCopied: (copy: Schemas['ProjectDetail']) => void;
}) {
  const { client } = useSession();
  const notify = useNotify();
  const nameInput = useRef<HTMLInputElement>(null);
  const [name, setName] = useState(`Copy of ${project.name ?? ''}`.slice(0, 255));

  const copy = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.POST('/v2/projects/{projectId}/copy', {
        params: { path: { projectId: project.id! } },
        body: { name: name.trim() },
      });
      if (!data) {
        throw toApiError(error, response, 'copy the project');
      }
      return data;
    },
    onSuccess: (result) => {
      notify.success('Project copied', result.name);
      onCopied(result);
      onHide();
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (name.trim()) {
      copy.mutate();
    }
  };

  return (
    <Dialog header={`Copy ${project.name}`} visible onHide={onHide} className="form-dialog" modal draggable={false}
      onShow={() => nameInput.current?.focus()}>
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="copy-name">New name</label>
        <InputText
          id="copy-name"
          ref={nameInput}
          value={name}
          onChange={(e) => setName(e.target.value)}
          maxLength={255}
          required
          onFocus={(e) => e.target.select()}
        />
        {copy.error && <Message severity="error" text={copy.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Copy" loading={copy.isPending} disabled={!name.trim()} />
        </div>
      </form>
    </Dialog>
  );
}

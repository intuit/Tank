import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { useRef, useState, type FormEvent } from 'react';
import { useNotify } from '../notify';
import { focusOnShow } from './focusOnShow';

/** Copies something under a new name; `copy` makes the call and returns the copy's name */
export function CopyDialog({
  noun,
  name,
  copy,
  onHide,
  onCopied,
}: {
  noun: string;
  name: string | undefined;
  copy: (newName: string) => Promise<{ name?: string }>;
  onHide: () => void;
  onCopied: () => void;
}) {
  const notify = useNotify();
  const [newName, setNewName] = useState(`Copy of ${name ?? ''}`.slice(0, 255));
  const nameInput = useRef<HTMLInputElement>(null);

  const run = useMutation({
    mutationFn: () => copy(newName.trim()),
    onSuccess: (result) => {
      notify.success(`${noun.charAt(0).toUpperCase()}${noun.slice(1)} copied`, result.name);
      onCopied();
      onHide();
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (newName.trim()) {
      run.mutate();
    }
  };

  return (
    <Dialog
      header={`Copy ${name}`}
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(nameInput)}
    >
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="copy-name">New name</label>
        <InputText
          id="copy-name"
          ref={nameInput}
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          maxLength={255}
          required
          onFocus={(e) => e.target.select()}
        />
        {run.error && <Message severity="error" text={run.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Copy" loading={run.isPending} disabled={!newName.trim()} />
        </div>
      </form>
    </Dialog>
  );
}

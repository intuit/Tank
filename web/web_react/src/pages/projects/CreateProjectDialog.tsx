import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { useRef, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router';
import { toApiError } from '../../api/errors';
import { useConfigOptions } from '../../hooks/useConfigOptions';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { focusOnShow } from '../../components/focusOnShow';

/** Creates a project with the default "Main" test plan, then opens it (CreateProjectBean). */
export function CreateProjectDialog({ onHide }: { onHide: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const nameInput = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const [productName, setProductName] = useState<string>();
  const [comments, setComments] = useState('');

  const options = useConfigOptions();
  const create = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.POST('/v2/projects', {
        body: {
          name: name.trim(),
          productName: productName || undefined,
          comments: comments.trim() || undefined,
          // server defaults; the editor sets them
          location: 'unspecified',
          stopBehavior: 'END_OF_SCRIPT_GROUP',
          variables: {},
        },
      });
      const id = data?.['ProjectId'];
      if (!id) {
        throw toApiError(error, response, 'create the project');
      }
      return Number(id);
    },
    onSuccess: (id) => {
      notify.success('Project created', name.trim());
      void queryClient.invalidateQueries({ queryKey: ['projects'] });
      onHide();
      void navigate(`/projects/${id}`);
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (name.trim()) {
      create.mutate();
    }
  };

  return (
    <Dialog header="New project" visible onHide={onHide} className="form-dialog" modal draggable={false}
      onShow={focusOnShow(nameInput)}>
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="project-name">Name</label>
        <InputText
          id="project-name"
          ref={nameInput}
          value={name}
          onChange={(e) => setName(e.target.value)}
          maxLength={255}
          required
        />
        <label htmlFor="project-product">Product</label>
        <Dropdown
          inputId="project-product"
          value={productName}
          options={options.data?.products ?? []}
          optionLabel="label"
          optionValue="value"
          onChange={(e) => setProductName(e.value as string)}
          placeholder="None"
          showClear
          loading={options.isPending}
        />
        <label htmlFor="project-comments">Comments</label>
        <InputTextarea
          id="project-comments"
          value={comments}
          onChange={(e) => setComments(e.target.value)}
          rows={3}
          autoResize
        />
        {create.error && <Message severity="error" text={create.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Create" loading={create.isPending} disabled={!name.trim()} />
        </div>
      </form>
    </Dialog>
  );
}

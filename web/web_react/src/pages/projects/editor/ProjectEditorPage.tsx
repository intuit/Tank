import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Badge } from 'primereact/badge';
import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { TabPanel, TabView } from 'primereact/tabview';
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { Link, useBlocker, useNavigate, useParams } from 'react-router';
import { toApiError } from '../../../api/errors';
import { formatDateTime } from '../../../format';
import { useNotify } from '../../../notify';
import { useSession } from '../../../session';
import { useConfigOptions } from '../../../hooks/useConfigOptions';
import { DataFilesTab } from './DataFilesTab';
import { ScriptsTab } from './ScriptsTab';
import { useProjectDraft } from './useProjectDraft';
import { Field, UsersAndTimesTab } from './UsersAndTimesTab';
import type { ProjectDetail, Section } from './validation';
import { VariablesTab } from './VariablesTab';

const TABS: { section: Section; header: string }[] = [
  { section: 'usersAndTimes', header: 'Users and times' },
  { section: 'scripts', header: 'Scripts' },
  { section: 'dataFiles', header: 'Data files' },
  { section: 'variables', header: 'Variables' },
];

/** The project editor (ProjectBean and projectview.xhtml) */
export function ProjectEditorPage() {
  const projectId = Number(useParams().projectId);
  const { user } = useSession();
  const navigate = useNavigate();
  const notify = useNotify();
  const project = useProjectDraft(projectId);
  const [tab, setTab] = useState(0);
  const [savingAs, setSavingAs] = useState(false);
  const [showProblems, setShowProblems] = useState(false);
  /** Set when leaving is the user's decision already (deleted, or saved as a copy) */
  const leaving = useRef(false);
  const leaveTo = (path: string) => {
    leaving.current = true;
    void navigate(path);
  };

  const { draft, saved, dirty, problems } = project;
  const readOnly = !saved?.permissions?.edit;

  // Leaving with unsaved changes: ask first, inside the app and when closing the tab
  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      dirty && !leaving.current && currentLocation.pathname !== nextLocation.pathname,
  );
  useEffect(() => {
    if (blocker.state === 'blocked') {
      confirmDialog({
        header: 'Unsaved changes',
        message: `Leave ${draft?.name ?? 'this project'} without saving your changes?`,
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Leave without saving',
        rejectLabel: 'Keep editing',
        acceptClassName: 'p-button-danger',
        defaultFocus: 'reject',
        accept: () => blocker.proceed(),
        reject: () => blocker.reset(),
        onHide: () => blocker.state === 'blocked' && blocker.reset(),
      });
    }
  }, [blocker, draft?.name]);
  useEffect(() => {
    if (!dirty) {
      return;
    }
    const warn = (event: BeforeUnloadEvent) => event.preventDefault();
    window.addEventListener('beforeunload', warn);
    return () => window.removeEventListener('beforeunload', warn);
  }, [dirty]);

  if (!Number.isInteger(projectId)) {
    return <Message severity="error" text="That isn't a project ID" />;
  }
  if (project.isLoading || (!draft && !project.loadError)) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (project.loadError || !draft || !saved) {
    return (
      <section>
        <Message severity="error" text={project.loadError?.message ?? 'Project not found'} />
        <p>
          <Link to="/projects">Back to projects</Link>
        </p>
      </section>
    );
  }

  const canChangeOwner = !readOnly && (!!user?.admin || user?.name === saved.owner);
  const problemCount = (section: Section) => problems.filter((p) => p.section === section).length;

  const save = () => {
    if (problems.length) {
      setShowProblems(true);
      const first = TABS.findIndex((t) => problemCount(t.section) > 0);
      if (first >= 0) {
        setTab(first);
      }
      return;
    }
    setShowProblems(false);
    project.save();
  };

  return (
    <section className="editor">
      <p className="breadcrumb">
        <Link to="/projects">Projects</Link> / {saved.name}
      </p>
      <div className="page-header">
        <h1>
          {draft.name || 'Unnamed project'}
          {dirty && <span className="unsaved"> (unsaved)</span>}
        </h1>
        <div className="editor-actions">
          {!readOnly && (
            <>
              <Button label="Revert" icon="pi pi-undo" text disabled={!dirty || project.saving} onClick={project.revert} />
              <Button label="Save as…" icon="pi pi-copy" outlined disabled={project.saving} onClick={() => setSavingAs(true)} />
              <Button label="Save" icon="pi pi-save" disabled={!dirty} loading={project.saving} onClick={save} />
            </>
          )}
          {saved.permissions?.delete && <DeleteProjectButton project={saved} onDeleted={() => leaveTo('/projects')} />}
        </div>
      </div>

      {readOnly && (
        <Message severity="info" text="You can view this project but not change it." className="editor-message" />
      )}
      {showProblems && problems.length > 0 && (
        <Message
          severity="error"
          className="editor-message"
          content={
            <div>
              <strong>Fix these before saving:</strong>
              <ul className="problem-list">
                {problems.map((p) => (
                  <li key={p.message}>{p.message}</li>
                ))}
              </ul>
            </div>
          }
        />
      )}
      {project.saveError && (
        <Message severity="error" text={project.saveError.message} className="editor-message" />
      )}

      <GeneralSection detail={draft} project={project} readOnly={readOnly} canChangeOwner={canChangeOwner} />

      <TabView activeIndex={tab} onTabChange={(e) => setTab(e.index)} className="editor-tabs">
        {TABS.map(({ section, header }) => (
          <TabPanel
            key={section}
            header={header}
            rightIcon={
              showProblems && problemCount(section) > 0 ? (
                <Badge value={problemCount(section)} severity="danger" className="tab-badge" />
              ) : undefined
            }
          >
            {section === 'usersAndTimes' && <UsersAndTimesTab detail={draft} update={project.update} readOnly={readOnly} />}
            {section === 'scripts' && <ScriptsTab detail={draft} update={project.update} readOnly={readOnly} />}
            {section === 'dataFiles' && <DataFilesTab detail={draft} update={project.update} readOnly={readOnly} />}
            {section === 'variables' && <VariablesTab detail={draft} update={project.update} readOnly={readOnly} />}
          </TabPanel>
        ))}
      </TabView>

      <p className="field-help">
        Created {formatDateTime(saved.created)} · Last saved {formatDateTime(saved.modified)}
      </p>

      <Dialog
        header="Someone else saved this project"
        visible={project.conflict}
        onHide={() => undefined}
        closable={false}
        className="form-dialog"
        modal
        draggable={false}
        footer={
          <div className="form-actions">
            <Button
              label="Reload and lose my changes"
              severity="danger"
              outlined
              onClick={() => void project.reload().then(() => notify.success('Project reloaded'))}
            />
          </div>
        }
      >
        <p>
          {saved.name} was changed since you opened it, so your changes weren't saved. Reload it to see the
          latest version, then make your changes again.
        </p>
      </Dialog>

      {savingAs && (
        <SaveAsDialog
          draft={draft}
          dirty={dirty}
          onHide={() => setSavingAs(false)}
          onSaved={(id) => leaveTo(`/projects/${id}`)}
        />
      )}
    </section>
  );
}

function GeneralSection({
  detail,
  project,
  readOnly,
  canChangeOwner,
}: {
  detail: ProjectDetail;
  project: ReturnType<typeof useProjectDraft>;
  readOnly: boolean;
  canChangeOwner: boolean;
}) {
  const { client } = useSession();
  const options = useConfigOptions();
  const owners = useQuery({
    queryKey: ['users', 'names'],
    enabled: canChangeOwner,
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/users/names', { signal });
      if (!data) {
        throw toApiError(error, response, 'load users');
      }
      return data;
    },
    staleTime: 5 * 60_000,
  });
  const products = options.data?.products ?? [];
  // keep a product that's no longer configured selectable
  const productOptions =
    detail.productName && !products.some((p) => p.value === detail.productName)
      ? [...products, { label: detail.productName, value: detail.productName }]
      : products;
  const ownerOptions = [...new Set([detail.owner ?? '', ...(owners.data ?? [])])].filter(Boolean);

  return (
    <div className="form-columns editor-general">
      <Field label="Name" htmlFor="project-name">
        <InputText
          id="project-name"
          value={detail.name ?? ''}
          onChange={(e) => project.update((d) => void (d.name = e.target.value))}
          maxLength={255}
          disabled={readOnly}
        />
      </Field>
      <Field label="Product" htmlFor="project-product">
        <Dropdown
          inputId="project-product"
          value={detail.productName ?? null}
          options={productOptions}
          optionLabel="label"
          optionValue="value"
          onChange={(e) => project.update((d) => void (d.productName = (e.value as string | null) ?? undefined))}
          placeholder="None"
          showClear={!readOnly}
          disabled={readOnly}
        />
      </Field>
      <Field label="Owner" htmlFor="project-owner">
        <Dropdown
          inputId="project-owner"
          value={detail.owner}
          options={ownerOptions}
          onChange={(e) => project.update((d) => void (d.owner = e.value as string))}
          filter={ownerOptions.length > 8}
          disabled={!canChangeOwner}
          tooltip={canChangeOwner || readOnly ? undefined : 'Only the owner or an admin can change the owner'}
          tooltipOptions={{ showOnDisabled: true, position: 'top' }}
        />
      </Field>
      <Field label="Comments" htmlFor="project-comments">
        <InputTextarea
          id="project-comments"
          value={detail.comments ?? ''}
          onChange={(e) => project.update((d) => void (d.comments = e.target.value))}
          rows={2}
          autoResize
          disabled={readOnly}
        />
      </Field>
    </div>
  );
}

/**
 * Saves the project under a new name (ProjectBean.saveAs): copies the stored project, then, if there
 * are unsaved edits, writes them to the copy. The original keeps its stored state.
 */
function SaveAsDialog({
  draft,
  dirty,
  onHide,
  onSaved,
}: {
  draft: ProjectDetail;
  dirty: boolean;
  onHide: () => void;
  onSaved: (id: number) => void;
}) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [name, setName] = useState(`Copy of ${draft.name ?? ''}`.slice(0, 255));
  const nameInput = useRef<HTMLInputElement>(null);

  const saveAs = useMutation({
    mutationFn: async () => {
      const copied = await client.POST('/v2/projects/{projectId}/copy', {
        params: { path: { projectId: draft.id! } },
        body: { name: name.trim() },
      });
      if (!copied.data) {
        throw toApiError(copied.error, copied.response, 'save the copy');
      }
      if (!dirty) {
        return copied.data;
      }
      const copy = copied.data;
      const written = await client.PUT('/v2/projects/{projectId}/full', {
        params: { path: { projectId: copy.id! } },
        body: { ...draft, id: copy.id, name: copy.name, owner: copy.owner, modified: copy.modified },
      });
      if (!written.data) {
        // the copy exists, without the edits
        throw toApiError(written.error, written.response, `save your changes to ${copy.name} (it was created without them)`);
      }
      return written.data;
    },
    onSuccess: (copy) => {
      notify.success('Saved as a new project', copy.name);
      void queryClient.invalidateQueries({ queryKey: ['projects'] });
      onHide();
      onSaved(copy.id!);
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (name.trim()) {
      saveAs.mutate();
    }
  };
  return (
    <Dialog
      header="Save as a new project"
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={() => nameInput.current?.select()}
    >
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="save-as-name">Name</label>
        <InputText id="save-as-name" ref={nameInput} value={name} onChange={(e) => setName(e.target.value)} maxLength={255} required />
        {dirty && <small className="field-help">The new project gets your unsaved changes; this one stays as it was last saved.</small>}
        {saveAs.error && <Message severity="error" text={saveAs.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Save as" loading={saveAs.isPending} disabled={!name.trim()} />
        </div>
      </form>
    </Dialog>
  );
}

function DeleteProjectButton({ project, onDeleted }: { project: ProjectDetail; onDeleted: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const remove = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.DELETE('/v2/projects', {
        params: { query: { ids: [project.id!] } },
      });
      if (!data) {
        throw toApiError(error, response, 'delete the project');
      }
    },
    onSuccess: () => {
      notify.success('Project deleted', project.name);
      queryClient.removeQueries({ queryKey: ['project', project.id] });
      void queryClient.invalidateQueries({ queryKey: ['projects'] });
      onDeleted();
    },
    onError: (error) => notify.error('Project not deleted', error.message),
  });
  return (
    <Button
      icon="pi pi-trash"
      severity="danger"
      text
      aria-label="Delete project"
      tooltip="Delete project"
      tooltipOptions={{ position: 'left' }}
      loading={remove.isPending}
      onClick={() =>
        confirmDialog({
          header: 'Delete project',
          message: `Delete project "${project.name}"? This can't be undone.`,
          icon: 'pi pi-exclamation-triangle',
          acceptLabel: 'Delete',
          rejectLabel: 'Cancel',
          acceptClassName: 'p-button-danger',
          defaultFocus: 'reject',
          accept: () => remove.mutate(),
        })
      }
    />
  );
}

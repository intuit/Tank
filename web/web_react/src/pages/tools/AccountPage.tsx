import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { Password } from 'primereact/password';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useState, type FormEvent } from 'react';
import { toApiError } from '../../api/errors';
import { Field } from '../../components/Field';
import { formatDateTime } from '../../format';
import { PREFERENCES_KEY } from '../../hooks/useTablePreferences';
import { useNotify } from '../../notify';
import { useSession } from '../../session';

/** MeServiceV2Impl.MIN_PASSWORD_LENGTH */
export const MIN_PASSWORD_LENGTH = 8;

/** The signed-in user's account (AccountModify and tools/account.xhtml) */
export function AccountPage() {
  const { client, config } = useSession();
  const me = useQuery({
    queryKey: ['me'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/me', { signal });
      if (!data) throw toApiError(error, response, 'load your account');
      return data;
    },
  });
  if (me.error) return <Message severity="error" text={me.error.message} />;
  if (!me.data) return <ProgressSpinner className="loading" aria-label="Loading" />;
  const user = me.data;

  return (
    <section className="editor">
      <div className="page-header">
        <h1>Your account</h1>
      </div>
      <dl className="validation-summary">
        <dt>User name</dt>
        <dd>{user.name}</dd>
        <dt>Groups</dt>
        <dd>{user.groups?.length ? user.groups.join(', ') : 'None'}</dd>
        <dt>Last signed in</dt>
        <dd>{user.lastLoginTs ? formatDateTime(user.lastLoginTs) : 'Never'}</dd>
      </dl>
      <EmailForm email={user.email ?? ''} />
      <PasswordForm sso={!!config?.ssoEnabled} />
      <ApiToken hasToken={!!user.hasApiToken} />
      <ResetPreferences />
    </section>
  );
}

function EmailForm({ email: saved }: { email: string }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const [email, setEmail] = useState(saved);
  const save = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.PUT('/v2/me', { body: { email: email.trim() } });
      if (!data) throw toApiError(error, response, 'change your email');
      return data;
    },
    onSuccess: (data) => {
      queryClient.setQueryData(['me'], data);
      notify.success('Email changed');
    },
  });
  const valid = /^[^@\s]+@[^@\s]+$/.test(email.trim());
  return (
    <form
      className="account-section"
      onSubmit={(e) => {
        e.preventDefault();
        if (valid) save.mutate();
      }}
    >
      <h2 className="form-section">Email</h2>
      <div className="form-columns">
        <Field label="Email" htmlFor="account-email">
          <InputText
            id="account-email"
            type="email"
            value={email}
            onChange={(e) => {
              save.reset();
              setEmail(e.target.value);
            }}
            maxLength={255}
            invalid={!!email.trim() && !valid}
          />
        </Field>
      </div>
      {save.error && <Message severity="error" text={save.error.message} />}
      <Button type="submit" label="Save email" size="small" disabled={!valid || email.trim() === saved} loading={save.isPending} />
    </form>
  );
}

function PasswordForm({ sso }: { sso: boolean }) {
  const { client } = useSession();
  const notify = useNotify();
  const [current, setCurrent] = useState('');
  const [next, setNext] = useState('');
  const [confirm, setConfirm] = useState('');
  const [problem, setProblem] = useState<string>();
  const save = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.PUT('/v2/me', { body: { currentPassword: current, newPassword: next } });
      if (!data) throw toApiError(error, response, 'change your password');
      return data;
    },
    onSuccess: () => {
      notify.success('Password changed');
      setCurrent('');
      setNext('');
      setConfirm('');
    },
  });
  const submit = (event: FormEvent) => {
    event.preventDefault();
    const issue = !current
      ? 'Enter your current password'
      : next.length < MIN_PASSWORD_LENGTH
        ? `The new password needs at least ${MIN_PASSWORD_LENGTH} characters`
        : next !== confirm
          ? "The new passwords don't match"
          : undefined;
    setProblem(issue);
    if (!issue) save.mutate();
  };
  const input = (id: string, label: string, value: string, set: (v: string) => void, autoComplete: string) => (
    <Field label={label} htmlFor={id}>
      <Password
        inputId={id}
        value={value}
        onChange={(e) => {
          setProblem(undefined);
          save.reset();
          set(e.target.value);
        }}
        feedback={false}
        toggleMask
        autoComplete={autoComplete}
      />
    </Field>
  );
  return (
    <form className="account-section" onSubmit={submit}>
      <h2 className="form-section">Password</h2>
      {sso && <p className="field-help">For signing in with a Tank user name and password. Single sign-on doesn't use it.</p>}
      <div className="form-columns">
        {input('current-password', 'Current password', current, setCurrent, 'current-password')}
        {input('new-password', 'New password', next, setNext, 'new-password')}
        {input('confirm-password', 'Confirm new password', confirm, setConfirm, 'new-password')}
      </div>
      {(problem ?? save.error) && <Message severity="error" text={problem ?? save.error!.message} />}
      <Button type="submit" label="Change password" size="small" loading={save.isPending} disabled={!current && !next} />
    </form>
  );
}

function ApiToken({ hasToken }: { hasToken: boolean }) {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  /** The new token, shown once: the server doesn't hand it out again */
  const [shown, setShown] = useState<string>();
  const refresh = () => queryClient.invalidateQueries({ queryKey: ['me'], exact: true });

  const create = useMutation({
    mutationFn: async () => {
      const { data, error, response } = await client.POST('/v2/me/api-token');
      if (!data?.apiToken) throw toApiError(error, response, 'create an API token');
      return data.apiToken;
    },
    onSuccess: (token) => {
      setShown(token);
      void refresh();
    },
  });
  const remove = useMutation({
    mutationFn: async () => {
      const { error, response } = await client.DELETE('/v2/me/api-token');
      if (!response.ok) throw toApiError(error, response, 'delete the API token');
    },
    onSuccess: () => {
      setShown(undefined);
      notify.success('API token deleted');
      void refresh();
    },
  });
  const confirmReplace = () =>
    confirmDialog({
      header: 'Replace API token',
      message: 'Tools and scripts using the current token will stop working until they use the new one.',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Replace',
      rejectLabel: 'Cancel',
      defaultFocus: 'reject',
      accept: () => create.mutate(),
    });
  const confirmDelete = () =>
    confirmDialog({
      header: 'Delete API token',
      message: 'Tools and scripts using it will stop working.',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptClassName: 'p-button-danger',
      defaultFocus: 'reject',
      accept: () => remove.mutate(),
    });

  return (
    <div className="account-section">
      <h2 className="form-section">API token</h2>
      <p className="field-help">
        Tools, the Jenkins plugin and scripts use it to call Tank as you: <code>Authorization: Bearer &lt;token&gt;</code>
      </p>
      {shown && (
        <Message
          severity="success"
          className="editor-message"
          content={
            <div className="token-shown">
              <span>Copy your token now. Tank won't show it again.</span>
              <code aria-label="New API token">{shown}</code>
              <Button
                label="Copy"
                icon="pi pi-copy"
                size="small"
                outlined
                onClick={() =>
                  void navigator.clipboard
                    ?.writeText(shown)
                    .then(() => notify.success('Token copied'))
                    .catch(() => notify.error('Copy failed', 'Select the token and copy it'))
                }
              />
            </div>
          }
        />
      )}
      {!shown && <p>{hasToken ? 'You have an API token.' : "You don't have an API token."}</p>}
      {(create.error ?? remove.error) && <Message severity="error" text={(create.error ?? remove.error)!.message} />}
      <div className="form-actions-start">
        <Button
          label={hasToken ? 'Replace token' : 'Create token'}
          icon="pi pi-key"
          size="small"
          outlined={hasToken}
          loading={create.isPending}
          onClick={() => (hasToken ? confirmReplace() : create.mutate())}
        />
        {hasToken && (
          <Button label="Delete token" icon="pi pi-trash" size="small" severity="danger" text loading={remove.isPending} onClick={confirmDelete} />
        )}
      </div>
    </div>
  );
}

function ResetPreferences() {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const reset = useMutation({
    mutationFn: async () => {
      const { error, response } = await client.DELETE('/v2/me/preferences');
      if (!response.ok) throw toApiError(error, response, 'reset your preferences');
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: PREFERENCES_KEY });
      notify.success('Preferences reset');
    },
    onError: (error) => notify.error('Preferences not reset', error.message),
  });
  return (
    <div className="account-section">
      <h2 className="form-section">Preferences</h2>
      <p className="field-help">Puts every table's columns and widths back to Tank's defaults.</p>
      <Button
        label="Reset preferences"
        icon="pi pi-refresh"
        size="small"
        outlined
        loading={reset.isPending}
        onClick={() =>
          confirmDialog({
            header: 'Reset preferences',
            message: 'Put every table back to its default columns and widths?',
            acceptLabel: 'Reset',
            rejectLabel: 'Cancel',
            defaultFocus: 'reject',
            accept: () => reset.mutate(),
          })
        }
      />
    </div>
  );
}

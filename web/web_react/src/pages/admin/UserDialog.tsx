import { useMutation, useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { MultiSelect } from 'primereact/multiselect';
import { Password } from 'primereact/password';
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { toApiError } from '../../api/errors';
import { focusOnShow } from '../../components/focusOnShow';
import { formatDateTime } from '../../format';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { MIN_PASSWORD_LENGTH } from '../tools/AccountPage';
import type { AdminUser } from './AdminUsersPage';

/** Adds or edits a user (UserEdit and admin/useredit.xhtml) */
export function UserDialog({ user, onHide, onSaved }: { user?: AdminUser; onHide: () => void; onSaved: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const first = useRef<HTMLInputElement>(null);
  const isNew = !user;
  const [name, setName] = useState(user?.name ?? '');
  const [email, setEmail] = useState(user?.email ?? '');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [groups, setGroups] = useState<string[]>(user?.groups ?? []);
  const [problem, setProblem] = useState<string>();
  /** A token generated in this dialog, shown once */
  const [token, setToken] = useState<string>();
  const [hasToken, setHasToken] = useState(!!user?.hasApiToken);

  const allGroups = useQuery({
    queryKey: ['admin-groups'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/admin/groups', { signal });
      if (!data) throw toApiError(error, response, 'load the groups');
      return data;
    },
    staleTime: Infinity,
  });
  // a new user starts in the default groups, as UserEdit does
  useEffect(() => {
    if (isNew && allGroups.data && groups.length === 0) {
      setGroups(allGroups.data.filter((g) => g.isDefault).map((g) => g.name!));
    }
  }, [allGroups.data]);

  const save = useMutation({
    mutationFn: async () => {
      const body = {
        email: email.trim(),
        groups,
        password: password || undefined,
      };
      const { data, error, response } = isNew
        ? await client.POST('/v2/admin/users', { body: { ...body, name: name.trim() } })
        : await client.PUT('/v2/admin/users/{userId}', { params: { path: { userId: user.id! } }, body });
      if (!data) throw toApiError(error, response, isNew ? 'create the user' : 'save the user');
      return data;
    },
    onSuccess: (saved) => {
      notify.success(isNew ? `Created ${saved.name}` : `Saved ${saved.name}`);
      onSaved();
      onHide();
    },
  });
  const tokenAction = useMutation({
    mutationFn: async (action: 'create' | 'delete') => {
      const path = { params: { path: { userId: user!.id! } } };
      if (action === 'create') {
        const { data, error, response } = await client.POST('/v2/admin/users/{userId}/api-token', path);
        if (!data?.apiToken) throw toApiError(error, response, 'create the API token');
        return data.apiToken;
      }
      const { error, response } = await client.DELETE('/v2/admin/users/{userId}/api-token', path);
      if (!response.ok) throw toApiError(error, response, 'delete the API token');
      return undefined;
    },
    onSuccess: (created) => {
      setToken(created);
      setHasToken(!!created);
      onSaved();
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    const issue =
      isNew && !name.trim()
        ? 'Name is required'
        : !/^[^@\s]+@[^@\s]+$/.test(email.trim())
          ? 'A valid email address is required'
          : isNew && !password
            ? 'A new user needs a password'
            : password && password.length < MIN_PASSWORD_LENGTH
              ? `The password needs at least ${MIN_PASSWORD_LENGTH} characters`
              : password !== confirm
                ? "The passwords don't match"
                : undefined;
    setProblem(issue);
    if (!issue) save.mutate();
  };
  const error = problem ?? save.error?.message;

  return (
    <Dialog
      header={isNew ? 'New user' : `Edit ${user.name}`}
      visible
      onHide={onHide}
      className="form-dialog"
      modal
      draggable={false}
      onShow={focusOnShow(first)}
    >
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="user-name">Name</label>
        <InputText
          id="user-name"
          ref={isNew ? first : undefined}
          value={name}
          onChange={(e) => setName(e.target.value)}
          maxLength={255}
          disabled={!isNew}
          autoComplete="off"
        />
        {!isNew && <small className="field-help">A user's name can't change: it's how Tank records what they own.</small>}
        <label htmlFor="user-email">Email</label>
        <InputText id="user-email" ref={isNew ? undefined : first} type="email" value={email} onChange={(e) => setEmail(e.target.value)} maxLength={255} />
        <label htmlFor="user-password">{isNew ? 'Password' : 'New password'}</label>
        <Password
          inputId="user-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          feedback={false}
          toggleMask
          autoComplete="new-password"
          placeholder={isNew ? undefined : 'Unchanged'}
        />
        <label htmlFor="user-confirm">Confirm password</label>
        <Password inputId="user-confirm" value={confirm} onChange={(e) => setConfirm(e.target.value)} feedback={false} toggleMask autoComplete="new-password" />
        <label htmlFor="user-groups">Groups</label>
        <MultiSelect
          inputId="user-groups"
          value={groups}
          options={(allGroups.data ?? []).map((g) => ({ label: g.isDefault ? `${g.name} (default)` : g.name, value: g.name }))}
          onChange={(e) => setGroups(e.value as string[])}
          display="chip"
          className="wrap-chips"
          placeholder="No groups"
        />
        {!isNew && (
          <>
            <span className="form-label">API token</span>
            <div className="user-token">
              {token ? (
                <div className="token-shown">
                  <span>Give the user this token now; Tank won't show it again.</span>
                  <code aria-label="New API token">{token}</code>
                </div>
              ) : (
                <span>{hasToken ? `Has a token${user.apiTokenHint ? ` ending ${user.apiTokenHint}` : ''}` : 'No token'}</span>
              )}
              <div className="form-actions-start">
                <Button
                  type="button"
                  label={hasToken ? 'Replace token' : 'Create token'}
                  size="small"
                  outlined
                  loading={tokenAction.isPending && tokenAction.variables === 'create'}
                  onClick={() => tokenAction.mutate('create')}
                />
                {hasToken && (
                  <Button
                    type="button"
                    label="Delete token"
                    size="small"
                    text
                    severity="danger"
                    loading={tokenAction.isPending && tokenAction.variables === 'delete'}
                    onClick={() => tokenAction.mutate('delete')}
                  />
                )}
              </div>
              {tokenAction.error && <Message severity="error" text={tokenAction.error.message} />}
            </div>
            <span className="form-label">Last signed in</span>
            <span>{user.lastLoginTs ? formatDateTime(user.lastLoginTs) : 'Never'}</span>
          </>
        )}
        {error && <Message severity="error" text={error} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label={isNew ? 'Create' : 'Save'} loading={save.isPending} />
        </div>
      </form>
    </Dialog>
  );
}

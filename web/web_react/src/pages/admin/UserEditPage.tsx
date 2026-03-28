import { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Toolbar } from 'primereact/toolbar';
import { PickList } from 'primereact/picklist';
import { Toast } from 'primereact/toast';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Divider } from 'primereact/divider';
import { useUser, useAllGroups, useCreateUser, useUpdateUser, useGenerateToken, useDeleteToken } from '../../hooks/useUsers';

export function UserEditPage() {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new';
  const userId = isNew ? 0 : parseInt(id!, 10);

  const navigate = useNavigate();
  const toast = useRef<Toast>(null);

  const { data: existingUser, isLoading: loadingUser } = useUser(userId);
  const { data: allGroups = [], isLoading: loadingGroups } = useAllGroups();
  const createUser = useCreateUser();
  const updateUser = useUpdateUser();
  const generateToken = useGenerateToken();
  const deleteToken = useDeleteToken();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [apiToken, setApiToken] = useState<string | null>(null);
  const [lastLoginTs, setLastLoginTs] = useState<string | null>(null);
  // availableGroups = groups not assigned; assignedGroups = groups assigned
  const [availableGroups, setAvailableGroups] = useState<string[]>([]);
  const [assignedGroups, setAssignedGroups] = useState<string[]>([]);

  useEffect(() => {
    if (!isNew && existingUser) {
      setName(existingUser.name);
      setEmail(existingUser.email);
      setApiToken(existingUser.apiToken);
      setLastLoginTs(existingUser.lastLoginTs);
      const assigned = existingUser.groups;
      setAssignedGroups(assigned);
      setAvailableGroups(allGroups.filter((g) => !assigned.includes(g)));
    }
  }, [isNew, existingUser]);

  useEffect(() => {
    if (isNew) {
      setAvailableGroups(allGroups);
      setAssignedGroups([]);
    }
  }, [isNew, allGroups]);

  const handleSave = async () => {
    if (!password && isNew) {
      toast.current?.show({ severity: 'error', summary: 'Password is required for new users.' });
      return;
    }
    if (password && password !== passwordConfirm) {
      toast.current?.show({ severity: 'error', summary: 'Passwords do not match.' });
      return;
    }
    try {
      if (isNew) {
        await createUser.mutateAsync({
          name,
          email,
          password,
          groups: assignedGroups,
        });
        toast.current?.show({ severity: 'success', summary: `User "${name}" created.` });
      } else {
        await updateUser.mutateAsync({
          id: userId,
          request: {
            email,
            ...(password ? { password } : {}),
            groups: assignedGroups,
          },
        });
        toast.current?.show({ severity: 'success', summary: `User "${name}" updated.` });
      }
      navigate('/admin/users');
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to save user.' });
    }
  };

  const handleGenerateToken = async () => {
    try {
      const res = await generateToken.mutateAsync(userId);
      setApiToken(res.data.apiToken);
      toast.current?.show({ severity: 'success', summary: 'API token generated.' });
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to generate token.' });
    }
  };

  const handleDeleteToken = async () => {
    try {
      await deleteToken.mutateAsync(userId);
      setApiToken(null);
      toast.current?.show({ severity: 'success', summary: 'API token deleted.' });
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to delete token.' });
    }
  };

  if (!isNew && loadingUser) return <ProgressSpinner />;
  if (!isNew && !existingUser) return <Message severity="error" text="User not found." />;
  if (loadingGroups) return <ProgressSpinner />;

  const title = isNew ? 'New User' : `Edit User: ${name}`;

  const leftToolbar = <span className="font-bold text-xl">{title}</span>;
  const rightToolbar = (
    <div className="flex gap-2">
      <Button label="Save" icon="pi pi-check" onClick={handleSave} />
      <Button label="Cancel" icon="pi pi-times" severity="secondary" outlined onClick={() => navigate('/admin/users')} />
    </div>
  );

  return (
    <div>
      <Toast ref={toast} />
      <Toolbar start={leftToolbar} end={rightToolbar} className="mb-4" />

      <div className="grid">
        <div className="col-12 md:col-6">
          <div className="p-fluid">
            <div className="field">
              <label htmlFor="name" className="font-semibold">Login Name *</label>
              <InputText
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                disabled={!isNew}
                required
              />
            </div>

            <div className="field">
              <label htmlFor="email" className="font-semibold">Email *</label>
              <InputText
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="field">
              <label htmlFor="password" className="font-semibold">
                {isNew ? 'Password *' : 'New Password'}
              </label>
              <Password
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                feedback={false}
                toggleMask
              />
            </div>

            <div className="field">
              <label htmlFor="passwordConfirm" className="font-semibold">
                {isNew ? 'Confirm Password *' : 'Confirm New Password'}
              </label>
              <Password
                id="passwordConfirm"
                value={passwordConfirm}
                onChange={(e) => setPasswordConfirm(e.target.value)}
                feedback={false}
                toggleMask
              />
            </div>

            {!isNew && (
              <>
                <Divider />
                <div className="field">
                  <label className="font-semibold">API Token</label>
                  <div className="flex gap-2 align-items-center mt-1">
                    <InputText
                      value={apiToken ?? ''}
                      readOnly
                      className="flex-1 font-mono text-sm"
                      placeholder="No token"
                    />
                    <Button
                      label="Generate"
                      icon="pi pi-key"
                      size="small"
                      disabled={!!apiToken}
                      onClick={handleGenerateToken}
                    />
                    <Button
                      label="Delete"
                      icon="pi pi-trash"
                      size="small"
                      severity="danger"
                      outlined
                      disabled={!apiToken}
                      onClick={handleDeleteToken}
                    />
                  </div>
                </div>

                <div className="field">
                  <label className="font-semibold">Last Login</label>
                  <InputText
                    value={lastLoginTs ? new Date(lastLoginTs).toLocaleString() : '—'}
                    readOnly
                  />
                </div>
              </>
            )}
          </div>
        </div>

        <div className="col-12">
          <Divider />
          <label className="font-semibold block mb-2">Groups</label>
          <PickList
            dataKey="name"
            source={availableGroups.map((g) => ({ name: g }))}
            target={assignedGroups.map((g) => ({ name: g }))}
            onChange={(e) => {
              setAvailableGroups(e.source.map((g: { name: string }) => g.name));
              setAssignedGroups(e.target.map((g: { name: string }) => g.name));
            }}
            sourceHeader="Available"
            targetHeader="Assigned"
            itemTemplate={(item: { name: string }) => <span>{item.name}</span>}
            showSourceControls={false}
            showTargetControls={false}
          />
        </div>
      </div>
    </div>
  );
}

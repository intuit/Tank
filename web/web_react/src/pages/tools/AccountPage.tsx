import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useAuth } from '../../context/AuthContext';
import { usersApi } from '../../api/users';
import type { UserTO, UserRequest } from '../../types/user';

// NOTE: The UserInfo shape from AuthContext only provides `username` and `roles`.
// A /v2/users/me endpoint is not yet available. This page fetches the full user
// record by matching username from the users list. Once a /v2/users/me endpoint
// is added to the backend, replace the lookup below with a direct call.

export function AccountPage() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [userData, setUserData] = useState<UserTO | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [loadingUser, setLoadingUser] = useState(true);

  const [email, setEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [saving, setSaving] = useState(false);

  const [tokenVisible, setTokenVisible] = useState(false);
  const [copySuccess, setCopySuccess] = useState(false);

  useEffect(() => {
    async function fetchUser() {
      if (!user?.username) {
        setLoadingUser(false);
        return;
      }
      try {
        const res = await usersApi.getAll();
        const found = (res.data as UserTO[]).find((u) => u.name === user.username) ?? null;
        setUserData(found);
        if (found) setEmail(found.email ?? '');
      } catch (e: unknown) {
        const msg = e instanceof Error ? e.message : 'Failed to load user data.';
        setLoadError(msg);
      } finally {
        setLoadingUser(false);
      }
    }
    fetchUser();
  }, [user?.username]);

  async function handleSave() {
    if (!userData) return;
    if (newPassword && newPassword !== confirmPassword) {
      setSaveError('Passwords do not match.');
      return;
    }
    setSaving(true);
    setSaveError(null);
    setSaveSuccess(false);
    try {
      const request: UserRequest = { email };
      if (newPassword) request.password = newPassword;
      await usersApi.update(userData.id, request);
      setNewPassword('');
      setConfirmPassword('');
      setSaveSuccess(true);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : 'Failed to save changes.';
      setSaveError(msg);
    } finally {
      setSaving(false);
    }
  }

  function handleCopyToken() {
    if (!userData?.apiToken) return;
    navigator.clipboard.writeText(userData.apiToken).then(() => {
      setCopySuccess(true);
      setTimeout(() => setCopySuccess(false), 2000);
    });
  }

  const maskedToken = userData?.apiToken
    ? userData.apiToken.slice(0, 6) + '••••••••••••••••••••' + userData.apiToken.slice(-4)
    : null;

  return (
    <div>
      <div className="flex align-items-center gap-2 mb-3">
        <Button
          icon="pi pi-arrow-left"
          severity="secondary"
          text
          onClick={() => navigate('/tools')}
          aria-label="Back to Tools"
        />
        <h2 className="m-0">Account Settings</h2>
      </div>

      {loadingUser && (
        <div className="flex justify-content-center mt-4">
          <ProgressSpinner style={{ width: '48px', height: '48px' }} />
        </div>
      )}

      {loadError && (
        <Message severity="error" text={loadError} style={{ width: '100%' }} className="mb-3" />
      )}

      {!loadingUser && !loadError && (
        <div className="grid">
          <div className="col-12 md:col-6">
            <Card title="Profile">
              <div className="flex flex-column gap-3">
                <div className="flex flex-column gap-1">
                  <label htmlFor="acc-username" className="font-semibold">
                    Username
                  </label>
                  <InputText id="acc-username" value={user?.username ?? ''} disabled />
                </div>

                <div className="flex flex-column gap-1">
                  <label htmlFor="acc-email" className="font-semibold">
                    Email
                  </label>
                  <InputText
                    id="acc-email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="email@example.com"
                  />
                </div>

                <div className="flex flex-column gap-1">
                  <label htmlFor="acc-new-password" className="font-semibold">
                    New Password
                  </label>
                  <Password
                    inputId="acc-new-password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    feedback={false}
                    toggleMask
                    placeholder="Leave blank to keep current"
                    style={{ width: '100%' }}
                    inputStyle={{ width: '100%' }}
                  />
                </div>

                <div className="flex flex-column gap-1">
                  <label htmlFor="acc-confirm-password" className="font-semibold">
                    Confirm Password
                  </label>
                  <Password
                    inputId="acc-confirm-password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    feedback={false}
                    toggleMask
                    placeholder="Re-enter new password"
                    style={{ width: '100%' }}
                    inputStyle={{ width: '100%' }}
                  />
                </div>

                {saveError && <Message severity="error" text={saveError} />}
                {saveSuccess && <Message severity="success" text="Changes saved successfully." />}

                <div className="flex gap-2">
                  <Button
                    label="Save Changes"
                    icon="pi pi-save"
                    onClick={handleSave}
                    loading={saving}
                    disabled={!userData}
                  />
                  <Button
                    label="Back to Tools"
                    icon="pi pi-arrow-left"
                    severity="secondary"
                    outlined
                    onClick={() => navigate('/tools')}
                  />
                </div>
              </div>
            </Card>
          </div>

          <div className="col-12 md:col-6">
            <Card title="API Token">
              {userData?.apiToken ? (
                <div className="flex flex-column gap-3">
                  <p className="m-0 text-color-secondary">
                    Your API token is used to authenticate programmatic access to Tank.
                  </p>
                  <div className="flex align-items-center gap-2">
                    <InputText
                      value={tokenVisible ? userData.apiToken : (maskedToken ?? '')}
                      readOnly
                      style={{ flex: 1, fontFamily: 'monospace' }}
                    />
                    <Button
                      icon={tokenVisible ? 'pi pi-eye-slash' : 'pi pi-eye'}
                      severity="secondary"
                      outlined
                      onClick={() => setTokenVisible((v) => !v)}
                      aria-label={tokenVisible ? 'Hide token' : 'Show token'}
                    />
                    <Button
                      icon={copySuccess ? 'pi pi-check' : 'pi pi-copy'}
                      severity={copySuccess ? 'success' : 'secondary'}
                      outlined
                      onClick={handleCopyToken}
                      aria-label="Copy token"
                    />
                  </div>
                </div>
              ) : (
                <p className="text-color-secondary m-0">No API token assigned to this account.</p>
              )}
            </Card>
          </div>
        </div>
      )}
    </div>
  );
}

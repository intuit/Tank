import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { Divider } from 'primereact/divider';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { Password } from 'primereact/password';
import { useState, type FormEvent } from 'react';
import { Navigate, useLocation } from 'react-router';
import { contextPath } from '../api/client';
import { useSession } from '../session';

export function LoginPage() {
  const { config, user, signIn } = useSession();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? '/';
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string>();
  const [busy, setBusy] = useState(false);

  if (user) {
    return <Navigate to={from} replace />;
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    setBusy(true);
    setError(await signIn(username, password));
    setBusy(false);
  }

  // returnTo is relative to the context path; the SPA lives under /app
  const ssoHref = `${contextPath()}/v2/auth/sso/authorize?returnTo=${encodeURIComponent('/app' + from)}`;

  return (
    <Card title={<h1 className="login-title">Sign in</h1>} className="login">
      <form onSubmit={(e) => void submit(e)} className="login-form">
        <label htmlFor="username">Username</label>
        <InputText
          id="username"
          autoComplete="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <label htmlFor="password">Password</label>
        <Password
          inputId="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          feedback={false}
          toggleMask
          required
        />
        {error && <Message severity="error" text={error} />}
        <Button type="submit" label="Sign in" loading={busy} />
      </form>
      {config?.ssoEnabled && (
        <>
          <Divider align="center">or</Divider>
          <a href={ssoHref} className="p-button p-button-outlined sso">
            Sign in with SSO
          </a>
        </>
      )}
    </Card>
  );
}

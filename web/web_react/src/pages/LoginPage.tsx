import { useState, FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { Message } from 'primereact/message';
import { Divider } from 'primereact/divider';
import { useAuth } from '../context/AuthContext';
import { getSsoRedirectUrl } from '../api/auth';
import '../assets/TankOverides.css';
import {TopNavBar} from "../components/layout/TopNavBar.tsx";

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [ssoLoading, setSsoLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSsoLogin = async () => {
    setSsoLoading(true);
    try {
      const redirectUrl = await getSsoRedirectUrl();
      window.location.href = redirectUrl;
    } catch {
      setError('SSO is not configured on this server.');
      setSsoLoading(false);
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username || !password) return;
    setLoading(true);
    setError('');
    try {
      await login({ username, password });
      navigate('/projects');
    } catch {
      setError('Invalid username or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="flex flex-column" style={{ minHeight: '100vh' }}>
        <TopNavBar />
        <div className="flex justify-content-center align-items-center" style={{ minHeight: '80vh', padding: '5rem' }}>
          <Card
            style={{ width: '360px'}}
            className="ui-tank-theme"
          >
            <form onSubmit={handleSubmit} className="flex flex-column gap-3">
              {error && <Message severity="error" text={error} />}
              <Button
                  type="button"
                  label={ssoLoading ? 'Redirecting…' : 'Login with SSO'}
                  icon="pi pi-shield"
                  disabled={ssoLoading}
                  onClick={handleSsoLogin}
              />
              <Divider align="center"><span className="text-color-secondary text-sm">or</span></Divider>
              <div className="flex flex-column gap-1">
                <label htmlFor="username">Username</label>
                <InputText
                  id="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  autoFocus
                  autoComplete="username"
                />
              </div>
              <div className="flex flex-column gap-1 p-fluid">
                <label htmlFor="password">Password</label>
                <Password
                  inputId="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  feedback={false}
                  toggleMask
                  autoComplete="current-password"
                  inputClassName="w-full"
                />
              </div>
              <Button
                type="submit"
                label={loading ? 'Logging in…' : 'Login'}
                icon="pi pi-sign-in"
                disabled={loading || !username || !password}
                className="mt-2"
              />
            </form>
          </Card>
        </div>
      </div>
  );
}

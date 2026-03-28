import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { Message } from 'primereact/message';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/auth';
import tankLogo from '../assets/TankLogo.svg';

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) return;
    setLoading(true);
    setError('');
    try {
      const res = await authApi.login({ username, password });
      login(res.data);
      navigate('/projects');
    } catch {
      setError('Invalid username or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card
        header={<img src={tankLogo} alt="Intuit Tank" style={{ height: '48px', margin: '1.5rem auto 0', display: 'block' }} />}
        style={{ width: '360px' }}
      >
        <form onSubmit={handleSubmit} className="flex flex-column gap-3">
          {error && <Message severity="error" text={error} />}
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
          <div className="flex flex-column gap-1">
            <label htmlFor="password">Password</label>
            <Password
              inputId="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              feedback={false}
              toggleMask
              autoComplete="current-password"
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
  );
}

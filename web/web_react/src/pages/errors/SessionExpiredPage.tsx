import { useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';

export function SessionExpiredPage() {
  const navigate = useNavigate();
  return (
    <div className="flex flex-column align-items-center justify-content-center" style={{ minHeight: '60vh', gap: '1rem' }}>
      <i className="pi pi-clock text-6xl text-500" />
      <h2 className="m-0">Session Expired</h2>
      <Message severity="warn" text="Your session has expired. Please log in again to continue." />
      <Button
        label="Log In"
        icon="pi pi-sign-in"
        onClick={() => navigate('/login', { replace: true })}
      />
    </div>
  );
}

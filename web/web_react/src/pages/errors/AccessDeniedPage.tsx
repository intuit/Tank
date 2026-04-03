import { useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';

export function AccessDeniedPage() {
  const navigate = useNavigate();
  return (
    <div className="flex flex-column align-items-center justify-content-center" style={{ minHeight: '60vh', gap: '1rem' }}>
      <i className="pi pi-ban text-6xl text-red-400" />
      <h2 className="m-0">Access Denied</h2>
      <p className="text-500 m-0">You do not have permission to view this page.</p>
      <Button
        label="Go to Projects"
        icon="pi pi-home"
        onClick={() => navigate('/projects', { replace: true })}
      />
    </div>
  );
}

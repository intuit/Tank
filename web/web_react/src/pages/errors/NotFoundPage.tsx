import { useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';

export function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="flex flex-column align-items-center justify-content-center" style={{ minHeight: '60vh', gap: '1rem' }}>
      <i className="pi pi-exclamation-circle text-6xl text-500" />
      <h2 className="m-0">Page Not Found</h2>
      <p className="text-500 m-0">The page you requested does not exist.</p>
      <Button
        label="Go to Projects"
        icon="pi pi-home"
        onClick={() => navigate('/projects', { replace: true })}
      />
    </div>
  );
}

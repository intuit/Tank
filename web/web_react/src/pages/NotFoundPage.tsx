import { Button } from 'primereact/button';
import { useNavigate } from 'react-router';

export function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <section>
      <h1>Page not found</h1>
      <Button label="Back to home" icon="pi pi-home" text onClick={() => void navigate('/')} />
    </section>
  );
}

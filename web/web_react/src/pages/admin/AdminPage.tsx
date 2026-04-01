import { useNavigate } from 'react-router-dom';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';

export function AdminPage() {
  const navigate = useNavigate();
  return (
    <div>
      <h2>Admin</h2>
      <div className="grid">
        <div className="col-12 md:col-4">
          <Card title="User Management">
            <p>Manage users and permissions.</p>
            <Button
              label="Manage Users"
              icon="pi pi-users"
              onClick={() => navigate('/admin/users')}
            />
          </Card>
        </div>
        <div className="col-12 md:col-4">
          <Card title="User Groups">
            <p>View groups and their members.</p>
            <Button
              label="Manage Groups"
              icon="pi pi-sitemap"
              severity="secondary"
              onClick={() => navigate('/admin/groups')}
            />
          </Card>
        </div>
        <div className="col-12 md:col-4">
          <Card title="Server Logs">
            <p>View server logs.</p>
            <Button
              label="View Server Logs"
              icon="pi pi-file-edit"
              onClick={() => navigate('/admin/logs')}
            />
          </Card>
        </div>
      </div>
    </div>
  );
}

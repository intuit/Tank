import { Card } from 'primereact/card';
import { Button } from 'primereact/button';

export function AdminPage() {
  return (
    <div>
      <h2>Admin</h2>
      <div className="grid">
        <div className="col-12 md:col-4">
          <Card title="User Management">
            <p>Manage users and permissions.</p>
            <a href="/admin/users.jsf">
              <Button label="Open in Classic UI" icon="pi pi-external-link" severity="secondary" outlined />
            </a>
          </Card>
        </div>
        <div className="col-12 md:col-4">
          <Card title="Server Logs">
            <p>View server logs.</p>
            <a href="/admin/logs.jsf">
              <Button label="Open in Classic UI" icon="pi pi-external-link" severity="secondary" outlined />
            </a>
          </Card>
        </div>
      </div>
    </div>
  );
}

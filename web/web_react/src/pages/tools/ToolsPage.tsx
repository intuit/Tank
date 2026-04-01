import { useNavigate } from 'react-router-dom';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';

export function ToolsPage() {
  const navigate = useNavigate();
  return (
    <div>
      <h2>Tools</h2>
      <div className="grid">
        <div className="col-12 md:col-4">
          <Card title="Script Debugger">
            <p>Download the Tank Debugger JAR to run scripts locally.</p>
            <a href="/tools/Tank-Debugger-all.jar" download>
              <Button label="Download Debugger" icon="pi pi-download" severity="secondary" />
            </a>
          </Card>
        </div>
        <div className="col-12 md:col-4">
          <Card title="Script Runner">
            <p>Download the Tank Script Runner tool.</p>
            <a href="/tools/Tank-Script-Runner-all.jar" download>
              <Button label="Download Script Runner" icon="pi pi-download" severity="secondary" />
            </a>
          </Card>
        </div>
        <div className="col-12 md:col-4">
          <Card title="Account Settings">
            <p>Manage your Tank account settings and API token.</p>
            <Button
              label="Account Settings"
              icon="pi pi-user-edit"
              onClick={() => navigate('/tools/account')}
            />
          </Card>
        </div>
      </div>
    </div>
  );
}

import { useParams, useNavigate, Link } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { useScript } from '../../hooks/useScripts';
import { scriptsApi } from '../../api/scripts';

export function ScriptDetailPage() {
  const { id } = useParams<{ id: string }>();
  const scriptId = Number(id);
  const navigate = useNavigate();
  const { data: script, isLoading, error } = useScript(scriptId);

  if (isLoading) return <ProgressSpinner />;
  if (error || !script) return <Message severity="error" text="Script not found." />;

  const handleDownload = async () => {
    const res = await scriptsApi.download(scriptId);
    const url = URL.createObjectURL(new Blob([res.data as BlobPart]));
    const a = document.createElement('a');
    a.href = url;
    a.download = `${script.name}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="flex flex-column gap-3">
      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/scripts')} />
        <h2 className="m-0">{script.name}</h2>
      </div>

      <Card title="Details">
        <div className="grid">
          <div className="col-4 font-bold">Product</div>
          <div className="col-8">{script.productName ?? '—'}</div>
          <div className="col-4 font-bold">Owner</div>
          <div className="col-8">{script.creator ?? '—'}</div>
          <div className="col-4 font-bold">Runtime</div>
          <div className="col-8">{script.runtime != null ? `${script.runtime} ms` : '—'}</div>
          <div className="col-4 font-bold">Modified</div>
          <div className="col-8">{script.modified ?? '—'}</div>
        </div>
      </Card>

      <div className="flex gap-2">
        <Link to={`/scripts/${scriptId}/edit`}>
          <Button label="Edit Script" icon="pi pi-pencil" />
        </Link>
        <Button label="Download XML" icon="pi pi-download" severity="secondary" onClick={handleDownload} />
      </div>
    </div>
  );
}

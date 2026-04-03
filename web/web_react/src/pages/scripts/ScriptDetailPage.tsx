import { useState, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { useScript, useUpdateScript } from '../../hooks/useScripts';
import { scriptsApi } from '../../api/scripts';

export function ScriptDetailPage() {
  const { id } = useParams<{ id: string }>();
  const scriptId = Number(id);
  const navigate = useNavigate();
  const { data: script, isLoading, error } = useScript(scriptId);
  const updateScript = useUpdateScript(scriptId);
  const toast = useRef<Toast>(null);

  const [showEdit, setShowEdit] = useState(false);
  const [editProduct, setEditProduct] = useState('');
  const [editComments, setEditComments] = useState('');

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

  const openEdit = () => {
    setEditProduct(script.productName ?? '');
    setEditComments(script.comments ?? '');
    setShowEdit(true);
  };

  const handleSaveEdit = async () => {
    try {
      await updateScript.mutateAsync({
        productName: editProduct.trim() || undefined,
        comments: editComments.trim() || undefined,
      });
      toast.current?.show({ severity: 'success', summary: 'Script updated' });
      setShowEdit(false);
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Failed to update script' });
    }
  };

  return (
    <div className="flex flex-column gap-3">
      <Toast ref={toast} />

      {/* Edit Details Dialog */}
      <Dialog
        header="Edit Script Details"
        visible={showEdit}
        style={{ width: '420px' }}
        onHide={() => setShowEdit(false)}
        footer={
          <div className="flex justify-content-end gap-2">
            <Button label="Cancel" severity="secondary" onClick={() => setShowEdit(false)} />
            <Button label="Save" icon="pi pi-check" onClick={handleSaveEdit} loading={updateScript.isPending} />
          </div>
        }
      >
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label htmlFor="editProduct" className="font-bold">Product</label>
            <InputText
              id="editProduct"
              value={editProduct}
              onChange={(e) => setEditProduct(e.target.value)}
              placeholder="Product name (optional)"
              autoFocus
            />
          </div>
          <div className="flex flex-column gap-1">
            <label htmlFor="editComments" className="font-bold">Comments</label>
            <InputTextarea
              id="editComments"
              value={editComments}
              onChange={(e) => setEditComments(e.target.value)}
              placeholder="Comments (optional)"
              rows={4}
              autoResize
            />
          </div>
        </div>
      </Dialog>

      <div className="flex align-items-center gap-2">
        <Button icon="pi pi-arrow-left" rounded text onClick={() => navigate('/scripts')} />
        <h2 className="m-0">{script.name}</h2>
      </div>

      <Card title="Details">
        <div className="grid">
          <div className="col-4 font-bold">Product</div>
          <div className="col-8">{script.productName ?? '—'}</div>
          <div className="col-4 font-bold">Comments</div>
          <div className="col-8">{script.comments ?? '—'}</div>
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
        <Link to={`/scripts/${scriptId}/filters`}>
          <Button label="Apply Filters" icon="pi pi-filter" severity="secondary" />
        </Link>
        <Button label="Edit Details" icon="pi pi-sliders-h" severity="secondary" onClick={openEdit} />
        <Button label="Download XML" icon="pi pi-download" severity="secondary" onClick={handleDownload} />
      </div>
    </div>
  );
}

import { useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { useState } from 'react';
import { contextPath, type Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { EntityList } from '../../components/EntityList';
import { ownedColumns, RowAction, RowDownload } from '../../components/entityColumns';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { PreviewDialog } from './PreviewDialog';
import { ReplaceDialog, UploadDialog } from './UploadDialogs';

type DataFileSummary = Schemas['DataFileSummary'];

/** Sort fields the server accepts (DataFileServiceV2Impl) */
const SORT_FIELDS = ['id', 'name', 'owner', 'created', 'modified'];

/** The data files list (DataFileBrowser and datafiles/index.xhtml) */
export function DataFilesPage() {
  const { client, user } = useSession();
  const queryClient = useQueryClient();
  const [uploading, setUploading] = useState(false);
  const [previewing, setPreviewing] = useState<DataFileSummary>();
  const [replacing, setReplacing] = useState<DataFileSummary>();
  const canCreate = hasRight(user, 'CREATE_DATAFILE');
  const canDelete = (f: DataFileSummary) => hasRightOrOwns(user, 'DELETE_DATAFILE', f.owner);
  const canEdit = (f: DataFileSummary) => hasRightOrOwns(user, 'EDIT_DATAFILE', f.owner);
  const refresh = () => void queryClient.invalidateQueries({ queryKey: ['datafiles'] });

  return (
    <>
      <EntityList<DataFileSummary>
        title="Data files"
        noun={{ one: 'data file', many: 'data files' }}
        table="datafiles"
        queryKey="datafiles"
        searchPlaceholder="Search name or comments"
        sortFields={SORT_FIELDS}
        fetchPage={async (query, signal) => {
          const { data, error, response } = await client.GET('/v2/datafiles', { params: { query }, signal });
          if (!data || !('items' in data)) {
            throw toApiError(error, response, 'load data files');
          }
          return data;
        }}
        deleteMany={async (ids) => {
          const { data, error, response } = await client.DELETE('/v2/datafiles', { params: { query: { ids } } });
          if (!data) {
            throw toApiError(error, response, 'delete the data files');
          }
          return data;
        }}
        canDelete={canDelete}
        columns={({ confirmDelete }) =>
          ownedColumns<DataFileSummary>({
            // a data file has no page of its own; its name opens the preview
            actions: (f) => (
              <>
                <RowAction icon="pi pi-eye" label="Preview" name={f.name} onClick={() => setPreviewing(f)} />
                <RowDownload href={`${contextPath()}/v2/datafiles/download/${f.id}`} label="Download" name={f.name} />
                {canEdit(f) && <RowAction icon="pi pi-upload" label="Replace contents" name={f.name} onClick={() => setReplacing(f)} />}
                {canDelete(f) && (
                  <RowAction icon="pi pi-trash" label="Delete" name={f.name} severity="danger" onClick={() => confirmDelete([f])} />
                )}
              </>
            ),
            onName: (f) => setPreviewing(f),
          })
        }
        headerActions={
          <Button
            label="Upload"
            icon="pi pi-upload"
            onClick={() => setUploading(true)}
            disabled={!canCreate}
            tooltip={canCreate ? undefined : "You don't have permission to upload data files"}
            tooltipOptions={{ showOnDisabled: true, position: 'left' }}
          />
        }
      />
      {uploading && <UploadDialog onHide={() => setUploading(false)} onUploaded={refresh} />}
      {replacing && <ReplaceDialog dataFile={replacing} onHide={() => setReplacing(undefined)} onReplaced={refresh} />}
      {previewing && <PreviewDialog dataFile={previewing} onHide={() => setPreviewing(undefined)} />}
    </>
  );
}

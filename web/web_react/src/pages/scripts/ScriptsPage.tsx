import { useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import { contextPath, type Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { CopyDialog } from '../../components/CopyDialog';
import { EntityList } from '../../components/EntityList';
import { ownedColumns, RowAction, RowDownload } from '../../components/entityColumns';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { ImportScriptDialog } from './ImportScriptDialog';
import { NewScriptDialog } from './NewScriptDialog';

type ScriptSummary = Schemas['ScriptSummary'];

/** Sort fields the server accepts (ScriptServiceV2Impl.SORTABLE_FIELDS) */
const SORT_FIELDS = ['id', 'name', 'productName', 'owner', 'created', 'modified', 'runtime'];

/** The scripts list (ScriptBean and scripts/index.xhtml) */
export function ScriptsPage() {
  const { client, user } = useSession();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [creating, setCreating] = useState(false);
  const [importing, setImporting] = useState(false);
  const [copying, setCopying] = useState<ScriptSummary>();
  const canCreate = hasRight(user, 'CREATE_SCRIPT');
  const canDelete = (script: ScriptSummary) => hasRightOrOwns(user, 'DELETE_SCRIPT', script.owner);

  return (
    <>
      <EntityList<ScriptSummary>
        title="Scripts"
        noun={{ one: 'script', many: 'scripts' }}
        table="scripts"
        queryKey="scripts"
        searchPlaceholder="Search name, product, comments"
        sortFields={SORT_FIELDS}
        fetchPage={async (query, signal) => {
          const { data, error, response } = await client.GET('/v2/scripts', { params: { query }, signal });
          if (!data || !('items' in data)) {
            throw toApiError(error, response, 'load scripts');
          }
          return data;
        }}
        deleteMany={async (ids) => {
          const { data, error, response } = await client.DELETE('/v2/scripts', { params: { query: { ids } } });
          if (!data) {
            throw toApiError(error, response, 'delete the scripts');
          }
          return data;
        }}
        canDelete={canDelete}
        columns={({ confirmDelete }) =>
          ownedColumns<ScriptSummary>({
            href: (s) => `/scripts/${s.id}`,
            actions: (s) => (
              <>
                <RowAction icon="pi pi-pencil" label="Open" name={s.name} onClick={() => void navigate(`/scripts/${s.id}`)} />
                {canCreate && <RowAction icon="pi pi-copy" label="Copy" name={s.name} onClick={() => setCopying(s)} />}
                <RowDownload href={`${contextPath()}/v2/scripts/download/${s.id}`} label="Download Tank XML" name={s.name} />
                {canDelete(s) && (
                  <RowAction icon="pi pi-trash" label="Delete" name={s.name} severity="danger" onClick={() => confirmDelete([s])} />
                )}
              </>
            ),
          })
        }
        headerActions={
          <>
            <Button label="Import Tank XML" icon="pi pi-upload" outlined onClick={() => setImporting(true)} disabled={!canCreate} />
            <Button
              label="New script"
              icon="pi pi-plus"
              onClick={() => setCreating(true)}
              disabled={!canCreate}
              tooltip={canCreate ? undefined : "You don't have permission to create scripts"}
              tooltipOptions={{ showOnDisabled: true, position: 'left' }}
            />
          </>
        }
      />
      {creating && <NewScriptDialog onHide={() => setCreating(false)} />}
      {importing && <ImportScriptDialog onHide={() => setImporting(false)} />}
      {copying && (
        <CopyDialog
          noun="script"
          name={copying.name}
          copy={async (name) => {
            const { data, error, response } = await client.POST('/v2/scripts/{scriptId}/copy', {
              params: { path: { scriptId: copying.id! } },
              body: { name },
            });
            if (!data) {
              throw toApiError(error, response, 'copy the script');
            }
            return data;
          }}
          onHide={() => setCopying(undefined)}
          onCopied={() => void queryClient.invalidateQueries({ queryKey: ['scripts'] })}
        />
      )}
    </>
  );
}

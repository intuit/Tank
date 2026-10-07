import { useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import type { Schemas } from '../../api/client';
import { toApiError } from '../../api/errors';
import { CopyDialog } from '../../components/CopyDialog';
import { EntityList } from '../../components/EntityList';
import { ownedColumns, RowAction } from '../../components/entityColumns';
import { hasRight, hasRightOrOwns } from '../../rights';
import { useSession } from '../../session';
import { CreateProjectDialog } from './CreateProjectDialog';

type ProjectSummary = Schemas['ProjectSummary'];

/** Sort fields the server accepts (ProjectServiceV2Impl.SORTABLE_FIELDS) */
const SORT_FIELDS = ['id', 'name', 'productName', 'owner', 'created', 'modified'];

export function ProjectsPage() {
  const { client, user } = useSession();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [creating, setCreating] = useState(false);
  const [copying, setCopying] = useState<ProjectSummary>();
  const canCreate = hasRight(user, 'CREATE_PROJECT');
  const canDelete = (project: ProjectSummary) => hasRightOrOwns(user, 'DELETE_PROJECT', project.owner);

  return (
    <>
      <EntityList<ProjectSummary>
        title="Projects"
        noun={{ one: 'project', many: 'projects' }}
        table="projects"
        queryKey="projects"
        searchPlaceholder="Search name, product, comments"
        sortFields={SORT_FIELDS}
        fetchPage={async (query, signal) => {
          const { data, error, response } = await client.GET('/v2/projects', { params: { query }, signal });
          if (!data || !('items' in data)) {
            throw toApiError(error, response, 'load projects');
          }
          return data;
        }}
        deleteMany={async (ids) => {
          const { data, error, response } = await client.DELETE('/v2/projects', { params: { query: { ids } } });
          if (!data) {
            throw toApiError(error, response, 'delete the projects');
          }
          return data;
        }}
        canDelete={canDelete}
        columns={({ confirmDelete }) =>
          ownedColumns<ProjectSummary>({
            href: (p) => `/projects/${p.id}`,
            actions: (p) => (
              <>
                <RowAction icon="pi pi-pencil" label="Open" name={p.name} onClick={() => void navigate(`/projects/${p.id}`)} />
                {canCreate && <RowAction icon="pi pi-copy" label="Copy" name={p.name} onClick={() => setCopying(p)} />}
                {canDelete(p) && (
                  <RowAction icon="pi pi-trash" label="Delete" name={p.name} severity="danger" onClick={() => confirmDelete([p])} />
                )}
              </>
            ),
          })
        }
        headerActions={
          <Button
            label="New project"
            icon="pi pi-plus"
            onClick={() => setCreating(true)}
            disabled={!canCreate}
            tooltip={canCreate ? undefined : "You don't have permission to create projects"}
            tooltipOptions={{ showOnDisabled: true, position: 'left' }}
          />
        }
      />
      {creating && <CreateProjectDialog onHide={() => setCreating(false)} />}
      {copying && (
        <CopyDialog
          noun="project"
          name={copying.name}
          copy={async (name) => {
            const { data, error, response } = await client.POST('/v2/projects/{projectId}/copy', {
              params: { path: { projectId: copying.id! } },
              body: { name },
            });
            if (!data) {
              throw toApiError(error, response, 'copy the project');
            }
            return data;
          }}
          onHide={() => setCopying(undefined)}
          onCopied={() => void queryClient.invalidateQueries({ queryKey: ['projects'] })}
        />
      )}
    </>
  );
}

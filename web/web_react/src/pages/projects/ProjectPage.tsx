import { useQuery } from '@tanstack/react-query';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Link, useParams } from 'react-router';
import { toApiError } from '../../api/errors';
import { formatDateTime } from '../../format';
import { useSession } from '../../session';

/** A project's header. The editor (ProjectBean / projectview.xhtml) is built on this next. */
export function ProjectPage() {
  const { client } = useSession();
  const projectId = Number(useParams().projectId);

  const project = useQuery({
    queryKey: ['project', projectId],
    enabled: Number.isInteger(projectId),
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/projects/{projectId}/full', {
        params: { path: { projectId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the project');
      }
      return data;
    },
  });

  if (!Number.isInteger(projectId)) {
    return <Message severity="error" text="That isn't a project ID" />;
  }
  if (project.isPending) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (project.error) {
    return (
      <section>
        <Message severity="error" text={project.error.message} />
        <p>
          <Link to="/projects">Back to projects</Link>
        </p>
      </section>
    );
  }
  const p = project.data;
  return (
    <section>
      <p className="breadcrumb">
        <Link to="/projects">Projects</Link> / {p.name}
      </p>
      <h1>{p.name}</h1>
      <dl className="facts">
        <dt>Product</dt>
        <dd>{p.productName || '—'}</dd>
        <dt>Owner</dt>
        <dd>{p.owner}</dd>
        <dt>Created</dt>
        <dd>{formatDateTime(p.created)}</dd>
        <dt>Modified</dt>
        <dd>{formatDateTime(p.modified)}</dd>
        {p.comments && (
          <>
            <dt>Comments</dt>
            <dd>{p.comments}</dd>
          </>
        )}
      </dl>
    </section>
  );
}

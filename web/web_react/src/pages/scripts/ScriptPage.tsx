import { useQuery } from '@tanstack/react-query';
import { Message } from 'primereact/message';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Link, useParams } from 'react-router';
import { contextPath } from '../../api/client';
import { toApiError } from '../../api/errors';
import { formatDateTime } from '../../format';
import { useSession } from '../../session';

/** A script's header and downloads. The step editor (ScriptEditor / script-edit-view.xhtml) builds on this */
export function ScriptPage() {
  const { client } = useSession();
  const scriptId = Number(useParams().scriptId);

  const script = useQuery({
    queryKey: ['script', scriptId],
    enabled: Number.isInteger(scriptId),
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/scripts/{scriptId}/steps', {
        params: { path: { scriptId } },
        signal,
      });
      if (!data) {
        throw toApiError(error, response, 'load the script');
      }
      return data;
    },
  });

  if (!Number.isInteger(scriptId)) {
    return <Message severity="error" text="That isn't a script ID" />;
  }
  if (script.isPending) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (script.error) {
    return (
      <section>
        <Message severity="error" text={script.error.message} />
        <p>
          <Link to="/scripts">Back to scripts</Link>
        </p>
      </section>
    );
  }
  const s = script.data;
  return (
    <section>
      <p className="breadcrumb">
        <Link to="/scripts">Scripts</Link> / {s.name}
      </p>
      <h1>{s.name}</h1>
      <dl className="facts">
        <dt>Product</dt>
        <dd>{s.productName || '—'}</dd>
        <dt>Owner</dt>
        <dd>{s.owner}</dd>
        <dt>Steps</dt>
        <dd>{s.steps?.length ?? 0}</dd>
        <dt>Created</dt>
        <dd>{formatDateTime(s.created)}</dd>
        <dt>Modified</dt>
        <dd>{formatDateTime(s.modified)}</dd>
        {s.comments && (
          <>
            <dt>Comments</dt>
            <dd>{s.comments}</dd>
          </>
        )}
      </dl>
      <div className="tab-toolbar">
        <a className="p-button p-button-outlined plain-link" href={`${contextPath()}/v2/scripts/download/${scriptId}`}>
          <i className="pi pi-download" aria-hidden />
          &nbsp;Tank XML
        </a>
        <a className="p-button p-button-text plain-link" href={`${contextPath()}/v2/scripts/harness/download/${scriptId}`}>
          <i className="pi pi-download" aria-hidden />
          &nbsp;Harness XML
        </a>
      </div>
    </section>
  );
}

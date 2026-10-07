import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router';
import { contextPath } from '../../api/client';
import { formatDateTime } from '../../format';
import { useSession } from '../../session';

/** The downloads tools/index.xhtml lists; the war has them only when built with -P release */
export const TOOLS = [
  {
    file: 'Tank-Debugger-all.jar',
    name: 'Agent visual debugger',
    description: 'Steps through a script request by request on your machine, showing each request, response and variable.',
    run: 'java -jar Tank-Debugger-all.jar',
  },
  {
    file: 'Tank-Script-Runner-all.jar',
    name: 'Script filter editor',
    description: 'Writes and tests JavaScript filters that change a script’s steps, for filters the filter editor can’t express.',
    run: 'java -jar Tank-Script-Runner-all.jar',
  },
  {
    file: 'Tank-Proxy-pkg.zip',
    name: 'Tank Proxy',
    description: 'Records a browser session as XML to upload as a new script. Unzip it and follow its README.',
  },
  {
    file: 'apiharness-1.0-all.jar',
    name: 'API test harness',
    description: 'The agent’s harness, for running a script locally from the command line while debugging it.',
    run: 'java -jar apiharness-1.0-all.jar',
  },
];

/** Whether the war has the file: a release build copies the tools in, a development build doesn't */
function useAvailable(file: string) {
  return useQuery({
    queryKey: ['tool', file],
    queryFn: async ({ signal }) => (await fetch(`${contextPath()}/tools/${file}`, { method: 'HEAD', signal })).ok,
    staleTime: Infinity,
    retry: false,
  });
}

/** Downloadable tools (tools/index.xhtml) */
export function ToolsPage() {
  const { config } = useSession();
  return (
    <section className="editor">
      <div className="page-header">
        <h1>Tools</h1>
      </div>
      <p className="field-help">
        Tools that run on your machine, built with this controller
        {config?.version ? ` (version ${config.version}` : ''}
        {config?.buildTimestamp ? `, ${formatDateTime(config.buildTimestamp)}` : ''}
        {config?.version ? ')' : ''}. They need Java 17 or later.
      </p>
      <ul className="tool-list">
        {TOOLS.map((tool) => (
          <Tool key={tool.file} {...tool} />
        ))}
      </ul>
      <p className="field-help">
        Your <Link to="/account">account</Link> has the API token these tools sign in with.
      </p>
    </section>
  );
}

function Tool({ file, name, description, run }: (typeof TOOLS)[number]) {
  const available = useAvailable(file);
  return (
    <li className="tool">
      <div>
        <h2 className="tool-name">{name}</h2>
        <p className="tool-description">{description}</p>
        {run && <code className="tool-run">{run}</code>}
      </div>
      {available.data === false ? (
        <span className="field-help" title={`${file} isn't in this build of the controller`}>
          Not included in this build
        </span>
      ) : (
        <a className="p-button p-button-outlined plain-link" href={`${contextPath()}/tools/${file}`} download>
          <i className="pi pi-download" aria-hidden />
          &nbsp;{file}
        </a>
      )}
    </li>
  );
}

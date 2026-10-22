import { contextPath } from '../../api/client';
import { useSession } from '../../session';

interface DocLink {
  label: string;
  /** A full URL, or a path in the war (under the context path) */
  href: string;
  external?: boolean;
}

/** The guides docs/index.xhtml links to; a war built with -P release has the ones under /docs and /apidocs */
export const HELP_SECTIONS: { title: string; description: string; links: DocLink[] }[] = [
  {
    title: 'Tank docs',
    description: 'The current documentation, on GitHub Pages.',
    links: [{ label: 'Tank docs', href: 'https://intuit.github.io/Tank/', external: true }],
  },
  {
    title: 'User guide',
    description: 'Creating scripts and projects and running tests, as built with this controller.',
    links: [
      { label: 'Multi-page HTML', href: '/docs/html/index.html' },
      { label: 'Single-page HTML', href: '/docs/html_single/index.html' },
    ],
  },
  {
    title: 'Installation guide',
    description: 'Installing and configuring the controller and agents.',
    links: [
      { label: 'Multi-page HTML', href: '/docs/install-guide/html/index.html' },
      { label: 'Single-page HTML', href: '/docs/install-guide/html_single/index.html' },
    ],
  },
  {
    title: 'REST API',
    description: 'The V2 REST API, which the tools and this UI use, signed in with your API token.',
    links: [{ label: 'REST V2 API documentation', href: '/swagger-ui.html' }],
  },
  {
    title: 'Javadoc',
    description: 'The classes, such as ScriptTO and StepDataTO, that external filter scripts work with.',
    links: [{ label: 'Javadoc', href: '/apidocs/index.html' }],
  },
];

/** Links to the documentation (docs/index.xhtml) */
export function HelpPage() {
  const { config } = useSession();
  return (
    <section className="editor">
      <div className="page-header">
        <h1>Help</h1>
      </div>
      {config?.version && <p className="field-help">Intuit Tank build {config.version}</p>}
      <ul className="tool-list">
        {HELP_SECTIONS.map((section) => (
          <li key={section.title} className="tool">
            <div>
              <h2 className="tool-name">{section.title}</h2>
              <p className="tool-description">{section.description}</p>
            </div>
            <div className="help-links">
              {section.links.map((link) => (
                <a
                  key={link.href}
                  href={link.external ? link.href : contextPath() + link.href}
                  target={link.external ? '_blank' : 'tr_docs'}
                  rel={link.external ? 'noopener noreferrer' : undefined}
                >
                  {link.label}
                </a>
              ))}
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}

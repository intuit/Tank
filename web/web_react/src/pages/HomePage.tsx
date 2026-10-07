import { Card } from 'primereact/card';
import { ADMIN_PAGE, CLASSIC_PAGES, classicUrl } from '../classicPages';
import { useSession } from '../session';

export function HomePage() {
  const { user } = useSession();
  const pages = [...CLASSIC_PAGES, ...(user?.admin ? [ADMIN_PAGE] : [])];
  return (
    <section>
      <h1>Welcome, {user?.name}</h1>
      <p>The new Tank UI is in progress. These pages still open in the classic UI:</p>
      <div className="page-grid">
        {pages.map((page) => (
          <a key={page.path} href={classicUrl(page.path)} className="page-link">
            <Card>
              <i className={page.icon} aria-hidden /> {page.label}
            </Card>
          </a>
        ))}
      </div>
    </section>
  );
}

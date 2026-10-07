import { Card } from 'primereact/card';
import { Link } from 'react-router';
import { ADMIN_PAGE, PAGES, classicUrl } from '../classicPages';
import { useSession } from '../session';

export function HomePage() {
  const { user } = useSession();
  const pages = [...PAGES, ...(user?.admin ? [ADMIN_PAGE] : [])];
  return (
    <section>
      <h1>Welcome, {user?.name}</h1>
      <p>The new Tank UI is in progress. Pages marked "classic" open in the previous UI.</p>
      <div className="page-grid">
        {pages.map((page) => {
          const card = (
            <Card>
              <i className={page.icon} aria-hidden /> {page.label}
              {page.classic && <span className="classic-tag">classic</span>}
            </Card>
          );
          return page.classic ? (
            <a key={page.path} href={classicUrl(page.path)} className="page-link">
              {card}
            </a>
          ) : (
            <Link key={page.path} to={page.path} className="page-link">
              {card}
            </Link>
          );
        })}
      </div>
    </section>
  );
}

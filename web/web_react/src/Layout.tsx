import { Button } from 'primereact/button';
import { Menubar } from 'primereact/menubar';
import type { MenuItem } from 'primereact/menuitem';
import { Link, Outlet } from 'react-router';
import { ADMIN_PAGE, CLASSIC_PAGES, classicUrl } from './classicPages';
import { useSession } from './session';

export function Layout() {
  const { config, user, signOut } = useSession();

  const items: MenuItem[] = user
    ? [...CLASSIC_PAGES, ...(user.admin ? [ADMIN_PAGE] : [])].map((page) => ({
        label: page.label,
        icon: page.icon,
        url: classicUrl(page.path),
      }))
    : [];

  return (
    <div className="app">
      {config?.textBanner && <div className="banner">{config.textBanner}</div>}
      <Menubar
        className="header"
        model={items}
        start={
          <Link to="/" className="brand">
            Tank
          </Link>
        }
        end={
          user && (
            <div className="header-user">
              <span>
                <i className="pi pi-user" aria-hidden /> {user.name}
              </span>
              <Button label="Sign out" icon="pi pi-sign-out" text size="small" onClick={() => void signOut()} />
            </div>
          )
        }
      />
      <main className="main">
        <Outlet />
      </main>
      {config?.version && <footer className="footer">Tank {config.version}</footer>}
    </div>
  );
}

import { Button } from 'primereact/button';
import { Menubar } from 'primereact/menubar';
import type { MenuItem } from 'primereact/menuitem';
import { ConfirmDialog } from 'primereact/confirmdialog';
import { Link, Outlet, useNavigate } from 'react-router';
import { ADMIN_PAGE, PAGES, classicUrl } from './classicPages';
import { useSession } from './session';

export function Layout() {
  const { config, user, signOut } = useSession();
  const navigate = useNavigate();

  const items: MenuItem[] = user
    ? [...PAGES, ...(user.admin ? [ADMIN_PAGE] : [])].map((page) =>
        page.classic
          ? { label: page.label, icon: page.icon, url: classicUrl(page.path) }
          : { label: page.label, icon: page.icon, command: () => void navigate(page.path) },
      )
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
      <ConfirmDialog />
      <main className="main">
        <Outlet />
      </main>
      {config?.version && <footer className="footer">Tank {config.version}</footer>}
    </div>
  );
}

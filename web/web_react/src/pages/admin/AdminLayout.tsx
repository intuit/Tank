import { Message } from 'primereact/message';
import { NavLink, Outlet } from 'react-router';
import { classicUrl } from '../../classicPages';
import { useSession } from '../../session';

/** The admin section (admin/index.xhtml): its pages, for admins only (AdminFilter) */
export function AdminLayout() {
  const { user } = useSession();
  if (!user?.admin) {
    return <Message severity="warn" text="Only administrators can use these pages." />;
  }
  return (
    <section className="editor editor-wide">
      <div className="page-header">
        <h1>Administration</h1>
      </div>
      <nav className="admin-tabs" aria-label="Administration">
        <NavLink to="/admin/users">Users</NavLink>
        <a href={classicUrl('/admin/logs.jsf')}>
          Logs <span className="classic-tag">classic</span>
        </a>
      </nav>
      <Outlet />
    </section>
  );
}

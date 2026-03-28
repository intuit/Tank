import { NavLink, useNavigate } from 'react-router-dom';
import { Menubar } from 'primereact/menubar';
import { Button } from 'primereact/button';
import { useAuth } from '../../context/AuthContext';
import type { MenuItem } from 'primereact/menuitem';
import tankLogo from '../../assets/TankLogo.svg';

export function TopNavBar() {
  const { user, logout, isLoggedIn } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems: MenuItem[] = [
    { label: 'Projects', icon: 'pi pi-folder', command: () => navigate('/projects') },
    { label: 'Scripts', icon: 'pi pi-file', command: () => navigate('/scripts') },
    { label: 'Filters', icon: 'pi pi-filter', command: () => navigate('/filters') },
    { label: 'Agent Tracker', icon: 'pi pi-desktop', command: () => navigate('/agents') },
    { label: 'Data Files', icon: 'pi pi-database', command: () => navigate('/datafiles') },
    { label: 'Tools', icon: 'pi pi-wrench', command: () => navigate('/tools') },
    ...(user?.role?.includes('admin')
      ? [{ label: 'Admin', icon: 'pi pi-shield', command: () => navigate('/admin') }]
      : []),
  ];

  const start = (
    <NavLink to="/projects" style={{ textDecoration: 'none' }}>
      <img src={tankLogo} alt="Intuit Tank" style={{ height: '32px', display: 'block' }} />
    </NavLink>
  );

  const end = isLoggedIn ? (
    <div className="flex align-items-center gap-2">
      <span className="text-sm text-color-secondary">{user?.name}</span>
      <Button
        label="Logout"
        icon="pi pi-sign-out"
        size="small"
        severity="secondary"
        outlined
        onClick={handleLogout}
      />
    </div>
  ) : null;

  return <Menubar model={navItems} start={start} end={end} />;
}

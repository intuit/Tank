import { Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { AccessDeniedPage } from '../../pages/errors/AccessDeniedPage';

export function AdminRoute() {
  const { isAdmin } = useAuth();
  return isAdmin ? <Outlet /> : <AccessDeniedPage />;
}

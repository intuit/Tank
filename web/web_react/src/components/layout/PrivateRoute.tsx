import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export function PrivateRoute() {
  const { loading } = useAuth();
  return !loading ? <Outlet /> : <Navigate to="/login" replace />;
}

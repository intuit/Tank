import { Outlet } from 'react-router-dom';
import { TopNavBar } from './TopNavBar';

export function AppLayout() {
  return (
    <div className="flex flex-column" style={{ minHeight: '100vh' }}>
      <TopNavBar />
      <main className="flex-1 p-4">
        <Outlet />
      </main>
    </div>
  );
}

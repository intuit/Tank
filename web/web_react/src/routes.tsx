import { ProgressSpinner } from 'primereact/progressspinner';
import { Navigate, Outlet, useLocation, type RouteObject } from 'react-router';
import { Layout } from './Layout';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { useSession } from './session';

/** Sends signed-out users to /login, remembering where they were going. */
function RequireUser() {
  const { user } = useSession();
  const location = useLocation();
  if (user === undefined) {
    return <ProgressSpinner className="loading" aria-label="Loading" />;
  }
  if (user === null) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
  }
  return <Outlet />;
}

export const routes: RouteObject[] = [
  {
    element: <Layout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      {
        element: <RequireUser />,
        children: [
          { index: true, element: <HomePage /> },
          {
            path: 'projects',
            lazy: async () => ({ Component: (await import('./pages/projects/ProjectsPage')).ProjectsPage }),
          },
          {
            path: 'scripts',
            lazy: async () => ({ Component: (await import('./pages/scripts/ScriptsPage')).ScriptsPage }),
          },
          {
            path: 'scripts/:scriptId',
            lazy: async () => ({
              Component: (await import('./pages/scripts/editor/ScriptEditorPage')).ScriptEditorPage,
            }),
          },
          {
            path: 'datafiles',
            lazy: async () => ({ Component: (await import('./pages/datafiles/DataFilesPage')).DataFilesPage }),
          },
          {
            path: 'tools',
            lazy: async () => ({ Component: (await import('./pages/tools/ToolsPage')).ToolsPage }),
          },
          {
            path: 'account',
            lazy: async () => ({ Component: (await import('./pages/tools/AccountPage')).AccountPage }),
          },
          {
            path: 'admin',
            lazy: async () => ({ Component: (await import('./pages/admin/AdminLayout')).AdminLayout }),
            children: [
              { index: true, element: <Navigate to="users" replace /> },
              {
                path: 'users',
                lazy: async () => ({ Component: (await import('./pages/admin/AdminUsersPage')).AdminUsersPage }),
              },
              {
                path: 'logs',
                lazy: async () => ({ Component: (await import('./pages/admin/AdminLogsPage')).AdminLogsPage }),
              },
            ],
          },
          {
            path: 'filters',
            lazy: async () => ({ Component: (await import('./pages/filters/FiltersPage')).FiltersPage }),
          },
          {
            path: 'filters/groups/:groupId',
            lazy: async () => ({
              Component: (await import('./pages/filters/FilterGroupEditorPage')).FilterGroupEditorPage,
            }),
          },
          {
            path: 'filters/:filterId',
            lazy: async () => ({ Component: (await import('./pages/filters/FilterEditorPage')).FilterEditorPage }),
          },
          {
            path: 'jobs',
            lazy: async () => ({ Component: (await import('./pages/jobs/JobsPage')).JobsPage }),
          },
          {
            path: 'projects/:projectId',
            lazy: async () => ({
              Component: (await import('./pages/projects/editor/ProjectEditorPage')).ProjectEditorPage,
            }),
          },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
];

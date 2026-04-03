import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PrimeReactProvider } from 'primereact/api';

import { AuthProvider } from './context/AuthContext';
import { PrivateRoute } from './components/layout/PrivateRoute';
import { AdminRoute } from './components/layout/AdminRoute';
import { AppLayout } from './components/layout/AppLayout';

import { LoginPage } from './pages/LoginPage';
import { NotFoundPage } from './pages/errors/NotFoundPage';
import { AccessDeniedPage } from './pages/errors/AccessDeniedPage';
import { SessionExpiredPage } from './pages/errors/SessionExpiredPage';
import { ProjectsListPage } from './pages/projects/ProjectsListPage';
import { ProjectDetailPage } from './pages/projects/ProjectDetailPage';
import { ProjectJobQueuePage } from './pages/projects/ProjectJobQueuePage';
import { ScriptsListPage } from './pages/scripts/ScriptsListPage';
import { ScriptDetailPage } from './pages/scripts/ScriptDetailPage';
import { ScriptEditPage } from './pages/scripts/ScriptEditPage';
import { ScriptFiltersPage } from './pages/scripts/ScriptFiltersPage';
import { FiltersListPage } from './pages/filters/FiltersListPage';
import { FilterEditPage } from './pages/filters/FilterEditPage';
import { AgentTrackerPage } from './pages/agents/AgentTrackerPage';
import { JobDetailPage } from './pages/agents/JobDetailPage';
import { DataFilesListPage } from './pages/datafiles/DataFilesListPage';
import { DataFileDetailPage } from './pages/datafiles/DataFileDetailPage';
import { ToolsPage } from './pages/tools/ToolsPage';
import { AdminPage } from './pages/admin/AdminPage';
import { UsersListPage } from './pages/admin/UsersListPage';
import { UserEditPage } from './pages/admin/UserEditPage';
import { ServerLogsPage } from './pages/admin/ServerLogsPage';
import { UserGroupsPage } from './pages/admin/UserGroupsPage';
import { AccountPage } from './pages/tools/AccountPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
    },
  },
});

export default function App() {
  return (
    <PrimeReactProvider>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <BrowserRouter basename="/app">
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/session-expired" element={<SessionExpiredPage />} />
              <Route path="/denied" element={<AccessDeniedPage />} />
              <Route element={<PrivateRoute />}>
                <Route element={<AppLayout />}>
                  <Route index element={<Navigate to="/projects" replace />} />
                  <Route path="/projects" element={<ProjectsListPage />} />
                  <Route path="/projects/:id" element={<ProjectDetailPage />} />
                  <Route path="/projects/:id/queue" element={<ProjectJobQueuePage />} />
                  <Route path="/scripts" element={<ScriptsListPage />} />
                  <Route path="/scripts/:id" element={<ScriptDetailPage />} />
                  <Route path="/scripts/:id/edit" element={<ScriptEditPage />} />
                  <Route path="/scripts/:id/filters" element={<ScriptFiltersPage />} />
                  <Route path="/filters" element={<FiltersListPage />} />
                  <Route path="/filters/new" element={<FilterEditPage />} />
                  <Route path="/filters/:id/edit" element={<FilterEditPage />} />
                  <Route path="/agents" element={<AgentTrackerPage />} />
                  <Route path="/jobs/:id" element={<JobDetailPage />} />
                  <Route path="/datafiles" element={<DataFilesListPage />} />
                  <Route path="/datafiles/:id" element={<DataFileDetailPage />} />
                  <Route path="/tools" element={<ToolsPage />} />
                  <Route path="/tools/account" element={<AccountPage />} />
                  <Route element={<AdminRoute />}>
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/admin/logs" element={<ServerLogsPage />} />
                    <Route path="/admin/groups" element={<UserGroupsPage />} />
                    <Route path="/admin/users" element={<UsersListPage />} />
                    <Route path="/admin/users/new" element={<UserEditPage />} />
                    <Route path="/admin/users/:id/edit" element={<UserEditPage />} />
                  </Route>
                  <Route path="*" element={<NotFoundPage />} />
                </Route>
              </Route>
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    </PrimeReactProvider>
  );
}

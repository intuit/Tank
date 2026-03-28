import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PrimeReactProvider } from 'primereact/api';

import { AuthProvider } from './context/AuthContext';
import { PrivateRoute } from './components/layout/PrivateRoute';
import { AppLayout } from './components/layout/AppLayout';

import { LoginPage } from './pages/LoginPage';
import { ProjectsListPage } from './pages/projects/ProjectsListPage';
import { ProjectDetailPage } from './pages/projects/ProjectDetailPage';
import { ScriptsListPage } from './pages/scripts/ScriptsListPage';
import { ScriptDetailPage } from './pages/scripts/ScriptDetailPage';
import { ScriptEditPage } from './pages/scripts/ScriptEditPage';
import { FiltersListPage } from './pages/filters/FiltersListPage';
import { AgentTrackerPage } from './pages/agents/AgentTrackerPage';
import { DataFilesListPage } from './pages/datafiles/DataFilesListPage';
import { DataFileDetailPage } from './pages/datafiles/DataFileDetailPage';
import { ToolsPage } from './pages/tools/ToolsPage';
import { AdminPage } from './pages/admin/AdminPage';

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
              <Route element={<PrivateRoute />}>
                <Route element={<AppLayout />}>
                  <Route index element={<Navigate to="/projects" replace />} />
                  <Route path="/projects" element={<ProjectsListPage />} />
                  <Route path="/projects/:id" element={<ProjectDetailPage />} />
                  <Route path="/scripts" element={<ScriptsListPage />} />
                  <Route path="/scripts/:id" element={<ScriptDetailPage />} />
                  <Route path="/scripts/:id/edit" element={<ScriptEditPage />} />
                  <Route path="/filters" element={<FiltersListPage />} />
                  <Route path="/agents" element={<AgentTrackerPage />} />
                  <Route path="/datafiles" element={<DataFilesListPage />} />
                  <Route path="/datafiles/:id" element={<DataFileDetailPage />} />
                  <Route path="/tools" element={<ToolsPage />} />
                  <Route path="/admin" element={<AdminPage />} />
                  <Route path="*" element={<Navigate to="/projects" replace />} />
                </Route>
              </Route>
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </QueryClientProvider>
    </PrimeReactProvider>
  );
}

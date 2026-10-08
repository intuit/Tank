import { QueryClientProvider } from '@tanstack/react-query';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import { PrimeReactProvider } from 'primereact/api';
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import { contextPath, createTankClient } from './api/client';
import { NotifyProvider } from './notify';
import { createQueryClient } from './queryClient';
import { routes } from './routes';
import { SessionProvider } from './session';
import './app.css';

const client = createTankClient();
const queryClient = createQueryClient();
const router = createBrowserRouter(routes, { basename: contextPath() + '/app' });

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <PrimeReactProvider>
      <QueryClientProvider client={queryClient}>
        <NotifyProvider>
          <SessionProvider client={client}>
            <RouterProvider router={router} />
          </SessionProvider>
        </NotifyProvider>
      </QueryClientProvider>
    </PrimeReactProvider>
  </StrictMode>,
);

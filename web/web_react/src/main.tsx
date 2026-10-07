import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import { PrimeReactProvider } from 'primereact/api';
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import { contextPath, createTankClient } from './api/client';
import { routes } from './routes';
import { SessionProvider } from './session';
import './app.css';

const client = createTankClient();
const router = createBrowserRouter(routes, { basename: contextPath() + '/app' });

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <PrimeReactProvider>
      <SessionProvider client={client}>
        <RouterProvider router={router} />
      </SessionProvider>
    </PrimeReactProvider>
  </StrictMode>,
);

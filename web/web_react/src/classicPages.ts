import { contextPath } from './api/client';

export interface AppPage {
  label: string;
  icon: string;
  /** A route in this app, or a path in the JSF UI (which shares the session) when classic */
  path: string;
  classic?: boolean;
}

/** The main sections. Those not yet moved to React open in the JSF UI. */
export const PAGES: AppPage[] = [
  { label: 'Projects', path: '/projects', icon: 'pi pi-folder' },
  { label: 'Job queue', path: '/jobs', icon: 'pi pi-server' },
  { label: 'Scripts', path: '/scripts', icon: 'pi pi-file' },
  { label: 'Filters', path: '/filters', icon: 'pi pi-filter' },
  { label: 'Data files', path: '/datafiles', icon: 'pi pi-database' },
  { label: 'Tools', path: '/tools', icon: 'pi pi-wrench' },
];

export const ADMIN_PAGE: AppPage = { label: 'Admin', path: '/admin/', icon: 'pi pi-cog', classic: true };

export function classicUrl(path: string): string {
  return contextPath() + path;
}

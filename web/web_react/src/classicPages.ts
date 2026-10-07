import { contextPath } from './api/client';

/** Pages not yet moved to React; they open in the JSF UI, which shares the same session. */
export const CLASSIC_PAGES = [
  { label: 'Projects', path: '/projects/', icon: 'pi pi-folder' },
  { label: 'Scripts', path: '/scripts/', icon: 'pi pi-file' },
  { label: 'Filters', path: '/filters/', icon: 'pi pi-filter' },
  { label: 'Data files', path: '/datafiles/', icon: 'pi pi-database' },
  { label: 'Agents', path: '/agents/', icon: 'pi pi-server' },
  { label: 'Tools', path: '/tools/', icon: 'pi pi-wrench' },
];

export const ADMIN_PAGE = { label: 'Admin', path: '/admin/', icon: 'pi pi-cog' };

export function classicUrl(path: string): string {
  return contextPath() + path;
}

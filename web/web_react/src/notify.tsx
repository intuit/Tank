import { Toast, type ToastMessage } from 'primereact/toast';
import { createContext, useCallback, useContext, useMemo, useRef, type ReactNode } from 'react';

interface Notify {
  success: (summary: string, detail?: string) => void;
  /** Something the user should know that isn't a failure */
  info: (summary: string, detail?: string) => void;
  error: (summary: string, detail?: string) => void;
}

const NotifyContext = createContext<Notify | undefined>(undefined);

export function useNotify(): Notify {
  const notify = useContext(NotifyContext);
  if (!notify) {
    throw new Error('useNotify must be used inside <NotifyProvider>');
  }
  return notify;
}

/** App-wide toasts for the outcome of actions. */
export function NotifyProvider({ children }: { children: ReactNode }) {
  const toast = useRef<Toast>(null);
  const show = useCallback((message: ToastMessage) => toast.current?.show(message), []);
  const notify = useMemo<Notify>(
    () => ({
      success: (summary, detail) => show({ severity: 'success', summary, detail, life: 4000 }),
      info: (summary, detail) => show({ severity: 'info', summary, detail, life: 4000 }),
      error: (summary, detail) => show({ severity: 'error', summary, detail, life: 8000 }),
    }),
    [show],
  );
  return (
    <NotifyContext.Provider value={notify}>
      <Toast ref={toast} position="top-right" />
      {children}
    </NotifyContext.Provider>
  );
}

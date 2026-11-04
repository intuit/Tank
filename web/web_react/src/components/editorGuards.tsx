import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dialog } from 'primereact/dialog';
import { useEffect, useRef } from 'react';
import { useBlocker, useNavigate } from 'react-router';

/**
 * Asks before leaving an editor with unsaved changes, inside the app and when closing the tab.
 *
 * @returns `leaveTo`, for leaving that is the user's decision already (deleted, saved as a copy)
 */
export function useUnsavedGuard(dirty: boolean, name: string | undefined) {
  const navigate = useNavigate();
  const leaving = useRef(false);

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      dirty && !leaving.current && currentLocation.pathname !== nextLocation.pathname,
  );
  useEffect(() => {
    if (blocker.state === 'blocked') {
      confirmDialog({
        header: 'Unsaved changes',
        message: `Leave ${name || 'this page'} without saving your changes?`,
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Leave without saving',
        rejectLabel: 'Keep editing',
        acceptClassName: 'p-button-danger',
        defaultFocus: 'reject',
        accept: () => blocker.proceed(),
        reject: () => blocker.reset(),
        onHide: () => blocker.state === 'blocked' && blocker.reset(),
      });
    }
  }, [blocker, name]);

  useEffect(() => {
    if (!dirty) {
      return;
    }
    const warn = (event: BeforeUnloadEvent) => event.preventDefault();
    window.addEventListener('beforeunload', warn);
    return () => window.removeEventListener('beforeunload', warn);
  }, [dirty]);

  return {
    leaveTo: (path: string) => {
      leaving.current = true;
      void navigate(path);
    },
  };
}

/** Shown when a save gets 409: someone saved since this was loaded, so the edits can't be applied */
export function ConflictDialog({
  visible,
  noun,
  name,
  onReload,
}: {
  visible: boolean;
  noun: string;
  name: string | undefined;
  onReload: () => void;
}) {
  return (
    <Dialog
      header={`Someone else saved this ${noun}`}
      visible={visible}
      onHide={() => undefined}
      closable={false}
      className="form-dialog"
      modal
      draggable={false}
      footer={
        <div className="form-actions">
          <Button label="Reload and lose my changes" severity="danger" outlined onClick={onReload} />
        </div>
      }
    >
      <p>
        {name} was changed since you opened it, so your changes weren't saved. Reload it to see the latest
        version, then make your changes again.
      </p>
    </Dialog>
  );
}

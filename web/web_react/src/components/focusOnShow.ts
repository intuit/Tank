import type { RefObject } from 'react';

/**
 * Focuses a dialog's first field once it has opened, unless the user is already typing in one of its
 * fields: the dialog shows after an animation, and focusing then would move their keystrokes elsewhere.
 */
export function focusOnShow(field: RefObject<HTMLInputElement | null>, select = false) {
  return () => {
    const active = document.activeElement;
    const typing =
      (active instanceof HTMLInputElement || active instanceof HTMLTextAreaElement) && !!active.closest('.p-dialog');
    if (!typing) {
      if (select) {
        field.current?.select();
      } else {
        field.current?.focus();
      }
    }
  };
}

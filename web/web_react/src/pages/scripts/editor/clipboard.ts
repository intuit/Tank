import { useSyncExternalStore } from 'react';
import type { ScriptStep } from './steps';

/**
 * Copied steps, kept for the session so they can be pasted into another script (CopyBuffer). Held in
 * memory: a reload empties it.
 */
let copied: ScriptStep[] = [];
const listeners = new Set<() => void>();

export function copySteps(steps: ScriptStep[]) {
  copied = structuredClone(steps);
  listeners.forEach((l) => l());
}

export function useCopiedSteps(): ScriptStep[] {
  return useSyncExternalStore(
    (listener) => {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
    () => copied,
  );
}

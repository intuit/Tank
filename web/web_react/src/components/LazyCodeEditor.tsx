import { lazy, Suspense, type ComponentProps } from 'react';

const CodeEditor = lazy(() => import('./CodeEditor'));

/** CodeEditor, downloaded the first time one is shown */
export function LazyCodeEditor(props: ComponentProps<typeof CodeEditor>) {
  return (
    <Suspense fallback={<div className="code-editor code-editor-loading">Loading editor…</div>}>
      <CodeEditor {...props} />
    </Suspense>
  );
}

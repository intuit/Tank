import { closeBrackets, closeBracketsKeymap } from '@codemirror/autocomplete';
import { defaultKeymap, history, historyKeymap, indentWithTab } from '@codemirror/commands';
import { javascriptLanguage } from '@codemirror/lang-javascript';
import { bracketMatching, defaultHighlightStyle, indentOnInput, LanguageSupport, syntaxHighlighting } from '@codemirror/language';
import { highlightSelectionMatches, searchKeymap } from '@codemirror/search';
import { EditorState } from '@codemirror/state';
import { drawSelection, EditorView, highlightActiveLine, highlightActiveLineGutter, keymap, lineNumbers } from '@codemirror/view';

/**
 * What a logic script needs from an editor; codemirror's basicSetup adds autocompletion and linting,
 * which nearly doubled the download.
 */
const setup = [
  lineNumbers(),
  highlightActiveLineGutter(),
  highlightActiveLine(),
  drawSelection(),
  history(),
  indentOnInput(),
  bracketMatching(),
  closeBrackets(),
  highlightSelectionMatches(),
  syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
  keymap.of([...closeBracketsKeymap, ...defaultKeymap, ...searchKeymap, ...historyKeymap, indentWithTab]),
];
import { useEffect, useRef } from 'react';

/**
 * A JavaScript editor (CodeMirror 6): line numbers, highlighting, bracket matching, undo and search keys.
 * Loaded lazily through LazyCodeEditor so pages that never show code don't download it.
 */
export default function CodeEditor({
  value,
  onChange,
  readOnly = false,
  label,
  minHeight = '12rem',
}: {
  value: string;
  onChange?: (value: string) => void;
  readOnly?: boolean;
  label: string;
  minHeight?: string;
}) {
  const host = useRef<HTMLDivElement>(null);
  const view = useRef<EditorView>(null);
  const onChangeRef = useRef(onChange);
  onChangeRef.current = onChange;

  useEffect(() => {
    const editor = new EditorView({
      parent: host.current!,
      state: EditorState.create({
        doc: value,
        extensions: [
          setup,
          // the JavaScript grammar without its completions
          new LanguageSupport(javascriptLanguage),
          EditorState.readOnly.of(readOnly),
          EditorView.editable.of(!readOnly),
          EditorView.contentAttributes.of({ 'aria-label': label }),
          EditorView.theme({ '&': { minHeight }, '.cm-scroller': { minHeight } }),
          EditorView.updateListener.of((update) => {
            if (update.docChanged) {
              onChangeRef.current?.(update.state.doc.toString());
            }
          }),
        ],
      }),
    });
    view.current = editor;
    return () => editor.destroy();
    // the editor is built once; value changes from outside are applied below
  }, [readOnly, label, minHeight]);

  useEffect(() => {
    const editor = view.current;
    if (editor && editor.state.doc.toString() !== value) {
      editor.dispatch({ changes: { from: 0, to: editor.state.doc.length, insert: value } });
    }
  }, [value]);

  return <div ref={host} className="code-editor" />;
}

import { render, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import CodeEditor from './CodeEditor';

describe('CodeEditor', () => {
  it('shows the code, labelled, and follows a new value', async () => {
    const onChange = vi.fn();
    const { container, rerender } = render(<CodeEditor value={'var a = 1;'} onChange={onChange} label="Script" />);
    const content = container.querySelector('.cm-content')!;
    expect(content).toHaveAttribute('aria-label', 'Script');
    expect(content.textContent).toContain('var a = 1;');

    rerender(<CodeEditor value={'var b = 2;'} onChange={onChange} label="Script" />);
    await waitFor(() => expect(container.querySelector('.cm-content')!.textContent).toContain('var b = 2;'));
    // CodeMirror reports every document change, including one set from outside; parents set the same value back
    expect(onChange).toHaveBeenLastCalledWith('var b = 2;');
  });

  it('can be read-only', () => {
    const { container } = render(<CodeEditor value="x" readOnly label="Help" />);
    expect(container.querySelector('.cm-content')).toHaveAttribute('contenteditable', 'false');
  });
});

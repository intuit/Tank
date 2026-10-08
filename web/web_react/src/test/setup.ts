import '@testing-library/jest-dom/vitest';
import { cleanup, configure } from '@testing-library/react';
import { afterEach } from 'vitest';

// The first test in a file loads lazy routes (the script editor, CodeMirror); in a parallel Maven build
// that can take longer than findBy's 1 s default, failing tests that pass on their own
configure({ asyncUtilTimeout: 5000 });

afterEach(() => {
  cleanup();
});

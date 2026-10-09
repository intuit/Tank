import { useLayoutEffect, useRef, useState } from 'react';
import type { DataTable, DataTableValueArray } from 'primereact/datatable';

/**
 * A scrollHeight that lets a scrollable DataTable reach the bottom of the window, as the JSF tables do.
 * The height leaves room for whatever follows the table on the page (a paginator, the footer) and never
 * drops below `min`, so a table far down a page, or in a narrow window, still shows some rows.
 */
export function useFillHeight<T extends DataTableValueArray>(min = 300) {
  const ref = useRef<DataTable<T>>(null);
  const [height, setHeight] = useState('60vh');

  useLayoutEffect(() => {
    const measure = () => {
      const wrapper = ref.current?.getElement()?.querySelector<HTMLElement>('.p-datatable-wrapper');
      const main = wrapper?.closest<HTMLElement>('.main');
      // the page's own content; .main itself stretches to push the footer down, so it can't be measured
      const page = main?.firstElementChild;
      if (!wrapper || !main || !page) {
        return;
      }
      const box = wrapper.getBoundingClientRect();
      const after = page.getBoundingClientRect().bottom - box.bottom;
      const padding = parseFloat(getComputedStyle(main).paddingBottom) || 0;
      const footer = document.querySelector<HTMLElement>('.footer')?.offsetHeight ?? 0;
      const top = box.top + window.scrollY;
      setHeight(`${Math.max(min, Math.floor(window.innerHeight - top - after - padding - footer))}px`);
    };
    measure();
    window.addEventListener('resize', measure);
    // content above the table can change size (a message appears, data loads) without the window resizing
    const observer = typeof ResizeObserver === 'undefined' ? undefined : new ResizeObserver(measure);
    observer?.observe(document.body);
    return () => {
      window.removeEventListener('resize', measure);
      observer?.disconnect();
    };
  }, [min]);

  return { ref, scrollHeight: height };
}

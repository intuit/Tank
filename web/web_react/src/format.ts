const DATE_TIME = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' });

export function formatDateTime(value: string | undefined): string {
  if (!value) {
    return '';
  }
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : DATE_TIME.format(date);
}

/**
 * The text of an HTML report from the server (such as JobDetailFormatter's), one line per <br/>. It is
 * parsed into an inert document, so nothing in it runs or loads, and only its text is kept.
 */
export function htmlToText(html: string): string {
  const doc = new DOMParser().parseFromString(html.replace(/<br\s*\/?>\n?/gi, '\n'), 'text/html');
  return (doc.body.textContent ?? '').replace(/ /g, ' ').trim();
}

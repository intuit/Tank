import type { TankClient } from '../../api/client';
import { toApiError } from '../../api/errors';

export const SCRIPT_FILE_TYPES = '.xml,.gz';

/**
 * Uploads a script file to POST /v2/scripts: a Tank Proxy recording when `recording` is given, else
 * a Tank script XML (which updates the script its XML names, or creates one when that ID is 0).
 * A .gz file is sent as gzip.
 *
 * @returns the script's ID and the server's message
 */
export async function uploadScript(
  client: TankClient,
  file: File,
  recording?: { name: string; productName?: string; filterIds: number[] },
): Promise<{ scriptId: number | undefined; message: string | undefined }> {
  const gzip = file.name.toLowerCase().endsWith('.gz');
  const { data, error, response } = await client.POST('/v2/scripts', {
    params: {
      query: recording
        ? {
            recording: '',
            name: recording.name,
            productName: recording.productName,
            filterIds: recording.filterIds.length ? recording.filterIds : undefined,
          }
        : {},
      header: gzip ? { 'Content-Encoding': 'gzip' } : {},
    },
    // the generated type calls the binary part a string
    body: { file: file as unknown as string },
    bodySerializer: () => {
      const form = new FormData();
      form.append('file', file, file.name);
      return form;
    },
  });
  if (!data) {
    throw toApiError(error, response, recording ? 'upload the recording' : 'import the script');
  }
  const id = Number(data['scriptId']);
  return { scriptId: Number.isFinite(id) && id > 0 ? id : undefined, message: data['message'] };
}

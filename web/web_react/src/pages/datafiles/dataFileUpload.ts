import type { Schemas, TankClient } from '../../api/client';
import { toApiError } from '../../api/errors';

/** What the batch upload takes: data files, and zips it searches for them (FileUploadBean) */
export const DATA_FILE_TYPES = '.csv,.txt,.xml,.zip';
/** What replacing one file's contents takes; a .gz is sent gzip-encoded */
export const REPLACEMENT_TYPES = '.csv,.txt,.xml,.gz';
/** POST /v2/datafiles/batch's limit */
export const MAX_FILES = 50;

const extension = (name: string) => name.slice(name.lastIndexOf('.')).toLowerCase();

/** Files the given accept list rejects, by name */
export function unsupported(files: File[], accept: string): string[] {
  const allowed = accept.split(',');
  return files.filter((f) => !allowed.includes(extension(f.name))).map((f) => f.name);
}

/** Uploads data files and zips of them; each data file found becomes its own data file */
export async function uploadDataFiles(client: TankClient, files: File[]): Promise<Schemas['DataFileBatchResult']> {
  const { data, error, response } = await client.POST('/v2/datafiles/batch', {
    // the generated type calls the binary parts strings
    body: { files: files as unknown as string[] },
    bodySerializer: () => {
      const form = new FormData();
      for (const file of files) form.append('files', file, file.name);
      return form;
    },
  });
  if (!data) throw toApiError(error, response, 'upload the files');
  return data;
}

/**
 * Replaces a data file's contents, keeping its name: the server names an overwritten file after the
 * upload, so the upload goes under the current name.
 */
export async function replaceDataFile(client: TankClient, id: number, name: string, file: File): Promise<void> {
  const gzip = extension(file.name) === '.gz';
  const renamed = new File([file], gzip ? `${name}.gz` : name, { type: file.type });
  const { error, response } = await client.POST('/v2/datafiles/upload', {
    params: { query: { id }, header: gzip ? { 'Content-Encoding': 'gzip' } : {} },
    body: renamed as unknown as string,
    bodySerializer: () => {
      const form = new FormData();
      form.append('file', renamed, renamed.name);
      return form;
    },
  });
  if (!response.ok) throw toApiError(error, response, 'replace the file');
}

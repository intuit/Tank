import { useMutation } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { Message } from 'primereact/message';
import { useState, type FormEvent } from 'react';
import type { Schemas } from '../../api/client';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { DATA_FILE_TYPES, MAX_FILES, REPLACEMENT_TYPES, replaceDataFile, unsupported, uploadDataFiles } from './dataFileUpload';

/** Uploads data files, or zips of them (FileUploadBean and the JSF upload dialog) */
export function UploadDialog({ onHide, onUploaded }: { onHide: () => void; onUploaded: () => void }) {
  const { client } = useSession();
  const notify = useNotify();
  const [files, setFiles] = useState<File[]>([]);
  const [skipped, setSkipped] = useState<string[]>();
  const wrongType = unsupported(files, DATA_FILE_TYPES);
  const problem = wrongType.length
    ? `Only .csv, .txt, .xml and .zip files can be uploaded: ${wrongType.join(', ')}`
    : files.length > MAX_FILES
      ? `Upload at most ${MAX_FILES} files at a time`
      : undefined;

  const upload = useMutation({
    mutationFn: () => uploadDataFiles(client, files),
    onSuccess: (result) => {
      const created = result.created?.length ?? 0;
      onUploaded();
      if (created) {
        notify.success(created === 1 ? `Added ${result.created![0]!.name}` : `Added ${created} data files`);
      }
      if (result.skipped?.length) {
        // stay open to say what wasn't added
        setSkipped(result.skipped);
        setFiles([]);
      } else {
        onHide();
      }
    },
  });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    if (files.length && !problem) upload.mutate();
  };

  return (
    <Dialog header="Upload data files" visible onHide={onHide} className="form-dialog" modal draggable={false}>
      <form onSubmit={submit} className="form-grid">
        <label htmlFor="datafile-files">Files</label>
        <input
          id="datafile-files"
          type="file"
          multiple
          accept={DATA_FILE_TYPES}
          onChange={(e) => {
            setSkipped(undefined);
            upload.reset();
            setFiles([...(e.target.files ?? [])]);
          }}
          className="file-input"
        />
        <small className="field-help">
          .csv, .txt or .xml files, or .zip files: every .csv, .txt and .xml file in a zip, in any folder, becomes a
          data file named after it. A name already in use adds another file with that name.
        </small>
        {problem && <Message severity="error" text={problem} />}
        {skipped && (
          <Message
            severity="warn"
            content={
              <div>
                <strong>Not added:</strong>
                <ul className="problem-list">
                  {skipped.map((name) => (
                    <li key={name}>{name}</li>
                  ))}
                </ul>
              </div>
            }
          />
        )}
        {upload.error && <Message severity="error" text={upload.error.message} />}
        <div className="form-actions">
          <Button type="button" label={skipped ? 'Done' : 'Cancel'} text onClick={onHide} />
          <Button
            type="submit"
            label={files.length > 1 ? `Upload ${files.length} files` : 'Upload'}
            icon="pi pi-upload"
            loading={upload.isPending}
            disabled={!files.length || !!problem}
          />
        </div>
      </form>
    </Dialog>
  );
}

/** Replaces one data file's contents, keeping its name and the projects that use it */
export function ReplaceDialog({
  dataFile,
  onHide,
  onReplaced,
}: {
  dataFile: Schemas['DataFileSummary'];
  onHide: () => void;
  onReplaced: () => void;
}) {
  const { client } = useSession();
  const notify = useNotify();
  const [file, setFile] = useState<File>();
  const wrongType = file ? unsupported([file], REPLACEMENT_TYPES).length > 0 : false;
  const replace = useMutation({
    mutationFn: () => replaceDataFile(client, dataFile.id!, dataFile.name ?? '', file!),
    onSuccess: () => {
      notify.success(`Replaced the contents of ${dataFile.name}`);
      onReplaced();
      onHide();
    },
  });

  return (
    <Dialog header={`Replace ${dataFile.name}`} visible onHide={onHide} className="form-dialog" modal draggable={false}>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          if (file && !wrongType) replace.mutate();
        }}
        className="form-grid"
      >
        <label htmlFor="datafile-replacement">New contents</label>
        <input
          id="datafile-replacement"
          type="file"
          accept={REPLACEMENT_TYPES}
          onChange={(e) => {
            replace.reset();
            setFile(e.target.files?.[0]);
          }}
          className="file-input"
        />
        <small className="field-help">
          A .csv, .txt or .xml file, or one gzipped (.gz). The data file keeps its name, so projects that use it get the
          new contents in their next job.
        </small>
        {wrongType && <Message severity="error" text="Choose a .csv, .txt, .xml or .gz file" />}
        {replace.error && <Message severity="error" text={replace.error.message} />}
        <div className="form-actions">
          <Button type="button" label="Cancel" text onClick={onHide} />
          <Button type="submit" label="Replace" icon="pi pi-upload" loading={replace.isPending} disabled={!file || wrongType} />
        </div>
      </form>
    </Dialog>
  );
}

export interface DataFileDescriptor {
  id: number;
  name: string;
  path?: string;
  creator?: string;
  created?: string;
  modified?: string;
  comments?: string;
}

export interface DataFileDescriptorContainer {
  dataFiles: DataFileDescriptor[];
}

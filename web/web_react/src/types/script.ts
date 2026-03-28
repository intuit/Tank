export interface ScriptDescription {
  id: number;
  name: string;
  runtime?: number;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  filterIds?: number[];
}

export interface ScriptDescriptionContainer {
  scripts: ScriptDescription[];
}

export interface ExternalScriptTO {
  id: number;
  name: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  script?: string;
}

export interface ExternalScriptContainer {
  externalScripts: ExternalScriptTO[];
}

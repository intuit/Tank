export interface ProjectTO {
  id: number;
  name: string;
  comments?: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  scriptGroupList?: ScriptGroupTO[];
  jobRegions?: JobRegionTO[];
  rampTime?: string;
  simulationTime?: number;
  userIntervalIncrement?: number;
  variables?: KeyValuePair[];
  dataFileIds?: number[];
}

export interface ProjectContainer {
  projects: ProjectTO[];
}

export interface ScriptGroupTO {
  name?: string;
  scriptGroupSteps?: ScriptGroupStepTO[];
  loop?: number;
}

export interface ScriptGroupStepTO {
  scriptId?: number;
  scriptName?: string;
  loop?: number;
}

export interface JobRegionTO {
  region?: string;
  users?: string;
  percentage?: string;
}

export interface KeyValuePair {
  key: string;
  value: string;
}

export interface AutomationRequest {
  name: string;
  productName?: string;
  comments?: string;
  rampTime?: string;
  simulationTime?: string;
  userIntervalIncrement?: number;
  location?: string;
  stopBehavior?: string;
  workloadType?: string;
  terminationPolicy?: string;
  dataFileIds?: number[];
  jobRegions?: JobRegionTO[];
  variables?: Record<string, string>;
}

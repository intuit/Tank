export interface AutomationTestPlan {
  name: string;
  userPercentage: number;
  position?: number;
  scriptGroups?: AutomationScriptGroup[];
}

export interface AutomationScriptGroup {
  name: string;
  loop: number;
  position?: number;
  scripts?: AutomationScriptGroupStep[];
}

export interface AutomationScriptGroupStep {
  scriptId?: number;
  scriptName?: string;
  loop?: number;
  position?: number;
}

export interface ProjectTO {
  id: number;
  name: string;
  comments?: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  testPlans?: AutomationTestPlan[];
  jobRegions?: JobRegionTO[];
  rampTime?: string;
  location?: string;
  stopBehavior?: string;
  simulationTime?: number;
  userIntervalIncrement?: number;
  variables?: KeyValuePair[];
  dataFileIds?: number[];
}

export interface ProjectContainer {
  projects: ProjectTO[];
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
  testPlans?: AutomationTestPlan[];
}

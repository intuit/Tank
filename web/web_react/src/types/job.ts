export interface JobTO {
  id: number;
  name?: string;
  status?: string;
  projectName?: string;
  projectId?: number;
  creator?: string;
  created?: string;
  modified?: string;
  rampTime?: string;
  simulationTime?: number;
  totalVirtualUsers?: number;
  startTime?: string;
  endTime?: string;
  regions?: JobRegionStatus[];
}

export interface JobContainer {
  jobs: JobTO[];
}

export interface JobRegionStatus {
  region?: string;
  users?: number;
  status?: string;
}

export interface CloudVmStatus {
  instanceId?: string;
  vmStatus?: string;
  jobId?: string;
  userCount?: number;
  region?: string;
  startTime?: string;
  endTime?: string;
}

export interface CloudVmStatusContainer {
  statuses: CloudVmStatus[];
}

export interface CreateJobRequest {
  projectId: number;
  totalVirtualUsers?: number;
  rampTime?: string;
  simulationTime?: string;
  location?: string;
  stopBehavior?: string;
}

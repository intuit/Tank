export interface UserTO {
  id: number;
  name: string;
  email: string;
  apiToken: string | null;
  lastLoginTs: string | null;
  groups: string[];
}

export interface UserRequest {
  name?: string;
  email?: string;
  password?: string;
  groups?: string[];
}

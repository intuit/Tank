export interface FilterConditionTO {
  scope: string;
  condition: string;
  value: string;
}

export interface FilterActionTO {
  action: string;
  scope: string;
  key?: string;
  value?: string;
}

export interface FilterTO {
  id: number;
  name: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  filterType?: 'INTERNAL' | 'EXTERNAL';
  allConditionsMustPass?: boolean;
  externalScriptId?: number;
  conditions?: FilterConditionTO[];
  actions?: FilterActionTO[];
}

export interface FilterContainer {
  filters: FilterTO[];
}

export interface FilterGroupTO {
  id: number;
  name: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
  filters?: FilterTO[];
}

export interface FilterGroupContainer {
  filterGroups: FilterGroupTO[];
}

// Scope options per action type
export const CONDITION_SCOPES = ['Hostname', 'Path', 'Query String', 'Post data'] as const;
export const CONDITION_TYPES = ['Contains', 'Matches', 'Does not contain', 'Starts with', 'Exist', 'Does not exist'] as const;

export const ACTION_TYPES = ['remove', 'replace', 'add'] as const;

export const REMOVE_SCOPES = ['request', 'requestHeader', 'requestCookie', 'queryString', 'postData'] as const;
export const REPLACE_SCOPES = ['requestHeader', 'requestCookie', 'queryString', 'postData', 'validation', 'assignment', 'path', 'host', 'onFailure'] as const;
export const ADD_SCOPES = ['responseData', 'validation', 'assignment', 'requestHeader', 'requestCookie', 'queryString', 'postData', 'thinkTime', 'sleepTime'] as const;

export function scopesForAction(action: string): readonly string[] {
  if (action === 'remove') return REMOVE_SCOPES;
  if (action === 'replace') return REPLACE_SCOPES;
  if (action === 'add') return ADD_SCOPES;
  return [];
}

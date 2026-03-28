export interface FilterTO {
  id: number;
  name: string;
  productName?: string;
  creator?: string;
  created?: string;
  modified?: string;
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

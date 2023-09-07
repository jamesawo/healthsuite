
export interface Modules {
  name: string;
  permissions: Permission[];
}

export  class Permission {
  id: number;
  name: string;
  description: string;
}

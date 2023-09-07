import { PermissionPayload } from '@app/shared/_payload/settings/permissionPayload';

export class RolePayload {
    id?: number;
    name?: string;
    description?: string;
    permissions?: PermissionPayload[];

    constructor() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.permissions = [];
    }
}

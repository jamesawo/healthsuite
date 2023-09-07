import { PermissionPayload } from '@app/shared/_payload/settings/permissionPayload';
import { RolePayload } from '@app/shared/_payload/settings/rolePayload';

export class RoleModulePayload {
    roles: RolePayload[];
    permissions: { module: string; authorities: PermissionPayload[] };
    constructor() {
        this.roles = [];
        this.permissions = { module: null, authorities: [] };
    }
}

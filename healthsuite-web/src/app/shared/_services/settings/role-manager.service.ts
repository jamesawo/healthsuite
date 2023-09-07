import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { ResponsePayload, RolePayload } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class RoleManagerService {
    url: string = environment.apiEndPoint + '/roleManager';

    constructor(private http: HttpClient) {}

    onGetRolesAndPermissions() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/roleModules/all/`);
    }

    onUpdateRole(payload: RolePayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/roleModules/update`, payload);
    }

    onGetRolePermissions(roleId: number) {
        return this.http.get<RolePayload>(`${this.url}/role-permissions/${roleId}`);
    }
}

import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {ChangePasswordPayload, MessagePayload, ResponsePayload} from '../../_payload';
import {UserPayload, UserUpdatePayload} from '@app/modules/settings/_payload/userPayload';

@Injectable({
    providedIn: 'root',
})
export class UserManagerService {
    url: string = environment.apiEndPoint + '/user-manager';

    constructor(private http: HttpClient) {}

    onGetAllUser() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/user/all`);
    }

    onCreateUser(user: UserPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/user/create`, user);
    }

    onGetAllConsultant() {
        return this.http.get<ResponsePayload<UserPayload[]>>(`${this.url}/user/get-consultants`);
    }

    onGetUserRole(userId: number) {
        return this.http.get<UserPayload>(`${this.url}/user/get-role/${userId}`);
    }

    onChangeUserPassword(payload: ChangePasswordPayload) {
        return this.http.post<boolean>(`${this.url}/change-password`, payload);
    }

    onUpdateUserRole(param: { roleId: number; userId: number }) {
        return this.http.post<boolean>(`${this.url}/update-role`, param);
    }

    onUpdateUserDetails(param: UserUpdatePayload) {
        return this.http.post<MessagePayload>(`${this.url}/update-user-detail`, param);
    }
}

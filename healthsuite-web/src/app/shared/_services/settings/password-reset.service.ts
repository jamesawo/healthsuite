import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { ResponsePayload } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';

@Injectable({
    providedIn: 'root',
})
export class PasswordResetService {
    url: string = environment.apiEndPoint + '/password';

    constructor(private http: HttpClient) {}

    onGetAllUsers() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/getAllUser`);
    }

    onResetPassword(user: UserPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/resetUserPassword`, user);
    }
}

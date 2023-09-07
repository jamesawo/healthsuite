import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { ResponsePayload, VendorPayload } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class VendorService {
    url: string = environment.apiEndPoint + '/vendor-management';

    constructor(private http: HttpClient) {}

    public onRegisterVendor(payload: VendorPayload) {
        return this.http.post<ResponsePayload<VendorPayload>>(
            `${this.url}/register-vendor`,
            payload,
            {
                observe: 'response',
            }
        );
    }

    public onUpdateVendor(payload: VendorPayload) {
        return this.http.put(`${this.url}/update-vendor`, payload, { observe: 'response' });
    }
}

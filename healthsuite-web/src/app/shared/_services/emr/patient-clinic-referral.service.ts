import {environment} from '@environments/environment';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PatientClinicReferralPayload} from '@app/shared/_payload/erm/patient.payload';

@Injectable({
    providedIn: 'root',
})
export class PatientClinicReferralService {
    url: string = environment.apiEndPoint + '/clinic-referral';

    constructor(private http: HttpClient) {}

    public onReferPatientToClinic(payload: PatientClinicReferralPayload ) {
        return this.http.post<{message: string}>(`${this.url}/refer-to-clinic`, payload);
    }
}

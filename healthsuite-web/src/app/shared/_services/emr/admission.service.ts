import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '@environments/environment';
import { MessagePayload } from '@app/shared/_payload';
import { AdmissionPayload } from '@app/modules/settings';
import {DischargePatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {PatientTransferPayload} from '@app/shared/_payload/erm/patient-transfer.payload';

@Injectable({
    providedIn: 'root',
})
export class AdmissionService {
    url: string = environment.apiEndPoint + '/admission-service';

    constructor(private http: HttpClient) {}

    public onAdmitPatient(admissionFormPayload: AdmissionPayload) {
        return this.http.post<MessagePayload>(
            `${this.url}/admit-patient`,
            admissionFormPayload
        );
    }

    public onDischargePatient(payload: DischargePatientPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/patient-discharge-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onTransferPatientWard(payload: PatientTransferPayload) {
        return this.http.post<MessagePayload>(`${this.url}/transfer-patient-ward`, payload);
    }
}

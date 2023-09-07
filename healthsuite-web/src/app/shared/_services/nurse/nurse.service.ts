import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessagePayload, PatientCardNotePayload } from '@app/shared/_payload';
import { ObstetricsHistoryPayload } from '@app/shared/_payload/nurse/nurse-anc.payload';
import { ClerkDrugItemsPayload } from '@app/shared/_payload/clerking/clerk-request.payload';
import {NursePatientFluid} from '@app/shared/_payload/nurse/nurse-care.payload';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Injectable({
    providedIn: 'root',
})
export class NurseService {
    url: string = environment.apiEndPoint + '/nurse';

    constructor(private http: HttpClient) {}

    public onPreviewPatientEFolder(payload: PatientCardNotePayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/download-patient-e-folder-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onPreviewPatientAncCardNote(payload: PatientCardNotePayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/download-patient-anc-card-note-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onSaveObstetricsHistory(payload: ObstetricsHistoryPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-obstetrics-history`, payload);
    }

    public onAdministerDrug(payload: ClerkDrugItemsPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-drug-administration`, payload);
    }

    public onSavePatientFluidBalance(payload: NursePatientFluid) {
        return this.http.post<MessagePayload>(`${this.url}/patient-fluid-balance-save`, payload);
    }

    public onGetPatientPrevFluidBalance(patient: PatientPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/patient-fluid-balance-get-previous`, patient, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onSaveIcuBounceBack(param: any) {
        return this.http.post<MessagePayload>(`${this.url}/save-icu-bounce-back`, param);
    }
}

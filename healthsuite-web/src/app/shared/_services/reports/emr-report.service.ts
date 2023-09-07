import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {
    PageResultPayload,
    PatientRegisterPayload,
} from '@app/shared/_payload/reports/reports.payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {InterimSearchPayload, SearchAdmissionPayload} from '@app/shared/_payload/reports/report-erm.payload';

@Injectable({
    providedIn: 'root',
})
export class EmrReportService {
    apiURL: string = environment.apiEndPoint + '/report/emr';

    constructor(private http: HttpClient) {}

    public findPatientRegisterByDate(payload: PatientRegisterPayload) {
        return this.http.post<PageResultPayload<PatientPayload>>(
            `${this.apiURL}/patient-register`,
            payload
        );
    }

    public downloadPatientRegisterReport(payload: PatientRegisterPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.apiURL}/patient-register-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onSearchAdmissionSession(payload: InterimSearchPayload) {
        return this.http.post<SearchAdmissionPayload[]>(`${this.apiURL}/search-admission-session`, payload);
    }

    public onSearchPatientInterimInvoice(payload: InterimSearchPayload) {
        return this.http.post(``, payload);
    }

    public downloadPatientInterimReport(searchPayload: InterimSearchPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.apiURL}/patient-interim-report-pdf`, searchPayload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }
}

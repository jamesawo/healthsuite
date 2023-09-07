import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {PatientVitalSign, VitalSignSearch, VitalSignSearchPayload} from '@app/shared/_payload';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PatientVitalSignService {
    url: string = environment.apiEndPoint + '/nurse-vital-sign';

    constructor(private http: HttpClient) {}

    public onSubmitVitalSign(payload: PatientVitalSign) {
        return this.http.post<string>(`${this.url}/capture-vital-sign`, payload);
    }

    public onGetPatientLastCapturedVital(patientId: number) {
        return this.http.get<PatientVitalSign>(`${this.url}/get-vital-sign/${patientId}`);
    }

    public onSearchPatientVitalSing(payload: VitalSignSearchPayload) {
        return this.http.post<VitalSignSearch>(`${this.url}/get-vital-sign-by-date-range`, payload);
    }
}

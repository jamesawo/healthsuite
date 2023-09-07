import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { DatePayload } from '@app/shared/_payload';
import { HttpClient } from '@angular/common/http';
import {
    ClerkDoctorRequestSearch,
    ClerkDrugRequestPayload, ClerkLabRequestPayload, ClerkRadiologyRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';

@Injectable({
    providedIn: 'root',
})
export class DoctorRequestService {
    private url = environment.apiEndPoint + '/doctor-request';

    constructor(private http: HttpClient) {}

    onSearchDrugRequest(payload: ClerkDoctorRequestSearch) {
        return this.http.post<ClerkDrugRequestPayload[]>(`${this.url}/find-drug-request-by-date-range`, payload);
    }

    onSearchLabRequest(payload: ClerkDoctorRequestSearch) {
        return this.http.post<ClerkLabRequestPayload[]>(`${this.url}/find-lab-request-by-date-range`, payload);
    }

    onSearchRadiologyRequest(payload: ClerkDoctorRequestSearch) {
        return this.http.post<ClerkRadiologyRequestPayload[]>(`${this.url}/find-radiology-request-by-date-range`, payload);
    }
}

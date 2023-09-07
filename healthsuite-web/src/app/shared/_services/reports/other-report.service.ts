import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {
    ChargeSheetCategoryEnum,
    PageResultPayload,
    PatientRegisterPayload,
} from '@app/shared/_payload/reports/reports.payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {InterimSearchPayload, SearchAdmissionPayload} from '@app/shared/_payload/reports/report-erm.payload';

@Injectable({
    providedIn: 'root',
})
export class OtherReportService {
    apiURL: string = environment.apiEndPoint + '/report/other';

    constructor(private http: HttpClient) {}


    public downloadServiceChargeSheet(category: ChargeSheetCategoryEnum, searchById: number) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.get<any>(`${this.apiURL}/service-charge-sheet/${category}/${searchById}`, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }
}

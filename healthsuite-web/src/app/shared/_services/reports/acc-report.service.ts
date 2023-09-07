import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ReportColByRevDepPayload } from '@app/shared/_payload/reports/report-acc.payload';

@Injectable({
    providedIn: 'root',
})
export class AccReportService {
    apiURL: string = environment.apiEndPoint + '/report/account';

    constructor(private http: HttpClient) {}

    public getDailyCashCollectionReport(payload: ReportColByRevDepPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.apiURL}/daily-cash-collection-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public getSchemeConsumptionReport(payload: any) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.apiURL}/download-scheme-consumption-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }
}

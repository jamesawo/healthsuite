import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {
    CashierCompiledShiftPayload,
    CashierFundReceptionPayload,
    CashierShiftSearch,
    CashierShitPayload,
    CashierShitWrapPayload
} from '@app/shared/_payload/shift/shit.payload';

@Injectable({
    providedIn: 'root',
})
export class ShiftManagerService {
    url: string = environment.apiEndPoint + '/manage-shift';

    constructor(private http: HttpClient) {}

    public onCloseCashierShift(shiftNumber: string, user: number) {
        return this.http.get<boolean>(`${this.url}/close-shift/${shiftNumber}/${user}`);
    }

    public onLookUpShiftCode(user: number) {
        return this.http.get<{ shiftNumber: string }>(`${this.url}/look-up-code/${user}`);
    }

    public onGetCashierShiftWrapperRecord(id: number) {
        return this.http.get<CashierShitWrapPayload>(`${this.url}/search-shift-wrap-by-id/${id}`);
    }

    public onGetCashierShiftRecord(id: number) {
        return this.http.get<CashierShitPayload>(`${this.url}/search-shift-by-id/${id}`);
    }

    public onGetCashierShiftRecordByDateRange(payload: CashierShiftSearch) {
        return this.http.post<CashierShitWrapPayload>(`${this.url}/search-shift-by-date-range`, payload);
    }

    public onCloseMultipleShiftRecord(closable: {closedBy: number, shiftsId: number[]}) {
        return this.http.post<boolean>(`${this.url}/close-multiple-shifts`, closable);
    }

    public onDownloadShiftReport(payload: CashierShiftSearch) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/download-cashier-shift-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onGetCashierShiftRecordByDateRangeAndUser(payload: CashierShiftSearch) {
        return this.http.post<CashierShitPayload[]>(`${this.url}/search-shift-by-date-range-and-user`, payload);
    }

    public onGetCashierShiftRecordByDateRangeCashierNotRequired(payload: CashierShiftSearch) {
        return this.http.post<CashierShitPayload[]>(`${this.url}/search-shift-by-date-range-and-user-not-required`, payload);
    }

    public onGetCashierShiftRecordByDateRangeForReception(payload: CashierShiftSearch) {
        return this.http.post<CashierShitPayload[]>(`${this.url}/search-shift-by-date-range-for-reception`, payload);
    }

    public onAcknowledgeCashierShift(acknowledgedPayload: CashierFundReceptionPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/acknowledge-cashier-shift-fund-pdf`, acknowledgedPayload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onCompileCashierShift(payload: CashierCompiledShiftPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/compile-cashier-shifts-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onGetAllShiftPerDayReport(searchPayload: CashierShiftSearch) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/get-all-shift-per-day-report-pdf`, searchPayload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }
}

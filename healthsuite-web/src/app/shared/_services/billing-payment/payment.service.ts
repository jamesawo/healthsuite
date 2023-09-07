import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {
    BillPayment,
    BillPaymentSearchByEnum,
    DepositPayload,
    PatientBill,
    PaymentTypeForEnum,
    ReceiptPayload,
    SearchPatientBillPayload,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DrugDispenseSearchByEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Injectable({
    providedIn: 'root',
})
export class PaymentService {
    api: string = environment.apiEndPoint;
    url: string = environment.apiEndPoint + '/billing';

    constructor(private http: HttpClient) {}

    public onCreateBill(payload: PatientBill) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/patient-billing`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onCreateDeposit(payload: DepositPayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/patient-deposit`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onGetPatientServiceBills(payload: { patientId: number; loadDeposit: boolean }) {
        const url = `${this.url}/group-patient-bill-deposit?search=${payload.patientId}&loadDeposit=${payload.loadDeposit}&searchBy=${BillPaymentSearchByEnum.PATIENT}`;
        return this.http.get<SearchPatientBillPayload>(url, { observe: 'response' });
    }

    public onMakeBillPayment(payload: BillPayment) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/make-payment`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }

    public onFindPatientPaymentReceipt(patientId: number, paymentFor: PaymentTypeForEnum) {
        const searchBy = DrugDispenseSearchByEnum.PATIENT_NUMBER;
        const endpoint = `${this.api}/receipt/search-patient-receipt?search=${patientId.toString()}&loadPatientDetail=false&loadPatientBill=true&searchBy=${searchBy}&filterFor=${paymentFor}`;
        return this.http.get<ReceiptPayload[]>(`${endpoint}`, { observe: 'response' });
    }

    public onSaveBillAdjustment(payload: PatientBill) {
        return this.http.post<{message: string}>(`${this.url}/adjust-bill`, payload);
    }

    public onGetNhisServicePrice(patientId: number, serviceId: number) {
        return this.http.get<number>(`${this.url}/get-nhis-service-price?patient=${patientId}&service=${serviceId}`);
    }
}

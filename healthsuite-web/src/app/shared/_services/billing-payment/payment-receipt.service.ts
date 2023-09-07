import { Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environments/environment';
import {MessagePayload} from '@app/shared/_payload';

@Injectable({providedIn: 'root'})
export class PaymentReceiptService {

    private url: string = environment.apiEndPoint + '/receipt';

    constructor(
        private http: HttpClient
    ) {
    }

    public onCancelPaymentReceipt(payload: any) {
        return this.http.post<MessagePayload>(`${this.url}/cancel-payment-receipt`, payload);
    }
}

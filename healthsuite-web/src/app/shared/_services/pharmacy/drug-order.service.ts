import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    DrugOrderPayload,
    PharmacyReceivedGoodsPayload,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Injectable({
    providedIn: 'root',
})
export class DrugOrderService {
    private url = environment.apiEndPoint + '/drug-order';
    private receiveGoodsUrl = environment.apiEndPoint + '/receive-goods';
    constructor(private httpClient: HttpClient) {}

    public onOrderDrugs(payload: DrugOrderPayload) {
        return this.httpClient.post(`${this.url}/outlet-drug-order`, payload, {
            observe: 'response',
        });
    }

    public onUpdateDrugOrder(payload: DrugOrderPayload) {
        return this.httpClient.patch(`${this.url}/update-order`, payload, {
            observe: 'response',
        });
    }

    /* received goods */
    public onSaveReceivedGoods(payload: PharmacyReceivedGoodsPayload) {
        return this.httpClient.post(`${this.receiveGoodsUrl}/save-received-goods`, payload, {
            observe: 'response',
        });
    }
}

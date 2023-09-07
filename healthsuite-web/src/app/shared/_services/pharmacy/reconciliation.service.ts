import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    DrugDispensePayload,
    OutletReconciliationPayload,
    OutletStockItemPayload,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { ResponsePayload, ValidationMessage } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class ReconciliationService {
    private url = environment.apiEndPoint;
    private drugReconcileUrl = 'drug-reconciliation';
    private drugDispenseUrl = 'drug-dispensing';

    constructor(private httpClient: HttpClient) {}

    /* DRUG RECONCILIATION ENDPOINT */

    public onReconcileStockBalance(payload: OutletReconciliationPayload) {
        return this.httpClient.post<ResponsePayload<any>>(
            `${this.url}/${this.drugReconcileUrl}/outlet-reconciliation`,
            payload,
            {
                observe: 'response',
            }
        );
    }

    public getOutletStockWithAvailableQty(outlet: number, searchText: string) {
        return this.httpClient.get<OutletStockItemPayload[]>(
            `${this.url}/${this.drugReconcileUrl}/search-outlet-stock?searchTerm=${searchText}&outletId=${outlet}`,
            {
                observe: 'response',
            }
        );
    }

    /* DRUG DISPENSE ENDPOINT */
    public onDispenseDrugItem(payload: DrugDispensePayload) {
        return this.httpClient.post<ValidationMessage>(
            `${this.url}/${this.drugDispenseUrl}/dispense-drug`,
            payload,
            {
                observe: 'response',
            }
        );
    }
}

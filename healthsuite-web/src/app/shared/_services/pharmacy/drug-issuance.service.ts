import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    DrugIssuancePayload,
    DrugRequisitionPayload,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Injectable({
    providedIn: 'root',
})
export class DrugIssuanceService {
    private url = environment.apiEndPoint + '/drug-issuance';

    constructor(private httpClient: HttpClient) {}

    public getRequisitionsByDateRange(payload: DrugIssuancePayload) {
        return this.httpClient.post<DrugRequisitionPayload[]>(
            `${this.url}/fetch-by-date`,
            payload,
            {
                observe: 'response',
            }
        );
    }
}

import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { DrugRequisitionPayload } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class RequisitionService {
    public requisitionS: BehaviorSubject<DrugRequisitionPayload>;
    public requisition$: Observable<DrugRequisitionPayload>;

    private url = environment.apiEndPoint + '/drug-requisition';

    constructor(private httpClient: HttpClient) {
        this.requisitionS = new BehaviorSubject<DrugRequisitionPayload>(
            new DrugRequisitionPayload()
        );
        this.requisition$ = this.requisitionS.asObservable();
    }

    public getRequisition() {
        return this.requisitionS.value;
    }

    public toStoreRequisition(payload: DrugRequisitionPayload) {
        return this.httpClient.post<DrugRequisitionPayload>(`${this.url}/to-store-req`, payload, {
            observe: 'response',
        });
    }

    public toOutletRequisition(payload: DrugRequisitionPayload) {
        return this.httpClient.post<DrugRequisitionPayload>(`${this.url}/to-outlet-req`, payload, {
            observe: 'response',
        });
    }

    public saveRequisition(payload: DrugRequisitionPayload) {
        return this.httpClient.post<DrugRequisitionPayload>(
            `${this.url}/save-requisition`,
            payload,
            {
                observe: 'response',
            }
        );
    }

    public onGrantRequisition(payload: DrugRequisitionPayload) {
        return this.httpClient.post<DrugRequisitionPayload>(
            `${this.url}/grant-requisition`,
            payload,
            {
                observe: 'response',
            }
        );
    }
}

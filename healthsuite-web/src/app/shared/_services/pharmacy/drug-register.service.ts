import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Injectable({
    providedIn: 'root',
})
export class DrugRegisterService {
    private url = environment.apiEndPoint + '/drug-register';
    constructor(private httpClient: HttpClient) {}

    public onCreateDrugRegister(payload: DrugRegisterPayload) {
        return this.httpClient.post<DrugRegisterPayload>(`${this.url}/create-one`, payload, {
            observe: 'response',
        });
    }

    public onUpdateDrug(payload: DrugRegisterPayload) {
        return this.httpClient.put<DrugRegisterPayload>(`${this.url}/update-one`, payload, {
            observe: 'response',
        });
    }

    public onUpdateColumn(payload: { column: any; id: any; value: any }) {
        return this.httpClient.patch(`${this.url}/update-drug-column`, payload);
    }

    public onSearchDrugs(searchTerm: {
        searchValue: string;
        searchType: DrugSearchTermEnum;
        loadAvailableStock?: boolean;
        outlet?: number;
    }) {
        const { searchType, searchValue } = searchTerm;
        let endpoint = ``;
        if (searchType === DrugSearchTermEnum.BRAND_NAME) {
            endpoint = `${this.url}/search-by-brand-name?search=${searchValue}`;
        } else if (searchType === DrugSearchTermEnum.GENERIC_NAME) {
            endpoint = `${this.url}/search-by-generic-name?search=${searchValue}`;
        } else if (searchType === DrugSearchTermEnum.GENERIC_OR_BRAND_NAME) {
            let loadStock = false;
            let outletId = 0;

            if (searchTerm.loadAvailableStock === true) {
                loadStock = true;
            }
            if (searchTerm.outlet) {
                outletId = searchTerm.outlet;
            }
            // let loadStock =  searchTerm.loadAvailableStock ? searchTerm.loadAvailableStock : false;
            endpoint = `${this.url}/search-by-brand-generic-name?search=${searchValue}&loadAvailableStock=${loadStock}&outlet=${outletId}`;
        }

        return this.httpClient.get<DrugRegisterPayload[]>(endpoint, { observe: 'response' });
    }
}

import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { ServiceUsageEnum } from '@app/shared/_payload/others/service-usage.enum';
import {
    HmisSharedDropDown,
    InputComponentChanged, MessagePayload,
    ProductServicePayload,
} from '@app/shared/_payload';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root',
})
export class ServiceRegisterService {
    public url: string = environment.apiEndPoint + '/service-register';

    constructor(private http: HttpClient) {}

    public serviceUsageEnumToList(): HmisSharedDropDown[] {
        return Object.keys(ServiceUsageEnum).map((key, value) => ({
            value: ServiceUsageEnum[key],
            name: ServiceUsageEnum[key],
            id: key,
        }));
    }

    public onSearch(payload: { searchBy: string; valueId: number }) {
        return this.http.get<ProductServicePayload[]>(
            `${this.url}/find-by-search-filter?searchBy=${payload.searchBy}&valueId=${payload.valueId}`,
            { observe: 'response' }
        );
    }

    public onUpdateColumn(payload: { column: any; id: any; value: any }) {
        return this.http.patch(`${this.url}/update-service-column`, payload, {
            observe: 'response',
        });
    }

    public onCreateService(payload: ProductServicePayload) {
        return this.http.post<ProductServicePayload>(`${this.url}/create-one`, payload, {
            observe: 'response',
        });
    }

    public onSaveSchemeServicePrice(payload: {schemeId: number, serviceId: number, price: number}) {
        return this.http.post<MessagePayload>(`${this.url}/save-scheme-service-price`, payload);
    }
}

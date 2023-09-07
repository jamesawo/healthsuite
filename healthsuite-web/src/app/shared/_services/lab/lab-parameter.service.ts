import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { LabParameterPayload } from '@app/shared/_payload/lab/lab.payload';

@Injectable({
    providedIn: 'root',
})
export class LabParameterService {
    url: string = environment.apiEndPoint + '/lab-parameter';

    constructor(private http: HttpClient) {}

    public createMany(payload: LabParameterPayload[]) {
        return this.http.post<boolean>(`${this.url}/create-many`, payload);
    }

    public getAll() {
        return this.http.get<LabParameterPayload[]>(`${this.url}/get-all`);
    }

    public removeMany(deleteTempList: LabParameterPayload[]) {
        return this.http.post<boolean>(`${this.url}/remove-many`, deleteTempList);
    }
}

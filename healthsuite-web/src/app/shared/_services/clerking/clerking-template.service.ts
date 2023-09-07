import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    ClerkingTemplateSearchPayload, GeneralClerkDeskPayload,
    OutPatientDeskPayload,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Injectable({ providedIn: 'root' })
export class ClerkingTemplateService {
    url: string = environment.apiEndPoint + '/doctor-template';

    constructor(private http: HttpClient) {}

    public onFindOutPatientDeskTemplate(templateId: number) {
        return this.http.get<OutPatientDeskPayload>(`${this.url}/out-patient-desk/${templateId}`);
    }

    public onFindGeneralClerkTemplate(templateId: number) {
        return this.http.get<GeneralClerkDeskPayload>(`${this.url}/general-clerk-desk/${templateId}`);
    }
}

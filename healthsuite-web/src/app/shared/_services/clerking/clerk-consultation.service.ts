import { environment } from '@environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OutPatientDeskPayload } from '@app/shared/_payload/clerking/outpatient-desk.payload';
import {MessagePayload, ResponsePayload} from '@app/shared/_payload';

@Injectable({ providedIn: 'root' })
export class ClerkConsultationService {
    url: string = environment.apiEndPoint + '/clerk-consultation';

    constructor(private http: HttpClient) {}

    public onSaveTemplateOnly(payload: OutPatientDeskPayload) {
        return this.http.post<boolean>(`${this.url}/save-general-out-patient-template-only`, payload);
    }


    public onSaveGeneralOutPatientDesk(payload: OutPatientDeskPayload) {
        return this.http.post<ResponsePayload<string>>(`${this.url}/save-general-out-patient-desk`, payload, {
            observe: 'response',
        });
    }

    public onSaveGeneralClerking(payload: any) {
        return this.http.post<MessagePayload>(`${this.url}/save-general-clerk-desk`, payload);
    }
}

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {
    ClerkDoctorRequestPayload,
    ClerkDrugRequestPayload,
    ClerkLabRequestPayload,
    ClerkRadiologyRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';
import {HttpClient} from '@angular/common/http';
import {MessagePayload} from '@app/shared/_payload';
import {environment} from '@environments/environment';

@Injectable({
    providedIn: 'root',
})
export class ClerkRequestService {
    url: string = environment.apiEndPoint + '/doctor-request';
    doctorRequestS: BehaviorSubject<ClerkDoctorRequestPayload>;
    doctorRequest$: Observable<ClerkDoctorRequestPayload>;

    constructor(private http: HttpClient) {
        this.doctorRequestS = new BehaviorSubject<ClerkDoctorRequestPayload>(
            new ClerkDoctorRequestPayload()
        );
        this.doctorRequest$ = this.doctorRequestS.asObservable();
    }

    onGetValue(): ClerkDoctorRequestPayload {
        return this.doctorRequestS.value || new ClerkDoctorRequestPayload();
    }

    onSetNext(payload: ClerkDoctorRequestPayload) {
        this.doctorRequestS.next(payload);
    }

    onSetDrugRequest(payload: ClerkDrugRequestPayload) {
        const value = this.doctorRequestS.value;
        value.drugRequest = payload;
        this.onSetNext(value);
    }

    onSetLabRequest(payload: ClerkLabRequestPayload) {
        const res = this.doctorRequestS.value;
        res.labRequest = payload;
        this.onSetNext(res);
    }

    onSetRadiologyRequest(payload: ClerkRadiologyRequestPayload) {
        const res = this.doctorRequestS.value;
        res.radiologyRequest = payload;
        this.onSetNext(res);
    }

    onSaveDocRequest(payload: ClerkDoctorRequestPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-doc-request`, payload);
    }
}

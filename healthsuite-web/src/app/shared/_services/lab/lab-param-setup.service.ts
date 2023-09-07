import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    LabParameterPayload,
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestPayload,
    LabTrackerPayload,
} from '@app/shared/_payload/lab/lab.payload';
import { MessagePayload } from '@app/shared/_payload';
import {LabParameterSetupItemPayload, LabParameterSetupPayload, LabParamRangePayload} from '@app/shared/_payload/lab/lab-setup.payload';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class LabParamSetupService {
    url: string = environment.apiEndPoint + '/lab-parameter-setup';
    parameterItems: BehaviorSubject<LabParameterSetupItemPayload[]> = new BehaviorSubject([]);
    parameterItems$: Observable<LabParameterSetupItemPayload[]>;

    selectedParameter: BehaviorSubject<LabParameterSetupItemPayload> = new BehaviorSubject<LabParameterSetupItemPayload>(null);
    selectedParemeter$: Observable<LabParameterSetupItemPayload>;

    constructor(private http: HttpClient) {
        this.parameterItems$ = this.parameterItems.asObservable();
        this.selectedParemeter$ = this.selectedParameter.asObservable();
    }

    public set selectedParameterValue(value: LabParameterSetupItemPayload) {
        this.selectedParameter.next(null);
        this.selectedParameter.next(value);
    }


    public saveParameterSetup(payload: LabParameterSetupPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-setup`, payload);
    }

    public onGetParameterByTest(testId: number) {
        return this.http.get<LabParameterSetupPayload>(
            `${this.url}/search-setup-by-test/${testId}`
        );
    }

    public onSaveRangeSetup(payload: LabParamRangePayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-range-setup`, payload);
    }

    public onGetParameterItemByTest(testId: number, paramId: number) {
        return this.http.get<LabParamRangePayload>(`${this.url}/search-range-by-test-param/${testId}/${paramId}`);
    }
}

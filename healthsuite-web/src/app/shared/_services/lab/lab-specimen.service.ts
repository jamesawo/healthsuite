import { Injectable } from '@angular/core';
import {environment} from '@environments/environment';
import {HttpClient} from '@angular/common/http';
import {
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestPayload, LabTestRequestSearchWrapper,
    LabTrackerPayload
} from '@app/shared/_payload/lab/lab.payload';
import {MessagePayload} from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class LabSpecimenService {
    url: string = environment.apiEndPoint + '/lab';

    constructor(private http: HttpClient) {}

    public onSaveSampleCollection(payload: LabSpecimenCollectionPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-sample-collection`, payload);
    }

    public onSaveSampleAcknowledgement(payload: LabSpecimenCollectionPayload) {
        return this.http.post<MessagePayload>(`${this.url}/save-sample-acknowledgement`, payload);
    }

    public onSearchLabTestRequest(searchBy: LabSearchByEnum, term: string) {
        return this.http.get<LabTestRequestSearchWrapper>(`${this.url}/search-lab-test?searchBy=${searchBy}&term=${term}`);
    }

    public onGetLabSpecimenCollection(testId: number) {
        return this.http.get<LabSpecimenCollectionPayload>(`${this.url}/get-sample-collection?testId=${testId}`);
    }

    public onTrackLabTest(testId: number, labBillTestRequestId: number) {
        return this.http.get<LabTrackerPayload>(`${this.url}/track-test?testId=${testId}&labBillTestRequestId=${labBillTestRequestId}`);
    }

}

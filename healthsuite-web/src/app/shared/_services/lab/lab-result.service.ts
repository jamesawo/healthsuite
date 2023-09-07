import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BatchOrSingleEnum, LabResultPrepDto, LabTestApprovePayload} from '@app/shared/_payload/lab/lab.payload';
import {MessagePayload} from '@app/shared/_payload';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class LabResultService {
    url: string = environment.apiEndPoint + '/lab-result';
    listen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    listen$: Observable<boolean>;

    // lab result subject shared with different result preparation component
    labResultSubject: BehaviorSubject<LabResultPrepDto> = new BehaviorSubject<LabResultPrepDto>(null);
    labResultSubject$: Observable<LabResultPrepDto>;

    constructor(private http: HttpClient) {
        this.listen$ = this.listen.asObservable();
        this.labResultSubject$ = this.labResultSubject.asObservable();
    }

    public onGetLabTestForResult(itemId: number, type: BatchOrSingleEnum) {
        return this.http.get<LabResultPrepDto>(`${this.url}/get-test/${itemId}/${type}`);
    }

    public onSaveLabTestResult(payload: LabResultPrepDto) {
        return this.http.post<MessagePayload>(`${this.url}/save-test-result`, payload);
    }

    public onViewLabTestResult(itemId: number) {
        return this.http.get<LabResultPrepDto>(`${this.url}/get-test-result/${itemId}`);
    }

    public onApproveLabResult(payload: LabTestApprovePayload) {
        return this.http.post<MessagePayload>(`${this.url}/approve-test-result`, payload);
    }

    public onDownloadLabTestResult(payload: LabTestApprovePayload) {
        const headers = new HttpHeaders();
        headers.append('Accept', 'application/pdf');
        return this.http.post<any>(`${this.url}/download-lab-result-pdf`, payload, {
            headers,
            responseType: 'blob' as 'json',
            observe: 'response',
        });
    }
}

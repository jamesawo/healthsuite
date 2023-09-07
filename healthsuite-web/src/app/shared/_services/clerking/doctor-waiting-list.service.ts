import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { DoctorWaitingPayload } from '@app/shared/_payload/clerking/clerking.payload';
import { HttpClient } from '@angular/common/http';
import { AttendedPayload, ResponsePayload } from '@app/shared/_payload';

@Injectable({ providedIn: 'root' })
export class DoctorWaitingListService {
    url: string = environment.apiEndPoint + '/doctor-waiting-list';
    doctorWaitingList: BehaviorSubject<DoctorWaitingPayload[]>;
    doctorWaitingList$: Observable<DoctorWaitingPayload[]>;

    constructor(private http: HttpClient) {
        this.doctorWaitingList = new BehaviorSubject<DoctorWaitingPayload[]>([]);
        this.doctorWaitingList$ = this.doctorWaitingList.asObservable();
        this.onRefreshWaitingList();
    }

    public onGetDoctorWaitingList() {
        return this.http.get<DoctorWaitingPayload[]>(`${this.url}/all`, {});
    }

    public onGetAttendedList(doctorId: number) {
        return this.http.get<any[]>(`${this.url}/get-attended-by/${doctorId}`);
    }

    public onGetDoctorWaitingListByLocation(locationId: number) {
        return this.http.get<ResponsePayload<DoctorWaitingPayload[]>>(`${this.url}/${locationId}`);
    }

    public onRefreshWaitingList() {
        this.onGetDoctorWaitingList()
            .toPromise()
            .then((data) => {
                this.onSetWaitingList(data);
            });
    }

    public onSetWaitingList(data: DoctorWaitingPayload[]) {
        if (data.length > 0) {
            this.doctorWaitingList.next(data);
        } else {
            this.doctorWaitingList.next([]);
        }
    }

    public onAddToWaitingList(payload: DoctorWaitingPayload) {
        const waitingPayloads = Object.assign([], this.doctorWaitingList.value);
        waitingPayloads.push(payload);
        this.onSetWaitingList(waitingPayloads);
    }

    public onRemoveFromWaitingList(patientId: number, docId: number) {
        let waitingPayloads = Object.assign([], this.doctorWaitingList.value);
        if (waitingPayloads.length > 1) {
            for (let i = 0; i < waitingPayloads.length; i++) {
                if (waitingPayloads[i].patientId === patientId) {
                    waitingPayloads.splice(i, 1);
                    this.onSetWaitingList(waitingPayloads);
                    break;
                }
            }
        } else if (waitingPayloads.length === 1) {
            if (waitingPayloads[0].patientId === patientId) {
                waitingPayloads = [];
                this.onSetWaitingList(waitingPayloads);
            }
        }
        this.onRemoteRemoveFromWaitingList(patientId, docId).toPromise().then();
    }

    public onRemoteRemoveFromWaitingList(patientId: number, doctorId: number) {
        return this.http.delete<boolean>(`${this.url}/remove/${patientId}/${doctorId}`);
    }
}

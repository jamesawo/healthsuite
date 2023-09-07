import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {
    AttendedPayload,
    NurseWaitingHistoryPayload,
    NurseWaitingPayload,
} from '@app/shared/_payload/nurse/nurse.payload';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { ResponsePayload } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class NurseWaitingListService {
    url: string = environment.apiEndPoint + '/patient-waiting-list';
    nurseWaitingListS: BehaviorSubject<NurseWaitingPayload[]>;
    nurseWaitingList$: Observable<NurseWaitingPayload[]>;
    nurseWaitingList: { hasData: boolean; collection: NurseWaitingPayload[] } = {
        hasData: false,
        collection: [],
    };

    constructor(private http: HttpClient) {
        this.nurseWaitingListS = new BehaviorSubject<NurseWaitingPayload[]>([]);
        this.nurseWaitingList$ = this.nurseWaitingListS.asObservable();
        if (!this.nurseWaitingList.hasData) {
            this.prepSubjectList();
        }
    }

    public onRefreshWaitingList() {
        this.onGetAllWaitingList()
            .toPromise()
            .then((value) => {
                if (value.length > 0) {
                    this.nurseWaitingList.collection = value;
                    this.onAssignNextCollection(this.nurseWaitingList.collection);
                } else {
                    this.nurseWaitingList.collection = [];
                    this.onAssignNextCollection(this.nurseWaitingList.collection);
                }
            });
    }

    public onGetAllWaitingList() {
        return this.http.get<NurseWaitingPayload[]>(`${this.url}/nurse-list`);
    }

    public onAddToWaitingList(waitingPayload: NurseWaitingPayload) {
        if (waitingPayload) {
            this.nurseWaitingList.collection.push(waitingPayload);
            this.onAssignNextCollection(this.nurseWaitingList.collection);
        }
    }

    public onRemoveFromWaitingList(patientId: number) {
        if (this.nurseWaitingList.collection.length > 1) {
            for (let i = 0; i < this.nurseWaitingList.collection.length; i++) {
                if (this.nurseWaitingList.collection[i].patientId == patientId) {
                    this.nurseWaitingList.collection.splice(i, 1);
                    this.onAssignNextCollection(this.nurseWaitingList.collection);
                    break;
                }
            }
        } else if (this.nurseWaitingList.collection.length == 1) {
            if (this.nurseWaitingList.collection[0].patientId == patientId) {
                this.nurseWaitingList.collection = [];
                this.onAssignNextCollection(this.nurseWaitingList.collection);
            }
        } else {
        }
    }

    public onRemoteRemoveFromWaitingList(payload: NurseWaitingHistoryPayload) {
        return this.http.patch<ResponsePayload<boolean>>(`${this.url}/remove-patient`, payload);
    }

    public onAssignNextCollection(waitingPayload: NurseWaitingPayload[]) {
        this.nurseWaitingListS.next(waitingPayload);
    }

    public onGetAttendedList(payload: AttendedPayload) {
        return this.http.post<NurseWaitingPayload[]>(`${this.url}/get-attended-list`, payload);
    }

    private prepSubjectList() {
        this.nurseWaitingList.hasData = true;
        this.onGetAllWaitingList()
            .toPromise()
            .then((value) => {
                this.nurseWaitingList.collection = value;
                this.nurseWaitingListS.next(value);
            });
    }
}

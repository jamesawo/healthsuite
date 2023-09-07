import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subscription } from 'rxjs';
import { NurseWaitingPayload, WaitingViewTypeEnum } from '@app/shared/_payload';
import { SocketClientService } from '@app/shared/_services/common/socket-client.service';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseWaitingListService } from '@app/shared/_services/nurse/nurse-waiting-list.service';
import { DepartmentPayload } from '@app/modules/settings';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';
import { DoctorWaitingListService } from '@app/shared/_services/clerking/doctor-waiting-list.service';
import { DoctorWaitingPayload } from '@app/shared/_payload/clerking/clerking.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';

@Component({
    selector: 'app-patient-waiting-list',
    templateUrl: './patient-waiting-list.component.html',
    styleUrls: ['./patient-waiting-list.component.css'],
})
export class PatientWaitingListComponent implements OnInit, OnDestroy {
    @Input() public props: { viewType: WaitingViewTypeEnum };
    @Input() public updateCount: (arg: number) => void;
    @Output() public selected: EventEmitter<NurseWaitingPayload> = new EventEmitter();
    public collection: any[] = [];
    public currentLoc: DepartmentPayload = new DepartmentPayload();
    public searchTerm = '';
    public scheme: PatientCategoryEnum.SCHEME;
    public collCopy: NurseWaitingPayload[] = [];
    public selectedIndex: number = undefined;
    public isLoading = true;

    private subscription: Subscription = new Subscription();
    private currentUsr: UserPayload = new UserPayload();

    constructor(
        private socketService: SocketClientService,
        private commonService: CommonService,
        private nurseWaitingListService: NurseWaitingListService,
        private doctorWaitingService: DoctorWaitingListService
    ) {}

    ngOnInit(): void {
        this.currentLoc = this.commonService.getCurrentLocation();
        this.currentUsr = this.commonService.getCurrentUser();

        if (this.props?.viewType === WaitingViewTypeEnum.NURSE) {
            this.onGetNurseWaitingList();
        } else if (this.props?.viewType === WaitingViewTypeEnum.DOCTOR) {
            this.onGetDoctorWaitingList();
        } else {
            console.log('no props view type');
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onGetNurseWaitingList() {
        this.subscription.add(
            this.nurseWaitingListService.nurseWaitingList$.subscribe(
                (data) => {
                    this.isLoading = true;
                    if (data.length) {
                        this.onFilterNurseListCollection(data);
                    } else {
                        this.onEmptyCollection();
                        this.isLoading = false;
                        this.onUpdateCount();
                    }
                },
                (error) => {
                    this.isLoading = false;
                    console.log(error);
                }
            )
        );
    }

    public onGetDoctorWaitingList() {
        this.subscription.add(
            this.doctorWaitingService.doctorWaitingList$.subscribe(
                (data) => {
                    this.isLoading = true;
                    if (data.length) {
                        this.onFilterDoctorListCollection(data);
                    } else {
                        this.onEmptyCollection();
                        this.isLoading = false;
                        this.onUpdateCount();
                    }
                },
                (error) => {
                    this.isLoading = false;
                    console.log(error);
                }
            )
        );
    }

    public onEmptyCollection() {
        this.collCopy = [];
        this.collection = [];
    }

    public onClearSelected() {
        this.selectedIndex = undefined;
    }

    public onPatientSelected(data: NurseWaitingPayload, i: number) {
        this.selectedIndex = i;
        this.selected.emit(data);
    }

    public search(value) {
        const term = this.searchTerm;
        if (!value) {
            this.copyCollection();
        }
        this.collCopy = Object.assign([], this.collection).filter(
            (item) => item.patientName.toLowerCase().indexOf(value.toLowerCase()) > -1
        );
    }

    public onRefreshList() {
        this.isLoading = true;
        if (this.props?.viewType === WaitingViewTypeEnum.NURSE) {
            this.nurseWaitingListService.onRefreshWaitingList();
        } else if (this.props?.viewType === WaitingViewTypeEnum.DOCTOR) {
            this.doctorWaitingService.onRefreshWaitingList();
        }
        this.onUpdateCount();
        // this.isLoading = false;
    }

    private onFilterNurseListCollection(data: NurseWaitingPayload[]) {
        this.onEmptyCollection();
        const locId: number = this.currentLoc.id;
        if (data.length) {
            for (const waitingPayload of data) {
                if (waitingPayload?.clinicIds && waitingPayload.clinicIds.includes(locId)) {
                    this.onCheckIsExistBeforePush(waitingPayload);
                }
            }
        }
        this.isLoading = false;
    }

    private onFilterDoctorListCollection(data: DoctorWaitingPayload[]) {
        this.onEmptyCollection();
        const locId: number = this.currentLoc?.id;
        const docId: number = this.currentUsr?.id;
        if (data.length) {
            for (const waitingPayload of data) {
                if (waitingPayload.doctorId && waitingPayload.doctorId === docId) {
                    this.onCheckIsExistBeforePush(waitingPayload);
                } else if (waitingPayload.clinicId && waitingPayload.clinicId === locId) {
                    this.onCheckIsExistBeforePush(waitingPayload);
                }
            }
        }
        this.isLoading = false;
    }

    private onCheckIsExistBeforePush(payload: NurseWaitingPayload | DoctorWaitingPayload) {
        const found = this.collection.find((value) => value.patientId === payload.patientId);
        if (!found) {
            this.collection.push(payload);
            this.copyCollection();
        }
        return;
    }

    private copyCollection() {
        this.collCopy = Object.assign([], this.collection);
        this.onUpdateCount();
    }

    private onUpdateCount() {
        this.updateCount(this.collCopy.length);
    }
}

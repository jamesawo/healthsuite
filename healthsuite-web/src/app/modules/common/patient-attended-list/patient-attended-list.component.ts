import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AttendedPayload, NurseWaitingPayload, WaitingViewTypeEnum } from '@app/shared/_payload';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseWaitingListService } from '@app/shared/_services/nurse/nurse-waiting-list.service';
import { Subscription } from 'rxjs';
import { DoctorWaitingListService } from '@app/shared/_services/clerking/doctor-waiting-list.service';

@Component({
    selector: 'app-patient-attended-list',
    templateUrl: './patient-attended-list.component.html',
    styleUrls: ['./patient-attended-list.component.css'],
})
export class PatientAttendedListComponent implements OnInit, OnDestroy {
    @Input() public updateCount: (arg: number) => void;
    @Output() public selected: EventEmitter<NurseWaitingPayload> = new EventEmitter();
    @Input() public props: { viewType: WaitingViewTypeEnum };
    searchTerm: string;
    isLoading = false;
    collCopy: NurseWaitingPayload[] = [];
    collection: NurseWaitingPayload[] = [];
    selectedIndex: number;
    scheme: PatientCategoryEnum = PatientCategoryEnum.SCHEME;
    attendedPayload: AttendedPayload = new AttendedPayload();

    private subscription: Subscription = new Subscription();
    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private waitingListService: NurseWaitingListService,
        private doctorWaitingListService: DoctorWaitingListService
    ) {}

    ngOnInit(): void {
        this.onInitializePayload();
        if (this.props?.viewType === WaitingViewTypeEnum.NURSE) {
            this.onRefreshNurseWaitingList();
        } else if (this.props?.viewType === WaitingViewTypeEnum.DOCTOR) {
            this.onRefreshDoctorAttendedList(this.attendedPayload.attendedBy.id);
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    search(value: string) {
        const term = this.searchTerm;
        if (!value) {
            this.copyCollection();
        }
        this.collCopy = Object.assign([], this.collection).filter(
            (item) => item.patientName.toLowerCase().indexOf(value.toLowerCase()) > -1
        );
    }

    onRefreshNurseWaitingList() {
        this.isLoading = true;
        this.subscription.add(
            this.waitingListService.onGetAttendedList(this.attendedPayload).subscribe(
                (res) => {
                    this.onPrepData(res);
                },
                (error) => {
                    this.isLoading = false;
                    console.log(error);
                }
            )
        );
    }

    onRefreshDoctorAttendedList(doctorId: number) {
        this.isLoading = true;
        this.subscription.add(
            this.doctorWaitingListService.onGetAttendedList(doctorId).subscribe(
                (res) => {
                    this.onPrepData(res);
                },
                (error) => {
                    this.isLoading = false;
                    console.log(error);
                }
            )
        );
    }

    onPatientSelected(data: NurseWaitingPayload, i: number) {
        this.selectedIndex = i;
        this.selected.emit(data);
    }

    public onRefreshWaitingList() {
        if (this.props?.viewType === WaitingViewTypeEnum.NURSE) {
            this.onRefreshNurseWaitingList();
        } else if (this.props?.viewType === WaitingViewTypeEnum.DOCTOR) {
            this.onRefreshDoctorAttendedList(this.attendedPayload.attendedBy.id);
        }
    }

    private onPrepData(list: any[]) {
        if (list.length) {
            this.collection = list;
            this.copyCollection();
        } else {
            this.collection = [];
            this.copyCollection();
        }
        this.isLoading = false;
        this.onRefreshCount();
    }

    private copyCollection() {
        this.collCopy = Object.assign([], this.collection);
        this.onRefreshCount();
    }

    private onInitializePayload() {
        this.attendedPayload.attendedBy = this.commonService.getCurrentUser();
        this.attendedPayload.clinic = this.commonService.getCurrentLocation();
    }

    private onRefreshCount() {
        this.updateCount(this.collCopy.length);
    }
}

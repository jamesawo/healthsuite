import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { PatientWaitingListComponent } from '@app/modules/common/patient-waiting-list/patient-waiting-list.component';
import {
    NurseWaitingHistoryPayload,
    NurseWaitingPayload,
    PatientVitalSign,
    ValidationMessage,
    WaitingStatusEnum,
    WaitingViewTypeEnum,
} from '@app/shared/_payload';
import { EmrService } from '@app/shared/_services';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientVitalSignService } from '@app/shared/_services/nurse/patient-vital-sign.service';
import { NurseWaitingListService } from '@app/shared/_services/nurse/nurse-waiting-list.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { UserSearchComponent } from '@app/modules/common/user-search/user-search.component';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-nurse-vital-sign-capture',
    templateUrl: './nurse-vital-sign-capture.component.html',
    styleUrls: ['./nurse-vital-sign-capture.component.css'],
})
export class NurseVitalSignCaptureComponent implements OnInit, OnDestroy {
    @ViewChild('listComponent') listComponent: PatientWaitingListComponent;
    @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
    public payload: PatientVitalSign = new PatientVitalSign();
    public activeView: 'waiting' | 'attended' = 'waiting';
    public nurse = WaitingViewTypeEnum.NURSE;
    public waitingCount = 0;
    public attendedCount = 0;
    private subscription: Subscription = new Subscription();

    constructor(
        private emrService: EmrService,
        private vitalSignService: PatientVitalSignService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private waitingListService: NurseWaitingListService
    ) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.CLINIC_LOCATION);
        if (isMatch) {
            this.onInitializeComponent();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onUpdateCount = (value: number) => {
        setTimeout(() => {
            this.waitingCount = value;
        }, 0);
    };

    public onUpdateAttendedCount = (value: number) => {
        setTimeout(() => {
            this.attendedCount = value;
        }, 0);
    };

    public onClearSelected(): void {
        this.payload = new PatientVitalSign();
        this.listComponent.onClearSelected();
        this.userSearchComponent.onClearField();
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onCalculateBmi() {
        const height = Number(this.payload.height);
        const weight = Number(this.payload.weight);
        const bmi = this.commonService.calculateBmi(weight, height);
        const dec = (Math.round(bmi * 100) / 100).toFixed(2);
        this.payload.bodyMassIndex = Number(dec);
    }

    public onPatientSelected(payload: NurseWaitingPayload) {
        if (payload?.patientId) {
            this.spinner.show().then();
            this.subscription.add(
                this.emrService.onFindPatientById(payload.patientId).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res.body) {
                            this.payload.patient = res.body;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(
                            'Something Went Wrong, Refresh And Try Again',
                            HmisConstants.ERR_TITLE
                        );
                    }
                )
            );
        }
    }

    public onConsultantSelected(payload: UserPayload) {
        if (payload) {
            this.payload.assignTo = payload;
        }
    }

    public async onResetForm(): Promise<void> {
        if ((this.payload?.patient?.patientId && this.payload.weight) || this.payload.height) {
            const areYouSure = await this.commonService.askAreYouSure(
                'ARE YOU SURE ?',
                'YOU HAVE UNSAVED ENTRY!',
                'warning'
            );
            if (areYouSure.isConfirmed === true) {
                this.onClearSelected();
            }
        } else {
            this.onClearSelected();
        }
    }

    public onSubmitVitalSign() {
        const res = this.onValidate();
        if (res.status === false) {
            this.toast.error(res.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.vitalSignService.onSubmitVitalSign(this.payload).subscribe(
                (value) => {
                    this.spinner.hide().then();
                    if (value) {
                        this.toast.success(
                            HmisConstants.LAST_ACTION_SUCCESS,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onRemoveFromWaitingList();
                        this.onClearSelected();
                        this.onInitializeComponent();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error.error.message);
                    this.toast.error(HmisConstants.ERR_SERVER_ERROR, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onChangeActiveView(viewType: 'waiting' | 'attended') {
        if (viewType) {
            this.activeView = viewType;
        }
    }

    public onAddToAttended() {
        this.onUpdateAttendedCount(this.attendedCount + 1);
    }

    public onPatientSearchSelected(patient: PatientPayload) {
        if (patient && patient.patientId) {
            this.payload.patient = patient;
        }
    }

    private onRemoveFromWaitingList() {
        this.waitingListService.onRemoveFromWaitingList(this.payload.patient.patientId);
        this.onAddToAttended();

        const payload: NurseWaitingHistoryPayload = new NurseWaitingHistoryPayload();
        payload.attendedBy = this.commonService.getCurrentUser();
        payload.clinic = this.commonService.getCurrentLocation();
        payload.patient = this.payload.patient;
        payload.status = WaitingStatusEnum.ATTENDED;
        this.subscription.add(
            this.waitingListService.onRemoteRemoveFromWaitingList(payload).subscribe(
                (res) => {},
                (error) => {
                    this.toast.error(
                        `There was a problem with last patient capture`,
                        'Error Occurred'
                    );
                }
            )
        );
    }

    private onValidate(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.payload.patient.patientId) {
            res.status = false;
            res.message += `Select a Patient <br>`;
        }
        /*
        if (!this.payload.height) {
            res.status = false;
            res.message += `Height is required <br>`;
        }
        if (!this.payload.weight) {
            res.status = false;
            res.message += `Weight is required <br>`;
        }
         */
        return res;
    }

    private onInitializeComponent() {
        this.payload.capturedBy = this.commonService.getCurrentUser();
        this.payload.captureFromLocation = this.commonService.getCurrentLocation();
        this.payload.isNurse = true;
    }
}

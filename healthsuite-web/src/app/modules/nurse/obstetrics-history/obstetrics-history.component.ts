import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { DateType } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import Stepper from 'bs-stepper';
import { ObstetricsHistoryPayload } from '@app/shared/_payload/nurse/nurse-anc.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {ObGeneralFormComponent} from '@app/modules/nurse/obstetrics-history/components/ob-general-form/ob-general-form.component';

@Component({
    selector: 'app-obstetrics-history',
    templateUrl: './obstetrics-history.component.html',
    styleUrls: ['./obstetrics-history.component.css'],
})
export class ObstetricsHistoryComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    @ViewChild('obGeneralFormComponent') obGeneralFormComponent: ObGeneralFormComponent;
    public payload: ObstetricsHistoryPayload = new ObstetricsHistoryPayload();
    public start = DateType.START;
    public end = DateType.END;
    public active = 1;
    public stepPosition = 1;

    private stepper: Stepper;
    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {
        this.stepper = new Stepper(document.querySelector('#bsStepper1'), {
            linear: false,
            animation: true,
        });
        this.onResetPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
        }
    }

    public onNext() {
        this.stepPosition += 1;
        this.stepper.next();
        this.commonService.onScroll(360, 100);
    }

    public onPrevious() {
        this.stepPosition -= 1;
        this.stepper.previous();
        this.commonService.onScroll(360, 100);
    }

    public onSubmit() {
        if (!this.payload.patient || !this.payload.patient.patientId) {
            this.toast.error('Select Patient', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.nurseService.onSaveObstetricsHistory(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                    }
                    this.onResetPayload();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.INTERNAL_SERVER_ERROR);
                }
            )
        );
    }

    public onReset() {
        this.commonService
            .askAreYouSure('CLEAR THE FORM', 'ARE YOU SURE ?', 'warning')
            .then((res) => {
                if (res.isConfirmed === true) {
                    this.onResetPayload();
                }
            });
    }

    private onResetPayload() {
        this.payload = new ObstetricsHistoryPayload();
        this.payload.clerkedBy = this.commonService.getCurrentUser();
        this.payload.location = this.commonService.getCurrentLocation();
        this.stepper.to(1);
        this.obGeneralFormComponent?.clearDateField();
    }
}

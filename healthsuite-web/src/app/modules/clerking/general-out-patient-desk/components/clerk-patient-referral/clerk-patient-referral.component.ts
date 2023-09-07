import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { PatientClinicReferralService } from '@app/shared/_services/emr/patient-clinic-referral.service';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import {
    PatientClinicReferralPayload,
    PatientPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { ValidationMessage } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-clerk-patient-referral',
    templateUrl: './clerk-patient-referral.component.html',
    styleUrls: ['./clerk-patient-referral.component.css'],
})
export class ClerkPatientReferralComponent implements OnInit, OnDestroy {
    public payload: PatientClinicReferralPayload = new PatientClinicReferralPayload();
    public data: { patientPayload: PatientPayload };

    private subscription: Subscription = new Subscription();
    constructor(
        private referralService: PatientClinicReferralService,
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.payload.patient = this.data.patientPayload;
        this.payload.referredFromClinic = this.commonService.getCurrentLocation();
        this.payload.referredBy = this.commonService.getCurrentUser();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onDoctorSelected(doctor: UserPayload) {
        if (doctor) {
            this.payload.referredToDoctor = doctor;
        }
    }

    public onClinicSelected(departmentPayload: DepartmentPayload) {
        if (departmentPayload) {
            this.payload.referredToClinic = departmentPayload;
        }
    }

    public onSubmit() {
        const valid = this.onValidateBeforeSave();
        if (valid.status === false) {
            this.toast.error(valid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.referralService.onReferPatientToClinic(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                        this.onCloseModal();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    private onValidateBeforeSave(): ValidationMessage {
        const resp: ValidationMessage = { message: '', status: true };
        if (!this.payload.referredToClinic) {
            resp.message += 'Select a clinic first. <br>';
            resp.status = false;
        }

        if (!this.payload.referralNotes) {
            resp.message += 'Referral Note is required. <br>';
            resp.status = false;
        }

        return resp;
    }

    private onCloseModal() {
        this.commonService.onCloseModal();
    }
}

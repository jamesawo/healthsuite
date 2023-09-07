import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { Subscription } from 'rxjs';
import { NursePatientFluid } from '@app/shared/_payload/nurse/nurse-care.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-patient-fluid-balance-capture',
    templateUrl: './patient-fluid-balance-capture.component.html',
    styleUrls: ['./patient-fluid-balance-capture.component.css'],
})
export class PatientFluidBalanceCaptureComponent implements OnInit, OnDestroy {
    public data: { patientPayload: PatientPayload };
    public payload: NursePatientFluid = new NursePatientFluid();

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {
        if (this.data.patientPayload && this.data.patientPayload.patientId) {
            this.payload.patient = this.data.patientPayload;
            this.payload.captureBy = this.commonService.getCurrentUser();
            this.payload.capturedFrom = this.commonService.getCurrentLocation();
        } else {
            this.toast.error('Patient is Required');
            this.commonService.onCloseModal();
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onSubmit() {
        this.spinner.show().then();
        this.subscription.add(
            this.nurseService.onSavePatientFluidBalance(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message.length) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                        this.onCloseModal();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.INTERNAL_SERVER_ERROR);
                }
            )
        );
    }

    onResetForm() {}

    onCloseModal() {
        this.commonService.onCloseModal();
    }

    onCalculateIntakeTotal(event: any) {
        const { blood, tube, oral, iv } = this.payload;
        const totalIntake = Number(blood) + Number(tube) + Number(oral) + Number(iv);
        this.payload.totalIntake = totalIntake;
        if (event) {
            this.onCalculateBalance(totalIntake, this.onCalculateOutputTotal(undefined));
        }
        return totalIntake;
    }

    onCalculateOutputTotal(event: any) {
        const { urine, tubeVomit, drainFaeces } = this.payload;
        const totalOutput = Number(urine) + Number(tubeVomit) + Number(drainFaeces);
        this.payload.totalOutput = totalOutput;
        if (event) {
            this.onCalculateBalance(this.onCalculateIntakeTotal(undefined), totalOutput);
        }
        return totalOutput;
    }

    onCalculateBalance(totalIntake: number, totalOutput: number) {
        this.payload.balance = totalIntake - totalOutput;
    }
}

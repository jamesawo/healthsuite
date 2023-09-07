import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-patient-icu-bounce-back',
    templateUrl: './patient-icu-bounce-back.component.html',
    styleUrls: ['./patient-icu-bounce-back.component.css'],
})
export class PatientIcuBounceBackComponent implements OnInit, OnDestroy {
    data: { patientPayload: PatientPayload };
    reasonForBounce = '';

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {
        // if (!this.data.patientPayload?.patientId) {
        //     this.toast.error('Patient Is Required', HmisConstants.VALIDATION_ERR);
        //     return;
        // }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSubmit() {
        if (!this.reasonForBounce) {
            this.toast.error('Enter a reason for bounce back', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        const payload = {
            patient: this.data.patientPayload,
            reason: this.reasonForBounce,
            capturedFrom: this.commonService.getCurrentLocation(),
            capturedBy: this.commonService.getCurrentUser()
        };
        this.subscription.add(
            this.nurseService.onSaveIcuBounceBack(payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res?.message?.length) {
                        this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                        this.commonService.onCloseModal();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE, HmisConstants.ERR_TITLE);
                    console.log(error);
                }
            )
        );
    }
}

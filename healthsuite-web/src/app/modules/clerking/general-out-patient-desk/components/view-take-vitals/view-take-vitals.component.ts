import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientVitalSign } from '@app/shared/_payload';
import { IVitalTabData } from '@app/shared/_payload/clerking/clerking.payload';
import { PatientVitalSignService } from '@app/shared/_services/nurse/patient-vital-sign.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-view-take-vitals',
    templateUrl: './view-take-vitals.component.html',
    styleUrls: ['./view-take-vitals.component.css'],
})
export class ViewTakeVitalsComponent implements OnInit, OnDestroy {
    public data: { vitalTabData: IVitalTabData };
    public vitalPayload: PatientVitalSign = new PatientVitalSign();
    public tempPatient: PatientPayload = undefined;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private nurseVitalService: PatientVitalSignService,
        private spinnerService: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        if (this.data.vitalTabData.patientId) {
            if (!this.isTakeVitalView()) {
                this.spinnerService.show().then();
                this.subscription.add(
                    this.nurseVitalService
                        .onGetPatientLastCapturedVital(this.data.vitalTabData.patientId)
                        .subscribe(
                            (res) => {
                                this.spinnerService.hide().then();
                                if (res) {
                                    this.vitalPayload = res;
                                } else {
                                    this.toast.warning('No Vital Found', HmisConstants.ERR_TITLE);
                                }
                            },
                            (error) => {
                                this.spinnerService.hide().then();
                                this.toast.error(
                                    'Failed To Get Vital',
                                    HmisConstants.ERR_SERVER_ERROR
                                );
                            }
                        )
                );
            }
        } else {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public isTakeVitalView(): boolean {
        return this.data.vitalTabData.isTakeVital;
    }

    onNumberKey(event: any) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    setVitalPayload() {
        this.vitalPayload.patient = new PatientPayload(this.data.vitalTabData.patientId);
        this.vitalPayload.isDoctor = true;
        this.vitalPayload.capturedBy = this.commonService.getCurrentUser();
        this.vitalPayload.captureFromLocation = this.commonService.getCurrentLocation();
    }

    onTakeVital() {
        if (!this.data.vitalTabData.patientId) {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
            return;
        }
        if (!this.vitalPayload.weight || !this.vitalPayload.height) {
            this.toast.error('Capture Height/Weight', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.setVitalPayload();
        this.spinnerService.show().then();
        this.subscription.add(
            this.nurseVitalService.onSubmitVitalSign(this.vitalPayload).subscribe(
                (value) => {
                    this.spinnerService.hide().then();
                    if (value) {
                        this.toast.success(
                            HmisConstants.LAST_ACTION_SUCCESS,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.vitalPayload = new PatientVitalSign();
                    }
                    this.commonService.onCloseModal();
                },
                (error) => {
                    this.spinnerService.hide().then();
                    this.toast.error(HmisConstants.ERR_SERVER_ERROR, HmisConstants.ERR_TITLE);
                }
            )
        );
    }
}

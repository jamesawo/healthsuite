import {Component, OnDestroy, OnInit} from '@angular/core';
import { ProductServicePayload } from '@app/shared/_payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';
import { ToastrService } from 'ngx-toastr';
import { BillViewTypeEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import {
    ClerkDoctorRequestPayload,
    ClerkRadiologyItemsPayload,
    ClerkRadiologyRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {Subscription} from 'rxjs';
import {NgxSpinnerService} from 'ngx-spinner';

@Component({
    selector: 'app-clerk-xray-request',
    templateUrl: './clerk-xray-request.component.html',
    styleUrls: ['./clerk-xray-request.component.css'],
})
export class ClerkXrayRequestComponent implements OnInit, OnDestroy  {
    data: { patientPayload: PatientPayload, process: boolean };
    public payload = new ClerkRadiologyRequestPayload();
    radiology = BillViewTypeEnum.RADIOLOGY_BILL;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private clerkRequestService: ClerkRequestService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService

    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }


    public onServiceSelected(productServicePayload: ProductServicePayload) {
        if (productServicePayload && !this.hasDuplicate(productServicePayload)) {
            const item = new ClerkRadiologyItemsPayload();
            item.service = productServicePayload;
            item.comment = '';
            item.examinationRequired = '';
            this.payload.radiologyItems.push(item);
        }
    }

    hasDuplicate(payload: ProductServicePayload): boolean {
        let flag = false;
        this.payload.radiologyItems.forEach((item) => {
            if (item.service.id === payload.id) {
                flag = true;
            }
        });
        return flag;
    }

    isAllCheckBoxChecked(): boolean {
        return false;
    }

    checkAllCheckBox(event: any) {}

    onRemoveAllCheckedBillItems() {}

    onSaveLabRequest() {
        if (!this.payload.radiologyItems.length) {
            this.toast.warning('Select Radiology Service First', HmisConstants.VALIDATION_ERR);
            return;
        }
        if (this.data.process === true) {
            this.onProcessLabRequest();
            return;
        } else {
            this.clerkRequestService.onSetRadiologyRequest(this.payload);
            this.onCloseModal();
        }
    }

    onProcessLabRequest() {
        const doctorRequest = new ClerkDoctorRequestPayload();
        doctorRequest.patient = this.data.patientPayload;
        doctorRequest.doctor = this.commonService.getCurrentUser();
        doctorRequest.department = this.commonService.getCurrentLocation();
        doctorRequest.radiologyRequest = this.payload;
        doctorRequest.labRequest = null;

        this.spinner.show().then();
        this.subscription.add(
            this.clerkRequestService.onSaveDocRequest(doctorRequest).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                        this.onCloseModal();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.FAILED_RESPONSE);
                    this.onCloseModal();
                }
            )
        );
    }

    onCloseModal() {
        this.commonService.onCloseModal();
    }

    onPatientCurrentStatusChange(value: string) {
        if (value) {
            this.payload.patientCurrentStatus = value;
        }
    }
}

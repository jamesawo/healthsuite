import {Component, OnDestroy, OnInit} from '@angular/core';
import {ProductServicePayload, SharedPayload} from '@app/shared/_payload';
import {
    ClerkDoctorRequestPayload,
    ClerkLabItemsPayload,
    ClerkLabRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {BillViewTypeEnum} from '@app/shared/_payload/bill-payment/bill.payload';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {NgxSpinnerService} from 'ngx-spinner';
import {Subscription} from 'rxjs';

@Component({
    selector: 'app-clerk-lab-request',
    templateUrl: './clerk-lab-request.component.html',
    styleUrls: ['./clerk-lab-request.component.css'],
})
export class ClerkLabRequestComponent implements OnInit, OnDestroy {
    data: { patientPayload: PatientPayload; process: boolean };

    public payload: ClerkLabRequestPayload;
    public labView = BillViewTypeEnum.LAB_BILL;

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private clerkRequestService: ClerkRequestService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {
        this.payload = new ClerkLabRequestPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onServiceSelected(service: ProductServicePayload) {
        if (service && !this.isDuplicateService(service)) {
            const labItem = new ClerkLabItemsPayload();
            labItem.service = service;
            labItem.comment = '';
            labItem.specimen = '';
            this.payload.labItems.push(labItem);
        }
    }

    isDuplicateService(payload: ProductServicePayload): boolean {
        let flag = false;
        this.payload.labItems.forEach((item) => {
            if (item.service.id === payload.id) {
                flag = true;
            }
        });
        return flag;
    }

    isAllCheckBoxChecked(): boolean {
        return false;
    }

    onCloseModal() {
        this.commonService.onCloseModal();
    }

    onSaveLabRequest() { // here
        if (!this.payload.labItems.length) {
            this.toast.warning('Lab Service Is Required', HmisConstants.VALIDATION_ERR);
            return;
        }

        if (this.data.process === true) {
            this.onProcessLabRequest();
            return;
        } else {
            this.clerkRequestService.onSetLabRequest(this.payload);
            this.onCloseModal();
        }
    }

    onProcessLabRequest() {
        const doctorRequest = new ClerkDoctorRequestPayload();
        doctorRequest.patient = this.data.patientPayload;
        doctorRequest.doctor = this.commonService.getCurrentUser();
        doctorRequest.department = this.commonService.getCurrentLocation();
        doctorRequest.labRequest = this.payload;

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

    checkAllCheckBox(event: any) {}

    onRemoveAllCheckedBillItems() {}

    onSpecimenSelected(sharedPayload: SharedPayload, i: number) {
        console.log(sharedPayload);
        if (sharedPayload) {
            this.payload.labItems[i].specimen = sharedPayload.name;
        }
    }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestItem,
    NewOrEditSampleEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { Subscription } from 'rxjs';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ValidationMessage } from '@app/shared/_payload';

@Component({
    selector: 'app-specimen-collection-acknowledgement',
    templateUrl: './specimen-collection-acknowledgement.component.html',
    styleUrls: ['./specimen-collection-acknowledgement.component.css'],
})
export class SpecimenCollectionAcknowledgementComponent implements OnInit, OnDestroy {
    public newView = NewOrEditSampleEnum.NEW;
    public editView = NewOrEditSampleEnum.EDIT;
    public invoiceNumber = LabSearchByEnum.INVOICE_NUMBER;
    public patient = LabSearchByEnum.PATIENT;
    public payload: LabSpecimenCollectionPayload;

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onInitPayload() {
        this.payload = new LabSpecimenCollectionPayload();
        this.payload.capturedBy = this.commonService.getCurrentUser();
        this.payload.capturedFrom = this.commonService.getCurrentLocation();
        this.payload.newOrEditSampleEnum = NewOrEditSampleEnum.NEW;
        this.payload.searchByEnum = LabSearchByEnum.INVOICE_NUMBER;
    }

    public onTypeChange(value: NewOrEditSampleEnum) {
        this.payload.newOrEditSampleEnum = value;
    }

    public onSpecimenAckChange(value: string, item: LabTestRequestItem) {
        if (value) {
            item.acknowledgement = value;
        }
    }

    public onSubmit() {
        this.spinner.show().then();
        const message = this.isAcknowledgmentSelected();
        if (message.status === false) {
            this.spinner.hide().then();
            this.toast.error(message.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.subscription.add(
            this.labService.onSaveSampleAcknowledgement(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(
                        HmisConstants.INTERNAL_SERVER_ERROR,
                        HmisConstants.FAILED_RESPONSE
                    );
                    this.onInitPayload();
                }
            )
        );
    }

    private isAcknowledgmentSelected(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        const labTestRequestItems = this.payload.labBillTestRequest.testItems;
        if (labTestRequestItems && labTestRequestItems.length) {
            labTestRequestItems.forEach((value) => {
                if (!value.acknowledgement) {
                    res.status = false;
                    res.message = 'Select All Item Ack. Status';
                }
            });
            return res;
        } else {
            res.status = false;
            res.message = 'Search For Patient Lab Test First';
            return res;
        }
    }
}

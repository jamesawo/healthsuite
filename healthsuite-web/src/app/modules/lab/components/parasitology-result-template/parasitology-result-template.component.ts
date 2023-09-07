import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    LabResultPrepDto,
    LabTestApprovePayload,
    LabVerificationViewEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LabResultService } from '@app/shared/_services/lab/lab-result.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import {
    ILabResultComponent,
    LabParasitologyTemplatePayload,
} from '@app/shared/_payload/lab/lab-result.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {
    LabDepartmentTypeEnum,
    LabResultPrepOrVerifyEnum,
} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-result-template',
    templateUrl: './parasitology-result-template.component.html',
    styleUrls: ['./parasitology-result-template.component.css'],
})
export class ParasitologyResultTemplateComponent implements OnInit, OnDestroy, ILabResultComponent {
    /*
    payload is passed into this component from its parent or requesting component
    (ex: med-result-preparation.component.ts onLoadResultComponent())
    */
    public payload: LabResultPrepDto;
    public resultPreparation: LabResultPrepOrVerifyEnum = LabResultPrepOrVerifyEnum.RESULT_PREPARATION;
    public pathologistVerify = LabVerificationViewEnum.PATHOLOGIST;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private labResultService: LabResultService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {
        if (!this.payload) {
            this.payload = new LabResultPrepDto();
            this.onInitForm();
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onLabSaveResultPreparation() {
        // save the result preparation template
        this.onPrepPayload();
        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onSaveLabTestResult(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.toast.success(
                            HmisConstants.OK_SUCCESS_RESPONSE,
                            HmisConstants.UPDATED
                        );
                        this.labResultService.listen.next(true);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE, HmisConstants.ERROR);
                    console.log(error);
                }
            )
        );
    }

    public onApproveTestResult() {
        const approved = new LabTestApprovePayload();
        approved.approvedBy = this.commonService.getCurrentUser();
        approved.approvedFrom = this.commonService.getCurrentLocation();
        approved.testItemId = this.payload.singleTestRequestItemId;
        approved.viewType = this.payload.verificationTypeEnum;
        if (
            this.payload.verificationTypeEnum === LabVerificationViewEnum.PATHOLOGIST
        ) {
            approved.pathologistComment = this.payload.pathologistComment;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onApproveLabResult(approved).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.toast.success(
                            HmisConstants.OK_SUCCESS_RESPONSE,
                            HmisConstants.UPDATED
                        );
                        this.labResultService.listen.next(true);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE, HmisConstants.ERROR);
                    console.log(error);
                }
            )
        );
    }

    public onDisapproveTestResult() {}

    public onInitForm() {
        this.payload.parasitologyTemplate = new LabParasitologyTemplatePayload();
    }

    private onPrepPayload(): void {
        this.payload.resultTypeEnum = LabDepartmentTypeEnum.MICROBIOLOGY;
        this.payload.preparedBy = this.commonService.getCurrentUser();
        this.payload.preparedFrom = this.commonService.getCurrentLocation();
    }
}

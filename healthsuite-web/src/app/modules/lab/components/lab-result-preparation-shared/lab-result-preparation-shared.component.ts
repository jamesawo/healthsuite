import { Component, OnDestroy, OnInit } from '@angular/core';
import { LabParamRangeSetupItem, LabResultPrepDto } from '@app/shared/_payload/lab/lab.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ILabResultComponent } from '@app/shared/_payload/lab/lab-result.payload';
import { LabResultService } from '@app/shared/_services/lab/lab-result.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { LabDepartmentTypeEnum } from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-lab-result-preparation-shared',
    templateUrl: './lab-result-preparation-shared.component.html',
    styleUrls: ['./lab-result-preparation-shared.component.css'],
})
export class LabResultPreparationSharedComponent implements ILabResultComponent, OnInit, OnDestroy {
    payload: LabResultPrepDto;

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
        }
        if (!this.payload.resultTypeEnum) {
            this.payload.resultTypeEnum = LabDepartmentTypeEnum.GENERAL;
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onLabSaveResultPreparation() {
        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onSaveLabTestResult(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(
                            HmisConstants.OK_SUCCESS_RESPONSE,
                            HmisConstants.UPDATED
                        );
                        this.labResultService.listen.next(true);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE);
                    console.log(error);
                }
            )
        );
    }

    public onRangeValue(event: any, range: LabParamRangeSetupItem) {
        const rangeValue = event.target.value;
        range.hasError = rangeValue < range.lowerLimit || rangeValue > range.upperLimit;
    }

    public onSubmitResultPrep() {
        if (this.payload.testParameterList.length === 1) {
            this.payload.singleTestRequestItemId = this.payload.testParameterList[0].testParamId;
        }
        this.payload.preparedFrom = this.commonService.getCurrentLocation();
        this.payload.preparedBy = this.commonService.getCurrentUser();
        this.onLabSaveResultPreparation();
    }
}

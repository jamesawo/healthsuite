import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ProductServicePayload, SharedPayload } from '@app/shared/_payload';
import { BillViewTypeEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import { Subscription } from 'rxjs';
import { LabParamSetupService } from '@app/shared/_services/lab/lab-param-setup.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {
    InputParams,
    LabParameterSetupItemPayload,
    LabParameterSetupPayload,
    LabParamRangePayload,
} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-range-test-param-setup',
    templateUrl: './range-test-param-setup.component.html',
    styleUrls: ['./range-test-param-setup.component.css'],
})
export class RangeTestParamSetupComponent implements OnInit, OnDestroy {
    public viewType: BillViewTypeEnum = BillViewTypeEnum.LAB_BILL;
    public paramPayload: LabParameterSetupPayload = new LabParameterSetupPayload();

    @Input('paramPayload')
    public rangePayload: LabParamRangePayload;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private labParamService: LabParamSetupService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        if (!this.rangePayload) {
            this.rangePayload = new LabParamRangePayload();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onLabTestSelected(productServicePayload: ProductServicePayload) {
        if (productServicePayload && productServicePayload.id) {
            this.spinner.show().then();
            this.subscription.add(
                this.labParamService.onGetParameterByTest(productServicePayload.id).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        this.paramPayload = res;
                        // set range payload
                        this.rangePayload.test = res.test;
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(HmisConstants.FAILED_RESPONSE, HmisConstants.ERROR);
                    }
                )
            );
        }
    }

    public onParameterSelected(item: LabParameterSetupItemPayload) {
        if (item) {
            this.rangePayload.labParameterSetupItem = item;
            this.labParamService.selectedParameterValue = item;
            this.onSearchRangeSetup();
        }
    }

    public onSearchRangeSetup() {
        const testId =  this.rangePayload.test.id;
        const paramId =  this.rangePayload.labParameterSetupItem.id;
        if (testId && paramId) {
            this.spinner.show().then();
            this.subscription.add(
                this.labParamService.onGetParameterItemByTest(testId, paramId).subscribe(
                    (res) => {
                        if (res?.rangeItems?.length) {
                            this.rangePayload.rangeItems = res.rangeItems;
                        }
                        this.spinner.hide().then();
                    },
                    (error) => {
                        this.spinner.hide().then();
                    }
                )
            );
        }
    }
}

import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {DepartmentPayload, RevenueDepartmentPayload} from '@app/modules/settings';
import { ChargeSheetCategoryEnum } from '@app/shared/_payload/reports/reports.payload';
import {of, Subscription} from 'rxjs';
import { OtherReportService } from '@app/shared/_services/reports/other-report.service';
import { saveAs } from 'file-saver';
import { ReportFileNameEnum } from '@app/shared/_payload';

@Component({
    selector: 'app-service-charge-report',
    templateUrl: './service-charge-report.component.html',
    styleUrls: ['./service-charge-report.component.css'],
})
export class ServiceChargeReportComponent implements OnInit {
    public category: ChargeSheetCategoryEnum = ChargeSheetCategoryEnum.BOTH;
    public searchById: number = undefined;
    public both: ChargeSheetCategoryEnum  = ChargeSheetCategoryEnum.BOTH;
    public serviceDepartment: ChargeSheetCategoryEnum = ChargeSheetCategoryEnum.SERVICE_DEPARTMENT;
    public revenueDepartment: ChargeSheetCategoryEnum = ChargeSheetCategoryEnum.REVENUE_DEPARTMENT;

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private reportService: OtherReportService
    ) {}

    ngOnInit(): void {}

    onCategorySelected(value: ChargeSheetCategoryEnum) {
        if (value) {
            this.category = value;
        }
    }

    onRevenueDepartmentSelected(value: RevenueDepartmentPayload) {
        if (value && value.id) {
            this.searchById = value.id;
        }
    }

    onServiceDepartmentSelected(value: DepartmentPayload) {
        if (value && value.id) {
            this.searchById = value.id;
        }
    }

    onPreviewReport() {
        const isValid: boolean = this.onValidate();
        if (isValid === true) {
            this.spinner.show().then();
            this.subscription.add(
                this.reportService
                    .downloadServiceChargeSheet(this.category, this.searchById)
                    .subscribe(
                        (res) => {
                            this.spinner.hide().then();
                            const file = new Blob([res.body], { type: 'application/pdf' });
                            saveAs(file, ReportFileNameEnum.SERVICE_CHARGE_SHEET);
                        },
                        (error) => {
                            console.log(error);
                            this.spinner.hide().then();
                            this.toast.error('Failed To Download', HmisConstants.ERR_SERVER_ERROR);
                        }
                    )
            );
        }
        return;
    }

    private onValidate(): boolean {
        if (!this.category) {
            this.toast.error('Please select a category', HmisConstants.VALIDATION_ERR);
            return false;
        } else if (
            this.category === ChargeSheetCategoryEnum.SERVICE_DEPARTMENT ||
            this.category === ChargeSheetCategoryEnum.REVENUE_DEPARTMENT
        ) {
            return !!this.searchById;
        }
        return true;
    }
}

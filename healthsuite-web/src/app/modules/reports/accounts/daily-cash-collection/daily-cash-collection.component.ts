import { Component, OnDestroy, OnInit } from '@angular/core';
import { DepartmentPayload, RevenueDepartmentPayload } from '@app/modules/settings';
import {
    DailyCollectionFilterTypeEnum,
    DatePayload,
    DateType,
    ProductServicePayload,
    ReportFileNameEnum,
} from '@app/shared/_payload';
import { ReportColByRevDepPayload } from '@app/shared/_payload/reports/report-acc.payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { AccReportService } from '@app/shared/_services/reports/acc-report.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';
import {
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-daily-cash-collection',
    templateUrl: './daily-cash-collection.component.html',
    styleUrls: ['./daily-cash-collection.component.css'],
})
export class DailyCashCollectionComponent implements OnInit, OnDestroy {
    start = DateType.START;
    end = DateType.END;
    drugSearchTerm = DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;
    payload = new ReportColByRevDepPayload();

    switchValue: DailyCollectionFilterTypeEnum = DailyCollectionFilterTypeEnum.REVENUE_DEPARTMENT;

    revenueDepartment = DailyCollectionFilterTypeEnum.REVENUE_DEPARTMENT;
    serviceDepartment = DailyCollectionFilterTypeEnum.SERVICE_DEPARTMENT;
    service = DailyCollectionFilterTypeEnum.SERVICE;
    drug = DailyCollectionFilterTypeEnum.DRUG;
    serviceOrDrug = DailyCollectionFilterTypeEnum.SERVICE_DRUG;

    private subscription: Subscription = new Subscription();

    constructor(
        private spinnerService: NgxSpinnerService,
        private toast: ToastrService,
        private accReportService: AccReportService
    ) {}

    ngOnInit(): void {
        this.payload.type = this.revenueDepartment;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onDepartmentSelected(departmentPayload: RevenueDepartmentPayload) {
        if (departmentPayload) {
            this.payload.revenueDepartment = departmentPayload;
        } else {
            this.payload.revenueDepartment = undefined;
        }
    }

    onDateSelected(datePayload: DatePayload, type: DateType) {
        if (type === this.start) {
            this.payload.startDate = datePayload;
        } else {
            this.payload.endDate = datePayload;
        }
    }

    onDownloadFile() {
        this.spinnerService.show().then();
        this.subscription.add(
            this.accReportService.getDailyCashCollectionReport(this.payload).subscribe(
                (res) => {
                    this.spinnerService.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.ACC_DAILY_CASH_COLLECTION);
                },
                (error) => {
                    this.spinnerService.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    console.log(error);
                }
            )
        );
    }

    onServiceDepartmentSelected(departmentPayload: DepartmentPayload) {
        if (departmentPayload) {
            this.payload.serviceDepartment = departmentPayload;
        }
    }

    onServiceSelected(servicePayload: ProductServicePayload) {
        if (servicePayload) {
            this.payload.service = servicePayload;
        }
    }

    onTypeChange(type: DailyCollectionFilterTypeEnum) {
        if (!type) {
            return;
        }
        this.onResetPayload();
        this.switchValue = type;
        if (type === this.serviceOrDrug) {
            type = this.service;
        }
        this.payload.type = type;
    }

    onDrugSelected(drugRegisterPayload: DrugRegisterPayload) {
        if (drugRegisterPayload) {
            this.payload.drug = drugRegisterPayload;
        }
    }

    onResetPayload() {
        this.payload.type = undefined;
        this.payload.revenueDepartment = undefined;
        this.payload.serviceDepartment = undefined;
        this.payload.service = undefined;
        this.payload.drug = undefined;
    }
}

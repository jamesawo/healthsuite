import { Component, OnDestroy, OnInit } from '@angular/core';
import { DatePayload, DateType, ReportFileNameEnum } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import {
    CashierShiftReportDetailTypeEnum,
    CashierShiftSearch,
    CashierShiftSearchPayload,
    CashierShitPayload,
    SearchShitByEnum,
} from '@app/shared/_payload/shift/shit.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';

@Component({
    selector: 'app-cashier-shift',
    templateUrl: './cashier-shift.component.html',
    styleUrls: ['./cashier-shift.component.css'],
})
export class CashierShiftComponent implements OnInit, OnDestroy {
    public searchPayload: CashierShiftSearch;
    public start = DateType.START;
    public end = DateType.END;
    public summaryType = CashierShiftReportDetailTypeEnum.SUMMARY;
    public detailedType = CashierShiftReportDetailTypeEnum.DETAILED;
    public shiftCollection: CashierShitPayload[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private shiftService: ShiftManagerService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.onResetSearchPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onResetSearchPayload() {
        this.searchPayload = {
            userId: this.commonService.getCurrentUser().id,
            endDate: this.commonService.getCurrentDate(),
            startDate: this.commonService.getCurrentDate(),
            searchBy: SearchShitByEnum.SHIFT_NUMBER,
            shiftNumber: undefined,
            reportType: this.summaryType,
            shiftId: undefined,
        };
    }

    public onSearchForShiftByDateRange() {
        this.spinner.show().then();
        this.subscription.add(
            this.shiftService.onGetCashierShiftRecordByDateRangeAndUser(this.searchPayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.shiftCollection = res;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onDateSelected(datePayload: DatePayload, type: DateType) {
        if (datePayload && type) {
            if (type === DateType.START) {
                this.searchPayload.startDate = datePayload;
            } else if (type === DateType.END) {
                this.searchPayload.endDate = datePayload;
            }
        }
    }

    public onReportDetailFormatChange(type: CashierShiftReportDetailTypeEnum) {
        if (type) {
            this.searchPayload.reportType = type;
        }
    }

    public onShiftSelected(shift: CashierShitPayload) {
        if (shift.id) {
            this.searchPayload.shiftNumber = shift.shiftNumber;
            this.searchPayload.shiftId = shift.id;
        }
    }

    public onDownloadShiftReport() {
        this.spinner.show().then();
        this.subscription.add(
            this.shiftService.onDownloadShiftReport(this.searchPayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.SHIFT_CASHIER_REPORT);
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    console.log(error);
                }
            )
        );
    }
}

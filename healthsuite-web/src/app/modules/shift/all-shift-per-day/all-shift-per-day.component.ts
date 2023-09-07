import { Component, OnDestroy, OnInit } from '@angular/core';
import { DatePayload, DateType, ReportFileNameEnum, ValidationMessage } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import {
    CashierShiftSearch,
    CashierShitWrapPayload,
    SearchShitByEnum,
} from '@app/shared/_payload/shift/shit.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { environment } from '@environments/environment';
import { saveAs } from 'file-saver';

@Component({
    selector: 'app-all-shift-per-day',
    templateUrl: './all-shift-per-day.component.html',
    styleUrls: ['./all-shift-per-day.component.css'],
})
export class AllShiftPerDayComponent implements OnInit, OnDestroy {
    public searchPayload: CashierShiftSearch;
    public start = DateType.START;
    public end = DateType.END;
    public data: CashierShitWrapPayload = new CashierShitWrapPayload();
    public currency = environment.currencySign;

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
            userId: undefined,
            endDate: this.commonService.getCurrentDate(),
            startDate: this.commonService.getCurrentDate(),
            searchBy: SearchShitByEnum.SHIFT_NUMBER,
            shiftNumber: undefined,
            reportType: undefined,
            shiftId: undefined,
        };
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

    public onSearchRecord() {
        this.spinner.show().then();
        this.subscription.add(
            this.shiftService.onGetCashierShiftRecordByDateRange(this.searchPayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.data.resultList = [];
                        this.data = res;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onDownloadAsPdfClick() {
        if (!this.data.resultList.length) {
            this.toast.error('Search For Record First', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.shiftService.onGetAllShiftPerDayReport(this.searchPayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    this.toast.success(
                        HmisConstants.LAST_ACTION_SUCCESS,
                        HmisConstants.OK_SUCCESS_RESPONSE
                    );
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.SHIFT_COMPILED_REPORT);
                    this.onResetPayload();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onResetPayload() {
        this.data = new CashierShitWrapPayload();
        this.onResetSearchPayload();
    }
}

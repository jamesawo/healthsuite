import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    CashierShiftReportDetailTypeEnum,
    CashierShiftSearch,
    CashierShiftSearchPayload,
    CashierShitPayload,
    CashierShitWrapPayload,
    searchBy,
    SearchShitByEnum,
} from '@app/shared/_payload/shift/shit.payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { DatePayload, DateType, ReportFileNameEnum, ValidationMessage } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { environment } from '@environments/environment';
import { SharedDateComponent } from '@app/modules/common/shared-date/shared-date.component';
import { saveAs } from 'file-saver';

@Component({
    selector: 'app-shift-manager',
    templateUrl: './shift-manager.component.html',
    styleUrls: ['./shift-manager.component.css'],
})
export class ShiftManagerComponent implements OnInit, OnDestroy {
    @ViewChild('startDateComponent') startDateComponent: SharedDateComponent;
    @ViewChild('endDateComponent') endDateComponent: SharedDateComponent;

    public currency = environment.currencySign;
    public searchByCollection = searchBy;
    public data: CashierShitWrapPayload = new CashierShitWrapPayload();
    public searchPayload: CashierShiftSearch;

    public shiftNumber = SearchShitByEnum.SHIFT_NUMBER;
    public summaryType = CashierShiftReportDetailTypeEnum.SUMMARY;
    public detailedType = CashierShiftReportDetailTypeEnum.DETAILED;
    public selectedPreviewFormat = CashierShiftReportDetailTypeEnum.SUMMARY;
    public payload: any;
    public start = DateType.START;
    public end = DateType.END;
    public closable: { closedBy: number; shiftsId: number[] } = {
        closedBy: undefined,
        shiftsId: [],
    };

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private shiftService: ShiftManagerService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.onResetPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSearchByChanged(search: SearchShitByEnum) {
        this.onResetPayload();
        this.searchPayload.startDate = this.startDateComponent.datePayload;
        this.searchPayload.endDate = this.endDateComponent.datePayload;
        this.onClearData();

        if (search) {
            this.searchPayload.searchBy = search;
        }
    }

    public onResetPayload() {
        this.searchPayload = {
            userId: undefined,
            endDate: undefined,
            startDate: undefined,
            searchBy: SearchShitByEnum.SHIFT_NUMBER,
            shiftNumber: undefined,
            reportType: this.summaryType,
            shiftId: undefined,
        };
        this.onResetClosable();
    }

    public onResetClosable() {
        this.closable = {
            closedBy: undefined,
            shiftsId: [],
        };
    }

    public onClearData() {
        this.data = new CashierShitWrapPayload();
        this.onResetClosable();
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
        const isValid: ValidationMessage = this.onValidateBeforeSearch();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.ERR_TITLE);
            return;
        }
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

    public onCashierSelected(userPayload: UserPayload) {
        this.onClearData();
        if (userPayload) {
            this.searchPayload.userId = userPayload.id;
        }
    }

    public onShiftSelected(shift: CashierShiftSearchPayload) {
        this.onClearData();
        if (shift.id) {
            this.searchPayload.shiftNumber = shift.shiftNumber;
            this.spinner.show().then();
            this.subscription.add(
                this.shiftService.onGetCashierShiftWrapperRecord(shift.id).subscribe(
                    (result) => {
                        this.spinner.hide().then();
                        if (result) {
                            this.data.resultList = [];
                            this.data = result;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    }
                )
            );
        }
    }

    public onReloadTableData() {
        this.closable.shiftsId = [];
        if (this.data.resultList.length === 0) {
            this.toast.warning('Search For Data First!', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.onSearchRecord();
    }

    public onCloseCheckShiftRemote(user: UserPayload) {
        if (this.closable.shiftsId.length > 0) {
            this.closable.shiftsId.forEach((item) => {
                const shift = this.data.resultList.find((value) => value.id === item);
                shift.isActive = false;
                shift.closedByUser = user;
            });
        }
    }

    public onCloseShift() {
        if (!this.closable.shiftsId.length) {
            this.toast.error('Select At Least 1 Shift Record.', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.commonService
            .askAreYouSure('Close Cashier Shift?', 'WARNING', 'warning')
            .then((value) => {
                if (value.isConfirmed === true) {
                    const user = this.commonService.getCurrentUser();
                    this.closable.closedBy = user.id;
                    this.spinner.show().then();
                    this.subscription.add(
                        this.shiftService.onCloseMultipleShiftRecord(this.closable).subscribe(
                            (res) => {
                                this.spinner.hide().then();
                                if (res) {
                                    this.toast.success(
                                        HmisConstants.SUCCESS_RESPONSE,
                                        HmisConstants.UPDATED
                                    );
                                    this.onCloseCheckShiftRemote(user);
                                } else {
                                    this.toast.success(
                                        HmisConstants.ERR_TITLE,
                                        HmisConstants.VALIDATION_ERR
                                    );
                                }
                            },
                            (error) => {
                                this.spinner.hide().then();
                            }
                        )
                    );
                }
            });
    }

    public onPreviewReportClicked() {
        const isValid: ValidationMessage = this.onValidateBeforePreview();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.searchPayload.shiftId = this.closable.shiftsId[0];
        this.searchPayload.reportType = this.selectedPreviewFormat;
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

    public onReportDetailFormatChange(type: CashierShiftReportDetailTypeEnum) {
        if (type) {
            this.selectedPreviewFormat = type;
        }
    }

    public onShiftItemChecked(item: CashierShitPayload, check: any) {
        const checked: boolean = check.target.checked;
        if (checked === true) {
            this.closable.shiftsId.push(item.id);
        } else {
            const index = this.closable.shiftsId.findIndex((value) => value === item.id);
            this.closable.shiftsId.splice(index, 1);
        }
    }

    public onValidateBeforeSearch(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.searchPayload.startDate && !this.searchPayload.endDate) {
            res.status = false;
            res.message += 'Start/End Dates Are Required. <br>';
        }
        if (
            this.searchPayload.searchBy === SearchShitByEnum.CASHIER_USERNAME &&
            !this.searchPayload.userId
        ) {
            res.status = false;
            res.message += 'Search For Cashier First. <br>';
        }
        return res;
    }

    public onValidateBeforePreview(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (this.data.resultList.length === 0) {
            res.status = false;
            res.message += 'Search For Data First! <br>';
        }

        if (!this.closable.shiftsId.length) {
            if (
                this.data.resultList.length === 1
            ) {
                this.closable.shiftsId.push(this.data.resultList[0].id);
            } else {
                res.status = false;
                res.message += 'Select Shift To Download! <br>';
            }
        }

        if (this.closable.shiftsId.length > 1) {
            res.status = false;
            res.message += 'Select Only 1 Shift To Download Report! <br>';
        }
        return res;
    }
}

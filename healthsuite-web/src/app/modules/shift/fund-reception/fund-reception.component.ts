import { Component, OnDestroy, OnInit } from '@angular/core';
import { DatePayload, DateType, ReportFileNameEnum } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import {
    CashierFundReceptionPayload,
    CashierShiftSearch,
    CashierShiftSearchPayload,
    CashierShitPayload,
    FundReceptionTypeEnum,
    searchBy,
    SearchShitByEnum,
} from '@app/shared/_payload/shift/shit.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';

@Component({
    selector: 'app-fund-reception',
    templateUrl: './fund-reception.component.html',
    styleUrls: ['./fund-reception.component.css'],
})
export class FundReceptionComponent implements OnInit, OnDestroy {
    public searchByCollection = searchBy;
    public start = DateType.START;
    public end = DateType.END;
    public pending = FundReceptionTypeEnum.PENDING;
    public acknowledged = FundReceptionTypeEnum.ACKNOWLEDGED;

    public shiftNumber = SearchShitByEnum.SHIFT_NUMBER;
    public cashierUsername = SearchShitByEnum.CASHIER_USERNAME;

    public searchPayload: CashierShiftSearch;
    public collection: CashierShitPayload[] = [];
    public shift: CashierShitPayload = undefined;
    public acknowledgedPayload: CashierFundReceptionPayload;

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
            reportType: undefined,
            shiftId: undefined,
            fundReceptionType: FundReceptionTypeEnum.PENDING,
        };
    }

    public onSearchByChanged(search: SearchShitByEnum) {
        this.onResetPayload();
        if (search) {
            this.searchPayload.searchBy = search;
        }
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
            this.shiftService
                .onGetCashierShiftRecordByDateRangeForReception(this.searchPayload)
                .subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res.length) {
                            this.collection = res;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    }
                )
        );
    }

    public onReceptionFormatChange(type: FundReceptionTypeEnum) {
        if (type) {
            this.searchPayload.fundReceptionType = type;
        }
    }

    public onShiftSelected(shiftSearchPayload: CashierShiftSearchPayload) {
        this.collection = [];
        if (shiftSearchPayload.id) {
            this.searchPayload.shiftId = shiftSearchPayload.id;
            this.searchPayload.shiftNumber = shiftSearchPayload.shiftNumber;

            this.searchPayload.shiftNumber = shiftSearchPayload.shiftNumber;
            this.spinner.show().then();
            this.subscription.add(
                this.shiftService.onGetCashierShiftRecord(shiftSearchPayload.id).subscribe(
                    (result) => {
                        this.spinner.hide().then();
                        if (result) {
                            this.collection = [];
                            this.collection.push(result);
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

    public onResetPayload() {
        this.collection = [];
        this.shift = undefined;
        this.onResetSearchPayload();
    }

    public onCashierSelected(cashier: UserPayload) {
        if (cashier.id) {
            this.searchPayload.userId = cashier.id;
        }
    }

    public onShiftItemChecked(item: CashierShitPayload, event: any) {
        if (item.id) {
            if (item.isFundReceived === true) {
                event.preventDefault();
                this.toast.error(
                    'Oops! Selected Shit Is Already Reconciled.',
                    HmisConstants.VALIDATION_ERR
                );
                return;
            }
            this.shift = item;
        }
    }

    public onFundAcknowledgeClicked() {
        if (!this.shift) {
            this.toast.error('Select Shift To Acknowledge First', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.commonService
            .askAreYouSure('ACKNOWLEDGE CASHIER SHIFT FUND ?', 'ARE YOU SURE?', 'warning')
            .then((res) => {
                if (res.isConfirmed === true) {
                    this.spinner.show().then();
                    this.onSetAcknowledgedByPayload();
                    this.subscription.add(
                        this.shiftService
                            .onAcknowledgeCashierShift(this.acknowledgedPayload)
                            .subscribe(
                                (res) => {
                                    if (res) {
                                        this.spinner.hide().then();
                                        this.toast.success(
                                            HmisConstants.LAST_ACTION_SUCCESS,
                                            HmisConstants.OK_SUCCESS_RESPONSE
                                        );
                                        const file = new Blob([res.body], {
                                            type: 'application/pdf',
                                        });
                                        saveAs(
                                            file,
                                            ReportFileNameEnum.SHIFT_ACKNOWLEDGEMENT_REPORT
                                        );
                                        this.onResetPayload();
                                    }
                                },
                                (error) => {
                                    this.spinner.hide().then();
                                    this.toast.error(
                                        HmisConstants.ERR_SERVER_ERROR,
                                        HmisConstants.ERR_TITLE
                                    );
                                }
                            )
                    );
                }
            });
    }

    public onSetAcknowledgedByPayload(): void {
        this.acknowledgedPayload = new CashierFundReceptionPayload();
        this.acknowledgedPayload.receivedBy = this.commonService.getCurrentUser();
        this.acknowledgedPayload.shift = this.shift;
        this.acknowledgedPayload.location = this.commonService.getCurrentLocation();
    }
}

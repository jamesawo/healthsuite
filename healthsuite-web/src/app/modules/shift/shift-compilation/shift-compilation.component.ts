import { Component, OnDestroy, OnInit } from '@angular/core';
import {DatePayload, DateType, ReportFileNameEnum, ValidationMessage} from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import {
    CashierCompiledShiftPayload, CashierShiftReportDetailTypeEnum,
    CashierShiftSearch,
    CashierShiftSearchPayload,
    CashierShitPayload,
    FundReceptionTypeEnum,
    searchBy,
    SearchShitByEnum,
    ShiftCompileTypeEnum,
} from '@app/shared/_payload/shift/shit.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import {saveAs} from 'file-saver';

@Component({
    selector: 'app-shift-compilation',
    templateUrl: './shift-compilation.component.html',
    styleUrls: ['./shift-compilation.component.css'],
})
export class ShiftCompilationComponent implements OnInit, OnDestroy {
    public searchByCollection = searchBy;
    public compilePayload = new CashierCompiledShiftPayload();
    public collection: CashierShitPayload[] = [];
    public searchPayload: CashierShiftSearch;

    public shiftNumber = SearchShitByEnum.SHIFT_NUMBER;
    public start = DateType.START;
    public end = DateType.END;
    public uncompiled = ShiftCompileTypeEnum.UNCOMPILED;
    public compiled = ShiftCompileTypeEnum.COMPILED;

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
            reportType: CashierShiftReportDetailTypeEnum.SUMMARY,
            shiftId: undefined,
            fundReceptionType: FundReceptionTypeEnum.PENDING,
        };
    }

    public onShiftSelected(shift: CashierShiftSearchPayload) {

        if (shift.id) {
            this.searchPayload.shiftId = shift.id;
            this.searchPayload.shiftNumber = shift.shiftNumber;
            this.spinner.show().then();
            this.subscription.add(
                this.shiftService.onGetCashierShiftRecord(shift.id).subscribe(
                    (result) => {
                        this.spinner.hide().then();
                        if (result) {
                            this.addShiftToCollection(result);
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

    public addShiftToCollection(shift: CashierShitPayload) {
        const find = this.collection.find(value => value.id === shift.id);
        if (!find) {
            this.collection.push(shift);
        }
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

    public onReportTypeChange(type: ShiftCompileTypeEnum) {
        if (type) {
            this.searchPayload.compileType = type;
        }
    }

    public onCashierSelected(cashier: UserPayload) {
        if (cashier.id) {
            this.searchPayload.userId = cashier.id;
        }
    }

    public onShiftItemChecked(item: CashierShitPayload, event: any) {
        const checked: boolean = event.target.checked;
        if (checked === true) {
            if (item.isShitCompiled === true) {
                event.preventDefault();
                this.toast.error('Shift Is Already Compiled', HmisConstants.VALIDATION_ERR);
                return;
            }
            this.compilePayload.cashierShifts.push(new CashierShitPayload(item.id));
        } else {
            const index = this.compilePayload.cashierShifts.findIndex(
                (value) => value.id === item.id
            );
            this.compilePayload.cashierShifts.splice(index, 1);
        }
    }

    public onResetPayload() {
        this.collection = [];
        this.compilePayload = new CashierCompiledShiftPayload();
        this.onResetSearchPayload();
    }

    public onSearchRecord() {
        this.spinner.show().then();
        this.subscription.add(
            this.shiftService
                .onGetCashierShiftRecordByDateRangeCashierNotRequired(this.searchPayload)
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

    public onCompileShift() {
        const validate = this.onValidateBeforeCompileShift();
        if (validate.status === false) {
            this.toast.error(validate.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.commonService.askAreYouSure('COMPILE CASHIER SHIFTS?', 'ARE YOU SURE ?', 'warning').then( response => {
            this.spinner.show().then();
            this.subscription.add(
                this.shiftService.onCompileCashierShift(this.compilePayload).subscribe(res => {
                    this.spinner.hide().then();
                    this.toast.success(
                        HmisConstants.LAST_ACTION_SUCCESS,
                        HmisConstants.OK_SUCCESS_RESPONSE
                    );
                    const file = new Blob([res.body], {type: 'application/pdf'});
                    saveAs(file, ReportFileNameEnum.SHIFT_COMPILED_REPORT);
                    this.onResetPayload();
                }, error => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.LAST_ACTION_FAILED, HmisConstants.OK_SUCCESS_RESPONSE);
                })
            );
        });

    }

    public onClearCollection() {
        this.collection = [];
    }

    private onValidateBeforeCompileShift(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };

        if (!this.compilePayload.cashierShifts.length) {
            res.status = false;
            res.message = 'select shift to compile <br>';
        }

        if (!this.compilePayload.compiledBy.id) {
           this.compilePayload.compiledBy = this.commonService.getCurrentUser();
        }

        if (!this.compilePayload.location.id) {
           this.compilePayload.location = this.commonService.getCurrentLocation();
        }
        return res;
    }
}

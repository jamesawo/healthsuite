import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    DrugDispensePayload,
    DrugDispenseSearchByEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { Subscription } from 'rxjs';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {
    PatientBill,
    PaymentTypeForEnum,
    ReceiptPayload,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { PaymentService } from '@app/shared/_services/billing-payment/payment.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { environment } from '@environments/environment';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import {
    GlobalSettingKeysEnum,
    GlobalSettingValueEnum,
    ValidationMessage,
} from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ReconciliationService } from '@app/shared/_services/pharmacy/reconciliation.service';
import { GlobalSettingService } from '@app/shared/_services';

const searchC: any[] = [
    { id: 1, name: 'RECEIPT NUMBER', value: DrugDispenseSearchByEnum.RECEIPT_NUMBER },
    { id: 2, name: 'PATIENT NUMBER', value: DrugDispenseSearchByEnum.PATIENT_NUMBER },
];
@Component({
    selector: 'app-drug-dispensing',
    templateUrl: './drug-dispensing.component.html',
    styleUrls: ['./drug-dispensing.component.css'],
})
export class DrugDispensingComponent implements OnInit, OnDestroy {
    public searchByCollection = searchC;
    public searchBySelected = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    public patientReceipts: ReceiptPayload[] = [];
    public patientBill: PatientBill = new PatientBill();
    public payload: DrugDispensePayload = new DrugDispensePayload();
    public currencySign = environment.currencySign;

    public searchByReceipt = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    public searchByPatient = DrugDispenseSearchByEnum.PATIENT_NUMBER;
    public drugReceipt: PaymentTypeForEnum = PaymentTypeForEnum.DRUG;
    public isUseStockInventory: boolean;

    private subscription: Subscription = new Subscription();
    constructor(
        private receiptService: PaymentService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private reconciliationService: ReconciliationService,
        private globalSettingService: GlobalSettingService
    ) {}

    ngOnInit(): void {
        this.subscription.add(
            this.globalSettingService
                .getSettingValueByKey(GlobalSettingKeysEnum.ACTIVATE_STOCK_INVENTORY)
                .subscribe((res) => {
                    this.isUseStockInventory = res.body.data.value === GlobalSettingValueEnum.YES;
                    if (!this.isUseStockInventory) {
                        this.toast.error('MODULE REQUIRES STOCK & INVENTORY', 'DEACTIVATED', {
                            disableTimeOut: true,
                        });
                    }
                    if (!this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION)) {
                        this.commonService.flagLocationError();
                    }
                })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSearchByChanged() {
        this.onClearTable();
    }

    public onPatientSelected(value: PatientPayload): void {
        this.onClearTable();
        if (value) {
            this.payload.patientDetailDto = value;

            this.spinner.show().then();
            this.subscription.add(
                this.receiptService
                    .onFindPatientPaymentReceipt(value.patientId, PaymentTypeForEnum.DRUG)
                    .subscribe(
                        (res) => {
                            this.spinner.hide().then();
                            if (res.body) {
                                this.patientReceipts = res.body;
                            }
                        },
                        (error) => {
                            this.spinner.hide().then();
                        }
                    )
            );
        }
    }

    public onReceiptSelected(value: ReceiptPayload): void {
        if (value) {
            this.patientBill = value.patientBill;
            this.payload.patientDetailDto = value.patientBill.patientDetailDto;
            this.payload.receiptId = value.receiptId;
            this.payload.patientBillDto = value.patientBill;
        }
    }

    public onDispenseItems(): void {
        this.payload.outlet = this.commonService.getCurrentLocation();
        this.payload.dispensedBy = this.commonService.getCurrentUser();
        const isValid = this.onValidateBeforeDispense();
        if (!isValid.status) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.reconciliationService.onDispenseDrugItem(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.body.status === true) {
                        this.toast.success(
                            HmisConstants.LAST_ACTION_SUCCESS,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onResetForm();
                    }
                },
                (error) => {
                    console.log(error);
                    let display = '';
                    if (error.error?.messages?.length) {
                        display = this.commonService.messageArrayToString(error.error.messages);
                    } else {
                        display = 'Contact Support For Help';
                    }
                    this.spinner.hide().then();
                    this.toast.error(display, HmisConstants.ERR_TITLE, { enableHtml: true });
                }
            )
        );
    }

    public onResetForm(): void {
        this.onClearTable();
        this.payload = new DrugDispensePayload();
        this.searchBySelected = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    }

    public onClearTable(): void {
        this.patientReceipts = [];
        this.patientBill = new PatientBill();
    }

    private onValidateBeforeDispense(): ValidationMessage {
        const resp: ValidationMessage = { message: '', status: true };
        if (!this.payload.outlet) {
            this.payload.outlet = this.commonService.getCurrentLocation();
        }
        if (!this.payload.receiptId) {
            resp.status = false;
            resp.message = `Patient Receipt is Required <br>`;
        }
        if (!this.payload.dispensedBy) {
            this.commonService.getCurrentUser();
        }
        return resp;
    }
}

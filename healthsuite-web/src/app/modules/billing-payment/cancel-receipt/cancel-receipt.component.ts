import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { PaymentTypeForEnum, ReceiptPayload } from '@app/shared/_payload/bill-payment/bill.payload';
import { CancelReceiptPayload } from '@app/shared/_payload/bill-payment/receipt.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PaymentReceiptService } from '@app/shared/_services/billing-payment/payment-receipt.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ValidationMessage } from '@app/shared/_payload';
import { PaymentReceiptSearchComponent } from '@app/modules/common/payment-receipt-search/payment-receipt-search.component';
import { environment } from '@environments/environment';
import { DrugDispenseSearchByEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-cancel-receipt',
    templateUrl: './cancel-receipt.component.html',
    styleUrls: ['./cancel-receipt.component.css'],
})
export class CancelReceiptComponent implements OnInit, OnDestroy {
    @ViewChild('receiptSearchComponent') receiptSearchComponent: PaymentReceiptSearchComponent;
    public payload: CancelReceiptPayload;
    public selectedReceiptsItems: any[];
    public currencySym = environment.currencySign;
    public searchReceiptBy = DrugDispenseSearchByEnum.RECEIPT_NUMBER;

    private subscription: Subscription = new Subscription();
    filterReceiptFor = PaymentTypeForEnum.SERVICE;

    constructor(
        private commonService: CommonService,
        private receiptService: PaymentReceiptService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.onResetPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onReceiptSelected(receipt: ReceiptPayload) {
        if (receipt && receipt.receiptId) {
            if (receipt.isUsed === true) {
                this.toast.error(HmisConstants.INVALID_RECEIPT, HmisConstants.VALIDATION_ERR);
                return;
            } else if (receipt.receiptPaymentTypeFor === PaymentTypeForEnum.DEPOSIT) {
                this.toast.error(HmisConstants.CANNOT_CANCEL_DEPOSIT, HmisConstants.ERR_TITLE);
                return;
            }

            this.payload.paymentReceiptId = receipt.receiptId;
            this.payload.receiptCode = receipt.receiptNumber;
            this.payload.patient = receipt.patientDetail;
            this.payload.patientBill = receipt.patientBill;
            const billTypeFor = receipt.receiptPaymentTypeFor;
            if (billTypeFor === PaymentTypeForEnum.SERVICE) {
                this.selectedReceiptsItems = receipt.patientBill.billItems;
            } else if (billTypeFor === PaymentTypeForEnum.DRUG) {
                this.selectedReceiptsItems = receipt.patientBill.pharmacyBillItems;
            }
        }
    }

    public onSubmit() {
        const validate = this.onValidateBeforeSubmit();
        if (validate.status === false) {
            this.toast.error(validate.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.commonService
            .askAreYouSure('CANCEL PAYMENT RECEIPT ?', 'ARE YOU SURE', 'warning')
            .then((res) => {
                if (res.isConfirmed === true) {
                    this.spinner.show().then();
                    this.subscription.add(
                        this.receiptService.onCancelPaymentReceipt(this.payload).subscribe(
                            (res) => {
                                this.spinner.hide().then();
                                if (res.message) {
                                    this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                                    this.onClearForm();
                                }
                            },
                            (error) => {
                                this.spinner.show().then();
                                this.toast.error(
                                    error.error.message,
                                    HmisConstants.INTERNAL_SERVER_ERROR
                                );
                            }
                        )
                    );
                }
            });
    }

    public onClearForm(): void {
        this.onResetPayload();
        this.selectedReceiptsItems = [];
        this.receiptSearchComponent.clearSearchField();
    }

    private onValidateBeforeSubmit(): ValidationMessage {
        const valid: ValidationMessage = { message: '', status: true };
        if (!this.payload.paymentReceiptId) {
            valid.message += 'Select a Receipt First. <br>';
            valid.status = false;
        }
        if (!this.payload.cancelledBy) {
            this.commonService.getCurrentUser();
        }
        if (!this.payload.cancelledFrom) {
            this.commonService.getCurrentLocation();
        }
        if (!this.payload.comment) {
            valid.message += 'Comment is Required. <br>';
            valid.status = false;
        }

        return valid;
    }

    private onResetPayload(): void {
        this.payload = new CancelReceiptPayload();
        this.payload.cancelledFrom = this.commonService.getCurrentLocation();
        this.payload.cancelledBy = this.commonService.getCurrentUser();
    }
}

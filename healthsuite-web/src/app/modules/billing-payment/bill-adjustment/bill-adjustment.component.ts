import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    BillAdjustmentPayload,
    BillPaymentSearchByEnum,
    PatientBill,
    PaymentTypeForEnum,
    searchByPatientOrBillNumber,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { Subscription } from 'rxjs';
import { PaymentService } from '@app/shared/_services/billing-payment/payment.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { NgSelectComponent } from '@ng-select/ng-select';
import { BillDataSearchComponent } from '@app/modules/common/bill-data-search/bill-data-search.component';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { ValidationMessage } from '@app/shared/_payload';

@Component({
    selector: 'app-bill-adjustment',
    templateUrl: './bill-adjustment.component.html',
    styleUrls: ['./bill-adjustment.component.css'],
})
export class BillAdjustmentComponent implements OnInit, OnDestroy {
    @ViewChild('billNumberSelectComponent') billNumberComponent: NgSelectComponent;
    @ViewChild('billDataSearchComponent') billDataSearchComponent: BillDataSearchComponent;
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    public payload: BillAdjustmentPayload = new BillAdjustmentPayload();
    public billSearchByCollection: any[] = searchByPatientOrBillNumber;
    public searchByPatient = BillPaymentSearchByEnum.PATIENT;
    public selectedBillItems: any[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private toast: ToastrService,
        private paymentService: PaymentService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onClearPayload() {
        this.billNumberComponent.clearModel();
        this.payload = new BillAdjustmentPayload();
        this.selectedBillItems = [];
    }

    public onSearchByDropdownChange(value: BillPaymentSearchByEnum) {
        this.onClearPayload();
        if (value) {
            this.payload.billSearchedBy = value;
        }
    }

    public onPatientSelect(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
            this.onRemoteFindBillsByPatient(patient.patientId);
        }
    }

    public onSearchByBillNumberSelected(bill: PatientBill) {
        if (bill) {
            if (this.canAdjustBill(bill) === false) {
                this.toast.error(HmisConstants.CANNOT_ADJUST_BILL, HmisConstants.ERROR);
                return;
            }
            if (this.commonService.hasBillItems(bill) === false) {
                this.toast.error(HmisConstants.NO_BILL_ITEM_FOUND, HmisConstants.VALIDATION_ERR);
                return;
            }
            this.payload.patient = bill.patientDetailDto;
            this.payload.patientBill = bill;
            this.onPrepSelectedBill(bill);
        }
    }

    public onSearchByPatientBillNumberSelect(bill: PatientBill) {
        if (bill) {
            if (this.canAdjustBill(bill) === false) {
                this.toast.error(HmisConstants.CANNOT_ADJUST_BILL, HmisConstants.ERROR);
                return;
            }
            this.payload.patientBill = bill;
            this.onPrepSelectedBill(bill);
        }
    }

    public canAdjustBill(bill: PatientBill): boolean {
        // todo:: check bill admission session, if patient is discharge then disallow bill adjustment
        return true;
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onPrepSelectedBill(bill: PatientBill) {
        const type = bill.billTypeFor;
        if (type === PaymentTypeForEnum.SERVICE && bill.billItems.length > 0) {
            this.selectedBillItems = bill.billItems;
        } else if (type === PaymentTypeForEnum.DRUG && bill.pharmacyBillItems.length > 0) {
            this.selectedBillItems = bill.pharmacyBillItems;
        }
    }

    public onRemoteFindBillsByPatient(patientId: number): void {
        this.subscription.add(
            this.paymentService
                .onGetPatientServiceBills({ patientId, loadDeposit: false })
                .subscribe(
                    (result) => {
                        this.spinner.hide().then();
                        if (result.status === 200) {
                            if (result.body.billDtoList.length) {
                                this.payload.patientBillList = result.body.billDtoList;
                            }
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    }
                )
        );
    }

    public onSaveBillAdjustment() {
        const isValid = this.isValidPayload();
        if (isValid.status === false ) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.paymentService.onSaveBillAdjustment(this.payload.patientBill).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(
                            HmisConstants.OK_SUCCESS_RESPONSE,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onResetForm();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onResetForm() {
        this.onClearPayload();
        this.billDataSearchComponent?.clearSearchField();
        this.billNumberComponent?.clearModel();
        this.patientSearchComponent?.clearSearchField();
    }

    public onBillQuantityChange(event: any, item: any) {
        const ev: number = event.target.value ? event.target.value : 0;
        const quantity = Number(ev);
        const isSameOldQty = item.quantity === quantity;
        item.newQuantity = quantity;
        item.netAmount = quantity * item.price;
        item.isAdjusted = !isSameOldQty;
    }

    private isValidPayload(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };

        if (!this.payload?.patientBill?.costByDto.id) {
            this.payload.patientBill.costByDto = this.commonService.getCurrentUser();
        }
        if (!this.payload?.patientBill?.locationDto?.id) {
            this.payload.patientBill.locationDto = this.commonService.getCurrentLocation();
        }
        if (!this.payload.patientBill.id) {
            res.status = false;
            res.message += 'Patient Bill is Required <br>';
        }
        if (!this.payload.patientBill.comment) {
            res.status = false;
            res.message += 'Comment is Required <br>';
        }
        return res;
    }
}

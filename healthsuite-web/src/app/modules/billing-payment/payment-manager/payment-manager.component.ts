import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subscription} from 'rxjs';
import {NgxSpinnerService} from 'ngx-spinner';
import {ToastrService} from 'ngx-toastr';
import {PatientCategoryEnum, PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {
    BillItem,
    BillPatientTypeEnum,
    BillPayment,
    BillPaymentOptionTypeEnum,
    BillPaymentSearchByEnum,
    BillViewTypeEnum,
    IBillTotal,
    ITableProps,
    PatientBill,
    PaymentMethodEnum,
    PaymentTypeForEnum,
    searchByPatientOrBillNumber, ServiceOrDrugEnum,
} from '@app/shared/_payload/bill-payment/bill.payload';
import {ProductServicePayload, SharedPayload, ValidationMessage} from '@app/shared/_payload';
import {PaymentService} from '@app/shared/_services/billing-payment/payment.service';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {environment} from '@environments/environment';
import {CommonService} from '@app/shared/_services/common/common.service';
import {BillTableDataComponent} from '@app/modules/common/bill-table-data/bill-table-data.component';
import {WalkInPatientComponent} from '@app/modules/common/walk-in-patient/walk-in-patient.component';
import {PaymentMethodComponent} from '@app/modules/common/payment-method/payment-method.component';
import {LocationConstants} from '@app/shared/_models/constant/locationConstants';
import {PharmacyBillItem} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-payment-manager',
    templateUrl: './payment-manager.component.html',
    styleUrls: ['./payment-manager.component.css'],
})
export class PaymentManagerComponent implements OnInit, OnDestroy {
    @ViewChild('billTableDataComponent') billTableDataComponent: BillTableDataComponent;
    @ViewChild(WalkInPatientComponent) walkInPatientComponent: WalkInPatientComponent;
    @ViewChild('paymentMethodComponent') paymentMethodComponent: PaymentMethodComponent;

    public currencySym = environment.currencySign;
    public billedItemBillType: BillPaymentOptionTypeEnum = BillPaymentOptionTypeEnum.BILLED_ITEM;
    public unBilledItemBillType: BillPaymentOptionTypeEnum = BillPaymentOptionTypeEnum.UN_BILLED_ITEM;
    public walkInBillType: BillPaymentOptionTypeEnum = BillPaymentOptionTypeEnum.WALK_IN;
    public searchByPatient: BillPaymentSearchByEnum = BillPaymentSearchByEnum.PATIENT;
    public payload: BillPayment = new BillPayment();
    public billSearchByCollection: any[] = searchByPatientOrBillNumber;
    public isPosTransaction = false;
    public patientBillList: PatientBill[] = [];
    public servicePaymentView: BillViewTypeEnum = BillViewTypeEnum.SERVICE_PAYMENT;
    public tableProps: ITableProps;
    public locationName = '';
    public posTransactionRef = '';
    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private paymentService: PaymentService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.CASH_LOCATION);
        if (isMatch) {
            this.onInitializeComponent();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onGetSchemeDiscount = (serviceOrDrug: ServiceOrDrugEnum): number => {
        let schemeDiscount = 0;
        if ( this.isSchemedPatient() === true) {
            const patientInsurance = this.payload.patientDetailPayload.patientInsurance;
            const schemePlan = patientInsurance.scheme.schemePlans.find(value => value.id === patientInsurance.schemePlanId);
            schemeDiscount = serviceOrDrug === ServiceOrDrugEnum.DRUG ? schemePlan.percentDrug : schemePlan.percentService;
        }
        return schemeDiscount;
    }

    public isSchemedPatient = (): boolean => {
        return this.payload.patientDetailPayload.patientCategoryEnum === PatientCategoryEnum.SCHEME;
    }

    public canAllocateDeposit = (billItemNetAmount: number): boolean => {
        return this.payload.depositPayload.totalAmount > billItemNetAmount;
    }

    // reset item deposit allocation when item qty chang
    public onResetItemDepositAllocation = (item: BillItem | PharmacyBillItem): void => {
        if (item && item.isAllocate === true) {
            item.isAllocate = false;
            this.payload.isAllocateToAllBill = false;
            this.reCalculateDepositAmount({
                type: 'deallocate',
                isAllItem: false,
                depositTotal: this.payload.depositPayload.totalAmount,
                billTotal: this.payload.patientBill.billTotal.netTotal,
                singleItem: item,
            });

        }
    }

    public onMakePayment(): void {
        const res = this.onValidatePayload();
        if (res.status === false) {
            this.toast.error(res.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.paymentService.onMakeBillPayment(this.payload).subscribe(
                (result) => {
                    this.spinner.hide().then();

                    if (result.status === 200 && result.body.size) {
                        this.toast.success(
                            'Submitted Successfully',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        const file = new Blob([result.body], { type: 'application/pdf' });
                        const fileURL = URL.createObjectURL(file);
                        window.open(fileURL);
                        this.commonService.lookUpCashierShift();
                        this.onResetForm();
                    }
                },
                (error) => {
                    this.toast.error(HmisConstants.INTERNAL_SERVER_ERROR, HmisConstants.ERR_TITLE);
                    this.spinner.hide().then();
                }
            )
        );
    }

    public onResetForm(): void {
        this.patientBillList = [];
        this.payload = new BillPayment();
        this.paymentMethodComponent.clearSearchFiled();
    }

    public onAllocateAllDepositChange(event): void {
        const checkValue = event.target.checked;
        const depositTotal = this.payload.depositPayload.tempAmount;
        const billTotal = this.payload.patientBill.billTotal.netTotal;

        // validate deposit amount before allocating to  all bill item
        if (checkValue === true) {
            const validReq = this.onValidateBeforeDepositAllocation(billTotal, depositTotal);
            if (!validReq.status) {
                this.toast.error(validReq.message, HmisConstants.VALIDATION_ERR);
                event.preventDefault();
                return;
            }
        }

        // process allocation
        this.payload.isAllocateToAllBill = checkValue;
        this.onAllocateAllBillItem(checkValue);
        this.reCalculateDepositAmount({
            type: checkValue === true ? 'allocate' : 'deallocate',
            isAllItem: true,
            depositTotal,
            billTotal}
        );

    }

    public reCalculateDepositAmount(props: {
        type: 'allocate' | 'deallocate';
        isAllItem: boolean;
        depositTotal: number;
        billTotal: number;
        singleItem?: BillItem | PharmacyBillItem;
        // singleItem property => netAmount is used in method block...
        // BillItem & PharmacyBillItem have similar property field name netAmount
        initial?: boolean;
    }): void {
        this.payload.patientBill.billTotal.billWaivedAmount = 0;

        if (props.singleItem) {
            props.singleItem.netAmount = Number(props.singleItem.tempNetAmount);
        }

        switch (props.type) {
            case 'allocate':
                if (props.isAllItem === true) {
                    this.payload.patientBill.billTotal.allocatedAmount += props.billTotal;
                    this.payload.depositPayload.totalAmount -= props.billTotal;
                    this.payload.patientBill.billTotal.netTotal = 0;

                } else {
                    const itemNetAmount = Number(props.singleItem.netAmount);
                    this.payload.patientBill.billTotal.allocatedAmount += itemNetAmount;
                    this.payload.patientBill.billTotal.netTotal -= itemNetAmount;
                    this.payload.depositPayload.totalAmount = Number(props.depositTotal) - itemNetAmount;
                    props.singleItem.netAmount = 0;
                }
                break;
            case 'deallocate':
                if (props.isAllItem) {
                    this.payload.patientBill.billTotal.allocatedAmount = 0;
                    this.payload.depositPayload.totalAmount =  props.depositTotal;
                    this.payload.patientBill.billTotal.netTotal = this.billTableDataComponent.onRecalculateNetTotal();
                } else {
                    const itemNetAmount = Number(props.singleItem.netAmount);
                    this.payload.patientBill.billTotal.allocatedAmount -= itemNetAmount;
                    this.payload.patientBill.billTotal.netTotal += itemNetAmount;
                    this.payload.depositPayload.totalAmount = Number(props.depositTotal) + itemNetAmount;
                    props.singleItem.netAmount = props.singleItem.tempNetAmount;
                }
                break;
        }
    }

    public onBillItemTypeChange(value: BillPaymentOptionTypeEnum): void {
        this.payload.billItemPaymentType = value;
        if (value !== BillPaymentOptionTypeEnum.BILLED_ITEM) {
            this.payload.paymentTypeForEnum = PaymentTypeForEnum.SERVICE;
            this.tableProps.view = BillViewTypeEnum.SERVICE_PAYMENT;
        } else {
            this.payload.paymentTypeForEnum = undefined;
            this.tableProps.view = BillViewTypeEnum.SERVICE_BILL;
        }
        this.onResetPayload(value);
    }

    public onPatientSelect(payload: PatientPayload): void {
        this.payload.patientBill = new PatientBill();
        if (payload && payload.patientId) {
            this.payload.patientDetailPayload = new PatientPayload();
            this.payload.patientDetailPayload = payload;
            this.payload.depositPayload.totalAmount = payload?.totalDepositAmount;
            this.payload.depositPayload.tempAmount = payload?.totalDepositAmount;
            if (this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.BILLED_ITEM) {
                this.onRemoteFindBillsByPatient(payload.patientId);
            }
        }
    }

    public onProductServiceSelected(payload: ProductServicePayload): void {
        if (
            this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.UN_BILLED_ITEM &&
            !this.payload.patientDetailPayload.patientId
        ) {
            this.toast.error(HmisConstants.PATIENT_IS_REQUIRED, HmisConstants.VALIDATION_ERR);
            return;
        }

        if (this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.WALK_IN) {
            const isWalkInPatientValid = this.walkInPatientComponent.onValidate();
            if (!isWalkInPatientValid.status) {
                this.toast.error(isWalkInPatientValid.message, HmisConstants.VALIDATION_ERR);
                return;
            } else {
                this.payload.walkInPatient =
                    this.walkInPatientComponent.onGetWalkInPatientPayload();
            }
        }

        if (payload) {
            this.billTableDataComponent.onAddBillItem(payload);
        }
    }

    public onSearchByBillNumberSelected(payload: PatientBill): void {
        if (payload) {
            if (this.onCheckBillItemsLength(payload) === false) {
                return;
            }

            this.payload.patientBill = new PatientBill();

            this.payload.patientDetailPayload = new PatientPayload();
            if (payload.billPatientType === BillPatientTypeEnum.REGISTERED) {
                this.payload.patientDetailPayload = payload.patientDetailDto;
            } else if (payload.billPatientType === BillPatientTypeEnum.WALK_IN) {
                this.payload.walkInPatient = payload.walkInPatient;
                this.payload.patientDetailPayload = payload.patientDetailDto;
            }
            this.payload.depositPayload = {
                totalAmount: payload?.deposit?.totalAmount,
                tempAmount: payload?.deposit?.totalAmount,
            };
            payload.billTotal.allocatedAmount = 0;
            payload.billTotal.billWaivedAmount = 0;
            this.payload.patientBill = payload;
            this.payload.paymentTypeForEnum = payload.billTypeFor;
            this.onSetBillTempNetAmount(payload);
            this.updateTableView(payload);
        }
    }

    // set bill tempNet Amount after bill is selected
    public onSetBillTempNetAmount(payload: PatientBill) {
        if (payload.billTypeFor === PaymentTypeForEnum.DRUG) {
            payload.pharmacyBillItems.map((value) => (value.tempNetAmount = value.netAmount));
            return;
        }
        if (payload.billTypeFor === PaymentTypeForEnum.SERVICE) {
            payload.billItems.map(value => value.tempNetAmount = value.netAmount);
            return;
        }
    }

    public updateTableView(payload: PatientBill) {
        if (payload.billTypeFor === PaymentTypeForEnum.DRUG && payload.pharmacyBillItems.length) {
            this.tableProps.view = BillViewTypeEnum.DRUG_PAYMENT;
            this.payload.paymentTypeForEnum = PaymentTypeForEnum.DRUG;
        } else if (payload.billTypeFor === PaymentTypeForEnum.SERVICE && payload.billItems.length) {
            this.tableProps.view = BillViewTypeEnum.SERVICE_PAYMENT;
            this.payload.paymentTypeForEnum = PaymentTypeForEnum.SERVICE;
        }
    }

    public onSearchByPatientBillNumberSelect(payload: PatientBill): void {
        if (payload) {
            if (this.onCheckBillItemsLength(payload) === false) {
                return;
            }
            this.payload.patientBill = new PatientBill();
            this.payload.patientBill = payload;
            this.payload.isAllocateToAllBill = false;
            if (this.payload?.depositPayload?.totalAmount) {
                setTimeout(() => {
                    this.reCalculateDepositAmount({
                        type: 'deallocate',
                        isAllItem: true,
                        depositTotal: this.payload?.depositPayload?.tempAmount,
                        billTotal: payload.billTotal.netTotal,
                        initial: true,
                    });
                }, 0);
            }
            this.payload.paymentTypeForEnum = payload.billTypeFor;
            this.onSetBillTempNetAmount(payload);
            this.updateTableView(payload);
        }
    }

    public onCheckBillItemsLength(bill: PatientBill): boolean {
        // let result = true;
        // const type = bill.billTypeFor;
        // const hasServiceBillItems = bill.billItems.length > 0;
        // const hasDrugBillItems = bill.pharmacyBillItems.length > 0;
        // if (type === PaymentTypeForEnum.DRUG && hasDrugBillItems === false) {
        //     result = false;
        //     this.toast.error(HmisConstants.NO_BILL_ITEM_FOUND, HmisConstants.VALIDATION_ERR);
        // } else if (type === PaymentTypeForEnum.SERVICE && hasServiceBillItems === false) {
        //     result = false;
        //     this.toast.error(HmisConstants.NO_BILL_ITEM_FOUND, HmisConstants.VALIDATION_ERR);
        // }
        // return result;
        // return this.commonService.hasBillItems(bill);
        const hasBillItems = this.commonService.hasBillItems(bill);
        if (hasBillItems === false) {
            this.toast.error(HmisConstants.NO_BILL_ITEM_FOUND, HmisConstants.VALIDATION_ERR);
        }
        return hasBillItems;
    }

    public onRemoteFindBillsByPatient(patientId: number): void {
        if (patientId) {
            this.spinner.show().then();
            const payload = { patientId, loadDeposit: true };
            this.subscription.add(
                this.paymentService.onGetPatientServiceBills(payload).subscribe(
                    (result) => {
                        this.spinner.hide().then();
                        if (result.status === 200) {
                            this.payload.depositPayload = {
                                totalAmount: result.body.depositAmount,
                                tempAmount: result.body.depositAmount
                            };
                            if (result.body.billDtoList.length) {
                                this.patientBillList = result.body.billDtoList;
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
    }

    public onResetPayload(type?: BillPaymentOptionTypeEnum): void {
        this.payload = new BillPayment();
        this.patientBillList = [];

        this.billSearchByCollection = searchByPatientOrBillNumber;
        if (type) {
            this.payload.billItemPaymentType = type;
            if (type === this.unBilledItemBillType) {
                this.payload.billSearchedBy = BillPaymentSearchByEnum.PATIENT;
                this.billSearchByCollection = [searchByPatientOrBillNumber[1]];
            } else if (type === this.walkInBillType) {
                this.payload.paymentTypeForEnum = PaymentTypeForEnum.SERVICE;
            }
        }
        this.tableProps.allowEdit = this.payload?.billItemPaymentType !== this.billedItemBillType;
    }

    public onPaymentMethodSelected(event: SharedPayload): void {
        this.isPosTransaction = event.name === PaymentMethodEnum.POS;
        this.payload.paymentMethod = { name: event.name, id: event.id };
    }

    public onBillTotalChanged(total: IBillTotal): void {
        if (total) {
            this.payload.patientBill.billTotal.netTotal = total.netTotal;
            this.payload.patientBill.billTotal.grossTotal = total.grossTotal;
            this.payload.patientBill.billTotal.discountTotal = total.discountTotal;
            this.payload.patientBill.billTotal.allocatedAmount = total.allocatedAmount;
            this.payload.patientBill.billTotal.billWaivedAmount = total.billWaivedAmount;
        }
    }

    public onBillItemIsAllocated(event: {
        checkValue: boolean;
        billItem: BillItem | PharmacyBillItem;
    }): void {
        this.reCalculateDepositAmount({
            type: event.checkValue === true ? 'allocate' : 'deallocate',
            isAllItem: false,
            depositTotal: this.payload.depositPayload.totalAmount,
            billTotal: this.payload.patientBill.billTotal.netTotal,
            singleItem: event.billItem,
        });
        setTimeout(() => {
            this.payload.isAllocateToAllBill = this.isAllItemAllocated();
        }, 0);
    }

    public onSearchByDropdownChange(value: BillPaymentSearchByEnum): void {
        this.payload.depositPayload = { totalAmount: 0, tempAmount: 0 };
        this.payload.patientDetailPayload = new PatientPayload();
        this.payload.patientBill = new PatientBill();
    }

    public isRemoveAllBillItem(value: boolean) {
        if (value === true) {
            this.payload.patientBill.billItems = [];
            this.payload.patientBill.pharmacyBillItems = [];
            if (this.payload.depositPayload.tempAmount) {
                this.payload.depositPayload.totalAmount = this.payload.depositPayload.tempAmount;
            }
            this.payload.isAllocateToAllBill = false;
        }
    }

    public getDepositAmount() {
        return this.payload?.depositPayload?.totalAmount ? this.payload?.depositPayload?.totalAmount
            : 0;
    }

    private onInitializeComponent(): void {
        setTimeout(() => {
            this.spinner.show().then();
            // this.billSearchByCollection = Object.values(BillPaymentSearchByEnum);
            this.billSearchByCollection = searchByPatientOrBillNumber;
            const dep = this.commonService.getCurrentLocation();
            this.locationName = dep.name;
            this.payload = new BillPayment();

            this.payload.department = dep;
            this.payload.user = this.commonService.getCurrentUser();
            this.payload.billSearchedBy = this.billSearchByCollection[0].name;
            this.payload.billItemPaymentType = BillPaymentOptionTypeEnum.BILLED_ITEM;
            this.payload.patientDetailPayload = new PatientPayload();
            this.payload.patientBill = new PatientBill();

            this.tableProps = {
                allowEdit: this.payload?.billItemPaymentType !== this.billedItemBillType,
                type: this.payload?.billItemPaymentType,
                view: this.servicePaymentView,
                billPatientCategory: this.payload?.patientDetailPayload?.patientCategoryEnum,
                isAllocateAll: false,
            };

            this.spinner.hide().then();
        }, 0);
    }

    private onAllocateAllBillItem(checkVal: boolean): void {
        this.payload.patientBill.billTotal.allocatedAmount = 0;

        if (this.tableProps.view === BillViewTypeEnum.DRUG_BILL || this.tableProps.view === BillViewTypeEnum.DRUG_PAYMENT) {
            const tempItems: PharmacyBillItem[] = [];
            this.payload.patientBill.pharmacyBillItems.map((val) => {
                val.isAllocate = checkVal;
                val.netAmount = checkVal === true ? 0 : val.tempNetAmount;
                tempItems.push(Object.assign({}, val));
            });
            this.payload.patientBill.pharmacyBillItems = tempItems;
        } else if (this.tableProps.view === BillViewTypeEnum.SERVICE_BILL || this.tableProps.view === BillViewTypeEnum.SERVICE_PAYMENT) {
            const tempItems: BillItem[] = [];
            this.payload.patientBill.billItems.map((val) => {
                val.isAllocate = checkVal;
                val.netAmount = checkVal === true ? 0 : val.tempNetAmount;
                tempItems.push(Object.assign({}, val));
            });
            this.payload.patientBill.billItems = tempItems;
        }

    }

    private isAllItemAllocated(): boolean {
        return this.billTableDataComponent.isAllItemAllocated();
    }

    /* check if its a drug or service bill/payment
    * check if deposit amount greater than bill amount */
    protected onValidateBeforeDepositAllocation(
        billTotal: number,
        depositTotal
    ): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        let billSize = 0;

        if (this.tableProps.view === BillViewTypeEnum.DRUG_BILL || this.tableProps.view === BillViewTypeEnum.DRUG_PAYMENT) {
            billSize = this.payload.patientBill.pharmacyBillItems.length;
        } else if (this.tableProps.view === BillViewTypeEnum.SERVICE_BILL || this.tableProps.view === BillViewTypeEnum.SERVICE_PAYMENT) {
            billSize = this.payload.patientBill.billItems.length;
        }

        if (billSize < 1) {
            res.message += 'NO BILL ITEM SELECTED <br>';
            res.status = false;
        } else if (depositTotal === billTotal) {
            res.message += 'DEPOSIT CANNOT BE EXACT EQUAL TO AMOUNT. TOP UP! <br>';
            res.status = false;
        } else if (depositTotal < billTotal) {
            res.message += 'DEPOSIT AMOUNT IS SMALLER THAN BILL TOTAL AMOUNT <br>';
            res.status = false;
        }
        return res;
    }

    protected onValidatePayload(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.BILLED_ITEM) {
            if (this.payload.patientBill.billPatientType === BillPatientTypeEnum.REGISTERED) {
                if (!this.payload.patientDetailPayload.patientId) {
                    res.status = false;
                    res.message += 'Patient Is Required <br>';
                }
            } else if (this.payload.patientBill.billPatientType === BillPatientTypeEnum.WALK_IN) {
                // bill was generated from patient bill as a walk in patient bill
                if (!this.payload.walkInPatient) {
                    // if walk in patient detials is not present in bill>
                    res.status = false;
                    res.message += 'WalkInPatient Details From Bill Is Required <br>';
                }
            }

            if (!this.payload.patientBill.id) {
                res.status = false;
                res.message += 'Bill is Required <br>';
            }
        } else if (this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.UN_BILLED_ITEM) {
            if (!this.payload.patientDetailPayload.patientId) {
                res.status = false;
                res.message += 'Patient Is Required <br>';
            }
        } else if (this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.WALK_IN) {
            const walkIn = this.walkInPatientComponent.onValidate();
            if (walkIn.status === false) {
                res.status = false;
                res.message += walkIn.message;
            }
        }

        if (
            this.tableProps.view === BillViewTypeEnum.SERVICE_BILL ||
            this.tableProps.view === BillViewTypeEnum.SERVICE_PAYMENT
        ) {
            if (this.payload.patientBill.billItems.length < 1) {
                res.status = false;
                res.message += 'No Service Bill Items Yet! <br>';
            }
        } else if (
            this.tableProps.view === BillViewTypeEnum.DRUG_PAYMENT ||
            this.tableProps.view === BillViewTypeEnum.DRUG_BILL
        ) {
            if (this.payload.patientBill.pharmacyBillItems.length < 1) {
                res.status = false;
                res.message += 'No Drug Bill Items Yet!<br>';
            }
        }

        if (!this.payload.paymentMethod.id) {
            res.status = false;
            res.message += 'Select Payment Method';
        }

        if (!this.payload.department) {
            this.payload.department = this.commonService.getCurrentLocation();
        }

        if (!this.payload.user) {
            this.payload.user = this.commonService.getCurrentUser();
        }

        if (
            !this.payload.paymentTypeForEnum &&
            this.payload.billItemPaymentType === BillPaymentOptionTypeEnum.UN_BILLED_ITEM
        ) {
            this.payload.paymentTypeForEnum = PaymentTypeForEnum.SERVICE;
        }

        return res;
    }

}

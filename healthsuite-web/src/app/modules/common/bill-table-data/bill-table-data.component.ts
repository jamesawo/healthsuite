import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import {
    BillItem,
    BillViewTypeEnum,
    IBillTotal,
    ITableProps,
    ServiceOrDrugEnum,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { ProductServicePayload } from '@app/shared/_payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {
    DrugRegisterPayload,
    PharmacyBillItem,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-bill-table-data',
    templateUrl: './bill-table-data.component.html',
    styleUrls: ['./bill-table-data.component.css'],
})
export class BillTableDataComponent implements OnInit, OnChanges {
    @Input('props') public props: ITableProps;
    @Input('serviceBillItems') public serviceBillItems?: BillItem[] = [];
    @Input('pharmacyBillItems') public pharmBillItems?: PharmacyBillItem[] = [];
    @Input('hasDeposit') public hasDeposit?: (param: number) => boolean;
    @Input('isSchemePatient') public isSchemePatient?: () => boolean;
    @Input('isEnforceCount') public isEnforceCount?: () => boolean;

    @Output('totalChanged') totalChanged: EventEmitter<IBillTotal> = new EventEmitter<IBillTotal>();
    @Output('allocateChanged') allocateChanged: EventEmitter<{
        checkValue: boolean;
        billItem: BillItem | PharmacyBillItem;
    }> = new EventEmitter<any>();
    @Output('isRemoveAllBillItem') public isRemoveAllBillItem: EventEmitter<boolean> =
        new EventEmitter<boolean>();
    @Output('onItemIsTouchedOrChanged') public onItemIsTouchedOrChanged: EventEmitter<any> =
        new EventEmitter<any>();

    @Input('onGetSchemePrice') public onGetSchemePrice?: (
        serviceOrDrug: ServiceOrDrugEnum
    ) => number;

    serviceBillView: BillViewTypeEnum = BillViewTypeEnum.SERVICE_BILL;
    servicePaymentView: BillViewTypeEnum = BillViewTypeEnum.SERVICE_PAYMENT;
    drugBillView: BillViewTypeEnum = BillViewTypeEnum.DRUG_BILL;
    drugPaymentView: BillViewTypeEnum = BillViewTypeEnum.DRUG_PAYMENT;

    constructor(private commonService: CommonService, private toast: ToastrService) {}

    public ngOnInit(): void {}

    public ngOnChanges(changes: SimpleChanges) {}

    public checkAllCheckBox(ev): void {
        if (
            this.props?.view === BillViewTypeEnum.DRUG_BILL ||
            this.props?.view === BillViewTypeEnum.DRUG_PAYMENT
        ) {
            this.pharmBillItems.forEach((x) => (x.checked = ev.target.checked));
        } else {
            this.serviceBillItems.forEach((x) => (x.checked = ev.target.checked));
        }
    }

    public onRemoveAllCheckedBillItems() {
        const isAllChecked = this.isAllCheckBoxChecked();
        if (this.props?.view === BillViewTypeEnum.DRUG_BILL) {
            if (this.pharmBillItems.length) {
                if (isAllChecked === true) {
                    this.pharmBillItems = [];
                } else {
                    for (let i = 0; i < this.pharmBillItems.length; i++) {
                        if (this.pharmBillItems[i].checked) {
                            this.pharmBillItems.splice(i, 1);
                        }
                    }
                }
            }
        } else {
            if (this.serviceBillItems.length) {
                if (isAllChecked) {
                    this.serviceBillItems = [];
                } else {
                    for (let i = 0; i < this.serviceBillItems.length; i++) {
                        if (this.serviceBillItems[i].checked) {
                            this.serviceBillItems.splice(i, 1);
                        }
                    }
                }
            }
        }
        this.onUpdateTotals();
        this.isRemoveAllBillItem.emit(isAllChecked);
    }

    public isAllCheckBoxChecked(): boolean {
        if (
            this.props?.view === BillViewTypeEnum.DRUG_BILL ||
            this.props?.view === BillViewTypeEnum.DRUG_PAYMENT
        ) {
            if (this.pharmBillItems.length) {
                return this.pharmBillItems.every((p) => p.checked);
            } else {
                return false;
            }
        } else {
            if (this.serviceBillItems.length) {
                return this.serviceBillItems.every((p) => p.checked);
            } else {
                return false;
            }
        }
    }

    public isAllItemAllocated(): boolean {
        let value = false;
        if (this.props?.view === BillViewTypeEnum.DRUG_PAYMENT && this.pharmBillItems.length > 0) {
            value = this.pharmBillItems.every((p) => p.isAllocate === true);
        } else if (
            this.props?.view === BillViewTypeEnum.SERVICE_PAYMENT &&
            this.serviceBillItems.length > 0
        ) {
            value = this.serviceBillItems.every((p) => p.isAllocate === true);
        }
        return value;
    }

    public onRemoveBillItem(bill: any): IBillTotal {
        if (this.props?.view === BillViewTypeEnum.DRUG_BILL) {
            const idx = this.pharmBillItems.indexOf(bill);
            if (idx !== -1) {
                this.pharmBillItems.splice(idx, 1);
            }
        } else {
            const idx = this.serviceBillItems.indexOf(bill);
            if (idx !== -1) {
                this.serviceBillItems.splice(idx, 1);
            }
        }
        this.onItemIsTouchedOrChanged.emit(bill);
        return this.onUpdateTotals();
    }

    public onBillQuantityChange(
        event: any,
        serviceBill?: BillItem,
        pharmBill?: PharmacyBillItem
    ): IBillTotal {
        let quantity: number = event.target.value ? event.target.value : 0;
        const isScheme = this.checkIsPatientScheme();

        if (this.props?.view === BillViewTypeEnum.DRUG_BILL) {
            if (this.isEnforceCount() === true && pharmBill.drugRegister.availableQty < quantity) {
                this.toast.error(HmisConstants.ITEM_QTY_TOO_LOW, HmisConstants.LOW_STOCK_COUNT);
                pharmBill.quantity = 0;
                quantity = 0;
            }
            // remove any allocated deposit in payment-manager.component.ts
            this.onItemIsTouchedOrChanged.emit(pharmBill);
            // set drug bill price
            this.onSetDrugBillPrice({
                item: pharmBill,
                isScheme,
                payload: pharmBill.drugRegister,
                quantity,
            });
            return this.onUpdateTotals();
        } else {
            // remove any allocated deposit in payment-manager.component.ts
            this.onItemIsTouchedOrChanged.emit(serviceBill);
            // set service bill price
            this.setServiceBillPrice(serviceBill, isScheme, serviceBill.productService, quantity);
            return this.onUpdateTotals();
        }
    }

    public onSetDrugBillPrice(billData: {
        item: PharmacyBillItem;
        isScheme: boolean;
        payload: DrugRegisterPayload;
        quantity: number;
    }): void {
        const { item, payload, quantity, isScheme } = billData;
        item.drugRegister = payload;
        item.quantity = quantity;

        if (isScheme) {
            item.price = payload.nhisSellingPrice;
            item.nhisPrice = payload.nhisSellingPrice;
            item.discountAmount = payload.discountPercent;
            item.grossAmount = payload.nhisSellingPrice * quantity;
/*
            if (payload.discountPercent > 0) {
                item.netAmount =
                    payload.nhisSellingPrice * quantity -
                    ((payload.nhisSellingPrice * payload.discountPercent) / 100) * quantity;
            } else {
                item.netAmount = payload.nhisSellingPrice * quantity;
            }*/

            const discount = payload.discountPercent + this.onGetSchemePrice(ServiceOrDrugEnum.DRUG);
            item.netAmount = this.getPercentValue(payload.nhisSellingPrice, quantity, discount);

            item.discountAmount = item.grossAmount - item.netAmount;
        } else {
            item.price = payload.regularSellingPrice;
            item.grossAmount = payload.regularSellingPrice * quantity;
            item.netAmount = payload.regularSellingPrice * quantity;
        }
        item.tempNetAmount = item.netAmount;
    }

    public getPercentValue(price: number, quantity: number, discountAmount: number): number {
        return price * quantity - ((price * discountAmount) / 100) * quantity;
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onAddBillItem(payload: ProductServicePayload) {
        const hasDuplicate = this.isDuplicate(payload);
        if (payload && !hasDuplicate) {
            const isScheme: boolean = this.checkIsPatientScheme();
            const billItem: BillItem = new BillItem();
            this.setServiceBillPrice(billItem, isScheme, payload, 1);
            this.serviceBillItems.push(billItem);
            this.onUpdateTotals();
        }
    }

    public checkIsPatientScheme(): boolean {
        // return this.props?.billPatientCategory == PatientCategoryEnum.SCHEME;
        return this.isSchemePatient();
    }

    public onBillPayCashChanged(
        event: any,
        serviceBill?: BillItem,
        pharmBillItem?: PharmacyBillItem
    ) {
        if (this.props?.view === BillViewTypeEnum.DRUG_BILL) {
            pharmBillItem.isPayCash = event.target.checked;
        } else {
            serviceBill.payCash = event.target.checked;
        }
    }

    public onBillAllocateDepositChange(
        event: any,
        serviceBillItem: BillItem,
        drugBillItem?: PharmacyBillItem
    ) {
        const checkValue = event.target.checked;
        let hasDeposit;

        if (serviceBillItem && !drugBillItem) {
            hasDeposit = this.hasDeposit(serviceBillItem.netAmount);
        } else if (drugBillItem && !serviceBillItem) {
            hasDeposit = this.hasDeposit(drugBillItem.netAmount);
        }

        if (!hasDeposit) {
            event.preventDefault();
            this.toast.error('Insufficient Deposit Balance', HmisConstants.VALIDATION_ERR);
            return;
        }

        if (
            this.props.view === BillViewTypeEnum.SERVICE_PAYMENT ||
            this.props.view === BillViewTypeEnum.SERVICE_BILL
        ) {
            this.allocateChanged.emit({ checkValue, billItem: serviceBillItem });
        } else if (
            this.props.view === BillViewTypeEnum.DRUG_PAYMENT ||
            this.props.view === BillViewTypeEnum.DRUG_BILL
        ) {
            this.allocateChanged.emit({ checkValue, billItem: drugBillItem });
        }
    }

    public onRecalculateNetTotal(): number {
        return this.onRecalculateSumValue('net');
    }

    public onDrugBillDosageChange(event: any, bill: PharmacyBillItem) {
        const dosage: number = event.target.value;

        if (dosage) {
            bill.dosage = dosage;
        } else {
            bill.dosage = 0;
        }
    }

    public onDrugFrequencySelected(freq: string, bill: PharmacyBillItem) {
        if (freq) {
            // bill.frequency = event.value;
            bill.frequency = freq;
        }
    }

    public onDrugDaysChanged(event: any, bill: PharmacyBillItem) {
        const days: number = event.target.value;
        if (days) {
            bill.days = days;
        } else {
            bill.days = 0;
        }
    }

    public onAutoCalculateDrugQty(): void {
        // todo:: calculate drug qty base on dosage, frequency & number of days
    }

    public onRecalculateSumValue(type: 'deposit' | 'net' | 'discount' | 'gross' | 'waived') {
        if (
            this.props.view === BillViewTypeEnum.SERVICE_PAYMENT ||
            this.props.view === BillViewTypeEnum.SERVICE_BILL
        ) {
            if (this.serviceBillItems.length) {
                return this.onRecalculateServiceItemListSumSwitch(this.serviceBillItems, type);
            }
            return 0;
        } else if (
            this.props.view === BillViewTypeEnum.DRUG_BILL ||
            this.props.view === BillViewTypeEnum.DRUG_PAYMENT
        ) {
            if (this.pharmBillItems.length) {
                return this.onRecalculateDrugItemListSumSwitch(this.pharmBillItems, type);
            }
            return 0;
        }
    }

    protected onUpdateTotals(): IBillTotal {
        const billTotal: IBillTotal = {
            discountTotal: 0,
            grossTotal: 0,
            netTotal: 0,
            allocatedAmount: 0,
            billWaivedAmount: 0,
        };
        billTotal.netTotal = this.onRecalculateSumValue('net');
        billTotal.discountTotal = this.onRecalculateSumValue('discount');
        billTotal.grossTotal = this.onRecalculateSumValue('gross');
        billTotal.allocatedAmount = this.onRecalculateSumValue('deposit');
        this.totalChanged.emit(billTotal);
        return billTotal;
    }

    protected isDuplicate(product: ProductServicePayload): boolean {
        let flag = false;
        this.serviceBillItems.forEach((item) => {
            if (item.productService.id === product.id) {
                flag = true;
            }
        });
        return flag;
    }

    protected setServiceBillPrice(
        billItem: BillItem,
        isScheme: boolean,
        payload: ProductServicePayload,
        quantity: number
    ): void {
        billItem.productServiceId = payload.id;
        billItem.productService = payload;
        billItem.payCash = true;
        billItem.quantity = quantity;

        if (isScheme) {
            billItem.price = 0.0;
            billItem.nhisPercent = payload.discount;
            billItem.nhisPrice = payload.nhisSellingPrice;
            billItem.grossAmount = payload.nhisSellingPrice * quantity;

            // if (payload.discount > 0) {
            //     billItem.netAmount =
            //         payload.nhisSellingPrice * quantity -
            //         ((payload.nhisSellingPrice * payload.discount) / 100) * quantity;
            // } else {
            //     billItem.netAmount = payload.nhisSellingPrice * quantity;
            // }

            const discount = payload.discount + this.onGetSchemePrice(ServiceOrDrugEnum.DRUG);
            billItem.netAmount = this.getPercentValue(payload.nhisSellingPrice, quantity, discount);

            billItem.discountAmount = billItem.grossAmount - billItem.netAmount;
            // billItem.discountAmount = payload.discount;
        } else {
            billItem.price = payload.regularSellingPrice;
            billItem.grossAmount = payload.regularSellingPrice * quantity;
            billItem.nhisPercent = 0.0;
            billItem.netAmount = payload.regularSellingPrice * quantity;
            billItem.nhisPrice = 0.0;
            billItem.discountAmount = payload.discount;
        }

        billItem.tempNetAmount = billItem.netAmount;
    }

    protected onRecalculateDrugItemListSumSwitch(
        list: PharmacyBillItem[],
        type: 'deposit' | 'net' | 'discount' | 'gross' | 'waived'
    ): number {
        switch (type) {
            case 'deposit':
                let amount = 0;
                list.forEach((item) => {
                    if (item.isAllocate) {
                        amount += item.grossAmount;
                    }
                });
                return amount;
            case 'discount':
                return list
                    .map((item) => item.discountAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'gross':
                return list
                    .map((item) => item.grossAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'net':
                return list
                    .map((item) => item.netAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'waived':
                return list
                    .map((item) => item.waivedAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            default:
                return 0;
        }
    }

    protected onRecalculateServiceItemListSumSwitch(
        list: BillItem[],
        type: 'deposit' | 'net' | 'discount' | 'gross' | 'waived'
    ): number {
        switch (type) {
            case 'deposit':
                let amount = 0;
                list.forEach((item) => {
                    if (item.isAllocate) {
                        amount += item.grossAmount;
                    }
                });
                return amount;
            case 'discount':
                return list
                    .map((item) => item.discountAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'gross':
                return list
                    .map((item) => item.grossAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'net':
                return list
                    .map((item) => item.netAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            case 'waived':
                return list
                    .map((item) => item.waivedAmount)
                    .reduce((a, b) => {
                        return a + b;
                    });
            default:
                return 0;
        }
    }
}

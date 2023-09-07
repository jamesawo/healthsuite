import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    DrugOrderPayload,
    PharmacyReceivedGoodsItemPayload,
    PharmacyReceivedGoodsPayload,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { DrugOrderService } from '@app/shared/_services/pharmacy/drug-order.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { DatePayload, ValidationMessage } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-receive-supplied-goods',
    templateUrl: './receive-supplied-goods.component.html',
    styleUrls: ['./receive-supplied-goods.component.css'],
})
export class ReceiveSuppliedGoodsComponent implements OnInit, OnDestroy {
    public payload: PharmacyReceivedGoodsPayload = new PharmacyReceivedGoodsPayload();

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private drugOrderService: DrugOrderService
    ) {}

    ngOnInit(): void {
        let isMatch = this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION);
        if (isMatch) {
            this.onInitializeComponent();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSubmit(): void {
        let validate = this.onValidateBeforeSave();
        if (validate.status == false) {
            this.toast.error(validate.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.drugOrderService.onSaveReceivedGoods(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.body) {
                        this.toast.success(HmisConstants.LAST_ACTION_SUCCESS);
                        this.onResetForm();
                    } else {
                        this.toast.error(HmisConstants.LAST_ACTION_FAILED);
                        this.onResetForm();
                    }
                },
                (error) => {
                    console.log(error);
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.LAST_ACTION_FAILED);
                    this.onResetForm();
                }
            )
        );
    }

    public onSelectDrugOrder(payload: DrugOrderPayload): void {
        if (payload) {
            const isSameLoca = this.checkDrugOrderLocation(payload.outlet.name);
            if (isSameLoca === false) {
                this.toast.error('Invalid Drug Order Creation Location', 'MisMatch Error!');
                this.onResetForm();
                return;
            }
            this.payload.drugOrder = payload;
            this.payload.supplyingCompany = payload.vendor;
            this.setItemsList(payload);
        }
    }

    public setItemsList(payload: DrugOrderPayload): void {
        if (payload.drugOrderItems.length > 0) {
            this.payload.receivedGoodsItemsList = [];
            payload.drugOrderItems.forEach((value) => {
                let listItem: PharmacyReceivedGoodsItemPayload = new PharmacyReceivedGoodsItemPayload();
                listItem.quantityOrdered = value.quantity;
                listItem.quantityReceived = 0;
                listItem.quantitySupplied = 0;
                listItem.rate = value.rate;
                listItem.unitOfIssue = value.drugRegister.unitOfIssue;
                listItem.totalCost = 0;
                listItem.batchNumber = '';
                listItem.expiryDate = { year: '', month: '', day: '' };
                listItem.drugRegister = value.drugRegister;
                listItem.pharmacyReceivedGoods = null;
                this.payload.receivedGoodsItemsList.push(listItem);
            });
        }
        this.reCalculateTotal();
    }

    public onInitializeComponent(): void {
        this.payload = new PharmacyReceivedGoodsPayload();
        this.payload.drugOrder.drugOrderItems = [];

        const department = this.commonService.getCurrentLocation();
        this.payload.outlet = department;
        this.payload.receivingDepartment = department;

        const user = this.commonService.getCurrentUser();
        this.payload.user = user;
        this.payload.receivedBy = user.userName;
    }

    public onDateSelected(value: DatePayload, item?: PharmacyReceivedGoodsItemPayload): void {
        if (value) {
            this.payload.invoiceDate = value;
            if (item) {
                item.expiryDate = value;
            }
        }
    }

    public onResetForm(): void {
        this.onInitializeComponent();
    }

    public onItemTotalReceivedChange(event: any, item: PharmacyReceivedGoodsItemPayload) {
        const val: number = event.target.value;
        if (val) {
            if (val > item.quantityOrdered) {
                item.quantityReceived = 0;
                item.totalCost = 0;
                this.toast.error(
                    `Total Received Cannot Be Greater Than Quantity Ordered`,
                    HmisConstants.VALIDATION_ERR
                );

                return;
            } else {
                item.quantityReceived = val;
                this.onCalculateItemTotal(item, val);
            }
        }
        this.reCalculateTotal();
    }

    public onCalculateItemTotal(item: PharmacyReceivedGoodsItemPayload, qtyReceived: number): void {
        const rate = item.rate;
        item.totalCost = qtyReceived * rate;
    }

    public onItemQtySuppliedChange(event: any, item: PharmacyReceivedGoodsItemPayload) {
        const val: number = event.target.value;
        if (val) {
            if (val > item.quantityOrdered) {
                this.toast.error(
                    `Quantity Supplied Cannot Be Greater Than Quantity Ordered `,
                    HmisConstants.VALIDATION_ERR
                );
                item.quantitySupplied = 0;
                item.totalCost = 0;
                return;
            } else {
                item.quantitySupplied = val;
                this.reCalculateTotal();
            }
        }

        this.reCalculateTotal();
    }

    public onItemBatchNumberChange(event: any, item: PharmacyReceivedGoodsItemPayload) {
        const val: string = event.target.value;
        if (val && val != '') {
            item.batchNumber = val;
        }
        this.reCalculateTotal();
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public reCalculateTotal() {
        this.payload.totalAmountSupplied = this.payload.receivedGoodsItemsList
            .map((value) => value.totalCost)
            .reduce((a, b) => a + b);
    }

    public hasZeroValueItem(): boolean {
        let flag: boolean = false;
        this.payload.receivedGoodsItemsList.forEach((value) => {
            flag = value.quantityReceived == 0;
        });
        return flag;
    }

    private onValidateBeforeSave(): ValidationMessage {
        let response: ValidationMessage = { message: '', status: true };
        if (!this.payload.drugOrder) {
            response.status = false;
            response.message += `Drug Order Is Required. <br>`;
        }

        if (!this.payload.deliveredBy) {
            response.status = false;
            response.message += `Delivered By is Required<br>`;
        }

        if (!this.payload.receivingDepartment) {
            this.payload.receivingDepartment = this.commonService.getCurrentLocation();
        }

        if (!this.payload.user) {
            this.payload.user = this.commonService.getCurrentUser();
        }

        if (this.payload.totalAmountSupplied == 0) {
            response.status = false;
            response.message += `Invalid Total Amount Supplied <br> `;
        }

        if (this.hasZeroValueItem()) {
            response.status = false;
            response.message += `Item With Zero Quantity Received, Exist <br> `;
        }

        return response;
    }

    private checkDrugOrderLocation(name: string): boolean {
        return this.commonService.getCurrentLocation().name == name;
    }
}

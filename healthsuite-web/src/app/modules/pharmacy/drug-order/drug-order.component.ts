import { Component, OnDestroy, OnInit } from '@angular/core';
import { DrugOrderService } from '@app/shared/_services/pharmacy/drug-order.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { Subscription } from 'rxjs';
import {
    DrugOrderItemPayload,
    DrugOrderPayload,
    DrugOrderSupplyCategoryEnum,
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { ValidationMessage, VendorPayload } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { environment } from '@environments/environment';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';

const supCat: any[] = [
    { id: 1, value: DrugOrderSupplyCategoryEnum.MOU, name: 'MOU' },
    { id: 2, value: DrugOrderSupplyCategoryEnum.EMERGENCY, name: 'EMERGENCY' },
    { id: 3, value: DrugOrderSupplyCategoryEnum.CREDIT, name: 'CREDIT' },
    { id: 4, value: DrugOrderSupplyCategoryEnum.CASH, name: 'CASH' },
];

@Component({
    selector: 'app-drug-order',
    templateUrl: './drug-order.component.html',
    styleUrls: ['./drug-order.component.css'],
})
export class DrugOrderComponent implements OnInit, OnDestroy {
    public payload: DrugOrderPayload = new DrugOrderPayload();
    public viewType = 'new';
    public drugSearchTerm: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;
    public currencySign = environment.currencySign;
    public supplierCategories: any[] = supCat;
    public orderVendor: VendorPayload = null;

    private subscription: Subscription = new Subscription();

    constructor(
        private drugOrderService: DrugOrderService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION);
        if (isMatch) {
            this.payload.outlet = this.commonService.getCurrentLocation();
            this.payload.user = this.commonService.getCurrentUser();
            this.setIsStore();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onViewTypeChange(value: 'new' | 'edit'): void {
        this.viewType = value;
    }

    public onSubmitOrder(): void {
        const isValid = this.onValidateBeforeSave();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.drugOrderService.onOrderDrugs(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.body) {
                        const message: any = res.body;
                        this.toast.success(
                            `Order Number: ${message.messages}`,
                            'Order Submitted Successfully',
                            { disableTimeOut: true }
                        );
                        this.onResetForm();
                    }
                },
                (error) => {
                    let display = '';
                    if (error.error.messages.length) {
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

    public onUpdateDrugOrder() {
        if (!this.payload.id) {
            this.toast.error(`Search For Drug Order First`, HmisConstants.VALIDATION_ERR);
            return;
        }
        const isValid = this.onValidateBeforeSave();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }
        this.spinner.show().then();

        this.subscription.add(
            this.drugOrderService.onUpdateDrugOrder(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.body) {
                        this.toast.success(`Order Updated`, 'Updated Successfully', {});
                        this.onResetForm();
                    }
                },
                (error) => {
                    console.log(error);
                    let display = '';
                    if (error.error.messages.length) {
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
        this.payload = new DrugOrderPayload();
        this.viewType = 'new';
        this.setIsStore();
    }

    public onDrugSelected(payload: DrugRegisterPayload): void {
        if (payload) {
            const drugOrder = this.mapToDrugOrderItem(payload);
            this.payload.drugOrderItems.push(drugOrder);
        }
    }

    public onVendorSelected(payload: VendorPayload): void {
        if (payload) {
            if (!payload.isPharmacyVendor) {
                this.toast.error(`Please Select A Pharmacy Supplier`);
                return;
            }
            this.payload.vendor = payload;
        }
    }

    public onSupplyCategorySelected(value: DrugOrderSupplyCategoryEnum): void {
        if (value) {
            this.payload.supplyCategory = value;
        }
    }

    public mapToDrugOrderItem(drug: DrugRegisterPayload): DrugOrderItemPayload {
        const drugOrderItem: DrugOrderItemPayload = new DrugOrderItemPayload();
        drugOrderItem.drugRegister = drug;
        drugOrderItem.quantity = 0;
        drugOrderItem.rate = 0;
        drugOrderItem.totalAmount = 0;
        return drugOrderItem;
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onItemQtyChange(event: any, item: DrugOrderItemPayload, column: 'qty' | 'rate') {
        let value = event.target.value;
        if (!value || value === '') {
            value = 0;
        }

        if (column === 'qty') {
            item.quantity = value;
        } else if (column === 'rate') {
            item.rate = value;
        }

        this.onCalculateItemTotal(value, item);
    }

    public onReCalculateSumTotal(): void {
        if (this.payload?.drugOrderItems.length > 0) {
            this.payload.sumTotalAmount = this.payload?.drugOrderItems
                .map((value) => value.totalAmount)
                .reduce((a, b) => {
                    return a + b;
                });
        }
    }

    public onCalculateItemTotal(value: any, item: DrugOrderItemPayload) {
        item.totalAmount = item.quantity * item.rate;
        this.onReCalculateSumTotal();
    }

    public onSelectDrugOrder(value: DrugOrderPayload): void {
        this.payload = new DrugOrderPayload();
        if (value) {
            if (value.outlet.name !== this.commonService.getCurrentLocation().name) {
                this.toast.error(`Order MisMatch Location Request`, `Unauthorized!`);
                return;
            }
        }
        this.orderVendor = value.vendor;
        this.payload = value;
        this.onReCalculateSumTotal();
    }

    public isAllCheckBoxChecked(): boolean {
        if (this.payload.drugOrderItems.length) {
            return this.payload.drugOrderItems.every((p) => p.checked);
        } else {
            return false;
        }
    }

    public checkAllCheckBox(ev): void {
        this.payload.drugOrderItems.forEach((x) => (x.checked = ev.target.checked));
    }

    public onRemoveAllCheckedItems() {
        if (this.payload?.drugOrderItems?.length) {
            if (this.isAllCheckBoxChecked()) {
                for (let i = 0; i < this.payload?.drugOrderItems?.length; i++) {
                    this.payload.drugOrderItems.splice(i, 1);
                    this.onReCalculateSumTotal();
                }
            } else {
                for (let i = 0; i < this.payload?.drugOrderItems?.length; i++) {
                    if (this.payload?.drugOrderItems[i].checked) {
                        this.payload.drugOrderItems.splice(i, 1);
                    }
                }
                this.onReCalculateSumTotal();
            }
            this.onReCalculateSumTotal();
        }
    }

    public onRemoveItem(item: any) {
        const idx = this.payload.drugOrderItems.indexOf(item);
        if (idx !== -1) {
            this.payload.drugOrderItems.splice(idx, 1);
            this.onReCalculateSumTotal();
        }
        this.onReCalculateSumTotal();
    }

    private onValidateBeforeSave(): ValidationMessage {
        const response: ValidationMessage = { status: true, message: '', messages: [] };

        if (!this.payload.vendor || !this.payload.vendor.id) {
            response.status = false;
            response.message += 'Vendor is Required<br>';
        }

        if (!this.payload.supplyCategory) {
            response.status = false;
            response.message += 'Supply Category is Required <br>';
        }

        if (!this.payload.drugOrderItems.length) {
            response.status = false;
            response.message += 'Order Items is Required <br>';
        }

        if (!this.payload.user || !this.payload.user.id) {
            this.payload.user = this.commonService.getCurrentUser();
        }

        if (!this.payload.outlet || !this.payload.outlet.id) {
            this.payload.outlet = this.commonService.getCurrentLocation();
        }

        return response;
    }

    private setIsStore() {
        const lName = this.commonService.getCurrentLocation().name;
        this.payload.isStore = this.commonService.isPharmacyStore(lName);
    }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { VendorService } from '@app/shared/_services/others/vendor.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { ValidationMessage, VendorPayload, VendorViewType } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-vendor-management',
    templateUrl: './vendor-management.component.html',
    styleUrls: ['./vendor-management.component.css'],
})
export class VendorManagementComponent implements OnInit, OnDestroy {
    public registerNewVendor: VendorViewType = VendorViewType.NEW_REGISTRATION;
    public editVendor: VendorViewType = VendorViewType.EDIT_VENDOR;
    public payload: VendorPayload = new VendorPayload();

    private subscription: Subscription = new Subscription();

    constructor(
        private vendorService: VendorService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onRegisterVendor(): void {
        let message = this.validatePayload(this.payload);
        if (message.status == false) {
            this.toast.error(message.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.vendorService.onRegisterVendor(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.status == 200) {
                        this.toast.success(
                            HmisConstants.RECORD_ADDED,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.payload = new VendorPayload();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onViewTypeChange(type: VendorViewType): void {
        this.payload = new VendorPayload();
        if (type) {
            this.payload.viewType = type;
        }
    }

    public onEditVendor(): void {
        let message = this.validatePayload(this.payload);
        if (message.status == false) {
            this.toast.error(message.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.vendorService.onUpdateVendor(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.status == 200) {
                        this.toast.success(
                            HmisConstants.RECORD_ADDED,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.payload = new VendorPayload();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onSupplierChange(payload: VendorPayload): void {
        if (payload) {
            this.payload = payload;
        }
    }

    protected validatePayload(payload: VendorPayload): ValidationMessage {
        let response: ValidationMessage = { message: '', status: true };
        if (!payload.supplierName) {
            response.status = false;
            response.message += 'Vendor Name is Required <br>';
        }
        if (!payload.phoneNumber) {
            response.status = false;
            response.message += 'Vendor Phone Number is Required <br>';
        }
        if (!payload.officeAddress) {
            response.status = false;
            response.message += 'Vendor Office Address is Required <br>';
        }

        return response;
    }
}

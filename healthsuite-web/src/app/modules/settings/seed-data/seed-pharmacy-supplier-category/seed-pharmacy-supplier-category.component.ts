import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-pharmacy-supplier-category',
    templateUrl: './seed-pharmacy-supplier-category.component.html',
    styleUrls: ['./seed-pharmacy-supplier-category.component.css'],
})
export class SeedPharmacySupplierCategoryComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    supplierForm: FormGroup;
    pharmacySupplierCategory: SharedPayload;
    pharmacySupplierCategoryList: SharedPayload[];
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.supplierForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });

        this.subscription = this.service.onGetAllPharmacySupplierCategory().subscribe((result) => {
            this.pharmacySupplierCategoryList = result.data;
        });
        this.pharmacySupplierCategory = { id: null, name: '' };
    }

    get form() {
        return this.supplierForm.controls;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onCreatePharmacySupplierCategory() {
        this.isSubmitted = true;
        if (this.supplierForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');

        this.pharmacySupplierCategory = { id: null, name: '' };
        this.pharmacySupplierCategory.name = this.supplierForm.get('name').value;
        if (this.isExist(this.pharmacySupplierCategory)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({
                type: 'error',
                message: 'Name Already Exist.',
            });
            return;
        }
        this.service.onCreatePharmacySupplierCategory(this.pharmacySupplierCategory).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Supplier Category Successfully Created.',
                    });
                    this.onAddPharmacyCategorySupplier(this.pharmacySupplierCategory);
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: result.message,
                    });
                }
            },
            (error) => {
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something went wrong, Contact Support.',
                });
            }
        );
    }

    isExist(data: SharedPayload): boolean {
        let flag = false;
        this.pharmacySupplierCategoryList.forEach((entry) => {
            if (entry.name.toLowerCase() === data.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    onAddPharmacyCategorySupplier(payload: SharedPayload) {
        this.pharmacySupplierCategoryList.push(payload);
        this.supplierForm.reset();
        this.isSubmitted = false;
    }
}

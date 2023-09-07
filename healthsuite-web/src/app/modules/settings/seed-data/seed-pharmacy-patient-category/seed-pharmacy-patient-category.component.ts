import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { PharmacyPatientCategoryPayload } from '@app/modules/settings/_payload/pharmacyPatientCategoryPayload';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-seed-pharmacy-patient-category',
    templateUrl: './seed-pharmacy-patient-category.component.html',
    styleUrls: ['./seed-pharmacy-patient-category.component.css'],
})
export class SeedPharmacyPatientCategoryComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    pharmacyPatientCategory: PharmacyPatientCategoryPayload;
    pharmacyPatientCategoryList: PharmacyPatientCategoryPayload[];
    paymentMethodList: SharedPayload[];
    categoryTypesList: SharedPayload[];
    categoryForm: FormGroup;
    subscriptions = new Subscription();

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.categoryForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
            paymentMethod: new FormControl('', [Validators.required]),
            categoryType: new FormControl('', [Validators.required]),
        });

        this.subscriptions.add(
            this.service.onGetAllPaymentMethod().subscribe((result) => {
                if (result.httpStatusCode === 200) {
                    this.paymentMethodList = result.data;
                }
            })
        );

        this.subscriptions.add(
            this.service.onGetAllPharmacyPatientCategoryTypes().subscribe((result) => {
                if (result.httpStatusCode === 200) {
                    this.categoryTypesList = result.data;
                }
            })
        );

        this.subscriptions.add(
            this.service.onGetAllPharmacyPatientCategory().subscribe((result) => {
                this.pharmacyPatientCategoryList = result.data;
            })
        );
        this.pharmacyPatientCategory = new PharmacyPatientCategoryPayload();
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
    }

    get form() {
        return this.categoryForm.controls;
    }

    onCreatePharmacyPatientCategory() {
        this.isSubmitted = true;
        if (this.categoryForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');
        this.pharmacyPatientCategory.paymentMethod.id = this.categoryForm.get(
            'paymentMethod'
        ).value;
        this.pharmacyPatientCategory.pharmacyPatientCategoryType.id = this.categoryForm.get(
            'categoryType'
        ).value;
        this.pharmacyPatientCategory.name = this.categoryForm.get('name').value;

        if (this.isExist(this.pharmacyPatientCategory)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Name Already Exist' });
            return;
        }

        this.service.onCreatePharmacyPatientCategory(this.pharmacyPatientCategory).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Created Successfully.',
                    });
                    this.onAddNewRecord({
                        id: result.data.id,
                        name: result.data.name,
                        pharmacyPatientCategoryType: {
                            id: result.data.pharmacyPatientCategoryType.id,
                            name: result.data.pharmacyPatientCategoryType.name,
                        },
                        paymentMethod: {
                            id: result.data.paymentMethod.id,
                            name: result.data.paymentMethod.name,
                        },
                    });
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something went wrong, Contact Support',
                });
            }
        );
    }

    isExist(pharmacyPatientCategory: PharmacyPatientCategoryPayload): boolean {
        let flag = false;
        this.pharmacyPatientCategoryList.forEach((entry) => {
            if (entry.name.toLowerCase() === pharmacyPatientCategory.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    onAddNewRecord(pharmacyPatientCategory: PharmacyPatientCategoryPayload): void {
        this.pharmacyPatientCategoryList.push(pharmacyPatientCategory);
        this.categoryForm.reset();
        this.isSubmitted = false;
    }
}

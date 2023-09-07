import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-bill-waiver-category',
    templateUrl: './seed-bill-waiver-category.component.html',
    styleUrls: ['./seed-bill-waiver-category.component.css'],
})
export class SeedBillWaiverCategoryComponent implements OnInit, OnDestroy {
    billWaiverForm: FormGroup;
    isSubmitted = false;
    billWaiverList: SharedPayload[];
    billWaiver: SharedPayload;
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.billWaiverForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });
        this.subscription = this.service.onGetAllBillWaiver().subscribe((result) => {
            this.billWaiverList = result.data;
        });
        this.billWaiver = { id: null, name: '' };
    }

    get form() {
        return this.billWaiverForm.controls;
    }
    onCreateBillWaiver() {
        this.isSubmitted = true;
        if (this.billWaiverForm.invalid) {
            return;
        }

        this.notification.useSpinner('show');
        this.billWaiver.name = this.billWaiverForm.get('name').value;
        if (this.isBillWaiverExist(this.billWaiver)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Bill Waiver Already Exist.' });
            return;
        }

        this.subscription = this.service.onCreateBillWaiver(this.billWaiver).subscribe(
            (result) => {
                this.notification.useSpinner('hide');
                if (result.httpStatusCode === 200) {
                    this.notification.showToast({
                        type: 'success',
                        message: 'Bill Waiver Created Successfully.',
                    });
                    this.onAddBillWaiverToList({ id: result.data.id, name: result.data.name });
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something Went Wrong, Contact Support.',
                });
            }
        );
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    isBillWaiverExist(data: SharedPayload): boolean {
        let checker = false;
        this.billWaiverList.forEach((entry) => {
            if (entry.name.toLowerCase() === data.name.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    onAddBillWaiverToList(billWaiver: SharedPayload) {
        this.billWaiverList.push(billWaiver);
        this.billWaiverForm.reset();
        this.isSubmitted = false;
    }
}

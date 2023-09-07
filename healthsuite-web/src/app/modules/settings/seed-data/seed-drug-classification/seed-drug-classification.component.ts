import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-drug-classification',
    templateUrl: './seed-drug-classification.component.html',
    styleUrls: ['./seed-drug-classification.component.css'],
})
export class SeedDrugClassificationComponent implements OnInit, OnDestroy {
    drugClassificationForm: FormGroup;
    isSubmitted = false;
    drugClassification: SharedPayload;
    drugClassificationList: SharedPayload[];
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.drugClassificationForm = new FormGroup({
            name: new FormControl('', []),
        });
        this.drugClassification = { name: '', id: null };
        this.subscription = this.service.getAllDrugClassification().subscribe((result) => {
            this.drugClassificationList = result.data;
        });
    }

    get form() {
        return this.drugClassificationForm.controls;
    }

    createDrugClassification() {
        this.isSubmitted = true;
        if (this.drugClassificationForm.invalid) {
            return;
        }
        this.drugClassification.name = this.drugClassificationForm.get('name').value;
        if (this.isDrugClassificationExist(this.drugClassification)) {
            this.notification.showToast({ type: 'error', message: 'Name Already Exist' });
            return;
        }
        this.notification.useSpinner('show');
        this.subscription = this.service
            .createDrugClassification(this.drugClassification)
            .subscribe(
                (result) => {
                    this.notification.useSpinner('hide');
                    if (result.httpStatusCode === 200) {
                        this.notification.showToast({
                            type: 'success',
                            message: 'Created Successfully.',
                        });
                        this.addDrugClassification({ name: result.data.name, id: result.data.id });
                    } else {
                        this.notification.showToast({ type: 'error', message: result.message });
                    }
                },
                (error) => {
                    console.log(error);
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: 'Something is wrong, Kindly Contact Support',
                    });
                }
            );
    }

    isDrugClassificationExist(drugClassification: SharedPayload): boolean {
        let flag = false;
        this.drugClassificationList.forEach((drugClassificationInList) => {
            if (
                drugClassificationInList.name.toLowerCase() ===
                drugClassification.name.toLowerCase()
            ) {
                flag = true;
            }
        });
        return flag;
    }
    addDrugClassification(drugClassification: SharedPayload) {
        this.drugClassificationList.push(drugClassification);
        this.drugClassificationForm.reset();
        this.isSubmitted = false;
    }
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { GenderPayload, SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-means-of-identification',
    templateUrl: './seed-means-of-identification.component.html',
    styleUrls: ['./seed-means-of-identification.component.css'],
})
export class SeedMeansOfIdentificationComponent implements OnInit, OnDestroy {
    meansOfIdForm: FormGroup;
    isSubmitted: boolean = false;
    subscription: Subscription = new Subscription();

    payload: SharedPayload;
    meansOfIdentificationList: SharedPayload[];

    constructor(
        private notificationService: NotificationService,
        private seedDataService: SeedDataService
    ) {}

    get form() {
        return this.meansOfIdForm.controls;
    }

    ngOnInit(): void {
        this.payload = { id: null, name: '' };
        this.meansOfIdForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );
        this.subscription.add(
            this.seedDataService.onGetMeansOfIdentification().subscribe((data) => {
                this.meansOfIdentificationList = data.data;
            })
        );
    }

    onSave() {
        this.isSubmitted = true;
        if (this.meansOfIdForm.invalid) {
            this.notificationService.showToast({ type: 'error', message: 'Enter  title' });
            return;
        }
        this.payload.name = this.meansOfIdForm.get('name').value;
        if (this.isExist(this.payload.name)) {
            this.notificationService.showToast({
                message: 'Title Already Exist',
                type: 'error',
            });
            return;
        }
        this.notificationService.useSpinner('show');
        this.subscription = this.seedDataService
            .onCreateMeansOfIdentification(this.payload)
            .subscribe(
                (result) => {
                    if (result) {
                        this.addNewTitle(this.payload.name);
                        this.notificationService.useSpinner('hide');
                        this.notificationService.showToast({
                            message: 'Successful',
                            type: 'success',
                        });
                    }
                },
                (error) => {
                    this.notificationService.useSpinner('hide');
                    this.notificationService.showToast({
                        message: 'Error, Contact Support.',
                        type: 'error',
                    });
                }
            );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    isExist(newValue: string): boolean {
        let checker = false;
        this.meansOfIdentificationList.forEach((value) => {
            if (value.name.toLowerCase() === newValue.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    private addNewTitle(name: string) {
        const newEntry: GenderPayload = { name: name, id: null };
        this.meansOfIdentificationList.push(newEntry);
        this.isSubmitted = false;
        this.meansOfIdForm.reset();
    }
}

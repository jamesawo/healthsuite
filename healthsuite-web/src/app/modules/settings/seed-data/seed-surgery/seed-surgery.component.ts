import { Component, OnDestroy, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-surgery',
    templateUrl: './seed-surgery.component.html',
    styleUrls: ['./seed-surgery.component.css'],
})
export class SeedSurgeryComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    surgeryList: SharedPayload[];
    surgery: SharedPayload;
    subscription: Subscription;
    surgeryForm: FormGroup;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.surgeryForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
                code: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );
        this.surgery = { name: '', id: null, code: '' };
        this.subscription = this.service
            .getAllSurgery()
            .subscribe((result) => (this.surgeryList = result.data));
    }

    get form() {
        return this.surgeryForm.controls;
    }

    createSurgery() {
        this.isSubmitted = true;
        if (this.surgeryForm.invalid) {
            return;
        }
        this.surgery.name = this.surgeryForm.get('name').value;
        this.surgery.code = this.surgeryForm.get('code').value;

        if (this.isSurgeryExist(this.surgery)) {
            this.notification.showToast({
                type: 'error',
                message: 'Surgery With This Name Already Exist',
            });
            return;
        }
        this.notification.useSpinner('show');
        this.subscription = this.service.createSurgery(this.surgery).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.addSurgery({
                        id: result.data.id,
                        name: result.data.name,
                        code: result.data.code,
                    });
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Surgery Seeded Successfully.',
                    });
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something Went Wrong, Please Contact Support',
                });
            }
        );
    }

    isSurgeryExist(surgery: SharedPayload): boolean {
        let checker = false;
        this.surgeryList.forEach((surgeryInList) => {
            if (surgeryInList.name.toLowerCase() === surgery.name.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    addSurgery(surgery: SharedPayload): void {
        this.surgeryList.push(surgery);
        this.surgeryForm.reset();
        this.isSubmitted = false;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

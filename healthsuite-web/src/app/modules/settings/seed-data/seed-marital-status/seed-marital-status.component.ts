import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-marital-status',
    templateUrl: './seed-marital-status.component.html',
    styleUrls: ['./seed-marital-status.component.css'],
})
export class SeedMaritalStatusComponent implements OnInit {
    isSubmitted = false;
    maritalStatusForm: FormGroup;
    payload: SharedPayload;
    maritalStatusList: SharedPayload[];
    subscription: Subscription;

    constructor(
        private service: SeedDataService,
        private notificationService: NotificationService
    ) {}

    ngOnInit(): void {
        this.maritalStatusForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );

        this.payload = { id: null, name: '' };

        this.subscription = this.service
            .getAllMaritalStatus()
            .subscribe((result) => (this.maritalStatusList = result.data));
    }

    get form() {
        return this.maritalStatusForm.controls;
    }

    saveMaritalStatus() {
        this.isSubmitted = true;
        if (this.maritalStatusForm.invalid) {
            return;
        }
        this.payload.name = this.maritalStatusForm.get('name').value;
        if (this.isMaritalStatusExist(this.maritalStatusList, this.payload)) {
            this.notificationService.showToast({ type: 'error', message: 'Duplicate Entry' });
            return;
        }
        this.notificationService.useSpinner('show');
        this.subscription = this.service.createMaritalStatus(this.payload).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.addNewMaritalStatus(this.maritalStatusList, this.payload.name);
                    this.notificationService.useSpinner('hide');
                    this.notificationService.showToast({
                        type: 'success',
                        message: result.message,
                    });
                }
            },
            (error) => {
                console.log(error);
                this.notificationService.useSpinner('hide');
                this.notificationService.showToast({
                    message: 'Connection Problem, Contact Support.',
                    type: 'error',
                });
            }
        );
    }

    isMaritalStatusExist(
        maritalStatusList: SharedPayload[],
        maritalStatus: SharedPayload
    ): boolean {
        let checker = false;
        if (maritalStatusList.length) {
            maritalStatusList.forEach((single) => {
                if (single.name.toLowerCase() === maritalStatus.name.toLowerCase()) {
                    checker = true;
                }
            });
            return checker;
        }
    }

    addNewMaritalStatus(maritalStatusList: SharedPayload[], maritalStatusName: string) {
        const maritalStatus: SharedPayload = { name: maritalStatusName, id: null };
        maritalStatusList.push(maritalStatus);
        this.isSubmitted = false;
        this.maritalStatusForm.reset();
    }
}

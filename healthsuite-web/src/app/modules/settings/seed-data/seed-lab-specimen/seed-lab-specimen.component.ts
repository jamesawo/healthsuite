import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-lab-specimen',
    templateUrl: './seed-lab-specimen.component.html',
    styleUrls: ['./seed-lab-specimen.component.css'],
})
export class SeedLabSpecimenComponent implements OnInit, OnDestroy {
    labSpecimenForm: FormGroup;
    isSubmitted = false;
    labSpecimenList: SharedPayload[];
    labSpecimen: SharedPayload;
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.subscription = this.service.onGetAllLabSpecimen().subscribe((result) => {
            this.labSpecimenList = result.data;
        });
        this.labSpecimenForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });
        this.labSpecimen = { id: null, name: '' };
    }

    get form() {
        return this.labSpecimenForm.controls;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onCreateLabSpecimen() {
        this.isSubmitted = true;
        if (this.labSpecimenForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');
        this.labSpecimen.name = this.labSpecimenForm.get('name').value;
        if (this.isLabSpecimenExist(this.labSpecimen)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Name Already Exist.' });
            return;
        }
        this.subscription = this.service.onCreateLabSpecimen(this.labSpecimen).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Created Successfully.',
                    });
                    this.addLabSpecimen({ id: result.data.id, name: result.data.name });
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
                    message: 'Something Has Gone Wrong, Contact Support.',
                });
            }
        );
    }

    isLabSpecimenExist(data: SharedPayload): boolean {
        let flag = false;
        this.labSpecimenList.forEach((entry) => {
            if (entry.name.toLowerCase() === data.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    private addLabSpecimen(param: SharedPayload) {
        this.labSpecimenList.push(param);
        this.labSpecimenForm.reset();
        this.isSubmitted = false;
    }
}

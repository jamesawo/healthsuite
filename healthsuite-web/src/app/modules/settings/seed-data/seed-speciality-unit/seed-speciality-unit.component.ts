import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-speciality-unit',
    templateUrl: './seed-speciality-unit.component.html',
    styleUrls: ['./seed-speciality-unit.component.css'],
})
export class SeedSpecialityUnitComponent implements OnInit, OnDestroy {
    specialityUnitForm: FormGroup;
    specialityUnit: SharedPayload;
    specialityUnitList: SharedPayload[];
    isSubmitted = false;
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.specialityUnitForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });
        this.subscription = this.service.onGetAllSpecialityUnit().subscribe((result) => {
            if (result.httpStatusCode === 200) {
                this.specialityUnitList = result.data;
            }
        });
        this.specialityUnit = { name: '', id: null };
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    get form() {
        return this.specialityUnitForm.controls;
    }
    onCreateSpecialityUnit() {
        this.isSubmitted = true;
        if (this.specialityUnitForm.invalid) {
            return;
        }
        this.specialityUnit.name = this.specialityUnitForm.get('name').value;
        this.notification.useSpinner('show');

        if (this.onIsSpecialityUnitExist(this.specialityUnit.name)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Unit Name Already Exist' });
            return;
        }

        this.service.onCreateSpecialityUnit(this.specialityUnit).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Unit Created Successfully.',
                    });
                    this.onAddSpecialityUnit({
                        id: result.data.id,
                        name: result.data.name,
                        code: result.data.code,
                    });
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                    this.notification.useSpinner('hide');
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
    onAddSpecialityUnit(payload: SharedPayload) {
        this.specialityUnitList.push(payload);
        this.specialityUnitForm.reset();
        this.isSubmitted = false;
    }
    onIsSpecialityUnitExist(name: string) {
        let flag = false;
        this.specialityUnitList.forEach((unit) => {
            if (unit.name.toLowerCase() === name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }
}

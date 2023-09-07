import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { RevenueDepartmentPayload } from '@app/modules/settings';

@Component({
    selector: 'app-seed-revenue-department',
    templateUrl: './seed-revenue-department.component.html',
    styleUrls: ['./seed-revenue-department.component.css'],
})
export class SeedRevenueDepartmentComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    revenueDepartmentForm: FormGroup;
    revenueDepartmentList: RevenueDepartmentPayload[] = [];
    revenueDepartment: RevenueDepartmentPayload;
    revenueDepartmentTypes: SharedPayload[] = [];
    subscriptions = new Subscription();

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.revenueDepartmentForm = new FormGroup({
            revDepartmentType: new FormControl('', [Validators.required]),
            revDepartmentName: new FormControl('', [Validators.required]),
        });

        this.subscriptions.add(
            this.service.onGetAllRevenueDepartmentTypes().subscribe((result) => {
                if (result.data) {
                    this.revenueDepartmentTypes = result.data;
                }
            })
        );

        this.subscriptions.add(
            this.service.onGetAllRevenueDepartment().subscribe((result) => {
                if (result.data) {
                    this.revenueDepartmentList = result.data;
                }
            })
        );

        this.revenueDepartment = new RevenueDepartmentPayload();
    }

    get form() {
        return this.form.controls;
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
    }

    onCreateRevenueDepartment() {
        if (this.revenueDepartmentForm.invalid) {
            this.notification.showToast({ message: 'invalid form field', type: 'error' });
            return;
        }

        this.revenueDepartment.name = this.revenueDepartmentForm.get('revDepartmentName').value;
        this.revenueDepartment.revenueDepartmentTypeDto.id =
            this.revenueDepartmentForm.get('revDepartmentType').value;

        this.notification.useSpinner('show');
        this.subscriptions.add(
            this.service.onCreateRevenueDepartment(this.revenueDepartment).subscribe(
                (result) => {
                    if (result.httpStatusCode === 200) {
                        this.notification.useSpinner('hide');
                        this.notification.showToast({
                            type: 'success',
                            message: 'Revenue Department Created.',
                        });
                        this.onAddRevenueDepartment(result.data);
                    } else {
                        this.notification.useSpinner('hide');
                        this.notification.showToast({ type: 'error', message: result.message });
                    }
                },
                (error) => {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: `Something isn't right, Contact Support`,
                    });
                }
            )
        );
    }

    onAddRevenueDepartment(revenueDepartment: RevenueDepartmentPayload): void {
        this.revenueDepartmentForm.reset();
        this.isSubmitted = false;
        if (revenueDepartment) {
            this.revenueDepartmentList.push(revenueDepartment);
        }
    }
}

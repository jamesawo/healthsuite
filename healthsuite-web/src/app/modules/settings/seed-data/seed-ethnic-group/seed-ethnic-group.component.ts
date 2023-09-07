import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-ethnic-group',
    templateUrl: './seed-ethnic-group.component.html',
    styleUrls: ['./seed-ethnic-group.component.css'],
})
export class SeedEthnicGroupComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    ethnicGroupForm: FormGroup;
    payload: SharedPayload;
    ethnicGroupList: SharedPayload[];
    subscription: Subscription;

    constructor(
        private service: SeedDataService,
        private notificationService: NotificationService
    ) {}

    ngOnInit(): void {
        this.ethnicGroupForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );

        this.payload = { id: null, name: '' };
        this.subscription = this.service.onGetAllEthnicGroup().subscribe((result) => {
            return (this.ethnicGroupList = result.data);
        });
    }
    get form() {
        return this.ethnicGroupForm.controls;
    }

    onSaveEthnicGroup() {
        this.isSubmitted = true;
        if (this.ethnicGroupForm.invalid) {
            return;
        }
        this.payload.name = this.ethnicGroupForm.get('name').value;
        if (this.isEthnicGroupExist(this.ethnicGroupList, this.payload)) {
            this.notificationService.showToast({ type: 'error', message: 'Duplicate Entry' });
            return;
        }
        this.notificationService.useSpinner('show');
        this.subscription = this.service.onCreateEthnicGroup(this.payload).subscribe(
            (result) => {
                if (result) {
                    this.addNewEthnicGroup(this.ethnicGroupList, this.payload.name);
                    this.notificationService.useSpinner('hide');
                    this.notificationService.showToast({
                        type: 'success',
                        message: 'Created Successfully',
                    });
                }
            },
            (error) => {
                this.notificationService.useSpinner('hide');
                this.notificationService.showToast({
                    message: 'Connection Problem',
                    type: 'error',
                });
            }
        );
    }

    isEthnicGroupExist(ethnicGroupList: SharedPayload[], ethnicGroup: SharedPayload): boolean {
        let checker = false;
        if (ethnicGroupList.length) {
            ethnicGroupList.forEach((single) => {
                if (single.name.toLowerCase() === ethnicGroup.name.toLowerCase()) {
                    checker = true;
                }
            });
            return checker;
        }
    }

    addNewEthnicGroup(ethnicGroupList: SharedPayload[], ethnicGroupName: string) {
        const ethnicGroup: SharedPayload = { name: ethnicGroupName, id: null };
        ethnicGroupList.push(ethnicGroup);
        this.isSubmitted = false;
        this.ethnicGroupForm.reset();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

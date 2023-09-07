import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NationalityPayload } from '@app/modules/settings/_payload/nationality.payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-nationality',
    templateUrl: './seed-nationality.component.html',
    styleUrls: ['./seed-nationality.component.css'],
})
export class SeedNationalityComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    nationalityForm: FormGroup;
    nationalityList: NationalityPayload[];
    statesCount: NationalityPayload[] = [];
    payload: NationalityPayload;
    state: NationalityPayload = { name: '' };
    subscription: Subscription = new Subscription();

    constructor(
        private notificationService: NotificationService,
        private seedDataService: SeedDataService
    ) {}

    ngOnInit(): void {
        this.nationalityForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );

        this.subscription.add(
            this.seedDataService.onGetNationalityParentOnly().subscribe(
                (result) => {
                    if (result) {
                        this.nationalityList = result.data;
                    }
                },
                (error) => {}
            )
        );
    }

    get form() {
        return this.nationalityForm.controls;
    }

    onAfterSave() {
        if (!this.state.name) {
            return;
        }
        this.statesCount.push(this.state);
        this.state = {};
    }

    onSave() {
        if (this.nationalityForm.invalid) {
            this.notificationService.showToast({ type: 'error', message: 'Enter country title' });
            return;
        }
        this.payload = new NationalityPayload();
        this.notificationService.useSpinner('show');
        // this.payload.name = this.nationalityForm.get('name').value;
        const stringList: string[] = [];

        this.statesCount.forEach((state) => {
            stringList.push(state.name);
        });

        this.payload.childrenString = stringList;
        this.payload.parent = this.nationalityForm.get('name').value;
        this.payload.name = this.nationalityForm.get('name').value;

        this.subscription.add(
            this.seedDataService.onCreateNationality(this.payload).subscribe(
                (result) => {
                    if (result) {
                        this.notificationService.useSpinner('hide');
                        this.nationalityList.push(this.payload);
                        this.state = {};
                        this.nationalityForm.reset();
                    }
                },
                (error) => {
                    this.notificationService.useSpinner('hide');
                }
            )
        );
    }

    onRemoveState(i: number) {
        this.statesCount.splice(i, 1);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

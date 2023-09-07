import { Component, OnDestroy, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-religion',
    templateUrl: './seed-religion.component.html',
    styleUrls: ['./seed-religion.component.css'],
})
export class SeedReligionComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    religion: SharedPayload  = { name: '', id: null };
    religionList: SharedPayload[];
    religionForm: FormGroup;
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.onSetForm();
        this.subscription = this.service.getAllReligion().subscribe((result) => {
            this.religionList = result.data;
        });
    }

    get form() {
        return this.religionForm.controls;
    }

    onSetForm() {
        this.religionForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );
    }

    saveReligion() {
        this.isSubmitted = true;
        if (this.religionForm.invalid) {
            return;
        }

        this.religion.name = this.religionForm.get('name').value;
        if (this.isReligionExist(this.religion)) {
            this.notification.showToast({ type: 'error', message: 'Religion Already Exist' });
            return;
        }
        this.notification.useSpinner('show');
        this.subscription = this.service.createReligion(this.religion).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'success', message: result.message });
                    this.addReligion(this.religion);
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({ type: 'error', message: '' });
            }
        );
    }

    isReligionExist(religion: SharedPayload): boolean {
        let checker = false;
        this.religionList.forEach((religionInList) => {
            if (religionInList.name.toLowerCase() === religion.name.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    addReligion(religion: SharedPayload): void {
        this.religion = {name: '', id: null};
        this.religionList.push(religion);
        this.onSetForm();
        this.isSubmitted = false;

    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

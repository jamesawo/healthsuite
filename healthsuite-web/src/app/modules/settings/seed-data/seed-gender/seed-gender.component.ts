import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { GenderPayload, SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-gender',
    templateUrl: './seed-gender.component.html',
    styleUrls: ['./seed-gender.component.css'],
})
export class SeedGenderComponent implements OnInit, OnDestroy {
    genderForm: FormGroup;
    genderPayload: GenderPayload;
    subscription: Subscription;
    isSubmitted = false;
    genders: GenderPayload[];

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.genderForm = new FormGroup(
            {
                genderName: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );

        this.genderPayload = {
            id: null,
            name: '',
        };

        this.subscription = this.service.getAllGender().subscribe((result) => {
            this.genders = result.data;
        });
    }

    get form() {
        return this.genderForm.controls;
    }

    saveGender() {
        // set isSubmit to true so user can have a good experience knowing that request is processing
        this.isSubmitted = true;

        // Check if form was filled
        if (this.genderForm.invalid) {
            return;
        }

        this.genderPayload.name = this.genderForm.get('genderName').value;

        // Check if gender exit
        if (this.isGenderExist(this.genders, this.genderPayload.name)) {
            this.notification.showToast({
                message: 'Gender Already Exist',
                type: 'error',
            });
            return;
        }

        // Save new gender
        this.notification.useSpinner('show');
        this.subscription = this.service.createGender(this.genderPayload).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.addNewGender(this.genders, this.genderPayload.name);
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        message: result.message,
                        type: 'success',
                    });
                } else {
                    this.notification.showToast({
                        message: result.message,
                        type: 'error',
                    });
                    this.notification.useSpinner('hide');
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    message: 'Connection Problem, Contact Support.',
                    type: 'error',
                });
            }
        );
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    // Check if gender already exist
    isGenderExist(genders: GenderPayload[], newGender: string): boolean {
        let checker = false;
        genders.forEach((gender) => {
            if (gender.name.toLowerCase() === newGender.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    // Add new gender to genders array to update users view
    addNewGender(genders: GenderPayload[], gender: string): void {
        const newGender: GenderPayload = { name: gender, id: null };
        genders.push(newGender);
        this.isSubmitted = false;
        this.genderForm.reset();
    }

    public onGenderChange(event: any) {
        const id: number = event.target.dataset.id;
        const name: string = event.target.value;

        if (id && name) {
            if (!this.isGenderExist(this.genders, name)) {
                let payload: GenderPayload = { id, name };
                this.subscription.add(
                    this.service.onUpdateGender(payload).subscribe(
                        (result) => {},
                        (error) => {}
                    )
                );
            }
        }
    }

    logMessageId(el) {
        //el -> element from view using #elementRef on html element
        let messageId = el.getAttribute('data-message-id');
        //let messageId = el.dataset.messageId;
        console.log('Message Id: ', messageId);
    }
}

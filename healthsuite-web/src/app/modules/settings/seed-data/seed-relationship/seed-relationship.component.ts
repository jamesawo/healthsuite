import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-seed-relationship',
    templateUrl: './seed-relationship.component.html',
    styleUrls: ['./seed-relationship.component.css'],
})
export class SeedRelationshipComponent implements OnInit, OnDestroy {
    relationshipForm: FormGroup;
    isSubmitted = false;
    subscription: Subscription;
    relationships: SharedPayload[];
    relationship: SharedPayload;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.relationshipForm = new FormGroup(
            {
                name: new FormControl('', [Validators.required]),
            },
            { updateOn: 'blur' }
        );
        this.subscription = this.service
            .getAllRelationship()
            .subscribe((result) => (this.relationships = result.data));

        this.relationship = { id: null, name: '' };
    }

    get form() {
        return this.relationshipForm.controls;
    }

    // create new relationship
    saveRelationship() {
        // set submitted to true to let user know the create request is processing by showing a spinner.
        this.isSubmitted = true;
        // if relationship name field on the form is invalid display some error.
        if (this.relationshipForm.invalid) {
            return;
        }
        this.relationship.name = this.relationshipForm.get('name').value;
        // check if relationship exist before processing create request.
        if (this.isRelationshipExist({ name: this.relationship.name, id: null })) {
            this.notification.showToast({ type: 'error', message: 'Relationship Already Exist.' });
            return;
        }
        // show spinner to let user know request is processing, todo::remove page spinner, replace with button spinner.
        this.notification.useSpinner('show');
        this.subscription = this.service.createRelationship(this.relationship).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.addNewRelationship({ name: result.data.name, id: result.data.id });
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ message: result.message, type: 'success' });
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                // something bad like a server error occurred, todo::add proper logging to frontend.
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({ type: 'error', message: "Something isn't right" });
            }
        );
    }

    isRelationshipExist(relationship: SharedPayload): boolean {
        let checker = false;
        this.relationships.forEach((relationshipInList) => {
            if (relationshipInList.name.toLowerCase() === relationship.name.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    addNewRelationship(relationship: SharedPayload) {
        this.relationships.push(relationship);
        this.relationshipForm.reset();
        this.isSubmitted = false;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

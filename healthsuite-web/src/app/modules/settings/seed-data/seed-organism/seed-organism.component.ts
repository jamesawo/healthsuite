import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-organism',
    templateUrl: './seed-organism.component.html',
    styleUrls: ['./seed-organism.component.css'],
})
export class SeedOrganismComponent implements OnInit {
    organismForm: FormGroup;
    isSubmitted = false;
    organismList: SharedPayload[];
    organism: SharedPayload;
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.organismForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });
        this.subscription = this.service.onGetAllOrganism().subscribe((result) => {
            this.organismList = result.data;
        });
        this.organism = { name: '', id: null };
    }

    get form() {
        return this.organismForm.controls;
    }
    onCreateOrganism() {
        this.isSubmitted = true;
        if (this.organismForm.invalid) {
            return;
        }
        this.organism = { name: '', id: null };
        this.organism.name = this.organismForm.get('name').value;
        this.notification.useSpinner('show');
        if (this.isOrganismExist(this.organism)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Name Already Exist' });
            return;
        }
        this.subscription = this.service.onCreateOrganism(this.organism).subscribe(
            (result) => {
                this.notification.useSpinner('hide');
                if (result.httpStatusCode === 200) {
                    this.notification.showToast({
                        type: 'success',
                        message: 'Created Successfully.',
                    });
                    this.onAddOrganismToList(this.organism);
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                this.notification.showToast({
                    type: 'error',
                    message: 'Something has gone wrong, Contact Support',
                });
            }
        );
    }

    isOrganismExist(data: SharedPayload): boolean {
        let checker = false;
        this.organismList.forEach((entry) => {
            if (entry.name.toLowerCase() === data.name.toLowerCase()) {
                checker = true;
            }
        });
        return checker;
    }

    onAddOrganismToList(organism: SharedPayload) {
        this.organismList.push(organism);
        this.organismForm.reset();
        this.isSubmitted = false;
    }
}

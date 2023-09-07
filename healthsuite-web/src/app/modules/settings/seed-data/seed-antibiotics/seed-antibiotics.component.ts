import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AntibioticsPayload } from '@app/modules/settings/_payload/antibioticsPayload';
import { SharedPayload } from '@app/shared/_payload';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-seed-antibiotics',
    templateUrl: './seed-antibiotics.component.html',
    styleUrls: ['./seed-antibiotics.component.css'],
})
export class SeedAntibioticsComponent implements OnInit, OnDestroy {
    antibioticsForm: FormGroup;
    antibiotics: AntibioticsPayload[];
    antibiotic: AntibioticsPayload;
    organisms: SharedPayload[];
    isSubmitted = false;
    subscription = new Subscription();

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    get form() {
        return this.antibioticsForm.controls;
    }
    ngOnInit(): void {
        this.antibioticsForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
            organism: new FormControl('', [Validators.required]),
        });

        this.subscription.add(
            this.service.onGetAllAntibiotics().subscribe((result) => {
                this.antibiotics = result.data;
            })
        );

        this.subscription.add(
            this.service.onGetAllOrganism().subscribe((result) => {
                this.organisms = result.data;
            })
        );
        this.antibiotic = new AntibioticsPayload();
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onCreateAntibiotics() {
        this.isSubmitted = true;
        if (this.antibioticsForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');
        this.antibiotic.name = this.antibioticsForm.get('name').value;
        this.antibiotic.organism.id = this.antibioticsForm.get('organism').value;
        if (this.isAntibioticsExist(this.antibiotic)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({ type: 'error', message: 'Antibiotic Already Exist.' });
            return;
        }

        this.subscription.add(
            this.service.onCreateAntibiotics(this.antibiotic).subscribe(
                (result) => {
                    this.notification.useSpinner('hide');
                    if (result.httpStatusCode === 200) {
                        this.notification.showToast({
                            type: 'success',
                            message: 'Antibiotics Created Successfully.',
                        });
                        this.onAddAntibiotic({
                            id: result.data.id,
                            name: result.data.name,
                            organism: {
                                id: result.data.organism.id,
                                name: result.data.organism.name,
                            },
                        });
                    } else {
                        this.notification.showToast({ type: 'error', message: result.message });
                    }
                },
                (error) => {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: 'Something has gone wrong, Contact Support',
                    });
                }
            )
        );
    }

    isAntibioticsExist(data: AntibioticsPayload): boolean {
        // Todo:: checker returns undefined: WIP
        let checker = false;
        this.antibiotics.forEach((entry) => {
            if (
                entry.organism.id === data.organism.id &&
                entry.name.toLowerCase() === data.name.toLowerCase()
            ) {
                checker = true;
            }
        });
        return checker;
    }
    onAddAntibiotic(antibiotic: AntibioticsPayload) {
        this.antibiotics.push(antibiotic);
        this.antibioticsForm.reset();
        this.isSubmitted = false;
    }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-drug-formulation',
    templateUrl: './seed-drug-formulation.component.html',
    styleUrls: ['./seed-drug-formulation.component.css'],
})
export class SeedDrugFormulationComponent implements OnInit, OnDestroy {
    drugFormulationForm: FormGroup;
    isSubmitted = false;
    drugFormulation: SharedPayload;
    drugFormulationList: SharedPayload[];
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.drugFormulationForm = new FormGroup({
            name: new FormControl('', []),
        });
        this.drugFormulation = { name: '', id: null };
        this.subscription = this.service.getAllDrugFormulation().subscribe((result) => {
            this.drugFormulationList = result.data;
        });
    }

    get form() {
        return this.drugFormulationForm.controls;
    }

    createDrugFormulation() {
        this.isSubmitted = true;
        if (this.drugFormulationForm.invalid) {
            return;
        }
        this.drugFormulation.name = this.drugFormulationForm.get('name').value;
        if (this.isDrugFormulationExist(this.drugFormulation)) {
            this.notification.showToast({ type: 'error', message: 'Name Already Exist' });
            return;
        }
        this.notification.useSpinner('show');
        this.subscription = this.service.createDrugFormulation(this.drugFormulation).subscribe(
            (result) => {
                this.notification.useSpinner('hide');
                if (result.httpStatusCode === 200) {
                    this.notification.showToast({
                        type: 'success',
                        message: 'Created Successfully.',
                    });
                    this.addDrugFormulation({ name: result.data.name, id: result.data.id });
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something is wrong, Kindly Contact Support',
                });
            }
        );
    }

    isDrugFormulationExist(drugFormulation: SharedPayload): boolean {
        let flag = false;
        this.drugFormulationList.forEach((drugFormulationInList) => {
            if (drugFormulationInList.name.toLowerCase() === drugFormulation.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }
    addDrugFormulation(drugFormulation: SharedPayload) {
        this.drugFormulationList.push(drugFormulation);
        this.drugFormulationForm.reset();
        this.isSubmitted = false;
    }
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

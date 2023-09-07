import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {
    CardHolderType,
    PatientCardHolder,
    PatientPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { EmrService } from '@app/shared/_services';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-patient-card-holder-details',
    templateUrl: './patient-card-holder-details.component.html',
    styleUrls: ['./patient-card-holder-details.component.css'],
})
export class PatientCardHolderDetailsComponent implements OnInit, OnDestroy {
    private subscription: Subscription = new Subscription();
    @Input('patient')
    public patientPayload: PatientPayload;
    public holder: CardHolderType = CardHolderType.HOLDER;
    public dependant: CardHolderType = CardHolderType.DEPENDANT;

    constructor(private emrService: EmrService) {}

    ngOnInit(): void {

        if (this.patientPayload?.patientId) {
        } else {
            this.subscription.add(
                this.emrService.patientSubject$.subscribe((payload) => {
                    this.patientPayload = payload;
                    this.patientPayload.patientCardHolder.cardHolderType = this.holder;
                })
            );
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onRelationshipSelected(value: SharedPayload) {
        if (value && value.id) {
            this.patientPayload.patientCardHolder.relationShipWithCardHolder = value;
        }
    }
}

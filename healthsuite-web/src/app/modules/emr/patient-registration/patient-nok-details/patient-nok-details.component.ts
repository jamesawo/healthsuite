import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { EmrService, SeedDataService } from '@app/shared/_services';
import { SharedPayload } from '@app/shared/_payload';
import { PatientNokDetails, PatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-patient-nok-details',
    templateUrl: './patient-nok-details.component.html',
    styleUrls: ['./patient-nok-details.component.css'],
})
export class PatientNokDetailsComponent implements OnInit, OnDestroy {
    private subscription: Subscription = new Subscription();
    public relationships: SharedPayload[] = [];
    public nokDetails: PatientNokDetails = {};
    public patientPayload: PatientPayload;

    constructor(private seedDataService: SeedDataService, private emrService: EmrService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.seedSubject$.subscribe((data) => {
                this.relationships = data.nokRelationship;
            })
        );
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((value) => {
                this.patientPayload = value;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

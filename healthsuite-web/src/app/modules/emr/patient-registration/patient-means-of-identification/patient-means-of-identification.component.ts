import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { EmrService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { DatePayload, SharedPayload } from '@app/shared/_payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';

@Component({
    selector: 'app-patient-means-of-identification',
    templateUrl: './patient-means-of-identification.component.html',
    styleUrls: ['./patient-means-of-identification.component.css'],
})
export class PatientMeansOfIdentificationComponent implements OnInit, OnDestroy {
    @ViewChild('dateComponent') public dateComponent: SharedDateComponent;
    private subscription: Subscription = new Subscription();
    public idTypes: SharedPayload[] = [];
    public patientPayload: PatientPayload;
    public minDate: Date = new Date(new Date().getFullYear(), 0, 1);

    constructor(private seedDataService: SeedDataService, private emrService: EmrService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.seedSubject$.subscribe((data) => {
                this.idTypes = data.meansOfIdentification;
            })
        );

        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patientPayload = payload;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onDateSelected(datePayload: DatePayload) {
        if (datePayload) {
            this.patientPayload.patientMeansOfIdentification.expiryDate = datePayload;
        }
    }

    onResetForm() {
        this.dateComponent.onClearDateField();
    }
}

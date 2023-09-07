import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { DateType, PatientCardNotePayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { ClerkDrugRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';

@Component({
    selector: 'app-drug-administration',
    templateUrl: './drug-administration.component.html',
    styleUrls: ['./drug-administration.component.css'],
})
export class DrugAdministrationComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    public payload: PatientCardNotePayload = new PatientCardNotePayload();
    public drugRequest: ClerkDrugRequestPayload[] = [];

    private subscription: Subscription = new Subscription();

    constructor() {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
            this.onPrepDrugRequest(patient.drugRequestList);
        }
    }

    public onPrepDrugRequest(drugRequestList: ClerkDrugRequestPayload[]) {
        if (drugRequestList && drugRequestList.length) {
            this.drugRequest = drugRequestList;
        }
    }
}

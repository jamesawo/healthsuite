import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { NurseNotePayload } from '@app/shared/_payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';

@Component({
    selector: 'app-nurse-note',
    templateUrl: './nurse-note.component.html',
    styleUrls: ['./nurse-note.component.css'],
})
export class NurseNoteComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') public patientSearchComponent: PatientSearchComponent;
    public payload: NurseNotePayload = new NurseNotePayload();
    public isSubmitting = false;

    private subscription: Subscription = new Subscription();

    constructor(private commonService: CommonService) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.CLINIC_LOCATION);
        if (isMatch) {
            this.onInitializeComponent();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(payload: PatientPayload) {
        if (payload) {
            this.payload.patient = payload;
        }
    }

    public onResetPayload() {
        this.payload = new NurseNotePayload();
        this.patientSearchComponent.clearSearchField();
    }

    private onInitializeComponent() {
        this.payload.nurse = this.commonService.getCurrentUser();
        this.payload.clinic = this.commonService.getCurrentLocation();
    }
}

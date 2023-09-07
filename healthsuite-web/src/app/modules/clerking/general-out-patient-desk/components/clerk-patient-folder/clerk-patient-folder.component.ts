import { Component, OnInit } from '@angular/core';
import { fadeInOnEnterAnimation, fadeOutOnLeaveAnimation } from 'angular-animations';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-clerk-patient-folder',
    templateUrl: './clerk-patient-folder.component.html',
    styleUrls: ['./clerk-patient-folder.component.css'],
    animations: [fadeInOnEnterAnimation(), fadeOutOnLeaveAnimation()],
})
export class ClerkPatientFolderComponent implements OnInit {
    public data: { patientPayload: PatientPayload };

    constructor() {}

    ngOnInit(): void {
    }

}

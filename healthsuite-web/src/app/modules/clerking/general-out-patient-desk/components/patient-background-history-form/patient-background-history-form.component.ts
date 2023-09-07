import { Component, Input, OnInit } from '@angular/core';
import { BackgroundHistoryFormPayload } from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-patient-background-history-form',
    templateUrl: './patient-background-history-form.component.html',
    styleUrls: ['./patient-background-history-form.component.css'],
})
export class PatientBackgroundHistoryFormComponent implements OnInit {
    @Input('props') public props: { data: BackgroundHistoryFormPayload };

    constructor() {}

    ngOnInit(): void {}
}

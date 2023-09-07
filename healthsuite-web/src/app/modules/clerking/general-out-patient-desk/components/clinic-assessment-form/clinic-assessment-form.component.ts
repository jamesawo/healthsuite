import { Component, Input, OnInit } from '@angular/core';
import { ClinicalAssessmentFormPayload } from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-clinic-assessment-form',
    templateUrl: './clinic-assessment-form.component.html',
    styleUrls: ['./clinic-assessment-form.component.css'],
})
export class ClinicAssessmentFormComponent implements OnInit {
    @Input('props')
    public props: { data: ClinicalAssessmentFormPayload };

    constructor() {}

    ngOnInit(): void {}

    public onToggleFollowUpNote(event: any) {
        this.props.data.hasFollowUpNote = event.target.checked;
    }
}

import {Component, Input, OnInit} from '@angular/core';
import {ObPreviousMedHisPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
    selector: 'app-ob-pre-medical-history',
    templateUrl: './ob-pre-medical-history.component.html',
    styleUrls: ['./ob-pre-medical-history.component.css'],
})
export class ObPreMedicalHistoryComponent implements OnInit {
    @Input('props') props: {payload: ObPreviousMedHisPayload};
    constructor() {}

    ngOnInit(): void {
        if (!this.props.payload) {
            this.props.payload = new ObPreviousMedHisPayload();
        }
    }
}

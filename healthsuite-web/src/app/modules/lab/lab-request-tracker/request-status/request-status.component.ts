import { Component, OnInit } from '@angular/core';
import { LabTrackerPayload } from '@app/shared/_payload/lab/lab.payload';

@Component({
    selector: 'app-request-status',
    templateUrl: './request-status.component.html',
    styleUrls: ['./request-status.component.css'],
})
export class RequestStatusComponent implements OnInit {
    public data: { labTestTracker: LabTrackerPayload };
    public payload: LabTrackerPayload;

    constructor() {}

    ngOnInit(): void {
        if (this.data?.labTestTracker) {
            this.payload = this.data.labTestTracker;
        } else {
            this.payload = new LabTrackerPayload();
        }
    }
}

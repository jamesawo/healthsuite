import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { EmrService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { DatePayload } from '@app/shared/_payload';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';


@Component({
    selector: 'app-patient-transfer-details',
    templateUrl: './patient-transfer-details.component.html',
    styleUrls: ['./patient-transfer-details.component.css'],
})
export class PatientTransferDetailsComponent implements OnInit, OnDestroy {
    @ViewChild('dateOfTransferComponent') dateOfTransferComponent: SharedDateComponent;

    private subscription: Subscription = new Subscription();
    public patientPayload: PatientPayload;

    constructor(private emrService: EmrService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patientPayload = payload;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onDateSelected(value: DatePayload) {
        this.patientPayload.patientTransferDetails.dateOfTransfer = value;
    }

    onResetForm() {
        this.dateOfTransferComponent.onClearDateField();
        // this.patientPayload.patientTransferDetails.dateOfTransfer = undefined;
    }
}

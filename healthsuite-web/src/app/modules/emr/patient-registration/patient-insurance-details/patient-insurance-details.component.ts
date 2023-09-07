import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { EmrService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import {PatientPayload, SchemeData, SchemePlan, TreatmentTypeEnum} from '@app/shared/_payload/erm/patient.payload';
import { DatePayload } from '@app/shared/_payload';

@Component({
    selector: 'app-patient-insurance-details',
    templateUrl: './patient-insurance-details.component.html',
    styleUrls: ['./patient-insurance-details.component.css'],
})
export class PatientInsuranceDetailsComponent implements OnInit, OnDestroy {
    @Input('patient')
    public patientPayload: PatientPayload;
    public primary = TreatmentTypeEnum.PRIMARY;
    public secondary = TreatmentTypeEnum.SECONDARY;

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService, private emrService: EmrService) {}

    ngOnInit(): void {
        if (this.patientPayload?.patientId) {
            const schemePlans = this.patientPayload.patientInsurance.scheme.schemePlans;
            const planId = this.patientPayload.patientInsurance.schemePlanId;
            if (schemePlans.length && planId) {
                this.patientPayload.schemePlan = schemePlans.find((value) => value.id === planId);
            }
        } else {
            this.patientPayload = new PatientPayload();
            this.patientPayload.patientInsurance = {};
            this.subscription.add(
                this.emrService.patientSubject$.subscribe((payload) => {
                    this.patientPayload = payload;
                })
            );
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSchemeSearchSelected(payload: SchemeData) {
        if (payload) {
            this.patientPayload.patientInsurance.scheme = payload;
        }
    }

    onSchemePlanSelected(schemePlan: SchemePlan) {
        if (schemePlan) {
            this.patientPayload.schemePlan =  schemePlan;
            this.patientPayload.patientInsurance.schemePlanId = schemePlan.id;
        }
    }

    onDateSelected(value: DatePayload, type: string) {
        if (value && type === 'startDate') {
            this.patientPayload.patientInsurance.approvalStartDate = value;
        } else if (value && type === 'endDate') {
            this.patientPayload.patientInsurance.approvalEndDate = value;
        }
    }
}

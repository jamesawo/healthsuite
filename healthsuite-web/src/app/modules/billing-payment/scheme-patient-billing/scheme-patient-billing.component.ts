import { Component, OnInit } from '@angular/core';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';
import { PaymentTypeForEnum } from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
    selector: 'app-scheme-patient-billing',
    templateUrl: './scheme-patient-billing.component.html',
    styleUrls: ['./scheme-patient-billing.component.css'],
})
export class SchemePatientBillingComponent implements OnInit {
    public viewType: PatientCategoryEnum = PatientCategoryEnum.SCHEME;
    public serviceType: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;

    constructor() {}

    ngOnInit(): void {}
}

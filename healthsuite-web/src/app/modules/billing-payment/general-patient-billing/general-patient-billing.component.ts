import { Component, OnInit } from '@angular/core';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';
import {BillViewTypeEnum, PaymentTypeForEnum} from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
    selector: 'app-general-patient-billing',
    templateUrl: './general-patient-billing.component.html',
    styleUrls: ['./general-patient-billing.component.css'],
})
export class GeneralPatientBillingComponent implements OnInit {
    public viewType: PatientCategoryEnum = PatientCategoryEnum.GENERAL;
    public serviceType: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;
    // public labBillView = BillViewTypeEnum.LAB_BILL;


    constructor() {}

    ngOnInit(): void {}
}

import { Component, OnInit } from '@angular/core';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';
import {
    BillViewTypeEnum,
    PaymentTypeForEnum,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { DepartmentPayload } from '@app/modules/settings';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-drug-billing',
    templateUrl: './drug-billing.component.html',
    styleUrls: ['./drug-billing.component.css'],
})
export class DrugBillingComponent implements OnInit {
    public patientCategoryEnum: PatientCategoryEnum = PatientCategoryEnum.GENERAL;
    public paymentTypeForEnum: PaymentTypeForEnum = PaymentTypeForEnum.DRUG;
    public drugViewType: BillViewTypeEnum = BillViewTypeEnum.DRUG_BILL;

    constructor(private commonService: CommonService) {}

    ngOnInit(): void {}
}

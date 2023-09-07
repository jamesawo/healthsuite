import { Component, OnInit } from '@angular/core';
import {PatientCategoryEnum} from '@app/shared/_payload/erm/patient.payload';
import {BillViewTypeEnum, PaymentTypeForEnum} from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
  selector: 'app-lab-billing',
  templateUrl: './lab-billing.component.html',
  styleUrls: ['./lab-billing.component.css']
})
export class LabBillingComponent implements OnInit {

  public generalPatientCategory: PatientCategoryEnum = PatientCategoryEnum.GENERAL;
  public servicePaymentType: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;
  public labViewType: BillViewTypeEnum = BillViewTypeEnum.LAB_BILL;

  constructor() { }

  ngOnInit(): void {
  }

}

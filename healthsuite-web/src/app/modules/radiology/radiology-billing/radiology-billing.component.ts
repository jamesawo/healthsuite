import { Component, OnInit } from '@angular/core';
import {PatientCategoryEnum} from '@app/shared/_payload/erm/patient.payload';
import {BillViewTypeEnum, PaymentTypeForEnum} from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
  selector: 'app-radiology-billing',
  templateUrl: './radiology-billing.component.html',
  styleUrls: ['./radiology-billing.component.css']
})
export class RadiologyBillingComponent implements OnInit {

  public generalPatientCategory: PatientCategoryEnum = PatientCategoryEnum.GENERAL;
  public servicePaymentType: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;
  public radiologyBillView: BillViewTypeEnum = BillViewTypeEnum.RADIOLOGY_BILL;


  constructor() { }

  ngOnInit(): void {
  }

}

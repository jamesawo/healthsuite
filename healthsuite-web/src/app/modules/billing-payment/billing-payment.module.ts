import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientBillingComponent } from './patient-billing/patient-billing.component';
import { SchemePatientBillingComponent } from './scheme-patient-billing/scheme-patient-billing.component';
import { BillingPaymentRoutingModule } from '@app/modules/billing-payment/billing-payment-routing.module';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { PaymentManagerComponent } from './payment-manager/payment-manager.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { GeneralPatientBillingComponent } from './general-patient-billing/general-patient-billing.component';
import { DepositPaymentComponent } from './deposit-payment/deposit-payment.component';
import { BillAdjustmentComponent } from './bill-adjustment/bill-adjustment.component';
import { CancelReceiptComponent } from './cancel-receipt/cancel-receipt.component';

@NgModule({
    declarations: [
        PatientBillingComponent,
        SchemePatientBillingComponent,
        PaymentManagerComponent,
        GeneralPatientBillingComponent,
        DepositPaymentComponent,
        BillAdjustmentComponent,
        CancelReceiptComponent,
    ],
    imports: [
        CommonModule,
        BillingPaymentRoutingModule,
        HmisCommonModule,
        NgSelectModule,
        FormsModule,
    ],
    exports: [PatientBillingComponent],
})
export class BillingPaymentModule {}

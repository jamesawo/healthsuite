import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RadiologyRoutingModule } from './radiology-routing.module';
import { RadiologyBillingComponent } from './radiology-billing/radiology-billing.component';
import {BillingPaymentModule} from '@app/modules/billing-payment/billing-payment.module';


@NgModule({
    declarations: [RadiologyBillingComponent],
    imports: [CommonModule, RadiologyRoutingModule, BillingPaymentModule],
})
export class RadiologyModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CanActivateGuard } from '@app/shared/_helpers';
import { SchemePatientBillingComponent } from '@app/modules/billing-payment/scheme-patient-billing/scheme-patient-billing.component';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import { PaymentManagerComponent } from '@app/modules/billing-payment/payment-manager/payment-manager.component';
import { GeneralPatientBillingComponent } from '@app/modules/billing-payment/general-patient-billing/general-patient-billing.component';
import { DepositPaymentComponent } from '@app/modules/billing-payment/deposit-payment/deposit-payment.component';
import {BillAdjustmentComponent} from '@app/modules/billing-payment/bill-adjustment/bill-adjustment.component';
import {CancelReceiptComponent} from '@app/modules/billing-payment/cancel-receipt/cancel-receipt.component';

const routes: Routes = [
    {
        path: 'bill-general-patient',
        component: GeneralPatientBillingComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'GENERAL PATIENT BILLING' },

    },
    {
        path: 'bill-scheme-patient',
        component: SchemePatientBillingComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'SCHEME PATIENT BILLING' },

    },
    {
        path: 'adjust-bill',
        component: BillAdjustmentComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'ADJUST PATIENT BILL' },

    },
    {
        path: 'make-payment',
        component: PaymentManagerComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'PAYMENT MANAGER' },

    },
    {
        path: 'pay-deposit',
        component: DepositPaymentComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'PAY DEPOSIT' },

    },
    {
        path: 'waive-bill',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'BILL WAIVER' },

    },
    {
        path: 'cancel-receipt',
        component: CancelReceiptComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'CANCEL RECEIPT' },

    },
    {
        path: 'hmo-billing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'HMO BILLING' },

    },
    {
        path: 'organization-billing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'ORGANIZATION BILLING' },

    },
    {
        path: 'adjust-scheme-bill',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'SCHEME BILL ADJUSTMENT' },

    },
    {
        path: 'payment-refunds',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'PAYMENT REFUNDS' },

    },
    {
        path: 'transfer-deposit',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
                data: { title: 'TRANSFER DEPOSIT' },

    },
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class BillingPaymentRoutingModule {}

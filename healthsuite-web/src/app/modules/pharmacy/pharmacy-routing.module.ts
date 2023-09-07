import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { DrugRegisterComponent } from '@app/modules/pharmacy/drug-register/drug-register.component';
import { DrugBillingComponent } from '@app/modules/pharmacy/drug-billing/drug-billing.component';
import { DrugOrderComponent } from '@app/modules/pharmacy/drug-order/drug-order.component';
import { OutletReconciliationComponent } from '@app/modules/pharmacy/outlet-reconciliation/outlet-reconciliation.component';
import { ReceiveSuppliedGoodsComponent } from '@app/modules/pharmacy/receive-supplied-goods/receive-supplied-goods.component';
import { StoreReconciliationComponent } from '@app/modules/pharmacy/store-reconciliation/store-reconciliation.component';
import { StoreRequisitionComponent } from '@app/modules/pharmacy/store-requisition/store-requisition.component';
import { OutletRequisitionComponent } from '@app/modules/pharmacy/outlet-requisition/outlet-requisition.component';
import { DrugDispensingComponent } from '@app/modules/pharmacy/drug-dispensing/drug-dispensing.component';
import { StoreIssuranceComponent } from '@app/modules/pharmacy/store-issurance/store-issurance.component';
import { OutletIssuanceComponent } from '@app/modules/pharmacy/outlet-issuance/outlet-issuance.component';

const routes: Routes = [
    {
        path: 'drug-register',
        component: DrugRegisterComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG REGISTER' },
    },
    {
        path: 'drug-billing',
        component: DrugBillingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG BILLING' },
    },
    {
        path: 'drug-order',
        component: DrugOrderComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG ORDER' },
    },
    {
        path: 'outlet-reconciliation',
        component: OutletReconciliationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'OUTLET RECONCILIATION' },
    },
    {
        path: 'receive-goods-from-suppliers',
        component: ReceiveSuppliedGoodsComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RECEIVE GOODS FROM SUPPLIERS' },
    },
    {
        path: 'store-reconciliation',
        component: StoreReconciliationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'STORE RECONCILIATION' },
    },
    {
        path: 'to-store-requisition',
        component: StoreRequisitionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'TO STORE REQUISITION' },
    },
    {
        path: 'to-outlet-requisition',
        component: OutletRequisitionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'TO OUTLET REQUISITION' },
    },
    {
        path: 'store-issuance',
        component: StoreIssuranceComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'STORE ISSUANCE' },
    },
    {
        path: 'outlet-issuance',
        component: OutletIssuanceComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'OUTLET ISSUANCE' },
    },
    {
        path: 'drug-dispensing',
        component: DrugDispensingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG DISPENSING' },
    },
    {
        path: 'batch-drug-dispensing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'BATCH DRUG DISPENSING' },
    },
    {
        path: 'drug-withdrawals',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG WITHDRAWALS' },
    },
    {
        path: 'patient-drug-return',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATIENT DRUG RETURN' },
    },
    {
        path: 'drug-return-note',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DRUG RETURN NOTE' },
    },
    {
        path: 'private-wing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PRIVATE WING' },
    },
    {
        path: 'executive-wing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'EXECUTIVE WING' },
    },
    {
        path: 'store-outlet-notifications',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'NOTIFICATION' },
    },
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class PharmacyRoutingModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PharmacyRoutingModule } from '@app/modules/pharmacy/pharmacy-routing.module';
import { DrugRegisterComponent } from './drug-register/drug-register.component';
import { DrugBillingComponent } from './drug-billing/drug-billing.component';
import { DrugOrderComponent } from './drug-order/drug-order.component';
import { ReceiveSuppliedGoodsComponent } from './receive-supplied-goods/receive-supplied-goods.component';
import { OutletReconciliationComponent } from './outlet-reconciliation/outlet-reconciliation.component';
import { StoreReconciliationComponent } from './store-reconciliation/store-reconciliation.component';
import { DrugReconciliationComponent } from './drug-reconciliation/drug-reconciliation.component';
import { StoreRequisitionComponent } from './store-requisition/store-requisition.component';
import { DrugRequisitionComponent } from './drug-requisition/drug-requisition.component';
import { OutletRequisitionComponent } from './outlet-requisition/outlet-requisition.component';
import { DrugDispensingComponent } from './drug-dispensing/drug-dispensing.component';
import { OutletIssuanceComponent } from './outlet-issuance/outlet-issuance.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { BillingPaymentModule } from '@app/modules/billing-payment/billing-payment.module';
import { OutletStoreReconciliationComponent } from './outlet-store-reconciliation/outlet-store-reconciliation.component';
import { OutletStoreRequisitionComponent } from './outlet-store-requisition/outlet-store-requisition.component';
import { StoreIssuranceComponent } from './store-issurance/store-issurance.component';
import { OutletStoreIssuanceComponent } from '@app/modules/pharmacy/outlet-store-insuance/outlet-store-issuance.component';
import { MomentModule } from 'ngx-moment';
import { DrugRequisitionViewComponent } from './drug-requisition-view/drug-requisition-view.component';

@NgModule({
    declarations: [
        DrugRegisterComponent,
        DrugBillingComponent,
        DrugOrderComponent,
        ReceiveSuppliedGoodsComponent,
        OutletReconciliationComponent,
        StoreReconciliationComponent,
        DrugReconciliationComponent,
        StoreRequisitionComponent,
        DrugRequisitionComponent,
        OutletRequisitionComponent,
        DrugDispensingComponent,
        OutletIssuanceComponent,
        OutletStoreReconciliationComponent,
        OutletStoreRequisitionComponent,
        StoreIssuranceComponent,
        OutletStoreIssuanceComponent,
        DrugRequisitionViewComponent,
    ],
    imports: [
        CommonModule,
        PharmacyRoutingModule,
        NgSelectModule,
        FormsModule,
        HmisCommonModule,
        MomentModule,
        NgxPaginationModule,
        BillingPaymentModule,
    ],
})
export class PharmacyModule {}

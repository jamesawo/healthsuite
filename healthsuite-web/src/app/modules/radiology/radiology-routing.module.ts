import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CanActivateGuard } from '@app/shared/_helpers';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import {RadiologyBillingComponent} from '@app/modules/radiology/radiology-billing/radiology-billing.component';

const routes: Routes = [
    {
        path: 'radiology-billing',
        component: RadiologyBillingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY BILLING' },
    },
    {
        path: 'result-preparation',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY RESULT PREPARATION' },
    },
    {
        path: 'result-verification',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY RESULT VERIFICATION' },
    },
    {
        path: 'radiology-result-view',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY RESULT VIEW' },
    },
    {
        path: 'provisional-result-view',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY PROVISIONAL RESULT VIEW' },
    },
    {
        path: 'radiology-request-tracker',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY REQUEST TRACKER' },
    },
    {
        path: 'radiology-draft',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RADIOLOGY DRAFT' },
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class RadiologyRoutingModule {}

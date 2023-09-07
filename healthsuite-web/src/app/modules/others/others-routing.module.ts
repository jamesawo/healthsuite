import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ServiceRegisterComponent } from '@app/modules/others/service-register/service-register.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { VendorManagementComponent } from '@app/modules/others/vendor-management/vendor-management.component';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import {DashboardComponent} from '@app/modules/others/dashboard/dashboard.component';
import {SchemeServicePriceComponent} from '@app/modules/others/scheme-service-price/scheme-service-price.component';

const routes: Routes = [
    {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [CanActivateGuard]
    },
    {
        path: 'request-tracker',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'REQUEST TRACKER' },

    },
    {
        path: 'service-register',
        component: ServiceRegisterComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SERVICE REGISTRATION' },
    },
    {
        path: 'vendor-management',
        component: VendorManagementComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'MANAGE VENDOR' },

    },
    {
        path: 'scheme-service-price',
        component: SchemeServicePriceComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SCHEME SERVICE PRICE' },
    },
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class OthersRoutingModule {}

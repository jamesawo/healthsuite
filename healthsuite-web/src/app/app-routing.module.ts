import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutsComponent } from '@app/layouts/layouts.component';
import { LoginComponent } from '@app/modules/auth/login/login.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { WelcomeComponent } from '@app/modules/emr/welcome/welcome.component';

const routes: Routes = [
    {
        path: 'emr',
        component: LayoutsComponent,
        loadChildren: () => import('src/app/modules/emr/emr.module').then((m) => m.EmrModule),
    },

    {
        path: 'pharmacy',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/pharmacy/pharmacy.module').then((m) => m.PharmacyModule),
    },

    {
        path: 'billing',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/billing-payment/billing-payment.module').then(
                (m) => m.BillingPaymentModule
            ),
    },

    {
        path: 'settings',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/settings/settings.module').then((m) => m.SettingsModule),
    },

    {
        path: 'nurse',
        component: LayoutsComponent,
        loadChildren: () => import('src/app/modules/nurse/nurse.module').then((m) => m.NurseModule),
    },
    {
        path: 'clerking',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/clerking/clerking.module').then((m) => m.ClerkingModule),
    },

    {
        path: 'others',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/others/others.module').then((m) => m.OthersModule),
    },

    {
        path: 'lab',
        component: LayoutsComponent,
        loadChildren: () => import('src/app/modules/lab/lab.module').then((m) => m.LabModule),
    },

    {
        path: 'radiology',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/radiology/radiology.module').then((m) => m.RadiologyModule),
    },
    {
        path: 'reports',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/reports/reports.module').then((m) => m.ReportsModule),
    },
    {
        path: 'shift',
        component: LayoutsComponent,
        loadChildren: () =>
            import('src/app/modules/shift/shift.module').then((m) => m.ShiftModule),
    },

    { path: 'login', component: LoginComponent },

    {
        path: '',
        component: LayoutsComponent,
        children: [{ path: '', component: WelcomeComponent }],
        pathMatch: 'full',
        canActivate: [CanActivateGuard],
    },

    { path: '**', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {
            enableTracing: false,
            scrollPositionRestoration: 'enabled',
            useHash: true,
        }),
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {}

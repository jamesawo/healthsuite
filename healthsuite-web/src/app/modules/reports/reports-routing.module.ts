import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CanActivateGuard } from '@app/shared/_helpers';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import {RegisteredPatientReportComponent} from '@app/modules/reports/emr/registered-patient-report/registered-patient-report.component';
import {PatientInterimInvoiceComponent} from '@app/modules/reports/emr/patient-interim-invoice/patient-interim-invoice.component';
import {AppointmentBookingReportComponent} from '@app/modules/reports/emr/appointment-booking-report/appointment-booking-report.component';
import {DailyCashCollectionComponent} from '@app/modules/reports/accounts/daily-cash-collection/daily-cash-collection.component';
import {ServiceChargeReportComponent} from '@app/modules/reports/other/service-charge-report/service-charge-report.component';
import {SchemeConsumptionReportComponent} from '@app/modules/reports/accounts/scheme-consumption-report/scheme-consumption-report.component';

const routes: Routes = [
    {
        path: 'registered-patient-report',
        component: RegisteredPatientReportComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'REGISTERED PATIENT REPORT' },
    },
    {
        path: 'patient-interim-invoice',
        component: PatientInterimInvoiceComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATIENT INTERIM INVOICE' },
    },
    {
        path: 'appointment-booking-report',
        component: AppointmentBookingReportComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'APPOINTMENT BOOKING REPORT' },
    },
    {
        path: 'admission-register-report',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'ADMISSION REGISTER REPORT' },
    },
    {
        path: 'all-shift-per-day',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'ALL SHIFT PER DAY' },
    },
    {
        path: 'daily-cash-collection',
        component: DailyCashCollectionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'DAILY CASH COLLECTION REPORT' },
    },
    {
        path: 'cancelled-receipt-report',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'CANCELLED RECEIPT REPORT' },
    },
    {
        path: 'payment-receipt-report',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PAYMENT RECEIPT REPORT' },
    },
    {
        path: 'stock-balance-per-outlet',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'STOCK BALANCE PER OUTLET' },
    },
    {
        path: 'outlet-activity-report',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'OUTLET ACTIVITY REPORT' },
    },
    {
        path: 'patient-e-folder',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATIENT E-FOLDER REPORT' },
    },

    {
        path: 'other-service-charge-report',
        component: ServiceChargeReportComponent,
        canActivate: [CanActivateGuard],
        data: {
            title: 'SERVICE CHARGE REPORT'
        },
    },

    {
        path: 'scheme-consumption-report',
        component: SchemeConsumptionReportComponent,
        canActivate: [CanActivateGuard],
        data: {
            title: 'SCEHEM CONSUMPTION REPORT'
        },
    }

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ReportsRoutingModule {}

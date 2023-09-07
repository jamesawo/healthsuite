import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportsRoutingModule } from './reports-routing.module';
import { RegisteredPatientReportComponent } from './emr/registered-patient-report/registered-patient-report.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { OthersModule } from '@app/modules/others/others.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { PatientInterimInvoiceComponent } from './emr/patient-interim-invoice/patient-interim-invoice.component';
import { AppointmentBookingReportComponent } from './emr/appointment-booking-report/appointment-booking-report.component';
import { DailyCashCollectionComponent } from './accounts/daily-cash-collection/daily-cash-collection.component';
import { ServiceChargeReportComponent } from './other/service-charge-report/service-charge-report.component';
import { SchemeConsumptionReportComponent } from './accounts/scheme-consumption-report/scheme-consumption-report.component';
import {FormsModule} from '@angular/forms';

@NgModule({
    declarations: [
        RegisteredPatientReportComponent,
        PatientInterimInvoiceComponent,
        AppointmentBookingReportComponent,
        DailyCashCollectionComponent,
        ServiceChargeReportComponent,
        SchemeConsumptionReportComponent,
    ],
    imports: [
        CommonModule,
        ReportsRoutingModule,
        NgSelectModule,
        HmisCommonModule,
        OthersModule,
        NgxPaginationModule,
        FormsModule,
    ],
})
export class ReportsModule {}

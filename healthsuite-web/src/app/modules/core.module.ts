import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LayoutsModule } from '@app/layouts/layouts.module';
import { LayoutsComponent } from '@app/layouts/layouts.component';

import { EmrModule } from '@app/modules/emr/emr.module';
import { AuthModule } from '@app/modules/auth/auth.module';
import { SettingsModule } from '@app/modules/settings/settings.module';
import { NgxSpinnerModule } from 'ngx-spinner';
import { GlobalSettingService } from '@app/shared/_services/settings/global-setting.service';
import { SnackbarModule } from 'ngx-snackbar';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { OthersModule } from '@app/modules/others/others.module';
import { BillingPaymentModule } from '@app/modules/billing-payment/billing-payment.module';
import { PharmacyModule } from '@app/modules/pharmacy/pharmacy.module';
import {ClerkingModule} from '@app/modules/clerking/clerking.module';
import {RadiologyModule} from '@app/modules/radiology/radiology.module';
import {ReportsModule} from '@app/modules/reports/reports.module';
import {ShiftModule} from '@app/modules/shift/shift.module';

@NgModule({
    declarations: [LayoutsComponent],
    imports: [
        LayoutsModule,
        CommonModule,
        FormsModule,
        AuthModule,
        EmrModule,
        SettingsModule,
        NgxSpinnerModule,
        SnackbarModule,
        HmisCommonModule,
        OthersModule,
        BillingPaymentModule,
        PharmacyModule,
        ClerkingModule,
        RadiologyModule,
        ReportsModule,
        ShiftModule
    ],
    providers: [GlobalSettingService],
    exports: [LayoutsComponent],
})
export class CoreModule {}

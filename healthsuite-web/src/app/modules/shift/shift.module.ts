import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShiftRoutingModule } from './shift-routing.module';
import { ShiftManagerComponent } from './shift-manager/shift-manager.component';
import { CashierShiftComponent } from './cashier-shift/cashier-shift.component';
import { FundReceptionComponent } from './fund-reception/fund-reception.component';
import { ShiftCompilationComponent } from './shift-compilation/shift-compilation.component';
import { AllShiftPerDayComponent } from './all-shift-per-day/all-shift-per-day.component';
import {HmisCommonModule} from '@app/modules/common/hmis-common.module';
import {NgSelectModule} from '@ng-select/ng-select';
import {FormsModule} from '@angular/forms';

@NgModule({
    declarations: [
        ShiftManagerComponent,
        CashierShiftComponent,
        FundReceptionComponent,
        ShiftCompilationComponent,
        AllShiftPerDayComponent,
    ],
    imports: [CommonModule, ShiftRoutingModule, HmisCommonModule, NgSelectModule, FormsModule],
})
export class ShiftModule {}

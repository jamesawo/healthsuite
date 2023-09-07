import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import {ShiftManagerComponent} from '@app/modules/shift/shift-manager/shift-manager.component';
import {CashierShiftComponent} from '@app/modules/shift/cashier-shift/cashier-shift.component';
import {FundReceptionComponent} from '@app/modules/shift/fund-reception/fund-reception.component';
import {ShiftCompilationComponent} from '@app/modules/shift/shift-compilation/shift-compilation.component';
import {AllShiftPerDayComponent} from '@app/modules/shift/all-shift-per-day/all-shift-per-day.component';

const routes: Routes = [
    {
        path: 'shift-manager',
        component: ShiftManagerComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SHIFT MANAGER' },
    },
    {
        path: 'cashier-shift',
        component: CashierShiftComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'CASHIER SHIFT REPORT' },
    },
    {
        path: 'fund-reception',
        component: FundReceptionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'FUND RECEPTION & ACKNOWLEDGEMENT' },
    },
    {
        path: 'shift-compilation',
        component: ShiftCompilationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SHIFT COMPILATION' },
    },
    {
        path: 'all-shift-per-day',
        component: AllShiftPerDayComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'ALL SHIFT PER DAY' },
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ShiftRoutingModule {}

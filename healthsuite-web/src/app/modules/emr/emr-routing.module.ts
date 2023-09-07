import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { PatientRevisitComponent } from '@app/modules/emr/patient-revisit/patient-revisit.component';
import { PatientRegistrationComponent } from '@app/modules/emr/patient-registration/patient-registration.component';
import { PatientEditComponent } from '@app/modules/emr/patient-edit/patient-edit.component';
import { PatientAdmissionComponent } from '@app/modules/emr/patient-admission/patient-admission.component';
import { PatientSchemeUpdateComponent } from '@app/modules/emr/patient-scheme-update/patient-scheme-update.component';
import { AppointmentBookingComponent } from '@app/modules/emr/appointment-booking/appointment-booking.component';
import { LastBookingComponent } from '@app/modules/emr/last-booking/last-booking.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { SchemeRegistrationComponent } from '@app/modules/emr/scheme-registration/scheme-registration.component';
import { AppointmentBookingSettingComponent } from '@app/modules/emr/appointment-booking-setting/appointment-booking-setting.component';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import {PatientDischargeComponent} from '@app/modules/emr/patient-discharge/patient-discharge.component';
import {PatientWardTransferComponent} from '@app/modules/emr/patient-ward-transfer/patient-ward-transfer.component';
import {PatientCategoryComponent} from '@app/modules/emr/patient-category/patient-category.component';

const routes: Routes = [
    {
        path: 'patient-registration',
        component: PatientRegistrationComponent,
        canActivate: [CanActivateGuard],
        data: { mid: 'patient-registration', title: 'PATIENT REGISTRATION' },
    },
    {
        path: 'patient-edit',
        component: PatientEditComponent,
        canActivate: [CanActivateGuard],
        data: { mid: 'patient-edit', title: 'PATIENT UPDATE' },
    },
    {
        path: 'patient-revisit',
        component: PatientRevisitComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Patient Record Revisit' },
    },

    {
        path: 'patient-category',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-category-management',
        component: PatientCategoryComponent,
        canActivate: [CanActivateGuard],
        data: {  title: 'MANAGE PATIENT CATEGORY' },

    },
    {
        path: 'patient-admission',
        component: PatientAdmissionComponent,
        canActivate: [CanActivateGuard],
        data: {  title: 'PATIENT ADMISSION' },

    },
    {
        path: 'patient-scheme-update',
        component: PatientSchemeUpdateComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-ward-transfer',
        component: PatientWardTransferComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'WARD TRANSFER' },
    },
    {
        path: 'patient-coding-indexing',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-appointment-booking',
        component: AppointmentBookingComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-last-booking',
        component: LastBookingComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'scheme-manager',
        component: SchemeRegistrationComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'MANAGE SCHEME'}
    },
    {
        path: 'appointment-booking-setup',
        component: AppointmentBookingSettingComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'admission-details',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'clinic-attendance',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'death-register',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-discharge',
        component: PatientDischargeComponent,
        canActivate: [CanActivateGuard],
        data: {  title: 'PATIENT DISCHARGE' },
    },
    {
        path: 'approval-code-office',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'birth-register',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-search-index',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'edit-organization-details',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },

    //
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class EmrRoutingModule {}

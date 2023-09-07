import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CanActivateGuard } from '@app/shared/_helpers';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import { CommonModule } from '@angular/common';
import { GeneralOutPatientDeskComponent } from '@app/modules/clerking/general-out-patient-desk/general-out-patient-desk.component';
import {GeneralClerkingDeskComponent} from '@app/modules/clerking/general-clerking-desk/general-clerking-desk.component';
import {PatientFileUploadComponent} from '@app/modules/clerking/patient-file-upload/patient-file-upload.component';
import {DoctorEFolderComponent} from '@app/modules/clerking/doctor-e-folder/doctor-e-folder.component';
import {InpatientClerkingComponent} from '@app/modules/clerking/inpatient-clerking/inpatient-clerking.component';

const routes: Routes = [
    {
        path: 'general-clerking-desk',
        component: GeneralClerkingDeskComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'General Clerking Desk' },
    },
    {
        path: 'anc-clerking',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'dental-clerking-desk',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'physio_clerking_desk',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'pediatrics-clerking-desk',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-e-folder',
        component: DoctorEFolderComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATIENT E-FOLDER' },
    },
    {
        path: 'inpatient-clerking',
        component: InpatientClerkingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'INPATIENT CLERKING' },

    },
    {
        path: 'antenatal-booking',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'patient-file-upload',
        component: PatientFileUploadComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATIENT FILE UPLOAD' },

    },
    {
        path: 'general-outpatient-desk',
        component: GeneralOutPatientDeskComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'General OutPatient Desk' },
    },
    {
        path: 'ophthalmology-clerking',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'surgical-outpatient-consultation',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'hop-clinic-consultation',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
    {
        path: 'psychiatry-consultation',
        component: UnderConstructionComponent,
        canActivate: [CanActivateGuard],
    },
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ClerkingRoutingModule {}

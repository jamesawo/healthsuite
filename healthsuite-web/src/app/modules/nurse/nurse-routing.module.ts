import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UnderConstructionComponent } from '../common/under-contsruction/under-construction.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { NurseVitalSignCaptureComponent } from '@app/modules/nurse/nurse-vital-sign-capture/nurse-vital-sign-capture.component';
import { NurseNoteComponent } from '@app/modules/nurse/nurse-note/nurse-note.component';
import { AntenatalBookingComponent } from '@app/modules/nurse/antenatal-booking/antenatal-booking.component';
import {NursePatientEFolderComponent} from '@app/modules/nurse/nurse-patient-e-folder/nurse-patient-e-folder.component';
import {ObstetricsHistoryComponent} from '@app/modules/nurse/obstetrics-history/obstetrics-history.component';
import {AncCardNoteComponent} from '@app/modules/nurse/anc-card-note/anc-card-note.component';
import {VisualAcuityComponent} from '@app/modules/nurse/visual-acuity/visual-acuity.component';
import {DrugAdministrationComponent} from '@app/modules/nurse/drug-administration/drug-administration.component';
import {VitalSignsTrendComponent} from '@app/modules/nurse/vital-signs-trend/vital-signs-trend.component';
import {NursingCareComponent} from '@app/modules/nurse/nursing-care/nursing-care.component';

const routes: Routes = [
    {
        path: 'vital-signs-capture',
        component: NurseVitalSignCaptureComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'NURSE VITAL SIGN CAPTURE' },
    },
    {
        path: 'vital-signs-trend',
        component: VitalSignsTrendComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'VITAL SIGN TREND' },
    },
    {
        path: 'nursing-note',
        component: NurseNoteComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Nursing Note' },
    },
    {
        path: 'nursing-care',
        component: NursingCareComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Nursing Care' },

    },
    {
        path: 'drug-administration',
        component: DrugAdministrationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Drug Administration' },

    },
    {
        path: 'patient-e-folder',
        component: NursePatientEFolderComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Patient E-Folder' },

    },
    {
        path: 'antenatal-booking',
        component: AntenatalBookingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Antenatal Booking' },
    },
    {
        path: 'obstetrics-history',
        component: ObstetricsHistoryComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Obstetrics History' },

    },
    {
        path: 'anc-card-note',
        component: AncCardNoteComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Anc Card Note' },

    },
    {
        path: 'visual-acuity',
        component: VisualAcuityComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'Visual Acuity' },

    },
];

@NgModule({
    imports: [CommonModule, RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class NurseRoutingModule {}

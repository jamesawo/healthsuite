import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CanActivateGuard} from '@app/shared/_helpers';
import {LabBillingComponent} from '@app/modules/lab/lab-billing/lab-billing.component';
import {SpecimenCollectionComponent} from '@app/modules/lab/specimen-collection/specimen-collection.component';
import {SpecimenCollectionAcknowledgementComponent} from '@app/modules/lab/specimen-collection-acknowledgement/specimen-collection-acknowledgement.component';
import {LabRequestTrackerComponent} from '@app/modules/lab/lab-request-tracker/lab-request-tracker.component';
import {LabParameterRegistrationComponent} from '@app/modules/lab/lab-parameter-registration/lab-parameter-registration.component';
import {LabPushSampleComponent} from '@app/modules/lab/lab-push-sample/lab-push-sample.component';
import {MedParamSetupComponent} from '@app/modules/lab/medical/med-param-setup/med-param-setup.component';
import {MedResultPreparationComponent} from '@app/modules/lab/medical/med-result-preparation/med-result-preparation.component';
import {MedResultVerificationComponent} from '@app/modules/lab/medical/med-result-verification/med-result-verification.component';
import {MedPathologistVerificationComponent} from '@app/modules/lab/medical/med-pathologist-verification/med-pathologist-verification.component';
import {MedResultViewComponent} from '@app/modules/lab/medical/med-result-view/med-result-view.component';
import {SerologyResultPrepComponent} from '@app/modules/lab/microbiology/serology/serology-result-prep/serology-result-prep.component';
import {ParasitologyResultPrepComponent} from '@app/modules/lab/microbiology/parasitology/parasitology-result-prep-modal/parasitology-result-prep.component';
import {SerologyPathologistVerifyComponent} from '@app/modules/lab/microbiology/serology/serology-pathologist-verify/serology-pathologist-verify.component';
import {SerologyResultVerificationComponent} from '@app/modules/lab/microbiology/serology/serology-result-verification/serology-result-verification.component';
import {ParasitologyResultVerificationComponent} from '@app/modules/lab/microbiology/parasitology/parasitology-result-verification/parasitology-result-verification.component';
import {ParasitologyPathologistVerifyComponent} from '@app/modules/lab/microbiology/parasitology/parasitology-pathologist-verify/parasitology-pathologist-verify.component';
import {MicrobiologyResultControlComponent} from '@app/modules/lab/microbiology/microbiology-result-control/microbiology-result-control.component';


const routes: Routes = [
    {
        path: 'lab-billing',
        component: LabBillingComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'LABORATORY BILLING' },
    },
    {
        path: 'specimen-collection',
        component: SpecimenCollectionComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SPECIMEN COLLECTION' },
    },
    {
        path: 'specimen-collection-acknowledgement',
        component: SpecimenCollectionAcknowledgementComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SPECIMEN COLLECTION ACKNOWLEDGEMENT' },
    },
    {
        path: 'lab-request-tracker',
        component: LabRequestTrackerComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'LAB REQUEST TRACKER' },
    },
    {
        path: 'lab-param-registration',
        component: LabParameterRegistrationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'LAB PARAM REGISTRATION' },
    },
    {
        path: 'lab-re-push-sample',
        component: LabPushSampleComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'LAB RE-PUSH SAMPLE' },
    },
    {
        path: 'medical-parameter-setup',
        component: MedParamSetupComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PARAMETER SETUP' },
    },

    {
        path: 'medical-lab-result-preparation',
        component: MedResultPreparationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RESULT PREPARATION' },
    },

    {
        path: 'medical-lab-result-verification',
        component: MedResultVerificationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'RESULT VERIFICATION' },
    },
    {
        path: 'medical-lab-pathologist-verification',
        component: MedPathologistVerificationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PATHOLOGIST VERIFICATION' },
    },
    {
        path: 'medical-lab-result-view',
        component: MedResultViewComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'LAB RESULT VIEW' },
    },
    {
        path: 'micro-biology-parasitology-result-preparation',
        component: ParasitologyResultPrepComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PARASITOLOGY RESULT PREPARATION' },
    },
    {
        path: 'micro-biology-parasitology-result-verification',
        component: ParasitologyResultVerificationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PARASITOLOGY RESULT VERIFICATION' },
    },
    {
        path: 'micro-biology-parasitology-pathologist-verification',
        component: ParasitologyPathologistVerifyComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PARASITOLOGY PATHOLOGIST VERIFICATION' },
    },
    {
        path: 'micro-biology-serology-result-preparation',
        component: SerologyResultPrepComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SEROLOGY RESULT PREPARATION' },
    },
    {
        path: 'micro-biology-serology-result-verification',
        component: SerologyResultVerificationComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SEROLOGY RESULT VERIFICATION' },
    },
    {
        path: 'micro-biology-serology-pathologist-verification',
        component: SerologyPathologistVerifyComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'SEROLOGY PATHOLOGIST VERIFICATION' },
    },
    {
        path: 'micro-biology-result-control',
        component: MicrobiologyResultControlComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'MICROBIOLOGY RESULT CONTROL' },
    },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LabRoutingModule { }

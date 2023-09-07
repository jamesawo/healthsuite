import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LabRoutingModule } from './lab-routing.module';
import { LabBillingComponent } from './lab-billing/lab-billing.component';
import { BillingPaymentModule } from '@app/modules/billing-payment/billing-payment.module';
import { SpecimenCollectionComponent } from './specimen-collection/specimen-collection.component';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { SpecimenCollectionAcknowledgementComponent } from './specimen-collection-acknowledgement/specimen-collection-acknowledgement.component';
import { LabRequestTrackerComponent } from './lab-request-tracker/lab-request-tracker.component';
import { RequestStatusComponent } from './lab-request-tracker/request-status/request-status.component';
import { LabParameterRegistrationComponent } from './lab-parameter-registration/lab-parameter-registration.component';
import { LabPushSampleComponent } from './lab-push-sample/lab-push-sample.component';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { MedParamSetupComponent } from './medical/med-param-setup/med-param-setup.component';
import { SpecimenTestSetupComponent } from './components/specimen-test-setup/specimen-test-setup.component';
import { ParamRankSetupComponent } from './components/param-rank-setup/param-rank-setup.component';
import { RangeTestParamSetupComponent } from './components/range-test-param-setup/range-test-param-setup.component';
import { RangeValueSetupComponent } from './components/range-value-setup/range-value-setup.component';
import { MedResultPreparationComponent } from './medical/med-result-preparation/med-result-preparation.component';
import { LabResultPreparationSharedComponent } from './components/lab-result-preparation-shared/lab-result-preparation-shared.component';
import { MedResultVerificationComponent } from './medical/med-result-verification/med-result-verification.component';
import { LabTestRequestSharedComponent } from './components/lab-test-request-shared/lab-test-request-shared.component';
import { LabResultVerificationSharedComponent } from './components/lab-result-verification-shared/lab-result-verification-shared.component';
import { MedPathologistVerificationComponent } from './medical/med-pathologist-verification/med-pathologist-verification.component';
import { MedResultViewComponent } from './medical/med-result-view/med-result-view.component';
import { SerologyResultPrepComponent } from './microbiology/serology/serology-result-prep/serology-result-prep.component';
import { ParasitologyResultPrepComponent } from './microbiology/parasitology/parasitology-result-prep-modal/parasitology-result-prep.component';
import { LabResultDirective } from '@app/shared/_directives/lab/lab-result.directive';
import { ParasitologyResultTemplateComponent } from './components/parasitology-result-template/parasitology-result-template.component';
import { ParasitologyMacroscopyComponent } from './components/parasitology-result-template/parasitology-macroscopy/parasitology-macroscopy.component';
import { ParasitologyMicroscopyComponent } from './components/parasitology-result-template/parasitology-microscopy/parasitology-microscopy.component';
import { ParasitologyCultureComponent } from './components/parasitology-result-template/parasitology-culture/parasitology-culture.component';
import { LabTestUserLogComponent } from './components/lab-test-user-log/lab-test-user-log.component';
import { ParasitologyResultVerificationComponent } from './microbiology/parasitology/parasitology-result-verification/parasitology-result-verification.component';
import { SerologyResultVerificationComponent } from './microbiology/serology/serology-result-verification/serology-result-verification.component';
import { SerologyPathologistVerifyComponent } from './microbiology/serology/serology-pathologist-verify/serology-pathologist-verify.component';
import { ParasitologyPathologistVerifyComponent } from './microbiology/parasitology/parasitology-pathologist-verify/parasitology-pathologist-verify.component';
import { MicrobiologyResultControlComponent } from './microbiology/microbiology-result-control/microbiology-result-control.component';

@NgModule({
    declarations: [
        LabBillingComponent,
        SpecimenCollectionComponent,
        SpecimenCollectionAcknowledgementComponent,
        LabRequestTrackerComponent,
        RequestStatusComponent,
        LabParameterRegistrationComponent,
        LabPushSampleComponent,
        MedParamSetupComponent,
        SpecimenTestSetupComponent,
        ParamRankSetupComponent,
        RangeTestParamSetupComponent,
        RangeValueSetupComponent,
        MedResultPreparationComponent,
        LabResultPreparationSharedComponent,
        MedResultVerificationComponent,
        LabTestRequestSharedComponent,
        LabResultVerificationSharedComponent,
        MedPathologistVerificationComponent,
        MedResultViewComponent,
        SerologyResultPrepComponent,
        ParasitologyResultPrepComponent,
        ParasitologyResultPrepComponent,
        LabResultDirective,
        ParasitologyResultTemplateComponent,
        ParasitologyMacroscopyComponent,
        ParasitologyMicroscopyComponent,
        ParasitologyCultureComponent,
        LabTestUserLogComponent,
        ParasitologyResultVerificationComponent,
        SerologyResultVerificationComponent,
        SerologyPathologistVerifyComponent,
        ParasitologyPathologistVerifyComponent,
        MicrobiologyResultControlComponent,
    ],
    imports: [
        CommonModule,
        LabRoutingModule,
        BillingPaymentModule,
        HmisCommonModule,
        NgSelectModule,
        FormsModule,
        NgxPaginationModule,
    ],
    exports: [LabResultDirective],
})
export class LabModule {}

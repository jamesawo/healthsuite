import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClerkingRoutingModule } from './clerking-routing.module';
import { NurseRoutingModule } from '@app/modules/nurse/nurse-routing.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { MomentModule } from 'ngx-moment';
import { NgxPaginationModule } from 'ngx-pagination';
import { BillingPaymentModule } from '@app/modules/billing-payment/billing-payment.module';
import { GeneralOutPatientDeskComponent } from './general-out-patient-desk/general-out-patient-desk.component';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { PhysicalExaminationFormComponent } from './general-out-patient-desk/components/physical-examination-form/physical-examination-form.component';
import { SystemicExaminationFormComponent } from './general-out-patient-desk/components/systemic-examination-form/systemic-examination-form.component';
import { CardioVascularFormComponent } from './general-out-patient-desk/components/cardio-vascular-form/cardio-vascular-form.component';
import { AbdomenFormComponent } from './general-out-patient-desk/components/abdomen-form/abdomen-form.component';
import { PerineumFormComponent } from './general-out-patient-desk/components/perineum-form/perineum-form.component';
import { MusculoSkeletalFormComponent } from './general-out-patient-desk/components/musculo-skeletal-form/musculo-skeletal-form.component';
import { NeurologicalFormComponent } from './general-out-patient-desk/components/neurological-form/neurological-form.component';
import { OtherInformationFormComponent } from './general-out-patient-desk/components/other-information-form/other-information-form.component';
import { ActualDiagnosisFormComponent } from './general-out-patient-desk/components/actual-diagnosis-form/actual-diagnosis-form.component';
import { ClerkingTabsComponent } from './general-out-patient-desk/components/clerking-tabs/clerking-tabs.component';
import { InformantDetailsComponent } from './general-out-patient-desk/components/informant-details/informant-details.component';
import { PatientBackgroundHistoryFormComponent } from './general-out-patient-desk/components/patient-background-history-form/patient-background-history-form.component';
import { ClinicAssessmentFormComponent } from './general-out-patient-desk/components/clinic-assessment-form/clinic-assessment-form.component';
import { SearchDiseasesComponent } from './general-out-patient-desk/components/search-diseases/search-diseases.component';
import { ViewTakeVitalsComponent } from './general-out-patient-desk/components/view-take-vitals/view-take-vitals.component';
import { ClerkPatientReferralComponent } from './general-out-patient-desk/components/clerk-patient-referral/clerk-patient-referral.component';
import { ClerkPatientAppointmentComponent } from './general-out-patient-desk/components/clerk-patient-appointment/clerk-patient-appointment.component';
import { ClerkPatientFolderComponent } from './general-out-patient-desk/components/clerk-patient-folder/clerk-patient-folder.component';
import { ClerkLabRequestComponent } from './general-out-patient-desk/components/clerk-lab-request/clerk-lab-request.component';
import { ClerkDrugRequestComponent } from './general-out-patient-desk/components/clerk-drug-request/clerk-drug-request.component';
import { ClerkXrayRequestComponent } from './general-out-patient-desk/components/clerk-xray-request/clerk-xray-request.component';
import { ClerkProcedureRequestComponent } from './general-out-patient-desk/components/clerk-procedure-request/clerk-procedure-request.component';
import { ClerkAdmissionRequestComponent } from './general-out-patient-desk/components/clerk-admission-request/clerk-admission-request.component';
import {OthersModule} from '@app/modules/others/others.module';
import { GeneralClerkingDeskComponent } from './general-clerking-desk/general-clerking-desk.component';
import { PatientFileUploadComponent } from './patient-file-upload/patient-file-upload.component';
import { DoctorEFolderComponent } from './doctor-e-folder/doctor-e-folder.component';
import {NurseModule} from '@app/modules/nurse/nurse.module';
import { InpatientClerkingComponent } from './inpatient-clerking/inpatient-clerking.component';

@NgModule({
    declarations: [
        GeneralOutPatientDeskComponent,
        PhysicalExaminationFormComponent,
        SystemicExaminationFormComponent,
        CardioVascularFormComponent,
        AbdomenFormComponent,
        PerineumFormComponent,
        MusculoSkeletalFormComponent,
        NeurologicalFormComponent,
        OtherInformationFormComponent,
        ActualDiagnosisFormComponent,
        ClerkingTabsComponent,
        InformantDetailsComponent,
        PatientBackgroundHistoryFormComponent,
        ClinicAssessmentFormComponent,
        SearchDiseasesComponent,
        ViewTakeVitalsComponent,
        ClerkPatientReferralComponent,
        ClerkPatientAppointmentComponent,
        ClerkPatientFolderComponent,
        ClerkLabRequestComponent,
        ClerkDrugRequestComponent,
        ClerkXrayRequestComponent,
        ClerkProcedureRequestComponent,
        ClerkAdmissionRequestComponent,
        GeneralClerkingDeskComponent,
        PatientFileUploadComponent,
        DoctorEFolderComponent,
        InpatientClerkingComponent,
    ],
    imports: [
        CommonModule,
        ClerkingRoutingModule,
        NurseRoutingModule,
        NgSelectModule,
        FormsModule,
        HmisCommonModule,
        MomentModule,
        NgxPaginationModule,
        BillingPaymentModule,
        AccordionModule,
        CollapseModule,
        BsDropdownModule,
        OthersModule,
        NurseModule,
    ],
})
export class ClerkingModule {}

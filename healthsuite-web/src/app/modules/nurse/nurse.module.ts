import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { MomentModule } from 'ngx-moment';
import { NgxPaginationModule } from 'ngx-pagination';
import { BillingPaymentModule } from '@app/modules/billing-payment/billing-payment.module';
import { NurseRoutingModule } from '@app/modules/nurse/nurse-routing.module';
import { NurseVitalSignCaptureComponent } from './nurse-vital-sign-capture/nurse-vital-sign-capture.component';
import { NurseNoteComponent } from './nurse-note/nurse-note.component';
import { AntenatalBookingComponent } from './antenatal-booking/antenatal-booking.component';
import { NursePatientEFolderComponent } from './nurse-patient-e-folder/nurse-patient-e-folder.component';
import { ObstetricsHistoryComponent } from './obstetrics-history/obstetrics-history.component';
import { AncCardNoteComponent } from './anc-card-note/anc-card-note.component';
import { VisualAcuityComponent } from './visual-acuity/visual-acuity.component';
import { DrugAdministrationComponent } from './drug-administration/drug-administration.component';
import { NursingCareComponent } from './nursing-care/nursing-care.component';
import { VitalSignsTrendComponent } from './vital-signs-trend/vital-signs-trend.component';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import { PregnancyHistoryModalComponent } from './obstetrics-history/components/pregnancy-history-modal/pregnancy-history-modal.component';
import { DrugAdministerHistoryComponent } from './drug-administration/drug-administer-history/drug-administer-history.component';
import { ObGeneralFormComponent } from './obstetrics-history/components/ob-general-form/ob-general-form.component';
import { ObPreMedicalHistoryComponent } from './obstetrics-history/components/ob-pre-medical-history/ob-pre-medical-history.component';
import { ObFamilyHistoryComponent } from './obstetrics-history/components/ob-family-history/ob-family-history.component';
import { ObPregnancyHistoriesComponent } from './obstetrics-history/components/ob-pregnancy-histories/ob-pregnancy-histories.component';
import { ObHistoryOfPresentPregComponent } from './obstetrics-history/components/ob-history-of-present-preg/ob-history-of-present-preg.component';
import { ObPhysicalExaminationComponent } from './obstetrics-history/components/ob-physical-examination/ob-physical-examination.component';
import { ObMeasurementComponent } from './obstetrics-history/components/ob-measurement/ob-measurement.component';
import { ObInletCavityOutletComponent } from './obstetrics-history/components/ob-inlet-cavity-outlet/ob-inlet-cavity-outlet.component';
import { NursePatientEFolderSpecificsComponent } from './nurse-patient-e-folder/nurse-patient-e-folder-specifics/nurse-patient-e-folder-specifics.component';
import { PatientFluidBalanceCaptureComponent } from './nursing-care/patient-fluid-balance-capture/patient-fluid-balance-capture.component';
import { PatientIcuBounceBackComponent } from './nursing-care/patient-icu-bounce-back/patient-icu-bounce-back.component';
import { SharedNurseNoteComponent } from './nurse-note/shared-nurse-note/shared-nurse-note.component';
import { PatientOxygenTrackerComponent } from './nursing-care/patient-oxygen-tracker/patient-oxygen-tracker.component';
import { SharedPatientDrugAdministrationComponent } from './nursing-care/shared-patient-drug-administration/shared-patient-drug-administration.component';
import {ChartsModule} from 'ng2-charts';
import { VitalSignChartComponent } from './vital-signs-trend/vital-sign-chart/vital-sign-chart.component';

@NgModule({
    declarations: [
        NurseVitalSignCaptureComponent,
        NurseNoteComponent,
        AntenatalBookingComponent,
        NursePatientEFolderComponent,
        ObstetricsHistoryComponent,
        AncCardNoteComponent,
        VisualAcuityComponent,
        DrugAdministrationComponent,
        NursingCareComponent,
        VitalSignsTrendComponent,
        PregnancyHistoryModalComponent,
        DrugAdministerHistoryComponent,
        ObGeneralFormComponent,
        ObPreMedicalHistoryComponent,
        ObFamilyHistoryComponent,
        ObPregnancyHistoriesComponent,
        ObHistoryOfPresentPregComponent,
        ObPhysicalExaminationComponent,
        ObMeasurementComponent,
        ObInletCavityOutletComponent,
        NursePatientEFolderSpecificsComponent,
        PatientFluidBalanceCaptureComponent,
        PatientIcuBounceBackComponent,
        SharedNurseNoteComponent,
        PatientOxygenTrackerComponent,
        SharedPatientDrugAdministrationComponent,
        VitalSignChartComponent,
    ],
    imports: [
        CommonModule,
        NurseRoutingModule,
        NgSelectModule,
        FormsModule,
        HmisCommonModule,
        MomentModule,
        NgxPaginationModule,
        BillingPaymentModule,
        NgbNavModule,
        ChartsModule,
    ],
    exports: [NursePatientEFolderComponent, NursePatientEFolderSpecificsComponent],
})
export class NurseModule {}

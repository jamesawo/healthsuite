import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmrRoutingModule } from '@app/modules/emr/emr-routing.module';
import { PatientRegistrationComponent } from '@app/modules/emr/patient-registration/patient-registration.component';
import { WelcomeComponent } from '@app/modules/emr/welcome/welcome.component';
import { PatientEditComponent } from './patient-edit/patient-edit.component';
import { PatientRevisitComponent } from './patient-revisit/patient-revisit.component';
import { PatientAdmissionComponent } from './patient-admission/patient-admission.component';
import { PatientSchemeUpdateComponent } from './patient-scheme-update/patient-scheme-update.component';
import { PatientCategoryComponent } from './patient-category/patient-category.component';
import { PatientWardTransferComponent } from './patient-ward-transfer/patient-ward-transfer.component';
import { CodingIndexingComponent } from './coding-indexing/coding-indexing.component';
import { AppointmentBookingComponent } from './appointment-booking/appointment-booking.component';
import { LastBookingComponent } from './last-booking/last-booking.component';
import {
    NgbButtonsModule,
    NgbDatepickerModule,
    NgbPopoverModule,
} from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PatientBasicDetailsComponent } from './patient-registration/patient-basic-details/patient-basic-details.component';
import { PatientMeansOfIdentificationComponent } from './patient-registration/patient-means-of-identification/patient-means-of-identification.component';
import { PatientContactDetailsComponent } from './patient-registration/patient-contact-details/patient-contact-details.component';
import { PatientNokDetailsComponent } from './patient-registration/patient-nok-details/patient-nok-details.component';
import { PatientCardHolderDetailsComponent } from './patient-registration/patient-card-holder-details/patient-card-holder-details.component';
import { PatientInsuranceDetailsComponent } from './patient-registration/patient-insurance-details/patient-insurance-details.component';
import { PatientTransferDetailsComponent } from './patient-registration/patient-transfer-details/patient-transfer-details.component';
import { PatientOtherDetailsComponent } from './patient-registration/patient-other-details/patient-other-details.component';
import { PatientFacialCaptureComponent } from './patient-registration/patient-facial-capture/patient-facial-capture.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { WebcamModule } from 'ngx-webcam';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { SchemeRegistrationComponent } from './scheme-registration/scheme-registration.component';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { AppointmentBookingSettingComponent } from './appointment-booking-setting/appointment-booking-setting.component';
import { TimepickerModule } from 'ngx-bootstrap/timepicker';
import { PatientDischargeComponent } from './patient-discharge/patient-discharge.component';
import { SharedWardTransferComponent } from './patient-ward-transfer/shared-ward-transfer/shared-ward-transfer.component';
import { SchemePlanModalComponent } from './scheme-registration/scheme-plan-modal/scheme-plan-modal.component';

@NgModule({
    declarations: [
        PatientRegistrationComponent,
        WelcomeComponent,
        PatientEditComponent,
        PatientRevisitComponent,
        PatientAdmissionComponent,
        PatientSchemeUpdateComponent,
        PatientCategoryComponent,
        PatientWardTransferComponent,
        CodingIndexingComponent,
        AppointmentBookingComponent,
        LastBookingComponent,
        PatientBasicDetailsComponent,
        PatientMeansOfIdentificationComponent,
        PatientContactDetailsComponent,
        PatientNokDetailsComponent,
        PatientCardHolderDetailsComponent,
        PatientInsuranceDetailsComponent,
        PatientTransferDetailsComponent,
        PatientOtherDetailsComponent,
        PatientFacialCaptureComponent,
        SchemeRegistrationComponent,
        AppointmentBookingSettingComponent,
        PatientDischargeComponent,
        SharedWardTransferComponent,
        SchemePlanModalComponent,
    ],
    imports: [
        CommonModule,
        EmrRoutingModule,
        NgbPopoverModule,
        ReactiveFormsModule,
        FormsModule,
        NgbButtonsModule,
        NgbDatepickerModule,
        NgSelectModule,
        WebcamModule,
        HmisCommonModule,
        BsDatepickerModule,
        TimepickerModule,
    ],
    exports: [PatientRegistrationComponent, WelcomeComponent],
})
export class EmrModule {}

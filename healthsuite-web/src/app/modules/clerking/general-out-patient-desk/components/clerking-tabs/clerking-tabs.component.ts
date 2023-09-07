import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FileUploadTypeEnum, ModalSizeEnum } from '@app/shared/_payload';
import { ModalPopupService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { ViewTakeVitalsComponent } from '@app/modules/clerking/general-out-patient-desk/components/view-take-vitals/view-take-vitals.component';
import { ClerkPatientReferralComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-patient-referral/clerk-patient-referral.component';
import { ClerkPatientAppointmentComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-patient-appointment/clerk-patient-appointment.component';
import { ClerkPatientFolderComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-patient-folder/clerk-patient-folder.component';
import { ClerkLabRequestComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-lab-request/clerk-lab-request.component';
import { ClerkDrugRequestComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-drug-request/clerk-drug-request.component';
import { ClerkXrayRequestComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-xray-request/clerk-xray-request.component';
import { ClerkProcedureRequestComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-procedure-request/clerk-procedure-request.component';
import { ClerkAdmissionRequestComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-admission-request/clerk-admission-request.component';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ToastrService } from 'ngx-toastr';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ClerkDoctorRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';

@Component({
    selector: 'app-clerking-tabs',
    templateUrl: './clerking-tabs.component.html',
    styleUrls: ['./clerking-tabs.component.css'],
})
export class ClerkingTabsComponent implements OnInit, OnChanges {
    @Input('props')
    public props: { patientPayload: PatientPayload };

    constructor(
        private modalService: ModalPopupService,
        private spinnerService: NgxSpinnerService,
        private toast: ToastrService,
        private clerkRequestService: ClerkRequestService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
    }

    ngOnChanges(changes: SimpleChanges) {
        this.onInitPayload();
    }

    public onTakeVitalModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ViewTakeVitalsComponent,
                {
                    data: {
                        vitalTabData: {
                            isTakeVital: true,
                            patientId: this.props.patientPayload.patientId,
                        },
                    },
                    title: 'Take Patient Vital Sign',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onViewVitalModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ViewTakeVitalsComponent,
                {
                    data: {
                        vitalTabData: {
                            isTakeVital: false,
                            patientId: this.props.patientPayload.patientId,
                        },
                    },
                    title: 'View Patient Vital',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onReferralNote() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkPatientReferralComponent,
                {
                    data: {patientPayload: this.props.patientPayload },
                    title: 'Patient Referral',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onAppointmentTab() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkPatientAppointmentComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'Patient Appointment',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onPharmacyModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkDrugRequestComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'Drug Request',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onLabModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkLabRequestComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'Lab Request',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onRadiologyModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkXrayRequestComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'X-Ray Request',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onProcedures() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkProcedureRequestComponent,
                {
                    data: { uploadTypeEnum: FileUploadTypeEnum.DRUG },
                    title: 'Procedure Request',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onAdmissionModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkAdmissionRequestComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'Admission',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onEFolderModal() {
        if (this.isPatientSelected()) {
            this.modalService.openModalWithComponent(
                ClerkPatientFolderComponent,
                {
                    data: { patientPayload: this.props.patientPayload },
                    title: 'Preview Patient E-Folder',
                },
                ModalSizeEnum.large
            );
        }
    }

    public isPatientSelected(): boolean {
        if (!this.props?.patientPayload?.patientId) {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
            return false;
        }
        return true;
    }

    private onInitPayload() {
        const clerkDoctorRequestPayload = new ClerkDoctorRequestPayload();
        clerkDoctorRequestPayload.department = this.commonService.getCurrentLocation();
        clerkDoctorRequestPayload.doctor = this.commonService.getCurrentUser();
        clerkDoctorRequestPayload.date = new Date();
        clerkDoctorRequestPayload.patient = this.props.patientPayload;
        this.clerkRequestService.onSetNext(clerkDoctorRequestPayload);
    }
}

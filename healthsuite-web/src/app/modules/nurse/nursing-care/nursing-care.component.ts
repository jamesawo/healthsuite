import { Component, OnDestroy, OnInit } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import {
    ModalSizeEnum,
    NurseWaitingPayload,
    ReportFileNameEnum,
    WaitingViewTypeEnum,
} from '@app/shared/_payload';
import { NurseCarePayload } from '@app/shared/_payload/nurse/nurse-care.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { EmrService, ModalPopupService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { PatientFluidBalanceCaptureComponent } from '@app/modules/nurse/nursing-care/patient-fluid-balance-capture/patient-fluid-balance-capture.component';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { saveAs } from 'file-saver';
import { ClerkPatientFolderComponent } from '@app/modules/clerking/general-out-patient-desk/components/clerk-patient-folder/clerk-patient-folder.component';
import { SharedWardTransferComponent } from '@app/modules/emr/patient-ward-transfer/shared-ward-transfer/shared-ward-transfer.component';
import { ViewTakeVitalsComponent } from '@app/modules/clerking/general-out-patient-desk/components/view-take-vitals/view-take-vitals.component';
import { PatientIcuBounceBackComponent } from '@app/modules/nurse/nursing-care/patient-icu-bounce-back/patient-icu-bounce-back.component';
import { SharedNurseNoteComponent } from '@app/modules/nurse/nurse-note/shared-nurse-note/shared-nurse-note.component';
import {SharedPatientDrugAdministrationComponent} from '@app/modules/nurse/nursing-care/shared-patient-drug-administration/shared-patient-drug-administration.component';

@Component({
    selector: 'app-nursing-care',
    templateUrl: './nursing-care.component.html',
    styleUrls: ['./nursing-care.component.css'],
})
export class NursingCareComponent implements OnInit, OnDestroy {
    public payload: NurseCarePayload = new NurseCarePayload();
    public activeView: 'waiting' | 'attended' = 'waiting';
    public waitingCount = 0;
    public attendedCount = 0;
    public nurse = WaitingViewTypeEnum.NURSE;

    private subscription: Subscription = new Subscription();

    constructor(
        private emrService: EmrService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private modalService: ModalPopupService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(patient: NurseWaitingPayload) {
        if (patient?.patientId) {
            this.spinner.show().then();
            this.subscription.add(
                this.emrService.onFindPatientById(patient.patientId).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res.body) {
                            this.payload.patient = res.body;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(
                            'Something Went Wrong, Refresh And Try Again',
                            HmisConstants.ERR_TITLE
                        );
                    }
                )
            );
        }
    }

    public onPatientSearchSelected(patient: PatientPayload) {
        if (patient && patient.patientId) {
            this.payload.patient = patient;
        }
    }

    public onChangeActiveView(viewType: 'waiting' | 'attended') {
        if (viewType) {
            this.activeView = viewType;
        }
    }

    public onUpdateCount = (value: number) => {
        setTimeout(() => {
            this.waitingCount = value;
        }, 0);
    };

    public onUpdateAttendedCount = (value: number) => {
        setTimeout(() => {
            this.attendedCount = value;
        }, 0);
    };

    public onOpenFluidBalanceModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                PatientFluidBalanceCaptureComponent,
                { data: { patientPayload: this.payload.patient }, title: 'Fluid Balance' },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenAdministerDrugModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                SharedPatientDrugAdministrationComponent,
                { data: { patientPayload: this.payload.patient }, title: 'DRUG ADMINISTRATION' },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenWardTransferModal() {
        if (this.hasPatient() === true) {
            if (this.payload.patient.isOnAdmission === false) {
                this.toast.warning('PATIENT IS NOT ON ADMISSION', HmisConstants.VALIDATION_ERR);
                return;
            }
            this.modalService.openModalWithComponent(
                SharedWardTransferComponent,
                {
                    data: { patientPayload: this.payload.patient },
                    title: 'Ward Transfer',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenViewPatientEFolder() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                ClerkPatientFolderComponent,
                {
                    data: { patientPayload: this.payload.patient },
                    title: 'Preview Patient E-Folder',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenTakeVitalSignModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                ViewTakeVitalsComponent,
                {
                    data: {
                        vitalTabData: {
                            isTakeVital: true,
                            patientId: this.payload.patient.patientId,
                        },
                    },
                    title: 'Take Patient Vital Sign',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenViewVitalModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                ViewTakeVitalsComponent,
                {
                    data: {
                        vitalTabData: {
                            isTakeVital: false,
                            patientId: this.payload.patient.patientId,
                        },
                    },
                    title: 'View Patient Vital',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenICUModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                PatientIcuBounceBackComponent,
                {
                    data: { patientPayload: this.payload.patient },
                    title: 'ICU BOUNCE BACK DETAILS',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onOpenNurseNoteModal() {
        if (this.hasPatient() === true) {
            this.modalService.openModalWithComponent(
                SharedNurseNoteComponent,
                {
                    data: { patientPayload: this.payload.patient },
                    title: 'NURSE NOTE',
                },
                ModalSizeEnum.large
            );
        }
    }

    public onGetPrevFluidBalance() {
        if (this.hasPatient() === false) {
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.nurseService.onGetPatientPrevFluidBalance(this.payload.patient).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.CLERK_E_FOLDER);
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                }
            )
        );
    }

    private hasPatient(): boolean {
        if (this.payload?.patient?.patientId) {
            return true;
        } else {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
            return false;
        }
    }
}

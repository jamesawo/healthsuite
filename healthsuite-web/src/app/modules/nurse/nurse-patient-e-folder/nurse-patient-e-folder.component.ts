import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {
    CardNoteType,
    DatePayload,
    DateType,
    PatientCardNotePayload,
    ReportFileNameEnum,
    ValidationMessage,
} from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';
import {
    NursePatientEFolderSpecificsComponent
} from '@app/modules/nurse/nurse-patient-e-folder/nurse-patient-e-folder-specifics/nurse-patient-e-folder-specifics.component';

@Component({
    selector: 'app-nurse-patient-e-folder',
    templateUrl: './nurse-patient-e-folder.component.html',
    styleUrls: ['./nurse-patient-e-folder.component.css'],
})
export class NursePatientEFolderComponent implements OnInit, OnDestroy {
    @ViewChild('eFolderSpecificsComponent')
    public eFolderSpecificsComponent: NursePatientEFolderSpecificsComponent;
    @Input('patient')
    public patientPayload: PatientPayload;
    public payload: PatientCardNotePayload;
    public allType = CardNoteType.ALL;
    public specific = CardNoteType.SPECIFIC;
    public start = DateType.START;
    public end = DateType.END;

    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {
        this.onResetPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onResetPayload() {
        this.payload = new PatientCardNotePayload();
        if (this.patientPayload) {
            this.payload.patient = this.patientPayload;
        }
        this.payload.user = this.commonService.getCurrentUser();
        this.payload.location = this.commonService.getCurrentLocation();
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
        }
    }

    public onRecordTypeChange(type: CardNoteType) {
        if (type) {
            this.payload.recordType = type;
        }
    }

    public onDateSelected(datePayload: DatePayload, type: DateType) {
        if (type === this.start) {
            this.payload.startDate = datePayload;
        } else {
            this.payload.endDate = datePayload;
        }
    }

    public onPreviewCardNote() {
        const valid = this.onValidateBeforePreview();
        if (valid.status === false) {
            this.toast.error(valid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.nurseService.onPreviewPatientEFolder(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, `${ReportFileNameEnum.NURSE_PATIENT_FLUID_BALANCE}`);
                    this.commonService.onCloseModal();
                    },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.INTERNAL_SERVER_ERROR);
                }
            )
        );
    }

    private onValidateBeforePreview(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.payload.patient) {
            res.message = 'Patient is required <br>';
            res.status = false;
        }
        if (this.payload.recordType === this.specific) {
            this.payload.specificTypes = this.eFolderSpecificsComponent.onGetSelectedRecords();
            if (!this.payload.specificTypes.length) {
                res.message = 'Select at least 1 specific record type <br>';
                res.status = false;
            }
        }
        if (!this.payload.user) {
            this.payload.user = this.commonService.getCurrentUser();
        }
        return res;
    }
}

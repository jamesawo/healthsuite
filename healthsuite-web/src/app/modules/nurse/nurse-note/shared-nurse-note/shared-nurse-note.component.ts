import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {
    ModalSizeEnum,
    NurseNotePayload,
    SharedPayload,
    ValidationMessage,
} from '@app/shared/_payload';
import { NurseLabelModalComponent } from '@app/modules/common/nurse-label-modal/nurse-label-modal.component';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ModalPopupService, SeedDataService } from '@app/shared/_services';
import { NurseNoteService } from '@app/shared/_services/nurse/nurse-note.service';
import { Subscription } from 'rxjs';
import { NgSelectComponent } from '@ng-select/ng-select';
import {PatientTransferPayload} from '@app/shared/_payload/erm/patient-transfer.payload';

@Component({
    selector: 'app-shared-nurse-note',
    templateUrl: './shared-nurse-note.component.html',
    styleUrls: ['./shared-nurse-note.component.css'],
})
export class SharedNurseNoteComponent implements OnInit {
    data: { patientPayload: PatientPayload };
    @ViewChild('ngSelectComponent') public selectLabel: NgSelectComponent;
    @Input('payload') public payload: NurseNotePayload;
    @Output('submitted') public submitted: EventEmitter<void> = new EventEmitter<any>();
    public isSubmitting = false;
    public labelList: SharedPayload[] = [];
    public isModal = false;

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private seedDataService: SeedDataService,
        private noteService: NurseNoteService,
        private modalService: ModalPopupService
    ) {}

    ngOnInit(): void {
        if (!this.payload && this.data.patientPayload) {
            this.isModal = true;
            this.payload = new NurseNotePayload();
            this.payload.patient = this.data?.patientPayload;
            this.payload.nurse = this.commonService.getCurrentUser();
            this.payload.clinic = this.commonService.getCurrentLocation();
        }

        this.subscription.add(
            this.seedDataService.onGetAllNursingNoteLabel().subscribe((result) => {
                this.labelList = result.data;
            })
        );
    }

    public onSubmitNote() {
        this.isSubmitting = true;
        const isValid = this.onValidatePayload();
        if (isValid.status === false) {
            this.isSubmitting = false;
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.noteService.onCreate(this.payload).subscribe(
                (res) => {
                    this.isSubmitting = false;
                    this.spinner.hide().then();
                    this.toast.success(HmisConstants.LAST_ACTION_SUCCESS, 'SUBMITTED SUCCESSFULLY');
                    this.onResetPayload();
                    this.commonService.onCloseModal();
                },
                (error) => {
                    this.isSubmitting = false;
                    console.log(error);
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.VALIDATION_ERR);
                }
            )
        );
    }

    public onLabelSelected(label: SharedPayload) {
        if (label?.id) {
            if (!this.payload?.patient?.patientId) {
                this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
                this.selectLabel.clearModel();
                return;
            }
            this.payload.label = label;
        }
    }

    public onOpenLabelModal() {
        if (this.isModal === false) {
            this.modalService.openModalWithComponent(
                NurseLabelModalComponent,
                {
                    data: { uploadTypeEnum: null, requisition: null, nurseLabelList: this.labelList },
                    title: '+ NURSING NOTE LABEL',
                },
                ModalSizeEnum.small
            );
        }

    }

    private onResetPayload() {
        this.submitted.emit();
    }

    private onValidatePayload(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.payload?.patient?.patientId) {
            res.status = false;
            res.message += `Patient is Required <br>`;
        }
        if (!this.payload?.clinic?.id) {
            res.status = false;
            res.message += `Clinic Location is Required <br>`;
        }

        if (!this.payload?.nurse?.id) {
            res.status = false;
            res.message += `Nurse is Required <br>`;
        }

        if (!this.payload.label) {
            res.status = false;
            res.message += `Select Label<br>`;
        }

        if (!this.payload.note) {
            res.status = false;
            res.message += `Note is Required<br>`;
        }

        return res;
    }
}

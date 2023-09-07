import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { Subscription } from 'rxjs';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {NgxSpinnerService} from 'ngx-spinner';
import {ToastrService} from 'ngx-toastr';
import {CommonService} from '@app/shared/_services/common/common.service';
import {NurseService} from '@app/shared/_services/nurse/nurse.service';
import {PatientCardNotePayload, ReportFileNameEnum} from '@app/shared/_payload';
import {PatientSearchComponent} from '@app/modules/common/patient-search/patient-search.component';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {saveAs} from 'file-saver';

@Component({
    selector: 'app-anc-card-note',
    templateUrl: './anc-card-note.component.html',
    styleUrls: ['./anc-card-note.component.css'],
})
export class AncCardNoteComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    public payload: PatientCardNotePayload =  new PatientCardNotePayload();

    private subscription: Subscription = new Subscription();
    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private nurseService: NurseService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }


    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
        }
    }

    public onPreviewCardNote() {
        if (!this.payload.patient || !this.payload.patient.patientId) {
            this.toast.error('Patient is required', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.nurseService.onPreviewPatientAncCardNote(this.payload).subscribe(res => {
                this.spinner.hide().then();
                const file = new Blob([res.body], { type: 'application/pdf' });
                saveAs(file, ReportFileNameEnum.CLERK_E_FOLDER);
                this.onClearPayload();
            },error => {
                this.spinner.hide().then();
                this.toast.error(error.error.message, HmisConstants.INTERNAL_SERVER_ERROR);
            })
        );
    }

    private onClearPayload(): void {
        this.payload = new PatientCardNotePayload();
        this.patientSearchComponent.clearSearchField();
    }
}

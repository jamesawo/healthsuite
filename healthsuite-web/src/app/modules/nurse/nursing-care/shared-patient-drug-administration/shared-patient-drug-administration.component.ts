import { Component, Input, OnInit } from '@angular/core';
import {
    ClerkDrugItemsPayload,
    ClerkDrugRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';
import { DrugAdministerHistoryComponent } from '@app/modules/nurse/drug-administration/drug-administer-history/drug-administer-history.component';
import { ModalSizeEnum, PatientCardNotePayload } from '@app/shared/_payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { ModalPopupService } from '@app/shared/_services';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-shared-patient-drug-administration',
    templateUrl: './shared-patient-drug-administration.component.html',
    styleUrls: ['./shared-patient-drug-administration.component.css'],
})
export class SharedPatientDrugAdministrationComponent implements OnInit {
    public data: { patientPayload: PatientPayload };

    @Input('payload')
    public payload: PatientCardNotePayload;
    @Input('items')
    public drugRequest: ClerkDrugRequestPayload[] = [];

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private nurseService: NurseService,
        private modalService: ModalPopupService
    ) {}

    ngOnInit(): void {
        if (!this.payload?.patient?.patientId && !this.drugRequest.length && this.data?.patientPayload?.patientId) {
            const { patientPayload } = this.data;
            this.payload = new PatientCardNotePayload();
            this.payload.patient = patientPayload;
            this.payload.location = this.commonService.getCurrentLocation();
            this.payload.user = this.commonService.getCurrentUser();
            this.drugRequest = [];
            this.drugRequest =
                patientPayload?.drugRequestList?.length > 0 ? patientPayload?.drugRequestList : [];
        }
    }

    public onAdministerDrug(request: ClerkDrugItemsPayload) {
        this.modalService.openModalWithComponent(
            DrugAdministerHistoryComponent,
            {
                data: { drugRequest: request, patientPayload: this.payload.patient },
                title: 'ADMINISTER DRUG',
            },
            ModalSizeEnum.large
        );
    }
}

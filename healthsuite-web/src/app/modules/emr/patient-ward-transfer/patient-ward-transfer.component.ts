import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { BedPayload, DepartmentPayload, WardPayload } from '@app/modules/settings';
import { PatientTransferPayload } from '@app/shared/_payload/erm/patient-transfer.payload';
import { DatePayload, ValidationMessage } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { AdmissionService } from '@app/shared/_services/emr/admission.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { WardSearchComponent } from '@app/modules/common/ward-search/ward-search.component';
import { UserSearchComponent } from '@app/modules/common/user-search/user-search.component';
import { SharedDateComponent } from '@app/modules/common/shared-date/shared-date.component';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-patient-ward-transfer',
    templateUrl: './patient-ward-transfer.component.html',
    styleUrls: ['./patient-ward-transfer.component.css'],
})
export class PatientWardTransferComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    payload: PatientTransferPayload;

    constructor(
        private toast: ToastrService,
        private commonService: CommonService
    ) {}

    ngOnInit() {
        this.onResetPayload();
    }

    ngOnDestroy() {
    }

    onResetPayload(submitted?: boolean) {
        this.payload = new PatientTransferPayload();
        this.payload.user = this.commonService.getCurrentUser();
        this.payload.location = this.commonService.getCurrentLocation();
        this.patientSearchComponent?.clearSearchField();
    }


    onPatientSearchSelected(patient: PatientPayload) {
        if (patient) {
            if (patient.isOnAdmission === false) {
                this.toast.error('Patient is not on admission', HmisConstants.VALIDATION_ERR);
                return;
            }
            this.payload.patient = patient;
            this.payload.currentAdmission = patient.admission;
        }
    }

}

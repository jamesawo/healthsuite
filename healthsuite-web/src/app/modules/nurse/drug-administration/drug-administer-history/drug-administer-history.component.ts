import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ClerkDrugItemsPayload } from '@app/shared/_payload/clerking/clerk-request.payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { NurseService } from '@app/shared/_services/nurse/nurse.service';
import { Subscription } from 'rxjs';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-drug-administer-history',
    templateUrl: './drug-administer-history.component.html',
    styleUrls: ['./drug-administer-history.component.css'],
})
export class DrugAdministerHistoryComponent implements OnInit, OnDestroy {
    public data: { drugRequest: ClerkDrugItemsPayload, patientPayload: PatientPayload };

    private subscription: Subscription = new Subscription();

    constructor(
        private nurseGeneralService: NurseService,
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onCloseModal() {
        this.commonService.onCloseModal();
    }

    public getDrugDescription() {
        return `${this.data.drugRequest.drugRegister.brandName} ${this.data.drugRequest.drugRegister.genericName} `;
    }

    public onSubmit() {
        if (!this.data.drugRequest.nurseComment) {
            this.toast.error('Enter some comment first');
            return;
        }
        this.spinner.show().then();
        this.data.drugRequest.patientId = this.data.patientPayload.patientId;
        this.data.drugRequest.locationId = this.commonService.getCurrentLocation().id;
        this.data.drugRequest.userId = this.commonService.getCurrentUser().id;
        this.subscription.add(
            this.nurseGeneralService.onAdministerDrug(this.data.drugRequest).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                        this.data.drugRequest.isAdministered = true;
                        this.onCloseModal();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.FAILED_RESPONSE);
                    console.log(error);
                }
            )
        );
    }
}

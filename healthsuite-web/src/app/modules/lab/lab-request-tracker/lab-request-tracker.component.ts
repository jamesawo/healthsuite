import { Component, OnDestroy, OnInit } from '@angular/core';
import { ModalSizeEnum } from '@app/shared/_payload';
import { ModalPopupService } from '@app/shared/_services';
import { RequestStatusComponent } from '@app/modules/lab/lab-request-tracker/request-status/request-status.component';
import {
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    NewOrEditSampleEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';

@Component({
    selector: 'app-lab-request-tracker',
    templateUrl: './lab-request-tracker.component.html',
    styleUrls: ['./lab-request-tracker.component.css'],
})
export class LabRequestTrackerComponent implements OnInit, OnDestroy {
    public payload: LabSpecimenCollectionPayload;
    private subscription: Subscription = new Subscription();

    constructor(
        private modalService: ModalPopupService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onInitPayload() {
        this.payload = new LabSpecimenCollectionPayload();
        this.payload.newOrEditSampleEnum = NewOrEditSampleEnum.NEW;
        this.payload.searchByEnum = LabSearchByEnum.INVOICE_NUMBER;
    }

    public onViewLabStatus(data: any, testId: number, reqId: number) {
        this.spinner.show().then();
        this.subscription.add(
            this.labService.onTrackLabTest(testId, reqId).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.modalService.openModalWithComponent(
                            RequestStatusComponent,
                            {
                                data: {labTestTracker: res},
                                title: 'Lab Request Status',
                            },
                            ModalSizeEnum.large
                        );
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                }
            )
        );
    }

}

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {LabResultPrepDto, LabTestApprovePayload, LabVerificationViewEnum} from '@app/shared/_payload/lab/lab.payload';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {ToastrService} from 'ngx-toastr';
import {NgxSpinnerService} from 'ngx-spinner';
import {CommonService} from '@app/shared/_services/common/common.service';
import {LabSpecimenService} from '@app/shared/_services/lab/lab-specimen.service';
import {LabResultService} from '@app/shared/_services/lab/lab-result.service';
import {Subscription} from 'rxjs';

@Component({
    selector: 'app-lab-result-verification-shared',
    templateUrl: './lab-result-verification-shared.component.html',
    styleUrls: ['./lab-result-verification-shared.component.css'],
})
export class LabResultVerificationSharedComponent implements OnInit {
    @Input('props')
    public props: { viewType: LabVerificationViewEnum };

    @Output('onDecline')
    public onDecline: EventEmitter<any> = new EventEmitter<any>();

    @Output('onApprove')
    public onApprove: EventEmitter<any> = new EventEmitter<any>();

    @Input('payload')
    public payload: LabResultPrepDto;
    public pathologist = LabVerificationViewEnum.PATHOLOGIST;

    private subscription: Subscription = new Subscription();
    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService,
        private labResultService: LabResultService,
    ) {}

    ngOnInit(): void {}


    public onApproveTestResult() {
        if (!this.payload.singleTestRequestItemId) {
            this.toast.error('Cannot Find Result To Approve', HmisConstants.ERROR);
            return;
        }
        this.spinner.show().then();
        const approvePayload = new LabTestApprovePayload();
        approvePayload.approvedFrom = this.commonService.getCurrentLocation();
        approvePayload.approvedBy = this.commonService.getCurrentUser();
        approvePayload.testItemId = this.payload.singleTestRequestItemId;
        approvePayload.labNote = this.payload.approvalLabNote;

        if (this.payload.verificationTypeEnum === LabVerificationViewEnum.PATHOLOGIST) {
            approvePayload.viewType = LabVerificationViewEnum.PATHOLOGIST;
            approvePayload.pathologistComment = this.payload.pathologistComment;
        }
        this.onApproveLabResultTest(approvePayload);
        return;
    }

    public onApproveLabResultTest(approvePayload: LabTestApprovePayload) {
        this.subscription.add(
            this.labResultService.onApproveLabResult(approvePayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                        this.labResultService.listen.next(true);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE);
                    console.log(error);
                }
            )
        );
    }
}

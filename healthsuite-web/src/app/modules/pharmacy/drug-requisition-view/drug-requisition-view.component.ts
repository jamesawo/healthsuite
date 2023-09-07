import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { IModalPopup, ValidationMessage } from '@app/shared/_payload';
import {
    DrugRequisitionItemPayload,
    DrugRequisitionPayload,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ModalPopupService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { RequisitionService } from '@app/shared/_services/pharmacy/requisition.service';
import { ModalPopupComponent } from '@app/modules/others/modal-popup/modal-popup.component';

@Component({
    selector: 'app-drug-requisition-view',
    templateUrl: './drug-requisition-view.component.html',
    styleUrls: ['./drug-requisition-view.component.css'],
})
export class DrugRequisitionViewComponent implements OnInit, IModalPopup, OnDestroy {
    data: { requisition: DrugRequisitionPayload };

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private toast: ToastrService,
        private modalService: ModalPopupService,
        private requisitionService: RequisitionService,
        private spinner: NgxSpinnerService,
        @Inject(ModalPopupComponent) private parent: ModalPopupComponent
    ) {}

    ngOnInit(): void {
        this.data.requisition.operatingUser = this.commonService.getCurrentUser();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onGrantRequisition() {
        const message = this.onValidateBeforeGrant();
        if (message.status == false) {
            this.toast.error(message.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.requisitionService.onGrantRequisition(this.data.requisition).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    this.toast.success(
                        HmisConstants.LAST_ACTION_SUCCESS,
                        HmisConstants.SUCCESS_RESPONSE
                    );
                    if (result.body) {
                        this.data.requisition = result.body;
                        this.requisitionService.requisitionS.next(result.body);
                        this.parent.onCloseButtonClick();
                    }
                },
                (error) => {
                    console.log(error);
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.LAST_ACTION_FAILED);
                }
            )
        );
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onItemQtyChange(event: any, item: DrugRequisitionItemPayload, index: number) {
        const quantity = Number(event.target.value);
        if (quantity > item.requestingQuantity) {
            this.toast.error(
                `Issuing Qty Is Greater Than Requested Qty`,
                HmisConstants.VALIDATION_ERR
            );
            item.issuingQuantity = 0;
            return;
        }
        this.data.requisition.requisitionItems[index].issuingQuantity = quantity;
    }

    public onValidateBeforeGrant(): ValidationMessage {
        let response: ValidationMessage = { message: '', status: true };
        let payload = this.data.requisition;
        if (payload.requisitionItems.length < 1) {
            response.status = false;
            response.message += `No Items To Grant <br>`;
        }

        let hasZero: boolean = false;
        payload.requisitionItems.forEach((value) => {
            if (value.issuingQuantity == 0) {
                hasZero = true;
            }
        });

        if (hasZero) {
            response.status = false;
            response.message += `Invalid Requesting Qty Exist <br>`;
        }

        return response;
    }
}

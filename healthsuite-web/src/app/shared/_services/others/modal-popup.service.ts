import { Injectable } from '@angular/core';
import { BsModalService, BsModalRef, ModalOptions } from 'ngx-bootstrap/modal';
import { ModalPopupComponent } from '@app/modules/others/modal-popup/modal-popup.component';
import { FileUploadTypeEnum, ModalSizeEnum, SharedPayload } from '@app/shared/_payload';
import Swal, { SweetAlertOptions, SweetAlertResult } from 'sweetalert2';
import { DrugRequisitionPayload } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { IVitalTabData } from '@app/shared/_payload/clerking/clerking.payload';
import {PatientPayload, SchemePlan} from '@app/shared/_payload/erm/patient.payload';
import {ObPrevPregnancy} from '@app/shared/_payload/nurse/nurse-anc.payload';
import {UserPayload} from '@app/modules/settings/_payload/userPayload';
import {ClerkDrugItemsPayload} from '@app/shared/_payload/clerking/clerk-request.payload';
import {LabTrackerPayload} from '@app/shared/_payload/lab/lab.payload';

@Injectable({
    providedIn: 'root',
})
export class ModalPopupService {
    // public bsModalRef: BsModalRef;
    // parent body component
    protected modalComponent: any = ModalPopupComponent;

    constructor(private modalService: BsModalService) {}

    // creates a modal pop using the component passed as method argument, on modal body
    public openModalWithComponent(
        component: any,
        props: {
            data: {
                uploadTypeEnum?: FileUploadTypeEnum;
                requisition?: DrugRequisitionPayload;
                nurseLabelList?: SharedPayload[];
                vitalTabData?: IVitalTabData;
                patientPayload?: PatientPayload;
                pregnancyHistories?: ObPrevPregnancy[];
                user?: UserPayload;
                drugRequest?: ClerkDrugItemsPayload;
                labTestTracker?: LabTrackerPayload;
                process?: boolean,
                schemePlans?: SchemePlan[]
            };
            title: string;
        },
        size: ModalSizeEnum
    ) {
        const initialState: any = {
            data: props.data,
            title: props.title,
            component,
        };
        const config: ModalOptions = {
            initialState,
            backdrop: true,
            ignoreBackdropClick: true,
            keyboard: false,
        };
        this.modalService.show(this.modalComponent, Object.assign(config, { class: size }));
    }

    public onShowNotification(options: SweetAlertOptions): Promise<SweetAlertResult> {
        return Swal.fire(options);
    }

    public onCloseModal() {
        this.modalService.hide();
    }
}

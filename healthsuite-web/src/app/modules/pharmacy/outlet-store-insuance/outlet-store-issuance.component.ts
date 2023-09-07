import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import {
    DrugIssuancePayload,
    DrugRequisitionPayload,
    IssuanceTypeEnum,
    PharmacyLocationTypeEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { Subscription } from 'rxjs';
import { DrugRegisterService } from '@app/shared/_services/pharmacy/drug-register.service';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { DrugIssuanceService } from '@app/shared/_services/pharmacy/drug-issuance.service';
import { DatePayload, FileUploadTypeEnum, ModalSizeEnum } from '@app/shared/_payload';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ModalPopupService } from '@app/shared/_services';
import { DrugRequisitionViewComponent } from '@app/modules/pharmacy/drug-requisition-view/drug-requisition-view.component';
import { RequisitionService } from '@app/shared/_services/pharmacy/requisition.service';

@Component({
    selector: 'app-outlet-store-issuance',
    templateUrl: './outlet-store-issuance.component.html',
    styleUrls: ['./outlet-store-issuance.component.css'],
})
export class OutletStoreIssuanceComponent implements OnInit, OnDestroy, OnChanges {
    @Input('props') public props: { locationType: PharmacyLocationTypeEnum };
    public new: IssuanceTypeEnum = IssuanceTypeEnum.NEW;
    public requisition: IssuanceTypeEnum = IssuanceTypeEnum.REQUISITION;
    public payload: DrugIssuancePayload;
    public collection: DrugRequisitionPayload[] = [];
    public p: number = 1;

    private subscription: Subscription = new Subscription();
    constructor(
        private drugRegisterService: DrugRegisterService,
        private commonService: CommonService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private issuanceService: DrugIssuanceService,
        private modalService: ModalPopupService,
        private requisitionService: RequisitionService
    ) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION);
        if (isMatch) {
            this.onInitializeObj();
            this.subscription.add(
                this.requisitionService.requisition$.subscribe((value) => {
                    this.onObserveRequisition(value);
                })
            );
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.requisitionService.requisitionS.next(null);
    }

    ngOnChanges(changes: SimpleChanges) {}

    public onTypeChange(type: IssuanceTypeEnum) {}

    public onGetRequisitionByDateRange() {
        if (!this.payload.startDate.year && !this.payload.endDate.year) {
            this.toast.error(`Select Start/End Date`, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.issuanceService.getRequisitionsByDateRange(this.payload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    this.collection = [];
                    if (result.body) {
                        this.collection = result.body;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                }
            )
        );
    }

    public onDateSelected(value: DatePayload, type: 'start' | 'end') {
        if (value) {
            if (type == 'start') {
                this.payload.startDate = value;
            } else {
                this.payload.endDate = value;
            }
        }
    }

    public viewRequisition(item: DrugRequisitionPayload) {
        this.modalService.openModalWithComponent(
            DrugRequisitionViewComponent,
            {
                data: { uploadTypeEnum: null, requisition: item },
                title: 'View Requisition Details',
            },
            ModalSizeEnum.large
        );
    }

    private onInitializeObj() {
        this.payload = new DrugIssuancePayload();
        let dep = this.commonService.getCurrentLocation();
        this.payload.user = this.commonService.getCurrentUser();
        this.payload.outlet = dep;
    }

    private onObserveRequisition(payload: DrugRequisitionPayload) {
        if (payload && payload.id > 0) {
            for (let i = 0; i < this.collection.length; i++) {
                if (this.collection[i].id == payload.id) {
                    this.collection[i] = payload;
                }
            }
        }
    }
}

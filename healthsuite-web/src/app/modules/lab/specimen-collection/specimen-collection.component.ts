import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { GlobalSettingKeysEnum, SharedPayload } from '@app/shared/_payload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import {
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestItem,
    LabTestRequestPayload,
    NewOrEditSampleEnum,
} from '@app/shared/_payload/lab/lab.payload';
import {
    PatientBill,
    PaymentTypeForEnum,
    ReceiptPayload,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { DrugDispenseSearchByEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { GlobalSettingService } from '@app/shared/_services';
import { YesNoEnum } from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-specimen-collection',
    templateUrl: './specimen-collection.component.html',
    styleUrls: ['./specimen-collection.component.css'],
})
export class SpecimenCollectionComponent implements OnInit, OnDestroy, AfterViewInit {
    public newView = NewOrEditSampleEnum.NEW;
    public editView = NewOrEditSampleEnum.EDIT;

    public invoiceNumber = LabSearchByEnum.INVOICE_NUMBER;
    public patient = LabSearchByEnum.PATIENT;
    public payload: LabSpecimenCollectionPayload;
    public searchBySelected = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    public isEnableSpecimenAckDuringCollection: boolean;

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService,
        private globalSettingService: GlobalSettingService
    ) {}

    ngOnInit(): void {
        this.isEnableSpecimenAckDuringCollection = false;
        this.onInitPayload();
    }

    ngAfterViewInit() {
        setTimeout(() => {
            this.spinner.show().then();
            this.globalSettingService
                .getSettingValueByKey(GlobalSettingKeysEnum.ENABLE_SPECIMEN_ACK_DURING_COLLECTION)
                .toPromise()
                .then((r) => {
                    this.spinner.hide().then();
                    const value = r.body.data.value.toLowerCase();
                    const res = value === YesNoEnum.YES.toLowerCase();
                    this.isEnableSpecimenAckDuringCollection = res;
                    this.payload.isAcknowledgeSpecimen = res;
                }).catch(err => {
                this.spinner.hide().then();
                this.toast.error(HmisConstants.LAST_ACTION_FAILED, HmisConstants.FAILED_RESPONSE);
            });
        }, 0);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onInitPayload() {
        this.payload = new LabSpecimenCollectionPayload();
        this.payload.capturedBy = this.commonService.getCurrentUser();
        this.payload.capturedFrom = this.commonService.getCurrentLocation();
        this.payload.newOrEditSampleEnum = NewOrEditSampleEnum.NEW;
        this.payload.searchByEnum = LabSearchByEnum.INVOICE_NUMBER;
    }

    public onTypeChange(value: NewOrEditSampleEnum) {
        if (value) {
            this.payload.newOrEditSampleEnum = value;
        }
    }

    public onSpecimenSelected(specimen: SharedPayload, index: LabTestRequestItem) {
        if (specimen) {
            index.specimenDto = specimen;
        }
    }

    public onCollectionStatusChange(event: any, index: LabTestRequestItem) {
        index.specimenStatus = event.target.checked;
    }

    public onSpecimenAckChange(value: string, item: LabTestRequestItem) {
        if (value) {
            item.acknowledgement = value;
        }
    }

    public onSubmitSampleCollection() {
        if (this.didSelectAllSpecimen() === false) {
            this.toast.error('Select a Specimen for each item', HmisConstants.VALIDATION_ERR);
            return;
        }

        if (this.isEnableSpecimenAckDuringCollection === true) {
            if (this.didAcknowledgeAll() === false) {
                this.toast.error(
                    'Select a Acknowledgement for each item',
                    HmisConstants.VALIDATION_ERR
                );
                return;
            }
        }

        this.spinner.show().then();
        this.subscription.add(
            this.labService.onSaveSampleCollection(this.payload).subscribe(
                (re) => {
                    this.spinner.hide().then();
                    if (re.message) {
                        this.toast.success(re.message, HmisConstants.SUCCESS_RESPONSE.toUpperCase());
                        this.onInitPayload();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                    this.toast.error(error.error.message);
                }
            )
        );
    }

    private didSelectAllSpecimen(): boolean {
        let flag = true;
        this.payload.labBillTestRequest.testItems.forEach((r) => {
            if (!r.specimenDto.id) {
                flag = false;
            }
        });
        return flag;
    }

    private didAcknowledgeAll(): boolean {
        let flag = true;
        this.payload.labBillTestRequest.testItems.forEach((r) => {
            if (!r.acknowledgement) {
                flag = false;
            }
        });
        return flag;
    }
}

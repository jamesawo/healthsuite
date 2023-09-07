import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestPayload,
    NewOrEditSampleEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { DrugDispenseSearchByEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import {
    PatientBill,
    PaymentTypeForEnum,
    ReceiptPayload,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-lab-test-request-shared',
    templateUrl: './lab-test-request-shared.component.html',
    styleUrls: ['./lab-test-request-shared.component.css'],
})
export class LabTestRequestSharedComponent implements OnInit, OnDestroy {
    @Input('payload')
    public payload: LabSpecimenCollectionPayload;

    public invoiceNumber = LabSearchByEnum.INVOICE_NUMBER;
    public labNumber = LabSearchByEnum.LAB_NUMBER;
    public patient = LabSearchByEnum.PATIENT;
    public salesReceipt = LabSearchByEnum.RECEIPT_NUMBER;
    public searchBySelected = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    public serviceReceipt: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;
    public testRequestList: LabTestRequestPayload[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSearchByChange(value: LabSearchByEnum) {
        if (value) {
            this.payload.searchByEnum = value;
        }
    }

    public onDateSelected(event: any, type: any) {}

    public onSearchRequest() {
        console.log('button clicked');
    }

    public onPatientTestSelected(test: LabTestRequestPayload) {
        if (test) {
            this.payload.labBillTestRequest = test;
        }
    }

    public onReceiptSelected(receiptPayload: ReceiptPayload) {
        if (receiptPayload) {
            this.payload.patient = receiptPayload.patientBill.patientDetailDto;
            this.onSearchLabTestRequest(
                LabSearchByEnum.RECEIPT_NUMBER,
                receiptPayload.receiptNumber
            );
        }
    }

    public onPatientSelected(patientPayload: PatientPayload) {
        if (patientPayload) {
            this.payload.patient = patientPayload;
            this.onSearchLabTestRequest(LabSearchByEnum.PATIENT, patientPayload.patientNumber);
        }
    }

    public onSearchByBillNumberSelected(bill: PatientBill) {
        if (bill) {
            this.payload.patient = bill.patientDetailDto;
            this.onSearchLabTestRequest(LabSearchByEnum.INVOICE_NUMBER, bill.invoiceNumber);
        }
    }

    public onSearchLabTestRequest(searchBy: LabSearchByEnum, term: string) {
        this.spinner.show().then();
        this.subscription.add(
            this.labService.onSearchLabTestRequest(searchBy, term).subscribe(
                (res) => {
                    const testItems = res.testItems;
                    const sampleCollectionData = res.sampleCollectionData;
                    this.payload.otherInformation = sampleCollectionData?.otherInformation;
                    this.payload.provisionalDiagnosis = sampleCollectionData?.provisionalDiagnosis;
                    this.payload.clinicalSummary = sampleCollectionData?.clinicalSummary;

                    if (testItems.length === 1) {
                        this.payload.labBillTestRequest = testItems[0];
                        this.testRequestList = testItems;
                    }
                    this.testRequestList = testItems;
                    this.spinner.hide().then();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(
                        HmisConstants.FAILED_RESPONSE,
                        HmisConstants.INTERNAL_SERVER_ERROR
                    );
                    console.log(error);
                }
            )
        );
    }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {DatePayload, DateType, ReportFileNameEnum} from '@app/shared/_payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { EmrReportService } from '@app/shared/_services/reports/emr-report.service';
import {
    AdmissionSessionTypeEnum,
    InterimSearchPayload,
    SearchAdmissionPayload,
} from '@app/shared/_payload/reports/report-erm.payload';
import { Subscription } from 'rxjs';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {saveAs} from 'file-saver';

@Component({
    selector: 'app-patient-interim-invoice',
    templateUrl: './patient-interim-invoice.component.html',
    styleUrls: ['./patient-interim-invoice.component.css'],
})
export class PatientInterimInvoiceComponent implements OnInit, OnDestroy {
    public start = DateType.START;
    public end = DateType.END;
    public currentVisitType = AdmissionSessionTypeEnum.CURRENT;
    public allVisitType = AdmissionSessionTypeEnum.ALL;
    public previousVisitType = AdmissionSessionTypeEnum.PREVIOUS;
    public searchPayload: InterimSearchPayload = new InterimSearchPayload();
    public admissionSessions: SearchAdmissionPayload[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private emrReportService: EmrReportService
    ) {}

    ngOnInit(): void {
        this.searchPayload.startDate = this.commonService.getCurrentDate();
        this.searchPayload.endDate = this.commonService.getCurrentDate();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onTypeChange(type: AdmissionSessionTypeEnum) {
        if (type) {
            this.onResetSearchPayload();
            this.searchPayload.visitType = type;
        }
    }

    public onResetSearchPayload() {
        this.searchPayload = new InterimSearchPayload();
        this.searchPayload.startDate = this.commonService.getCurrentDate();
        this.searchPayload.endDate = this.commonService.getCurrentDate();
    }

    public onPatientSelected(patientPayload: PatientPayload) {
        if (patientPayload && patientPayload.patientId) {
            if (!patientPayload.isOnAdmission) {
                this.toast.error('PATIENT IS NOT ON ADMISSION', HmisConstants.VALIDATION_ERR);
                return;
            }
            this.searchPayload.patient = patientPayload;
            this.onSearchAdmissionSession();
        }
    }

    public onDateSelected(datePayload: DatePayload, type: DateType) {
        if (datePayload) {
            if (type && type === DateType.START) {
                this.searchPayload.startDate = datePayload;
            } else if (type && type === DateType.END) {
                this.searchPayload.endDate = datePayload;
            }
            this.onSearchAdmissionSession();
        }
    }

    public onSearchReport() {
        this.spinner.show().then();
        this.subscription.add(
            this.emrReportService.downloadPatientInterimReport(this.searchPayload).subscribe(res => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.EMR_PATIENT_INTERIM_INVOICE);
                    // this.onResetSearchPayload();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error('Failed To Download', HmisConstants.ERR_SERVER_ERROR);
                }
            )
        );
    }

    public onSearchAdmissionSession() {
        if (
            this.searchPayload.visitType === AdmissionSessionTypeEnum.PREVIOUS &&
            this.searchPayload.patient &&
            this.searchPayload.patient.patientId
        ) {
            this.spinner.show().then();
            this.subscription.add(
                this.emrReportService.onSearchAdmissionSession(this.searchPayload).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        this.admissionSessions = [];
                        this.admissionSessions = res;
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(HmisConstants.FAILED_RESPONSE);
                    }
                )
            );
        }
    }

    public onAdmissionSessionSelected(session: SearchAdmissionPayload) {
        if (session) {
            this.searchPayload.session = session;
            this.searchPayload.admissionCode = session.admissionNumber;
        }
    }
}

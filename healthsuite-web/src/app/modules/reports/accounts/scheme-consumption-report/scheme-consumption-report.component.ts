import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { DepartmentPayload, RevenueDepartmentPayload } from '@app/modules/settings';
import { PatientPayload, SchemeData } from '@app/shared/_payload/erm/patient.payload';
import { DatePayload, ReportFileNameEnum, ValidationMessage } from '@app/shared/_payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { AccReportService } from '@app/shared/_services/reports/acc-report.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';

enum SchemeConsumptionSearchByEnum {
    HMO = 'HMO',
    PATIENT = 'PATIENT',
    SERVICE_DEPARTMENT = 'SERVICE_DEPARTMENT',
    REVENUE_DEPARTMENT = 'REVENUE_DEPARTMENT',
}

enum SchemeConsumptionReportTypeEnum {
    SUMMARY = 'SUMMARY',
    DETAILED = 'DETAILED',
}

interface SchemeConsumptionPayload {
    startDate: DatePayload;
    endDate: DatePayload;
    patient: PatientPayload;
    schemeData: SchemeData;
    searchBy: SchemeConsumptionSearchByEnum;
    revenueDepartment: RevenueDepartmentPayload;
    serviceDepartment: DepartmentPayload;
    reportType: SchemeConsumptionReportTypeEnum;
    treatmentType: string;
    patientType: string;
}

@Component({
    selector: 'app-scheme-consumption-report',
    templateUrl: './scheme-consumption-report.component.html',
    styleUrls: ['./scheme-consumption-report.component.css'],
})
export class SchemeConsumptionReportComponent implements OnInit, OnDestroy {
    public patient = SchemeConsumptionSearchByEnum.PATIENT;
    public hmo = SchemeConsumptionSearchByEnum.HMO;
    public serviceDepartment = SchemeConsumptionSearchByEnum.SERVICE_DEPARTMENT;
    public revenueDepartment = SchemeConsumptionSearchByEnum.REVENUE_DEPARTMENT;
    public summaryType = SchemeConsumptionReportTypeEnum.SUMMARY;
    public detailedType = SchemeConsumptionReportTypeEnum.DETAILED;
    public payload: SchemeConsumptionPayload = {
        reportType: SchemeConsumptionReportTypeEnum.SUMMARY,
        patient: undefined,
        schemeData: undefined,
        searchBy: SchemeConsumptionSearchByEnum.HMO,
        revenueDepartment: undefined,
        serviceDepartment: undefined,
        startDate: undefined,
        endDate: undefined,
        treatmentType: undefined,
        patientType: undefined,
    };

    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private reportService: AccReportService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSearchByChange(value: SchemeConsumptionSearchByEnum) {
        if (value) {
            this.payload.searchBy = value;
        }
    }

    public onRevenueDepartmentSelected(revenue: RevenueDepartmentPayload) {
        if (revenue) {
            this.payload.revenueDepartment = revenue;
        }
    }

    public onServiceDepartmentSelected(service: DepartmentPayload) {
        if (service) {
            this.payload.serviceDepartment = service;
        }
    }

    public onSchemeSelected(scheme: SchemeData) {
        if (scheme) {
            this.payload.schemeData = scheme;
        }
    }

    public onPatientSelected(patientPayload: PatientPayload) {
        if (patientPayload) {
            this.payload.patient = patientPayload;
        }
    }

    public onDateSelected(date: DatePayload, type: 'start' | 'end') {
        setTimeout(() => {
            if (date && type) {
                if (type === 'start') {
                    this.payload.startDate = date;
                } else if (type === 'end') {
                    this.payload.endDate = date;
                }
            }
        }, 0);
    }

    public onSearchResult() {
        const validMessage = this.onValidateBeforeSearch();
        if (validMessage.status === false) {
            this.toast.error(validMessage.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.reportService.getSchemeConsumptionReport(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.SCHEME_CONSUMPTION_REPORT);
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.ERROR, HmisConstants.FAILED_RESPONSE);
                    console.log(error);
                }
            )
        );
    }

    private onValidateBeforeSearch(): ValidationMessage {
        const result: ValidationMessage = { status: true, message: '' };
        if (!this.payload.searchBy) {
            result.status = false;
            result.message += 'Search By is required <br>';
        } else {
            switch (this.payload.searchBy) {
                case SchemeConsumptionSearchByEnum.HMO:
                    if (!this.payload.schemeData || !this.payload.schemeData.id) {
                        result.status = false;
                        result.message += 'Select HMO to search by <br>';
                    }
                    break;
                case SchemeConsumptionSearchByEnum.PATIENT:
                    if (!this.payload.patient || !this.payload.patient.patientId) {
                        result.status = false;
                        result.message += 'Select Patient to search by <br>';
                    }
                    break;
                case SchemeConsumptionSearchByEnum.REVENUE_DEPARTMENT:
                    if (!this.payload.revenueDepartment || !this.payload.revenueDepartment.id) {
                        result.status = false;
                        result.message += 'Select Revenue Department to search by <br>';
                    }
                    break;
                case SchemeConsumptionSearchByEnum.SERVICE_DEPARTMENT:
                    if (!this.payload.serviceDepartment || !this.payload.serviceDepartment.id) {
                        result.status = false;
                        result.message += 'Select Service Department to search by <br>';
                    }
                    break;
            }
        }

        if (!this.payload.startDate) {
            result.status = false;
            result.message += 'Start Date is required <br>';
        }

        if (!this.payload.endDate) {
            result.status = false;
            result.message += 'End Date is required <br>';
        }
        return result;
    }
}

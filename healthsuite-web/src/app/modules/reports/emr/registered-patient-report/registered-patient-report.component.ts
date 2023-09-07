import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DepartmentPayload } from '@app/modules/settings';
import {
    DatePayload,
    DateType,
    GenderPayload,
    ReportFileNameEnum,
    ValidationMessage,
} from '@app/shared/_payload';
import {
    PageResultPayload,
    PatientRegisterPayload,
} from '@app/shared/_payload/reports/reports.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { EmrReportService } from '@app/shared/_services/reports/emr-report.service';
import { PatientPayload, PatientType } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';
import { ClinicDropdownComponent } from '@app/modules/common/clinic-dropdown/clinic-dropdown.component';
import { GenderDropdownComponent } from '@app/modules/common/gender-dropdown/gender-dropdown.component';
import { SharedDateComponent } from '@app/modules/common/shared-date/shared-date.component';

@Component({
    selector: 'app-registered-patient-report',
    templateUrl: './registered-patient-report.component.html',
    styleUrls: ['./registered-patient-report.component.css'],
})
export class RegisteredPatientReportComponent implements OnInit, OnDestroy {
    @ViewChild('clinicDropdownComponent') public clinicComponent: ClinicDropdownComponent;
    @ViewChild('genderDropdownComponent') public genderComponent: GenderDropdownComponent;
    @ViewChild('startDateComponent') public startDateComponent: SharedDateComponent;
    @ViewChild('endDateComponent') public endDateComponent: SharedDateComponent;

    payload: PatientRegisterPayload = new PatientRegisterPayload();
    data: PageResultPayload<PatientPayload> = new PageResultPayload<PatientPayload>();

    new = PatientType.NEW;
    old = PatientType.OLD;
    both = PatientType.BOTH;
    start = DateType.START;
    end = DateType.END;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private emrReportService: EmrReportService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.payload.user = this.commonService.getCurrentUser();
        this.payload.type = this.new;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onTypeChange(type: PatientType) {
        if (type) {
            this.payload.type = type;
        }
    }

    onWardSelected(department: DepartmentPayload) {
        if (department) {
            this.payload.department = department;
        }
    }

    onDateSelected(datePayload: DatePayload, type: DateType) {
        if (datePayload && type) {
            if (type === DateType.START) {
                this.payload.startDate = datePayload;
                return;
            }

            if (type === DateType.END) {
                this.payload.endDate = datePayload;
                return;
            }
        }
    }

    onSelectGender(gender: GenderPayload) {
        if (gender) {
            this.payload.gender = gender;
        }
    }

    onSearchRecord() {
        const isValid = this.onValidate();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.emrReportService.findPatientRegisterByDate(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    this.data = res;
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    onValidate(): ValidationMessage {
        const valid: ValidationMessage = { message: '', status: true };
        if (!this.payload?.startDate) {
            valid.message += 'Enter start date';
            valid.status = false;
        }
        if (!this.payload?.endDate) {
            valid.message += 'Enter end date';
            valid.status = false;
        }
        return valid;
    }

    pageChanged(page: number) {
        this.payload.page.pageNumber = page;
        this.onSearchRecord();
    }

    dateTransform(datePayload: DatePayload) {
        return this.commonService.transformToDate(datePayload);
    }

    onDownloadFile() {
        if (this.data.result.length === 0) {
            this.toast.info('Search For Record First!', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.emrReportService.downloadPatientRegisterReport(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.SERVICE_CHARGE_SHEET);
                    this.onResetForm();
                },
                (error) => {
                    console.log(error);
                    this.spinner.hide().then();
                    this.toast.error('Failed To Download', HmisConstants.ERR_SERVER_ERROR);
                }
            )
        );
    }

    onResetForm() {
        this.payload = new PatientRegisterPayload();
        this.data = new PageResultPayload<PatientPayload>();

        this.payload.type = PatientType.NEW;
        this.genderComponent.onClearField();
        this.clinicComponent.onClearField();
        this.startDateComponent.onSetDateFromDate();
        this.endDateComponent.onSetDateFromDate();
    }
}

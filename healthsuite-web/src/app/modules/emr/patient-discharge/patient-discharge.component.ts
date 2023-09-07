import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import {
    DischargePatientPayload,
    DischargeStatusConst,
    PatientPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { DatePayload, ReportFileNameEnum, ValidationMessage } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import {
    AdmissionSessionTypeEnum,
    InterimSearchPayload,
    SearchAdmissionPayload,
} from '@app/shared/_payload/reports/report-erm.payload';
import { environment } from '@environments/environment';
import { EmrReportService } from '@app/shared/_services/reports/emr-report.service';
import { saveAs } from 'file-saver';
import { AdmissionService } from '@app/shared/_services/emr/admission.service';
import { UserSearchComponent } from '@app/modules/common/user-search/user-search.component';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { DischargeStatusDropdownComponent } from '@app/modules/common/discharge-status-dropdown/discharge-status-dropdown.component';

@Component({
    selector: 'app-patient-discharge',
    templateUrl: './patient-discharge.component.html',
    styleUrls: ['./patient-discharge.component.css'],
})
export class PatientDischargeComponent implements OnInit, OnDestroy {
    @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    @ViewChild('dischargeStatusDropdownComponent')
    dischargeStatusDropdownComponent: DischargeStatusDropdownComponent;
    public payload: DischargePatientPayload = new DischargePatientPayload();
    public admission: SearchAdmissionPayload = new SearchAdmissionPayload();
    public currency = environment.currencySign;

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private emrReportService: EmrReportService,
        private admissionService: AdmissionService
    ) {}

    ngOnInit(): void {
        this.onResetPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onResetPayload() {
        this.payload = new DischargePatientPayload();
        this.payload.dischargedDate = this.commonService.getCurrentDate();
        this.payload.dischargedBy = this.commonService.getCurrentUser();
        this.payload.location = this.commonService.getCurrentLocation();
        // this.payload.dischargeStatus = DischargeStatusConst[0].value;
        this.payload.patient = new PatientPayload();
        this.admission = new SearchAdmissionPayload();
    }

    public onPatientSelected(patientPayload: PatientPayload) {
        if (patientPayload && patientPayload.patientId) {
            if (!patientPayload.isOnAdmission) {
                this.toast.error('PATIENT IS NOT ON ADMISSION', HmisConstants.VALIDATION_ERR);
                return;
            }
            this.searchPatientAdmissionDetails(patientPayload);
            this.payload.patient = patientPayload;
        }
    }

    public searchPatientAdmissionDetails(patient: PatientPayload) {
        this.spinner.show().then();
        const payload: InterimSearchPayload = new InterimSearchPayload();
        payload.visitType = AdmissionSessionTypeEnum.CURRENT;
        payload.patient = patient;

        this.subscription.add(
            this.emrReportService.onSearchAdmissionSession(payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.length) {
                        this.admission = res[0];
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                    this.toast.error(HmisConstants.INTERNAL_SERVER_ERROR);
                }
            )
        );
    }

    public onDischargePatient() {
        const isValid = this.onCheckBeforeDischarge();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.commonService
            .askAreYouSure('DISCHARGE PATIENT?', 'ARE YOU SURE', 'warning')
            .then((res) => {
                if (res.isConfirmed === true) {
                    this.spinner.show().then();
                    this.subscription.add(
                        this.admissionService.onDischargePatient(this.payload).subscribe(
                            (res) => {
                                this.spinner.hide().then();
                                const file = new Blob([res.body], { type: 'application/pdf' });
                                saveAs(file, ReportFileNameEnum.EMR_PATIENT_DISCHARGE_GATE_PASS);
                                this.onClearSearchFields();
                                this.onResetPayload();
                            },
                            (error) => {
                                this.spinner.hide().then();
                                this.toast.error(HmisConstants.FAILED_RESPONSE);
                            }
                        )
                    );
                }
            });
    }

    public onDateSelected(date: DatePayload) {
        if (date) {
            this.payload.dischargedDate = date;
        }
    }

    public onConsultantSelect(userPayload: UserPayload) {
        if (userPayload) {
            this.payload.consultant = userPayload;
        }
    }

    public onDischargeStatusSelected(status: any) {
        if (status) {
            this.payload.dischargeStatus = status;
        }
    }

    private onCheckBeforeDischarge(): ValidationMessage {
        const res: ValidationMessage = { status: true, message: '' };
        if (!this.payload.patient.patientId) {
            res.status = false;
            res.message += 'Patient is Required. <br>';
        }
        if (!this.payload.location.id) {
            this.payload.location = this.commonService.getCurrentLocation();
        }
        if (!this.payload.dischargedBy.id) {
            this.payload.dischargedBy = this.commonService.getCurrentUser();
        }
        if (!this.payload.dischargeStatus) {
            res.status = false;
            res.message += 'Discharge Status is Required. <br>';
        }
        if (!this.payload.dischargedDate) {
            res.status = false;
            res.message += 'Discharge Date is Required. <br>';
        }
        return res;
    }

    private onClearSearchFields(): void {
        this.userSearchComponent.onClearField();
        this.patientSearchComponent.clearSearchField();
        this.dischargeStatusDropdownComponent.onClearField();
    }
}

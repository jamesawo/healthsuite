import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    PatientAccountSummary,
    PatientPayload,
    PatientRevisitPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { DepartmentPayload } from '@app/modules/settings';
import { EmrService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import Swal from 'sweetalert2';
import { ResponsePayload } from '@app/shared/_payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { ClinicDropdownComponent } from '@app/modules/common/clinic-dropdown/clinic-dropdown.component';
import { error } from 'protractor';
import {environment} from '@environments/environment';

@Component({
    selector: 'app-patient-revisit',
    templateUrl: './patient-revisit.component.html',
    styleUrls: ['./patient-revisit.component.css'],
})
export class PatientRevisitComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent')
    public patientSearchComponent: PatientSearchComponent;
    @ViewChild('clinicDropdownComponent')
    public clinicDropdownComponent: ClinicDropdownComponent;
    public accountSummary: PatientAccountSummary = new PatientAccountSummary();
    public currencyCode = environment.currencySign;


    public selectedPatient: PatientPayload = new PatientPayload();
    public revisitPayload: PatientRevisitPayload = new PatientRevisitPayload();
    private subscription: Subscription = new Subscription();

    constructor(
        private toastService: ToastrService,
        private emrService: EmrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService
    ) {}

    ngOnInit() {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onRevisitPatient() {
        const isValid = this.isPayloadValid(this.revisitPayload);
        if (isValid.data === false) {
            this.toastService.error('Invalid Form Selection', HmisConstants.ERR_TITLE, {});
            return;
        }

        this.revisitPayload.revisitDate = this.commonService.getCurrentDate();
        this.revisitPayload.revisitType = 'NEW';
        this.revisitPayload.revisitFrom = this.commonService.getCurrentLocation();
        this.revisitPayload.revisitBy = this.commonService.getCurrentUser();

        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onRevisitPatient(this.revisitPayload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    if (result.status === 200) {
                        if (result.body.data) {
                            Swal.fire({
                                title: `Visit ID: ${result.body.data?.code}`,
                                icon: 'success',
                                allowOutsideClick: false,
                                confirmButtonColor: '#24ac09',
                                confirmButtonText: `<i class="fa fa-thumbs-up"></i> OK`,
                            }).then((r) => {
                                if (r.isConfirmed) {
                                    this.resetForm();
                                } else {
                                }
                            });
                        }
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toastService.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            if (patient.isOnAdmission === true) {
                this.toastService.error(
                    HmisConstants.PATIENT_ON_ADMISSION,
                    HmisConstants.ERR_TITLE,
                    {
                        tapToDismiss: true,
                        closeButton: true,
                        enableHtml: true,
                    }
                );
            } else if (patient.revisitStatus === true) {
                this.toastService.error(HmisConstants.PATIENT_IS_ACTIVE, HmisConstants.ERR_TITLE, {
                    tapToDismiss: true,
                    closeButton: true,
                    enableHtml: true,
                });
            } else {
                this.toastService.clear();
                this.revisitPayload.patientDetail = patient;
                this.onGetPatientAccountSummary(patient.patientId);
            }
        }
    }

    public onGetPatientAccountSummary(patientId: number) {
        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onGetPatientRevisitAccountSummary(patientId).subscribe(
                (res) => {
                    if (res) {
                        this.spinner.hide().then();
                        this.accountSummary = res;
                    }
                },
                (error) => {
                    console.log(error);
                    this.spinner.hide().then();
                    this.toastService.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onClinicSelected(clinic: DepartmentPayload) {
        if (clinic) {
            this.revisitPayload.clinic = clinic;
        }
    }

    protected isPayloadValid(payload: PatientRevisitPayload): ResponsePayload<boolean> {
        const response: ResponsePayload<boolean> = { message: '', data: true };
        if (!payload.clinic.id) {
            response.data = false;
            response.message = 'Kindly Select Clinic Before Revisiting';
        } else if (!payload.patientDetail.patientId) {
            response.data = false;
            response.message = 'Kindly Select Patient To Revisit';
        }
        return response;
    }

    private resetForm() {
        this.revisitPayload = new PatientRevisitPayload();
        this.patientSearchComponent.clearSearchField();
        this.clinicDropdownComponent.onClearField();
        this.accountSummary = new PatientAccountSummary();
    }
}

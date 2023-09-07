import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { AdmissionPayload, BedPayload, WardPayload } from '@app/modules/settings';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import Swal from 'sweetalert2';
import { Subscription } from 'rxjs';
import { DatePayload } from '@app/shared/_payload';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';
import {PatientSearchComponent} from '@app/modules/common/patient-search/patient-search.component';
import {UserSearchComponent} from '@app/modules/common/user-search/user-search.component';
import {WardSearchComponent} from '@app/modules/common/ward-search/ward-search.component';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {CommonService} from '@app/shared/_services/common/common.service';
import {AdmissionService} from '@app/shared/_services/emr/admission.service';

@Component({
    selector: 'app-patient-admission',
    templateUrl: './patient-admission.component.html',
    styleUrls: ['./patient-admission.component.css'],
})
export class PatientAdmissionComponent implements OnInit, OnDestroy {
    @ViewChild('dateComponent') public dateComponent: SharedDateComponent;
    @ViewChild('patientSearchComponent') public patientSearchComponent: PatientSearchComponent;
    @ViewChild('userSearchComponent') public userSearchComponent: UserSearchComponent;
    @ViewChild('wardSearchComponent') public wardSearchComponent: WardSearchComponent;
    public selectedPatient: PatientPayload = new PatientPayload();
    public bedsList: BedPayload[] = [];
    public isSubmit = false;
    public admissionFormPayload: AdmissionPayload = new AdmissionPayload();
    public subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private admissionService: AdmissionService,
        private commonService: CommonService
    ) {}

    ngOnInit() {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onAdmitPatient() {
        this.isSubmit = true;
        const valid = this.isValid(this.admissionFormPayload);
        if (valid.status === false) {
            this.toast.error(valid.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.admissionService.onAdmitPatient(this.admissionFormPayload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    Swal.fire({
                        title: `Admission ID: ${result.message}`,
                        icon: 'success',
                        allowOutsideClick: false,
                        confirmButtonColor: '#24ac09',
                        confirmButtonText: `<i class="fa fa-thumbs-up"></i> OK`,
                    }).then((r) => {
                        this.onResetPayload();
                    });
                },
                (error) => {
                    this.spinner.hide().then();
                    if (error.error.code === 500) {
                        this.toast.error('Something has gone wrong!', 'Please Contact Support! ');
                    } else {
                        this.toast.error(error.error.message, 'Patient Admission Failed ');
                    }
                }
            )
        );
    }

    public onResetPayload() {
        this.selectedPatient = new PatientPayload();
        this.admissionFormPayload = new AdmissionPayload();
        this.dateComponent.onSetDateFromDate();
        this.patientSearchComponent.clearSearchField();
        this.userSearchComponent.onClearField();
        this.wardSearchComponent.onClearPayload();
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient.isOnAdmission === true) {
            this.toast.error('Patient is already on admission', 'Error');
            this.selectedPatient = new PatientPayload();
            return;
        }
        this.toast.clear();
        this.selectedPatient = patient;
        this.admissionFormPayload.patientId = patient.patientId;
    }

    public onWardSelected(ward: WardPayload) {
        if (ward) {
            this.bedsList = [];
            if (ward?.beds?.length > 0) {
                this.bedsList = ward.beds.filter((value) => value.isOccupied === false);
            }
            this.admissionFormPayload.wardId = ward.id;
        }
    }

    public onConsultantSelect(user: UserPayload) {
        if (user && user.id) {
            this.admissionFormPayload.consultantId = user.id;
        }
    }

    public onDateSelected(value: DatePayload) {
        if (value) {
            this.admissionFormPayload.admissionDate = value;
        }
    }

    protected isValid(payload: AdmissionPayload): { status: boolean; message: string } {
        const checker: { status: boolean; message: string } = { status: true, message: '' };
        if (!payload.patientId) {
            checker.status = false;
            checker.message += 'Patient is required <br>';
        }
        if (!payload.wardId) {
            checker.status = false;
            checker.message += 'Ward is required <br>';
        }
        if (!payload?.admittedBy?.id) {
            this.admissionFormPayload.admittedBy = this.commonService.getCurrentUser();
        }
        if (!payload?.location?.id) {
            this.admissionFormPayload.location = this.commonService.getCurrentLocation();
        }
        return checker;
    }
}

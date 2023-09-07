import { Component, OnDestroy, OnInit } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { AdmissionPayload, BedPayload, WardPayload } from '@app/modules/settings';
import {DatePayload} from '@app/shared/_payload';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import Swal from 'sweetalert2';
import {ToastrService} from 'ngx-toastr';
import {NgxSpinnerService} from 'ngx-spinner';
import {AdmissionService} from '@app/shared/_services/emr/admission.service';
import {CommonService} from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-clerk-admission-request',
    templateUrl: './clerk-admission-request.component.html',
    styleUrls: ['./clerk-admission-request.component.css'],
})
export class ClerkAdmissionRequestComponent implements OnInit, OnDestroy {
    data: { patientPayload: PatientPayload };
    public bedsList: BedPayload[] = [];
    public payload: AdmissionPayload = new AdmissionPayload();

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private admissionService: AdmissionService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        this.payload.patient = this.data.patientPayload;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onWardSelected(ward: WardPayload) {
        if (ward) {
            this.bedsList = [];
            if (ward?.beds?.length > 0) {
                this.bedsList = ward.beds.filter((value) => value.isOccupied === false);
            }
            this.payload.wardId = ward.id;
        }
    }

    public onDateSelected(value: DatePayload) {
        if (value) {
            this.payload.admissionDate = value;
        }
    }

    public onAdmitPatient() {
        const isValid = this.isValid(this.payload);
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.commonService.askAreYouSure('ARE YOU SURE ?',
            'PROCESS PATIENT ADMISSION', 'warning').then(res => {
            if (res.isConfirmed === true) {
                this.spinner.show().then();
                this.subscription.add(
                    this.admissionService.onAdmitPatient(this.payload).subscribe(
                        (result) => {
                            this.spinner.hide().then();
                            Swal.fire({
                                title: `Admission ID: ${result.message}`,
                                icon: 'success',
                                allowOutsideClick: false,
                                confirmButtonColor: '#24ac09',
                                confirmButtonText: `<i class="fa fa-thumbs-up"></i> OK`,
                            }).then((r) => {
                                this.onCloseModal();
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
        });
    }

    public onCloseModal() {
        this.commonService.onCloseModal();
    }

    protected isValid(payload: AdmissionPayload): { status: boolean; message: string } {
        const checker: { status: boolean; message: string } = { status: true, message: '' };
        if (!payload.patientId) {
            const patient = this.data.patientPayload;
            this.payload.patient = patient;
            this.payload.patientId = patient.patientId;
        }
        if (!payload.wardId) {
            checker.status = false;
            checker.message += 'Ward is required <br>';
        }
        if (!payload?.admittedBy?.id) {
            const currentUser = this.commonService.getCurrentUser();
            this.payload.admittedBy = currentUser;
            this.payload.consultant = currentUser;
        }
        if (!payload?.location?.id) {
            this.payload.location = this.commonService.getCurrentLocation();
        }
        return checker;
    }

}

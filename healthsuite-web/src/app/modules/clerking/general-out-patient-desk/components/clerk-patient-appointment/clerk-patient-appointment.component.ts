import {Component, OnDestroy, OnInit} from '@angular/core';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import {DatePayload, SharedPayload, ValidationMessage} from '@app/shared/_payload';
import {DepartmentPayload} from '@app/modules/settings';
import {AppointmentBooking, PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {Subscription} from 'rxjs';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {HttpErrorResponse} from '@angular/common/http';
import {EmrService} from '@app/shared/_services';
import {ToastrService} from 'ngx-toastr';
import {NgxSpinnerService} from 'ngx-spinner';
import {CommonService} from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-clerk-patient-appointment',
    templateUrl: './clerk-patient-appointment.component.html',
    styleUrls: ['./clerk-patient-appointment.component.css'],
})
export class ClerkPatientAppointmentComponent implements OnInit, OnDestroy {
    data: { patientPayload: PatientPayload };
    public payload: AppointmentBooking = new AppointmentBooking();

    private subscription: Subscription = new Subscription();
    constructor(
        private emrService: EmrService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        this.payload.patientId = this.data.patientPayload.patientId;
        this.payload.bookedById = this.commonService.getCurrentUser().id;
        this.payload.locationId = this.commonService.getCurrentLocation().id;
    }

    ngOnDestroy() {}

    public onDoctorSelected(doctor: UserPayload) {
        if (doctor) {
            this.payload.consultant = doctor;
            this.payload.consultantId = doctor.id;
        }
    }

    public onSpecialityUnitSelected(sharedPayload: SharedPayload) {
        if (sharedPayload) {
            this.payload.specialityUnit = sharedPayload;
            this.payload.specialityId =  sharedPayload.id;
        }
    }

    public onClinicSelected(departmentPayload: DepartmentPayload) {
        if (departmentPayload) {
            this.payload.clinic = departmentPayload;
            this.payload.clinicId = departmentPayload.id;
        }
    }

    public onDateSelected(datePayload: DatePayload) {
        if (datePayload) {
            this.payload.dateTime = datePayload;
        }
    }

    public onCreateAppointment() {
        const isValid = this.validatePayload(this.payload);
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.ERR_TITLE, {
                enableHtml: true,
            });
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.emrService.patientAppointmentBooking(this.payload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    this.toast.success(
                        'Appointment Booked Successful. ',
                        HmisConstants.SUCCESS_RESPONSE
                    );
                    this.onCloseModal();
                },
                (error: HttpErrorResponse) => {
                        this.toast.error(HmisConstants.LAST_ACTION_FAILED, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    private onCloseModal() {
        this.commonService.onCloseModal();
    }

    protected validatePayload(payload: AppointmentBooking): ValidationMessage {
        let status = true;
        let message = '';
        if (!payload.consultantId) {
            status = false;
            message += 'Select Consultant <br>';
        }

        if (!payload.specialityId) {
            status = false;
            message += 'Select Speciality <br>';
        }

        if (!payload.clinicId) {
            status = false;
            message += 'Select Clinic <br>';
        }

        if (!payload.dateTime) {
            status = false;
            message += 'Choose a valid date & Time';
        }

        if (!payload.patientId) {
            this.payload.patientId = this.data.patientPayload.patientId;
        }

        return { status, message };
    }
}

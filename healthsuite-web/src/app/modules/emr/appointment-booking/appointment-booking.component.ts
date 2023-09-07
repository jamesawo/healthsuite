import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
    AppointmentBooking,
    AppointmentTypeEnum,
    PatientPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DatePayload, SharedPayload, ValidationMessage } from '@app/shared/_payload';
import { DepartmentPayload } from '@app/modules/settings';
import { EmrService, ModalPopupService } from '@app/shared/_services';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { LocationService } from '@app/shared/_services/settings/location.service';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { HttpErrorResponse } from '@angular/common/http';
import {UserSearchComponent} from '@app/modules/common/user-search/user-search.component';
import {SpecialitySearchComponent} from '@app/modules/common/speciality-search/speciality-search.component';
import {ClinicDropdownComponent} from '@app/modules/common/clinic-dropdown/clinic-dropdown.component';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';
import {PatientSearchComponent} from '@app/modules/common/patient-search/patient-search.component';

@Component({
    selector: 'app-appointment-booking',
    templateUrl: './appointment-booking.component.html',
    styleUrls: ['./appointment-booking.component.css'],
})
export class AppointmentBookingComponent implements OnInit, OnDestroy {
    @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
    @ViewChild('specialitySearchComponent') specialitySearchComponent: SpecialitySearchComponent;
    @ViewChild('clinicDropdownComponent') clinicDropdownComponent: ClinicDropdownComponent;
    @ViewChild('dateComponent') dateComponent: SharedDateComponent;
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    public newAppointment = AppointmentTypeEnum.NEW;
    public editAppointment = AppointmentTypeEnum.EDIT;
    public patientPayload: PatientPayload = new PatientPayload();
    public selectedType: AppointmentTypeEnum;
    public time: any;
    public payload: AppointmentBooking = new AppointmentBooking();
    public isFetchingAppointments = false;
    public patientAppointments: AppointmentBooking[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private emrService: EmrService,
        private toastrService: ToastrService,
        private spinnerService: NgxSpinnerService,
        private authService: AuthService,
        private locationService: LocationService,
        private modalService: ModalPopupService
    ) {}

    ngOnInit() {
        this.selectedType = this.newAppointment;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onAppointmentTypeChange(type: AppointmentTypeEnum) {
        this.payload = new AppointmentBooking();
        this.patientPayload = new PatientPayload();
        this.isFetchingAppointments = false;
        this.patientAppointments = [];
        this.selectedType = type;
    }

    public onCreateAppointment() {
        const isValid = this.validatePayload(this.payload);
        if (isValid.status === false) {
            this.toastrService.error(isValid.message, HmisConstants.ERR_TITLE, {
                enableHtml: true,
            });
            return;
        }
        this.payload.bookedById = this.authService.currentUserValue.userId;
        this.payload.locationId = this.locationService.locationSetting.id;

        this.spinnerService.show().then();
        this.subscription.add(
            this.emrService.patientAppointmentBooking(this.payload).subscribe(
                (result) => {
                    this.spinnerService.hide().then();
                    this.toastrService.success(
                        'Appointment Booked Successful. ',
                        HmisConstants.SUCCESS_RESPONSE
                    );
                    this.onClearForm();
                },
                (error: HttpErrorResponse) => {
                    const errorMessages = this.getMessageFromErrors(
                        error.error.errors,
                        HmisConstants.VALIDATION_ERR
                    );
                    this.spinnerService.hide().then();
                    if (error.status === 400) {
                        this.toastrService.error(errorMessages, error.error.message);
                    } else {
                        this.toastrService.error(error.error.message, HmisConstants.ERR_TITLE);
                    }
                }
            )
        );
    }

    public onPatientSelect(patient: PatientPayload) {
        if (patient) {
            this.patientPayload = patient;
            const patientId = patient.patientId;
            this.payload.patientId = patientId;
            if (this.selectedType === AppointmentTypeEnum.EDIT) {
                this.onFindPatientAppointment(patientId);
            }
        }
    }

    protected onFindPatientAppointment(patientId: number) {
        this.isFetchingAppointments = true;
        this.subscription.add(
            this.emrService.findAllPatientAppointment({ patientId }).subscribe(
                (res) => {
                    this.patientAppointments = res.body;
                    this.isFetchingAppointments = false;
                },
                (error) => {
                    this.toastrService.error('Could not get appointment', HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onConsultantSelected(consultant: UserPayload) {
        if (consultant) {
            this.payload.consultantId = consultant.id;
        }
    }

    public onSpecialitySelect(speciality: SharedPayload) {
        if (speciality) {
            this.payload.specialityId = speciality.id;
        }
    }

    public onClinicSelect(clinic: DepartmentPayload) {
        if (clinic) {
            this.payload.clinicId = clinic.id;
        }
    }

    public onDateTimeSelected(dateTime: DatePayload) {
        if (dateTime) {
            this.payload.dateTime = dateTime;
        }
    }

    public onTransformDate(date: DatePayload) {
        const { year, month, day }: any = date;
        return new Date(year, month, day);
    }

    public onCancelAppointment(appointmentId: number) {
        this.modalService
            .onShowNotification({
                icon: 'error',
                title: 'Cancel Appointment?',
                html: 'Are you sure you want to cancel this appointment?',
                confirmButtonText: 'Yes',
                confirmButtonColor: '#447AF8',
                cancelButtonText: 'No!',
                showCancelButton: true,
                cancelButtonColor: '#DD3C45',
                allowOutsideClick: false,
                backdrop: true,
            })
            .then((value) => {
                if (value.isConfirmed === true) {
                    // cancel appointment
                    this.spinnerService.show().then();
                    this.subscription.add(
                        this.emrService.onCancelAppointment({ appointmentId }).subscribe(
                            (res) => {
                                this.spinnerService.hide().then();
                                if (res.body.data === true) {
                                    this.removeItemFromList(appointmentId);
                                    this.toastrService.success(
                                        'Cancelled Successfully',
                                        HmisConstants.SUCCESS_RESPONSE
                                    );
                                }
                            },
                            (error) => {
                                this.spinnerService.hide().then();
                                this.toastrService.error(
                                    error.error.message,
                                    HmisConstants.ERR_TITLE
                                );
                            }
                        )
                    );
                }
            });
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

        return { status, message };
    }

    protected getMessageFromErrors(errors: string[], er: string): string {
        let message = er ? er : '';
        if (errors && errors.length) {
            errors.forEach((error) => {
                message += `${error} <br>`;
            });
        }
        return message;
    }

    private removeItemFromList(appointmentId: number) {
        const index = this.patientAppointments.findIndex((d) => d.id === appointmentId);
        this.patientAppointments.splice(index, 1); // remove element from array
    }

    private onClearForm() {
        this.payload = new AppointmentBooking();
        this.userSearchComponent.onClearField();
        this.specialitySearchComponent.onClearField();
        this.clinicDropdownComponent.onClearField();
        this.dateComponent.onClearDateField();
        this.patientSearchComponent.clearSearchField();
        this.patientPayload = new PatientPayload();
    }
}

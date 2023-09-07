import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { AntenatalBookingPayload, SharedPayload, ValidationMessage } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseAncBookingService } from '@app/shared/_services/nurse/nurse-anc-booking.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { UserSearchComponent } from '@app/modules/common/user-search/user-search.component';
import { SpecialitySearchComponent } from '@app/modules/common/speciality-search/speciality-search.component';
import { ClinicDropdownComponent } from '@app/modules/common/clinic-dropdown/clinic-dropdown.component';

@Component({
    selector: 'app-antenatal-booking',
    templateUrl: './antenatal-booking.component.html',
    styleUrls: ['./antenatal-booking.component.css'],
})
export class AntenatalBookingComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientComponent: PatientSearchComponent;
    @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
    @ViewChild('specialitySearchComponent') specialitySearchComponent: SpecialitySearchComponent;
    @ViewChild('clinicDropdownComponent') clinicDropDownComponent: ClinicDropdownComponent;
    newAppointment: any = 'new';
    editAppointment: any = 'edit';
    subscription: Subscription = new Subscription();
    payload: AntenatalBookingPayload;
    selectedType: 'new' | 'edit' = 'new';
    constructor(
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private bookingService: NurseAncBookingService
    ) {}

    ngOnInit(): void {
        let isMatch = this.commonService.isLocationMatch(LocationConstants.CLINIC_LOCATION);
        if (isMatch) {
            this.onInitializePayload();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onAppointmentTypeChange(type: 'new' | 'edit') {
        this.selectedType = type;
    }

    onPatientSelect(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
        }
    }

    onConsultantSelected(consultant: UserPayload) {
        if (consultant) {
            this.payload.caseConsultant = consultant;
        }
    }

    onSpecialitySelected(specialityUnit: SharedPayload) {
        if (specialityUnit) {
            this.payload.specialityUnit = specialityUnit;
        }
    }

    onClinicSelected(clinic: DepartmentPayload) {
        if (clinic) {
            this.payload.clinic = clinic;
        }
    }

    onSubmitBooking() {
        let isValid = this.onValidateBeforeSave();
        if (isValid.status == false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.bookingService.onCreateBooking(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.toast.success(
                            HmisConstants.SUCCESS_RESPONSE,
                            HmisConstants.LAST_ACTION_SUCCESS
                        );
                        this.onResetPayload();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    onInitializePayload() {
        this.payload = new AntenatalBookingPayload();
        this.payload.bookedFrom = this.commonService.getCurrentLocation();
        this.payload.bookedBy = this.commonService.getCurrentUser();
    }

    onResetPayload() {
        this.onInitializePayload();
        this.clinicDropDownComponent.onClearField();
        this.patientComponent.clearSearchField();
        this.specialitySearchComponent.onClearField();
        this.userSearchComponent.onClearField();
    }

    onValidateBeforeSave(): ValidationMessage {
        let result: ValidationMessage = { message: '', status: true };
        if (!this.payload?.patient?.patientId) {
            result.status = false;
            result.message += `Patient is required <br>`;
        }

        if (!this.payload?.clinic?.id) {
            result.status = false;
            result.message += `Clinic is required <br>`;
        }
        return result;
    }
}

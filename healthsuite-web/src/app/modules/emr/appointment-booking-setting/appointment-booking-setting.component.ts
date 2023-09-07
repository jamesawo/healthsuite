import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { EmrService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { AppointmentBookingSetup } from '@app/shared/_payload/erm/patient.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { SharedPayload } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-appointment-booking-setting',
    templateUrl: './appointment-booking-setting.component.html',
    styleUrls: ['./appointment-booking-setting.component.css'],
})
export class AppointmentBookingSettingComponent implements OnInit, OnDestroy {
    public setUpPayload: AppointmentBookingSetup = new AppointmentBookingSetup();
    private subscription: Subscription = new Subscription();

    constructor(
        private emrService: EmrService,
        private spinnerService: NgxSpinnerService,
        private toastService: ToastrService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onSaveSetup() {
        const validPayload = this.isValidPayload(this.setUpPayload);
        if (validPayload.status === false) {
            this.toastService.error(validPayload.message, 'Form Error!', { enableHtml: true });
            return;
        }
        this.spinnerService.show().then();
        this.subscription.add(
            this.emrService.appointmentBookingSetup(this.setUpPayload).subscribe(
                (result) => {
                    this.spinnerService.hide().then();
                    this.toastService.success(HmisConstants.SUCCESS_RESPONSE, 'Success');
                },
                (error: HttpErrorResponse) => {
                    this.spinnerService.hide().then();
                    this.toastService.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onSelectStaff(staff: UserPayload) {
        if (staff) {
            this.setUpPayload.consultantId = staff.id;
        }
    }

    public onSpecialitySelected(payload: SharedPayload) {
        if (payload) {
            this.setUpPayload.specialityUnitId = payload.id;
        }
    }

    protected isValidPayload(
        payload: AppointmentBookingSetup
    ): { message: string; status: boolean } {
        let flag = true;
        let message = '';
        if (!payload.staffLimit || payload.staffLimit < 1) {
            flag = false;
            message += 'Add Staff Limit. <br>';
        }

        if (!payload.specialityUnitId) {
            flag = false;
            message += 'Select Speciality Unit. <br>';
        }

        if (!payload.consultantId) {
            flag = false;
            message += 'Select Staff. <br>';
        }
        return { message, status: flag };
    }
}

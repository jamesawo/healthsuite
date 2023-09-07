import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserManagerService } from '@app/shared/_services/settings/user-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ChangePasswordPayload, ValidationMessage } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { Subscription } from 'rxjs';
import {AuthService} from '@app/shared/_services/auth/auth.service';

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html',
    styleUrls: ['./change-password.component.css'],
})
export class ChangePasswordComponent implements OnInit, OnDestroy {
    payload: ChangePasswordPayload;
    oldToggle = true;
    newToggle = true;
    confirmToggle = true;

    private subscription: Subscription = new Subscription();
    constructor(
        private userManagerService: UserManagerService,
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.payload = new ChangePasswordPayload();
        this.payload.userId = this.commonService.getCurrentUser().id;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onChangePassword() {
        this.commonService
            .askAreYouSure('ARE YOU SURE YOU WANT TO CHANGE PASSWORD?', 'WARNING !!!', 'warning')
            .then((res) => {
                if (res && res.isConfirmed === true) {
                    this.onRequestPasswordChange();
                }
            });
    }

    onRequestPasswordChange() {
        const isValid = this.onValidPayload();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.userManagerService.onChangeUserPassword(this.payload).subscribe(
                (res) => {
                    if (res === true) {
                        this.toast.success('Password Changed Successfully.', HmisConstants.SUCCESS_RESPONSE);
                        this.commonService.onCloseModal();
                        this.authService.logout();
                    }
                    this.spinner.hide().then();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    onValidPayload(): ValidationMessage {
        const resp: ValidationMessage = { message: '', status: true };
        if (!this.payload.newPassword) {
            resp.status = false;
            resp.message += 'New Password Is Required <br>';
        }
        if (!this.payload.oldPassword) {
            resp.status = false;
            resp.message += 'Old Password Is Required <br>';
        }
        if (!this.payload.confirmPassword) {
            resp.status = false;
            resp.message += 'Confirm Password Is Required <br>';
        }
        if (!this.payload.userId) {
            this.payload.userId = this.commonService.getCurrentUser().id;
        }

        if (this.payload.newPassword && this.payload.confirmPassword) {
            const isMatch = this.payload.newPassword === this.payload.confirmPassword;
            if (isMatch === false) {
                resp.status = false;
                resp.message += 'New Password & Confirm Password Don\'t Match <br>';
            }
        }
        return resp;
    }

    onToggleInputType(input: any, icon: any): any {
        this.commonService.onToggleInputType(input, icon);
    }
}

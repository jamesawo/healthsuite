import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { PasswordResetService } from '@app/shared/_services/settings/password-reset.service';
import { NotificationService } from '@app/shared/_services';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UserSearchComponent } from '@app/modules/common/user-search/user-search.component';

@Component({
    selector: 'app-password-reset',
    templateUrl: './password-reset.component.html',
    styleUrls: ['./password-reset.component.css'],
})
export class PasswordResetComponent implements OnInit, OnDestroy {
    @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
    user: UserPayload = undefined;
    subscription: Subscription = new Subscription();
    isSubmit = false;
    isNotMatch: boolean;
    passwordResetForm: FormGroup;


    constructor(private service: PasswordResetService, private notification: NotificationService) {}

    get form() {
        return this.passwordResetForm.controls;
    }

    ngOnInit() {
        this.onSetForm();
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onSetForm() {
        this.passwordResetForm = new FormGroup({
            password: new FormControl('', [Validators.required, Validators.minLength(6)]),
            confirmPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
        });
    }

    onSubmit() {
        this.isSubmit = true;
        if (this.passwordResetForm.invalid) {
            return;
        }
        const password = this.passwordResetForm.get('password').value;
        const confirmPassword = this.passwordResetForm.get('confirmPassword').value;

        if (password.toLowerCase() !== confirmPassword.toLowerCase()) {
            this.isNotMatch = true;
            return;
        }
        this.notification.useSpinner('show');
        this.user.password = password;
        this.user.confirmPassword = confirmPassword;
        this.isSubmit = false;
        this.subscription = this.service.onResetPassword(this.user).subscribe(
            (result) => {
                this.notification.useSpinner('hide');
                if (result.httpStatusCode === 200) {
                    this.notification.showToast({
                        type: 'success',
                        message: 'Password Reset Successful',
                    });
                } else {
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Error Occurred, Contact Support.',
                });
            }
        );
    }

    onUserSelected(user: UserPayload) {
        if (user) {
            this.user = user;
        }
    }

    onClearField() {
        this.onSetForm();
        this.userSearchComponent.onClearField();
    }
}

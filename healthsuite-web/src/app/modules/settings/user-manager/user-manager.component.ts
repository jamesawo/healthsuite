import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UserManagerService } from '@app/shared/_services/settings/user-manager.service';
import {ModalPopupService, NotificationService, SeedDataService} from '@app/shared/_services';
import { DepartmentPayload } from '@app/modules/settings';
import { Subscription } from 'rxjs';
import {DatePayload, ModalSizeEnum, SharedPayload, ValidationMessage} from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';
import {CommonService} from '@app/shared/_services/common/common.service';
import {UserUpdateModalComponent} from '@app/modules/settings/user-manager/user-update-modal/user-update-modal.component';

@Component({
    selector: 'app-user-manager',
    templateUrl: './user-manager.component.html',
    styleUrls: ['./user-manager.component.css'],
})
export class UserManagerComponent implements OnInit, OnDestroy {
    @ViewChild('dateComponent') dateComponent: SharedDateComponent;
    user: UserPayload;
    usersList: UserPayload[];
    subscriptions = new Subscription();
    departments: DepartmentPayload[];
    roles: SharedPayload[];
    @ViewChild('f') form: any;
    isSubmitted = false;
    now: Date = new Date();
    minDate: NgbDateStruct;
    userRole = { id: null, name: '' };
    page = 1;
    collectionSize;
    pageSize = 10;
    todayDate = new Date();
    passwordEyeToggle = true;
    confirmPasswordEyeToggle = true;

    constructor(
        private service: UserManagerService,
        private notification: NotificationService,
        private seedService: SeedDataService,
        private toast: ToastrService,
        private commonService: CommonService,
        private modalService: ModalPopupService
    ) {}

    ngOnInit() {
        this.minDate = {
            year: this.now.getFullYear(),
            month: this.now.getMonth() + 1,
            day: this.now.getDate() - 1,
        };
        this.user = new UserPayload();
        this.subscriptions.add(
            this.seedService.getAllDepartment().subscribe((departments) => {
                this.departments = departments.data;
            })
        );
        this.subscriptions.add(
            this.seedService.getAllRole().subscribe((roles) => {
                this.roles = roles.data;
            })
        );
        this.subscriptions.add(
            this.service.onGetAllUser().subscribe((users) => {
                this.usersList = users.data;
                this.collectionSize = users.data.length;
            })
        );
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
    }

    onCreateUser() {
        this.isSubmitted = true;
        const isValid = this.validateUserBeforeCreate();
        if (isValid.status === false) {
            this.isSubmitted = false;
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.user.role.push(this.userRole);
        this.notification.useSpinner('show');

        this.subscriptions.add(
            this.service.onCreateUser(this.user).subscribe(
                (result) => {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: result.httpStatusCode,
                        message: result.message,
                    });
                    this.onAddNewUser(result.data);
                },
                (error) => {
                    console.log(error);
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: 'Something has gone wrong, contact support',
                    });
                }
            )
        );
    }

    validateUserBeforeCreate(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.user.expiryDate) {
            res.status = false;
            res.message += 'User expiry date is required <br>';
        }
        if (!this.user.userName) {
            res.status = false;
            res.message += 'Username is required <br>';
        }
        if (!this.user.department.id) {
            res.status = false;
            res.message += 'Select department. <br>';
        }
        if (!this.user.password) {
            res.status = false;
            res.message += 'Provide password <br>';
        }
        if (!this.user.confirmPassword) {
            res.status = false;
            res.message += 'Provide confirm password <br>';
        }
        if (!this.user.phone) {
            res.status = false;
            res.message += 'Phone number is required <br>';
        }
        if (!this.userRole.id) {
            res.status = false;
            res.message += 'Select a role <br>';
        }

        return res;
    }

    displayDate(date: DatePayload) {
        if (date) {
            return this.commonService.transformToDate(date);
        }
    }

    onAddNewUser(user: UserPayload) {
        if (user.id) {
            this.usersList.push(user);
        }
        this.form.reset();
        this.isSubmitted = false;
        this.dateComponent.onClearDateField();
    }

    onExpiryDateSelected(date: DatePayload) {
        if (date) {
            this.user.expiryDate = date;
        }
    }

    onToggleInputTypeEye(password: any, passwordEye: any) {
        this.commonService.onToggleInputType(password, passwordEye);
    }

    onUpdateUserDetail(userPayload: UserPayload) {
        if (userPayload && userPayload.id) {
            this.modalService.openModalWithComponent(
                UserUpdateModalComponent,
                {
                    data: {
                        user: userPayload
                    },
                    title: 'UPDATE USER DETAIL'
                },
                ModalSizeEnum.large
            );
        }
    }

}

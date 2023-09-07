import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserPayload, UserUpdatePayload} from '@app/modules/settings/_payload/userPayload';
import { DatePayload, RolePayload } from '@app/shared/_payload';
import { DepartmentPayload } from '@app/modules/settings';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import {NgxSpinnerService} from 'ngx-spinner';
import {Subscription} from 'rxjs';
import {UserManagerService} from '@app/shared/_services/settings/user-manager.service';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-user-update-modal',
    templateUrl: './user-update-modal.component.html',
    styleUrls: ['./user-update-modal.component.css'],
})
export class UserUpdateModalComponent implements OnInit, OnDestroy {
    public data: { user: UserPayload };

    private subscription: Subscription = new Subscription();
    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private emrService: UserManagerService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onDateSelected(date: DatePayload) {
        if (date) {
            this.data.user.expiryDate = date;
        }
    }

    public onDepartmentSelected(department: DepartmentPayload) {
        if (department) {
            this.data.user.department = department;
        }
    }

    public onRoleSelected(role: RolePayload) {
        if (role) {
            this.data.user.role[0] = role;
        }
    }

    public onUpdateUserDetail() {
        const {id, department, role, lastName, otherNames, phone, expiryDate} = this.data.user;
        const userUpdate = new UserUpdatePayload();
        userUpdate.userId = id;
        userUpdate.department = department;
        userUpdate.role = role[0];
        userUpdate.otherNames = otherNames;
        userUpdate.lastName = lastName;
        userUpdate.phoneNumber = phone;
        userUpdate.accExpiryDate = expiryDate;
        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onUpdateUserDetails(userUpdate).subscribe(res => {
                this.spinner.hide().then();
                if (res.message) {
                    this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                    this.onCloseModal();
                }
            }, error => {
                this.spinner.hide().then();
                console.log(error);
                this.toast.error(error.error.message, HmisConstants.FAILED_RESPONSE);
            })
        );
    }

    public onCloseModal() {
        this.commonService.onCloseModal();
    }
}

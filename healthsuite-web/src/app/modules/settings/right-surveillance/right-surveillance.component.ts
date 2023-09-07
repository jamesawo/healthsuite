import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { UserManagerService } from '@app/shared/_services/settings/user-manager.service';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { PermissionPayload, RolePayload } from '@app/shared/_payload';
import { SeedDataService } from '@app/shared/_services';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { RoleManagerService } from '@app/shared/_services/settings/role-manager.service';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-right-surveillance',
    templateUrl: './right-surveillance.component.html',
    styleUrls: ['./right-surveillance.component.css'],
})
export class RightSurveillanceComponent implements OnInit, OnDestroy {
    user: UserPayload;
    permission: any;
    roles: RolePayload[] = [];
    tempUser: UserPayload = new UserPayload();
    selectedRoleId = undefined;
    permissions: PermissionPayload[] = [];

    dropDownSettings: IDropdownSettings = {
        singleSelection: false,
        idField: 'id',
        textField: 'name',
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        itemsShowLimit: 1,
        allowSearchFilter: true,
    };

    private subscription = new Subscription();

    constructor(
        private userService: UserManagerService,
        private seedService: SeedDataService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private roleManagerService: RoleManagerService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        this.user = new UserPayload();
        this.subscription.add(
            this.seedService.getAllRole().subscribe((roles) => {
                this.roles = roles.data;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onViewRights() {
        if (!this.tempUser.id) {
            this.toast.error('Search For User First!', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.userService.onGetUserRole(this.tempUser.id).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.role) {
                        this.user = res;
                        this.selectedRoleId = res.role[0].id;
                        this.permissions = res.role[0].permissions;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    onUserSelected(userPayload: UserPayload) {
        if (userPayload) {
            this.tempUser = userPayload;
        }
    }

    onRoleSelected(roleId: number, user: UserPayload) {
        this.spinner.show().then();
        this.subscription.add(
            this.roleManagerService.onGetRolePermissions(roleId).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        user.role = [res];
                        this.permissions = res.permissions;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                }
            )
        );
    }

    onUpdateUserRole(user: UserPayload) {
        this.commonService
            .askAreYouSure('Are You Sure?', 'CHANGE USER ROLE?', 'warning')
            .then((ans) => {
                if (ans.isConfirmed === true) {
                    this.spinner.show().then();
                    this.subscription.add(
                        this.userService
                            .onUpdateUserRole({ userId: user.id, roleId: user.role[0].id })
                            .subscribe(
                                (res) => {
                                    this.spinner.hide().then();
                                    if (res === true) {
                                        this.toast.success(
                                            HmisConstants.LAST_ACTION_SUCCESS,
                                            HmisConstants.UPDATED
                                        );
                                    } else {
                                        this.toast.error(HmisConstants.LAST_ACTION_FAILED, HmisConstants.ERR_TITLE);
                                    }
                                },
                                (error) => {
                                    this.spinner.hide().then();
                                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                                }
                            )
                    );
                }
            });
    }
}

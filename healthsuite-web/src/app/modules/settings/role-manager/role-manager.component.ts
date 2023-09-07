import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { PermissionPayload, RoleModulePayload, RolePayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { RoleManagerService } from '@app/shared/_services/settings/role-manager.service';
import { NotificationService } from '@app/shared/_services';

@Component({
    selector: 'app-role-manager',
    templateUrl: './role-manager.component.html',
    styleUrls: ['./role-manager.component.css'],
    encapsulation: ViewEncapsulation.None,
})
export class RoleManagerComponent implements OnInit, OnDestroy {
    roles: RolePayload[];
    modules: PermissionPayload[];
    dropDownSettings: IDropdownSettings = {
        singleSelection: true,
        idField: 'id',
        textField: 'name',
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        itemsShowLimit: 3,
        allowSearchFilter: true,
    };
    dropDownSettings2: IDropdownSettings = {
        singleSelection: false,
        idField: 'id',
        textField: 'description',
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        itemsShowLimit: 2,
        allowSearchFilter: true,
    };
    subscription: Subscription;
    selectedRole = new RolePayload();
    role = [];
    permission: PermissionPayload[] = [];
    isSubmitted = false;

    constructor(private service: RoleManagerService, private notification: NotificationService) {}

    ngOnInit() {
        this.subscription = this.service.onGetRolesAndPermissions().subscribe((result) => {
            this.roles = result.data.roles;
            this.modules = result.data.permissions;
        });
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onSelectRole(item: any): void {
        this.role[0].name = item.name;
        if (item.id) {
            this.roles.forEach((role) => {
                if (role.id === item.id) {
                    this.permission = role.permissions;
                }
            });
        }
    }

    onDeSelectRole() {
        this.selectedRole.id = null;
        this.selectedRole.name = '';
    }

    onUpdateRole() {
        if (this.role.length < 1) {
            this.notification.showToast({ type: 'error', message: 'Select Role' });
            return;
        } else if (this.role.length > 0 && this.permission.length < 1) {
            this.notification.showToast({ type: 'error', message: 'Select Modules' });
            return;
        }

        const roleToUpdate = new RolePayload();
        roleToUpdate.id = this.role[0].id;
        roleToUpdate.name = this.role[0].name;
        roleToUpdate.permissions = this.permission;

        this.notification.useSpinner('show');
        this.subscription = this.service.onUpdateRole(roleToUpdate).subscribe(
            (result) => {
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: result.httpStatusCode,
                    message: result.message,
                });
                this.ngOnInit();
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Network Failure, Contact Support',
                });
            }
        );
    }
    // todo create a separate component for drop down list and reuse the component
}

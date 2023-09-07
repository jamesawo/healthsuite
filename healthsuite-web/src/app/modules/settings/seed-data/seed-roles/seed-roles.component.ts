import { Component, OnDestroy, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-roles',
    templateUrl: './seed-roles.component.html',
    styleUrls: ['./seed-roles.component.css'],
})
export class SeedRolesComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    roleList: SharedPayload[];
    role: SharedPayload;
    subscription: Subscription;
    roleForm: FormGroup;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.onSetForm();
        this.subscription = this.service
            .getAllRole()
            .subscribe((result) => (this.roleList = result.data));
        this.role = { name: '', id: null, description: '' };
    }

    get form() {
        return this.roleForm.controls;
    }

    onSetForm() {
        this.roleForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
            description: new FormControl('', []),
        });
    }

    createRole() {
        this.isSubmitted = true;
        if (this.roleForm.invalid) {
            return;
        }
        this.role.name = this.roleForm.get('name').value;
        this.role.description = this.roleForm.get('description').value;

        if (this.isRoleExist(this.role)) {
            this.notification.showToast({
                type: 'error',
                message: 'Role With This Name Already Exist',
            });
            return;
        }
        this.notification.useSpinner('show');
        this.subscription = this.service.createRole(this.role).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.addRole(this.role);
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Role Created Successfully.',
                    });
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            },
            (error) => {
                console.log(error);
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something is not right, Contact Support',
                });
            }
        );
    }

    isRoleExist(role: SharedPayload): boolean {
        let flag = false;
        this.roleList.forEach((roleInList) => {
            if (roleInList.name.toLowerCase() === role.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    addRole(role: SharedPayload): void {
        this.role = {name: '', id: null};
        this.roleList.push(role);
        this.roleForm.reset();
        this.isSubmitted = false;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

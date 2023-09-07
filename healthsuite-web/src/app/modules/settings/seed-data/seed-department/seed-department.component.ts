import { Component, OnDestroy, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { DepartmentPayload } from '@app/modules/settings';

@Component({
    selector: 'app-seed-department',
    templateUrl: './seed-department.component.html',
    styleUrls: ['./seed-department.component.css'],
})
export class SeedDepartmentComponent implements OnInit, OnDestroy {
    departmentCategories: SharedPayload[];
    isSubmitted = false;
    collection: DepartmentPayload[] = [];
    department: DepartmentPayload;
    subscriptions = new Subscription();
    departmentForm: FormGroup;
    // upload
    selectedFiles: FileList;
    currentFile: File;
    progress = 0;
    message = '';
    page = 1;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.subscriptions.add(
            this.service.getAllDepartmentCategories().subscribe((result) => {
                this.departmentCategories = result.data;
            })
        );

        this.subscriptions.add(
            this.service.getAllDepartment().subscribe((result) => {
                this.collection = result.data;
            })
        );

        this.departmentForm = new FormGroup({
            departmentName: new FormControl('', [Validators.required]),
            departmentCategory: new FormControl('', [Validators.required]),
        });

        this.department = new DepartmentPayload();
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
    }

    get form() {
        return this.departmentForm.controls;
    }

    onCreateDepartment() {
        this.isSubmitted = true;

        if (this.departmentForm.invalid) {
            return;
        }

        this.department.name = this.departmentForm.get('departmentName').value;
        this.department.departmentCategory.id = this.departmentForm.get('departmentCategory').value;

        if (this.isExist(this.department)) {
            this.notification.showToast({ type: 'error', message: 'Name Already Exist' });
            return;
        }

        this.notification.useSpinner('show');
        this.department.departmentCategory = { id: this.department.departmentCategory.id };
        this.subscriptions.add(
            this.service.createDepartment(this.department).subscribe(
                (result) => {
                    if (result.httpStatusCode === 200) {
                        this.notification.useSpinner('hide');
                        this.notification.showToast({
                            type: 'success',
                            message: 'Created Department Successfully.',
                        });
                        this.onAddDepartmentToDepartmentList(result.data);
                    } else {
                        this.notification.useSpinner('hide');
                        this.notification.showToast({ type: 'error', message: result.message });
                    }
                },
                (error) => {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: `Something isn't right, Contact support.`,
                    });
                }
            )
        );
    }

    isExist(department: SharedPayload): boolean {
        let flag = false;
        this.collection.forEach((departmentInList) => {
            if (departmentInList.name.toLowerCase() === department.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    onSelectFile(event) {
        this.selectedFiles = event.target.files;
    }

    onUploadFile() {
        this.progress = 0;
        this.currentFile = this.selectedFiles.item(0);
        this.service.onUploadDepartmentFile(this.currentFile).subscribe((result) => {
            console.log(result);
        });
        this.selectedFiles = undefined;
    }

    onAddDepartmentToDepartmentList(department: DepartmentPayload): void {
        this.collection.push(department);
        this.departmentForm.reset();
        this.isSubmitted = false;
    }

    onUpdateDepartment(department: DepartmentPayload) {
        // console.log(department); todo:: add modal to update department name only
    }
}

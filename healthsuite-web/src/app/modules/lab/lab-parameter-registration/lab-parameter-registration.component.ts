import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { LabParameterService } from '@app/shared/_services/lab/lab-parameter.service';
import { LabParameterPayload } from '@app/shared/_payload/lab/lab.payload';
import { Subscription } from 'rxjs';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { CommonService } from '@app/shared/_services/common/common.service';
import { error } from 'protractor';
import Swal from 'sweetalert2';
import { find } from 'rxjs/operators';

@Component({
    selector: 'app-lab-parameter-registration',
    templateUrl: './lab-parameter-registration.component.html',
    styleUrls: ['./lab-parameter-registration.component.css'],
})
export class LabParameterRegistrationComponent implements OnInit, OnDestroy {
    public p = 1;
    tempList: LabParameterPayload[] = [];
    deleteTempList: LabParameterPayload[] = [];
    paramList: LabParameterPayload[] = [];
    param = new LabParameterPayload();
    title = '';

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private paramService: LabParameterService,
        private commonService: CommonService
    ) {}

    ngOnInit(): void {
        setTimeout(() => {
            this.onGetAllParameter();
        }, 0);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onGetAllParameter() {
        this.spinner.show().then();
        this.subscription.add(
            this.paramService.getAll().subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.length > 0) {
                        this.paramList = res;
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    onAddParameter(title: string) {
        if (!title) {
            this.toast.error('Enter Parameter Title', HmisConstants.VALIDATION_ERR);
            return;
        }

        if (!this.isDuplicate(title)) {
            this.spinner.show().then();
            const newLabParam = new LabParameterPayload(title);
            this.tempList.push(newLabParam);

            this.subscription.add(
                this.paramService.createMany(this.tempList).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        this.toast.success(HmisConstants.SUCCESS_RESPONSE, 'Parameter Added');
                        this.paramList.unshift(newLabParam);
                        this.onResetPayload();
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                    }
                )
            );
        } else {
            this.toast.warning(HmisConstants.POSSIBLE_DUPLICATE, HmisConstants.VALIDATION_ERR);
        }
    }

    onResetPayload() {
        this.param = new LabParameterPayload();
        this.tempList = [];
        this.title = '';
    }

    onToggleCheckAllItems(event: any) {}

    onRemoveAllCheckedItems() {
        this.deleteTempList = [];
        if (this.isAnyChecked()) {
            this.commonService
                .askAreYouSure(
                    'Are sure you want to delete parameter?',
                    'Warning, Deletion!',
                    'warning'
                )
                .then((r) => {
                    if (r.isConfirmed) {
                        this.spinner.show().then();
                        this.subscription.add(
                            this.paramService.removeMany(this.deleteTempList).subscribe(
                                (res) => {
                                    this.spinner.hide().then();
                                    if (res) {
                                        this.removeDeletedItems();
                                    }
                                },
                                (error) => {
                                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                                    this.spinner.hide().then();
                                }
                            )
                        );
                    }
                });
        }
    }

    removeDeletedItems() {
        this.deleteTempList.forEach((item) => {
            const findIdx = this.paramList.findIndex((value) => value.title === item.title);
            this.paramList.splice(findIdx, 1);
        });
    }

    isAnyChecked(): boolean {
        let flag = false;
        if (this.paramList.length > 0) {
            for (const labParameterPayload of this.paramList) {
                if (labParameterPayload.checked) {
                    flag = true;
                    this.deleteTempList.push(labParameterPayload);
                }
            }
        }
        return flag;
    }

    isDuplicate(param: string): boolean {
        return !!this.paramList.find((value) => value.title === param);
    }

    onParamCheckChange(event: any, param: LabParameterPayload) {
        param.checked = event.target.checked;
    }
}

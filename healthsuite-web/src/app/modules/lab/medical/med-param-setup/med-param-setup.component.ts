import { Component, OnDestroy, OnInit } from '@angular/core';
import { ParamRangeEnum } from '@app/shared/_payload/lab/lab.payload';
import {
    LabDepartmentTypeEnum,
    LabParameterSetupItemPayload,
    LabParameterSetupPayload,
    LabParamRangePayload,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { LabParamSetupService } from '@app/shared/_services/lab/lab-param-setup.service';
import { Subscription } from 'rxjs';
import { ValidationMessage } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-med-param-setup',
    templateUrl: './med-param-setup.component.html',
    styleUrls: ['./med-param-setup.component.css'],
})
export class MedParamSetupComponent implements OnInit, OnDestroy {
    public parameterSetup = ParamRangeEnum.PARAM;
    public rangeSetup = ParamRangeEnum.RANGE;
    public payload: LabParameterSetupPayload;
    public rangePayload: LabParamRangePayload;
    public chemicalLab = LabDepartmentTypeEnum.CHEMICAL;
    public haematology = LabDepartmentTypeEnum.HAEMATOLOGY;
    public anatomical = LabDepartmentTypeEnum.ANATOMICAL;
    public microbiology = LabDepartmentTypeEnum.MICROBIOLOGY;
    public general = LabDepartmentTypeEnum.GENERAL;
    public selectedLabView = undefined;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private labParamSetupService: LabParamSetupService
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onInitPayload(reset?: boolean) {
        const currentUser = this.commonService.getCurrentUser();
        const currentLocation = this.commonService.getCurrentLocation();
        this.payload = new LabParameterSetupPayload();
        this.payload.capturedBy = currentUser;
        this.payload.capturedFrom = currentLocation;

        this.rangePayload = new LabParamRangePayload();
        this.rangePayload.capturedFrom = currentLocation;
        this.rangePayload.capturedBy = currentUser;
    }

    public onLabViewSelected(type: LabDepartmentTypeEnum) {
        if (type) {
            this.onInitPayload();
            this.selectedLabView = type;
            this.payload.departmentTypeEnum = type;
        } else {
            this.onInitPayload();
            this.selectedLabView = undefined;
        }
    }

    public onRemoveRangeItem = (index: number): void => {
        this.rangePayload.rangeItems.splice(index, 1);
    };

    public onViewTypeChange(rangeEnum: ParamRangeEnum) {
        if (rangeEnum) {
            this.payload.viewType = rangeEnum;
        }
    }

    public onSaveEntries(list: LabParameterSetupItemPayload[]) {
        this.payload.parameterSetupItems = list;
        const validate = this.onValidateBeforeSave();
        if (validate.status === false) {
            this.toast.error(validate.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.labParamSetupService.saveParameterSetup(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                        this.onInitPayload();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.FAILED_RESPONSE);
                    console.log(error);
                }
            )
        );
    }

    public onSaveRangeEntries(entries: any) {
        const isValid = this.onValidateRangePayload();
        if (isValid.status === false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.labParamSetupService.onSaveRangeSetup(this.rangePayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                        this.onInitPayload();
                        // this.onViewTypeChange(ParamRangeEnum.PARAM);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.INTERNAL_SERVER_ERROR, HmisConstants.ERROR);
                    console.log(error);
                }
            )
        );
    }

    private onValidateRangePayload(): ValidationMessage {
        const res: ValidationMessage = { status: true, message: '' };
        if (!this.rangePayload?.rangeItems?.length) {
            res.status = false;
            res.message = 'Enter Range Items <br>';
        }
        if (!this.rangePayload.test || !this.rangePayload.test.id) {
            res.status = false;
            res.message = 'Select Lab Test <br>';
        }
        if (
            !this.rangePayload.labParameterSetupItem ||
            !this.rangePayload.labParameterSetupItem.id
        ) {
            res.status = false;
            res.message = 'Select Parameter <br>';
        }

        return res;
    }

    private onValidateBeforeSave(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };

        if (!this.payload.test || !this.payload.test.id) {
            res.status = false;
            res.message += 'Select Lab Test. <br>';
        }

        if (!this.payload.specimen || !this.payload.specimen.id) {
            res.status = false;
            res.message += 'Select Specimen. <br>';
        }

        if (!this.payload.parameterSetupItems.length) {
            res.status = false;
            res.message += 'Add Parameter Items <br>';
        }

        return res;
    }
}

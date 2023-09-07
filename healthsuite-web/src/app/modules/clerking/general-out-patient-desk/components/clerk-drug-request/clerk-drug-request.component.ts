import {Component, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import { DepartmentPayload } from '@app/modules/settings';
import {
    drugAdministrationRoute, drugFrequencyData,
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { fadeInOnEnterAnimation, fadeOutOnLeaveAnimation } from 'angular-animations';
import { DepartmentCategoryEnum } from '@app/shared/_payload';
import { DrugSearchComponent } from '@app/modules/common/drug-search/drug-search.component';
import { DrugSearchByDepartmentComponent } from '@app/modules/common/drug-search-by-department/drug-search-by-department.component';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';
import {
    ClerkDoctorRequestPayload,
    ClerkDrugItemsPayload,
    ClerkDrugRequestPayload
} from '@app/shared/_payload/clerking/clerk-request.payload';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {Subscription} from 'rxjs';
import {NgxSpinnerService} from 'ngx-spinner';

@Component({
    selector: 'app-clerk-drug-request',
    templateUrl: './clerk-drug-request.component.html',
    styleUrls: ['./clerk-drug-request.component.css'],
    animations: [fadeInOnEnterAnimation(), fadeOutOnLeaveAnimation()],
})
export class ClerkDrugRequestComponent implements OnInit, OnChanges, OnDestroy {
    public data: { patientPayload: PatientPayload; process: boolean };
    @ViewChild('drugSearchComponent')
    public drugSearchComponent: DrugSearchComponent;
    @ViewChild('drugSearchByDepartmentComponent') drugSearchByDep: DrugSearchByDepartmentComponent;
    public col = '12';
    public drugSearchProp: {
        searchTerm: DrugSearchTermEnum;
        showLabel?: boolean;
        loadIssOutletStockCount?: boolean;
        issuingOutletId?: number;
        showIssOutletStockCount?: boolean;
        excludeZeroItems?: boolean;
        outletId: number;
    };
    public departmentSearchProps: {
        showLabel: boolean;
        loadByCategory?: boolean;
        category?: DepartmentCategoryEnum;
    };
    public payload: ClerkDrugRequestPayload;

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private clerkRequestService: ClerkRequestService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {
        if (!this.data.patientPayload) {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
            this.data.patientPayload = new PatientPayload();
        }
        this.departmentSearchProps = {
            showLabel: false,
            loadByCategory: true,
            category: DepartmentCategoryEnum.PHARMACY,
        };
        this.drugSearchProp = {
            searchTerm: DrugSearchTermEnum.GENERIC_OR_BRAND_NAME,
            showLabel: false,
            loadIssOutletStockCount: false,
            issuingOutletId: 0,
            showIssOutletStockCount: false,
            excludeZeroItems: false,
            outletId: 0,
        };
        this.payload = new ClerkDrugRequestPayload();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {}

    onDrugSelected(drug: DrugRegisterPayload) {
        if (drug) {
            this.onAddBillItem(drug);
        }
    }

    onAddBillItem(drugPayload: DrugRegisterPayload) {
        const hasDuplicate = this.isHasDuplicate(drugPayload);
        if (hasDuplicate === false) {
            const billItem = new ClerkDrugItemsPayload();
            billItem.drugRegister = drugPayload;
            billItem.dosage = 1;
            billItem.days = 1;
            billItem.adminRoute = drugAdministrationRoute[0].value;
            billItem.frequency = drugFrequencyData[0].value;
            this.payload.drugItems.push(billItem);
        }
    }

    isHasDuplicate(payload: DrugRegisterPayload) {
        let flag = false;
        this.payload.drugItems.forEach((item) => {
            if (item.drugRegister.id === payload.id) {
                flag = true;
            }
        });
        return flag;
    }

    isAllCheckBoxChecked(): boolean {
        if (this.payload.drugItems.length) {
            return this.payload.drugItems.every((p) => p.checked);
        }
        return false;
    }

    checkAllCheckBox(event: any) {}

    onRemoveAllCheckedBillItems() {
        const isAllChecked = this.isAllCheckBoxChecked();
        for (let i = 0; i < this.payload.drugItems.length; i++) {
            if (this.payload.drugItems[i].checked) {
                this.payload.drugItems.splice(i, 1);
            }
        }
    }

    onDepartmentSelected(departmentPayload: DepartmentPayload) {
        if (departmentPayload) {
            const depId = departmentPayload.id;
            this.drugSearchProp.outletId = depId;
            this.payload.outlet = depId;
        }
    }

    onToggleUseDepartmentFilter(event: any) {
        const isChecked = event.target.checked;
        this.payload.useDepartmentFilter = isChecked;
        if (isChecked === true) {
            setTimeout(() => {
                this.drugSearchByDep.ngOnInit();
            }, 0);
        }
    }

    onToggleExcludeZeroItems(event: any) {
        const checked = event.target.checked;
        this.drugSearchProp.excludeZeroItems = checked;
        this.payload.excludeZeroQty = checked;
    }

    numberMatch(event: any) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    onDrugFrequencySelected(value: any, bill: ClerkDrugItemsPayload) {
        if (value) {
            bill.frequency = value;
        }
    }

    onDrugAdminRouteSelected(value: any, bill: ClerkDrugItemsPayload) {
        if (value) {
            bill.adminRoute = value;
        }
    }

    onSaveDrugRequestState() {

        const processData: boolean = this.data?.process;
        if (!this.payload.drugItems.length) {
            this.toast.warning('Please Search & Enter Some Drugs', HmisConstants.VALIDATION_ERR);
            return;
        }

        if (processData === true) {
            // call api to save drug request
            this.onProcessDrugRequest();
            return;
        } else {
            this.clerkRequestService.onSetDrugRequest(this.payload);
            this.onCloseModal();
        }

    }

    onProcessDrugRequest() {
        const doctorRequest = new ClerkDoctorRequestPayload();
        doctorRequest.patient = this.data.patientPayload;
        doctorRequest.doctor = this.commonService.getCurrentUser();
        doctorRequest.department = this.commonService.getCurrentLocation();
        doctorRequest.drugRequest = this.payload;


        this.spinner.show().then();
        this.subscription.add(
            this.clerkRequestService.onSaveDocRequest(doctorRequest).subscribe((res) => {
                this.spinner.hide().then();
                if (res.message) {
                    this.toast.success(res.message, HmisConstants.OK_SUCCESS_RESPONSE);
                    this.onCloseModal();
                }
            }, error => {
                this.spinner.hide().then();
                this.toast.error(error.error.message, HmisConstants.FAILED_RESPONSE);
                this.onCloseModal();
            })
        );
    }

    onCloseModal() {
        this.commonService.onCloseModal();
    }

    onToggleBillItemCheck(value: any, bill: ClerkDrugItemsPayload) {
        bill.checked = value.target.checked;
    }
}

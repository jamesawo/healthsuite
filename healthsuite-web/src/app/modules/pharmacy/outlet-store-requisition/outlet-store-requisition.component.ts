import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { DrugRegisterService } from '@app/shared/_services/pharmacy/drug-register.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import {
    DrugRegisterPayload,
    DrugRequisitionItemPayload,
    DrugRequisitionPayload,
    DrugSearchTermEnum,
    PharmacyLocationTypeEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { DepartmentPayload } from '@app/modules/settings';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { ValidationMessage } from '@app/shared/_payload';
import { RequisitionService } from '@app/shared/_services/pharmacy/requisition.service';
import { DrugSearchComponent } from '@app/modules/common/drug-search/drug-search.component';
import { PharmacyLocationSearchComponent } from '@app/modules/common/pharmacy-location-search/pharmacy-location-search.component';

@Component({
    selector: 'app-outlet-store-requisition',
    templateUrl: './outlet-store-requisition.component.html',
    styleUrls: ['./outlet-store-requisition.component.css'],
})
export class OutletStoreRequisitionComponent implements OnInit, OnDestroy {
    @Input('props') public props: { locationType: PharmacyLocationTypeEnum };
    @ViewChild('drugSearchComponent') drugSearchComponent: DrugSearchComponent;
    @ViewChild('locationSearchComponent') locationSearchComponent: PharmacyLocationSearchComponent;

    public drugSearchTerm: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;
    public currentLocationName: string = '';
    public payload: DrugRequisitionPayload = new DrugRequisitionPayload();
    public currentLocation: DepartmentPayload = new DepartmentPayload();
    public issueOutlet: number = 0;

    private subscription: Subscription = new Subscription();

    constructor(
        private commonService: CommonService,
        private drugRegisterService: DrugRegisterService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private requisitionService: RequisitionService
    ) {}

    ngOnInit(): void {
        if (this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION)) {
            this.initializeObj();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSubmit() {
        let isValid = this.onValidate();
        if (isValid.status == false) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR, { enableHtml: true });
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.requisitionService.saveRequisition(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    this.toast.success(
                        HmisConstants.LAST_ACTION_SUCCESS,
                        HmisConstants.SUCCESS_RESPONSE
                    );
                    this.onReset();
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(`Failed to Submit`, HmisConstants.ERR_TITLE);
                    console.log(error);
                }
            )
        );
    }

    public onSelectDrug(payload: DrugRegisterPayload) {
        if (payload && !this.isDuplicate(payload)) {
            if (!this.payload.issuingDepartment) {
                this.toast.error(
                    `Please select issuing outlet first`,
                    HmisConstants.VALIDATION_ERR
                );
                return;
            }
            this.addRequisitionItem(payload);
        }
    }

    protected isDuplicate(product: DrugRegisterPayload): boolean {
        let flag = false;
        this.payload.requisitionItems.forEach((item) => {
            if (item.drugRegister.id === product.id) {
                flag = true;
            }
        });
        return flag;
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onQtyChange(event: any, item: DrugRequisitionItemPayload) {
        let quantity: number = event.target.value ? event.target.value : 0;
        if (item.drugRegister.issuingOutletBal <= quantity) {
            this.toast.error(`Issuer does not have enough quantity`);
            item.requestingQuantity = 0;
            return;
        }
        item.requestingQuantity = quantity;
    }

    public addRequisitionItem(drug: DrugRegisterPayload) {
        let item: DrugRequisitionItemPayload = new DrugRequisitionItemPayload();
        item.drugRegister = drug;
        item.issuingOutletBalance = drug.issuingOutletBal;
        item.requestingQuantity = 0;
        item.unitOfIssue = drug.unitOfIssue;
        item.isChecked = false;

        this.payload.requisitionItems.push(item);
    }

    public onIssueOutletSelect(payload: DepartmentPayload) {
        if (payload) {
            if (payload.id == this.currentLocation.id) {
                this.locationSearchComponent.onClear();
                this.toast.error(
                    `Issuing & Receiving Points Must Be Different`,
                    HmisConstants.VALIDATION_ERR
                );
                return;
            } else if (!this.commonService.isPharmacyLocation(payload)) {
                this.locationSearchComponent.onClear();
                this.toast.error(`Please Select A Pharmacy Unit`, HmisConstants.VALIDATION_ERR);
                return;
            } else if (this.props?.locationType === PharmacyLocationTypeEnum.STORE) {
                if (!this.commonService.isPharmacyStore(payload.name)) {
                    this.toast.error(`Issuing Unit Must Be A Store`, HmisConstants.VALIDATION_ERR);
                    this.locationSearchComponent.onClear();
                    return;
                }
            }
            //
            this.issueOutlet = payload.id;
            this.payload.issuingDepartment = payload;
        }
    }

    public onReset() {
        this.drugSearchComponent.onClearField();
        this.locationSearchComponent.onClear();
        this.initializeObj();
    }

    private onValidate(): ValidationMessage {
        const response: ValidationMessage = { message: '', status: true };
        if (!this.payload.requisitionItems.length) {
            response.status = false;
            response.message += 'Add request items before submitting <br>';
        }
        if (!this.payload.issuingDepartment) {
            response.status = false;
            response.message += 'Select issuing outlet <br>';
        }

        return response;
    }

    private initializeObj() {
        const location = this.commonService.getCurrentLocation();
        if (location) {
            this.currentLocationName = location.name;
            this.currentLocation = location;

            this.payload = new DrugRequisitionPayload();
            this.payload.requisitionItems = [];
            this.payload.receivingDepartment = location;
            this.payload.location = location;
            this.payload.operatingUser = this.commonService.getCurrentUser();
            this.payload.requisitionTypeEnum =
                this.props?.locationType || PharmacyLocationTypeEnum.OUTLET;
        }
    }
}

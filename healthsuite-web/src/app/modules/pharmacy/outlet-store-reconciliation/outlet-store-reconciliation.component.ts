import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
    OutletReconciliationPayload,
    PharmacyLocationTypeEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { DrugRegisterService } from '@app/shared/_services/pharmacy/drug-register.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { ReconciliationService } from '@app/shared/_services/pharmacy/reconciliation.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { DepartmentPayload } from '@app/modules/settings';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-outlet-store-reconciliation',
    templateUrl: './outlet-store-reconciliation.component.html',
    styleUrls: ['./outlet-store-reconciliation.component.css'],
})
export class OutletStoreReconciliationComponent implements OnInit, OnDestroy {
    @Input('props') public props: { type: PharmacyLocationTypeEnum };
    public locationName = '';
    public searchTerm = '';
    public payload: OutletReconciliationPayload = new OutletReconciliationPayload();
    public p = 1;
    private subscription: Subscription = new Subscription();

    constructor(
        private drugRegisterService: DrugRegisterService,
        private commonService: CommonService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private reconciliationService: ReconciliationService
    ) {}

    ngOnInit(): void {
        if (
            this.commonService.checkLocationAndType(
                LocationConstants.PHARMACY_LOCATION,
                this.props?.type
            )
        ) {
            this.initializeObj();
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public initializeObj(): void {
        let currentLocation: DepartmentPayload = this.commonService.getCurrentLocation();

        this.locationName = currentLocation.name;
        this.payload = new OutletReconciliationPayload();
        this.payload.location = currentLocation;
        this.payload.outlet = currentLocation;
    }

    public onSearchMatchingDrugItems(): void {
        let searchValue: string = this.searchTerm.trim();
        if (!searchValue || searchValue.length === 0 || searchValue == '') {
            this.toast.error(`Provide Search Text First.`, HmisConstants.VALIDATION_ERR);
            return;
        }

        if (!this.payload.location.id && !this.payload.outlet.id) {
            this.toast.error(`Invalid Location Settings `, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinner.show().then();
        this.subscription.add(
            this.reconciliationService
                .getOutletStockWithAvailableQty(this.payload.outlet.id, searchValue)
                .subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res.body) {
                            this.payload.items = res.body;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        console.log(error);
                        this.toast.error(HmisConstants.ERR_SERVER_ERROR, HmisConstants.ERR_TITLE);
                    }
                )
        );
    }

    public onSubmit(): void {
        if (!this.hasItemsToReconcile()) {
            this.toast.error(`Search for items first!`, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();

        this.subscription.add(
            this.reconciliationService.onReconcileStockBalance(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.status == 200) {
                        this.toast.success(
                            HmisConstants.LAST_ACTION_SUCCESS,
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.initializeObj();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    console.log(error);
                    this.toast.error('Contact Support For Help', HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public hasItemsToReconcile(): boolean {
        return this.payload.items.length > 0;
    }

    public checkAllItems(ev) {
        this.payload.items.forEach((x) => (x.isChecked = ev.target.checked));
    }

    public isAllItemChecked() {
        if (this.payload.items.length) {
            return this.payload.items.every((p) => p.isChecked);
        }
    }

    public onRemoveMany() {
        if (this.payload.items.length) {
            if (this.isAllItemCheckboxChecked()) {
                for (let i = 0; i < this.payload.items.length; i++) {
                    this.payload.items.splice(i);
                }
            } else {
                for (let i = 0; i < this.payload.items.length; i++) {
                    if (this.payload.items[i].isChecked) {
                        this.payload.items.splice(i, 1);
                    }
                }
            }
        }
    }

    public onRemoveOne(i: number) {
        this.payload.items.splice(i, 1);
    }

    public isAllItemCheckboxChecked() {
        if (this.payload.items.length) {
            return this.payload.items.every((p) => p.isChecked);
        }
    }
}

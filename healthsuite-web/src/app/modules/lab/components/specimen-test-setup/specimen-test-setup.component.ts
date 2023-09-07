import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ProductServicePayload, SharedPayload } from '@app/shared/_payload';
import { BillViewTypeEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import {
    LabDepartmentTypeEnum,
    LabParameterSetupPayload,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { LabParamSetupService } from '@app/shared/_services/lab/lab-param-setup.service';

@Component({
    selector: 'app-specimen-test-setup',
    templateUrl: './specimen-test-setup.component.html',
    styleUrls: ['./specimen-test-setup.component.css'],
})
export class SpecimenTestSetupComponent implements OnInit, OnChanges, OnDestroy {
    @Input('payload')
    public payload: LabParameterSetupPayload;
    public viewType: BillViewTypeEnum = BillViewTypeEnum.LAB_BILL;

    @Input('labViewType')
    public labView: LabDepartmentTypeEnum;

    public chemical = LabDepartmentTypeEnum.CHEMICAL;
    public anatomical = LabDepartmentTypeEnum.ANATOMICAL;
    public microbiology = LabDepartmentTypeEnum.MICROBIOLOGY;
    public haematology = LabDepartmentTypeEnum.HAEMATOLOGY;

    private subscription: Subscription = new Subscription();

    constructor(private spinner: NgxSpinnerService, private labService: LabParamSetupService) {}

    ngOnInit(): void {
        if (!this.payload) {
            this.payload = new LabParameterSetupPayload();
        }

        this.subscription.add(
            this.labService.parameterItems.subscribe((res) => {
                if (res.length) {
                    this.payload.parameterSetupItems = res;
                }
            })
        );
    }

    ngOnChanges(changes: SimpleChanges) {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onLabTestSelected(productServicePayload: ProductServicePayload) {
        if (productServicePayload) {
            this.payload.test = productServicePayload;
            this.onSearchLabParameterSetupByService(productServicePayload);
        }
    }

    public onSearchLabParameterSetupByService(service: ProductServicePayload) {
        this.spinner.show().then();
        this.subscription.add(
            this.labService.onGetParameterByTest(service.id).subscribe(
                (res) => {
                    if (res && res.id) {
                        this.payload.id = res.id;
                        this.payload.test = res.test;
                        this.payload.specimen = res.specimen;
                        this.payload.specimenColor = res.specimenColor;
                        this.payload.isBoneMarrowTest = res.isBoneMarrowTest;
                        this.payload.isSpecialTest = res.isBoneMarrowTest;
                        this.payload.isImmunoTest = res.isImmunoTest;
                        this.payload.isParasitologTest = res.isParasitologTest;
                        this.payload.isRequirePathologist = res.isRequirePathologist;
                        this.payload.isHistopathologySFA = res.isHistopathologySFA;

                        this.labService.parameterItems.next(res.parameterSetupItems);
                    }
                    this.spinner.hide().then();
                },
                (error) => {
                    this.spinner.hide().then();
                }
            )
        );
    }

    public onLabSpecimenColorSelected(color: string) {
        if (color) {
            this.payload.specimenColor = color;
        }
    }

    public onSpecimenSelected(sharedPayload: SharedPayload) {
        if (sharedPayload) {
            this.payload.specimen = sharedPayload;
        }
    }
}

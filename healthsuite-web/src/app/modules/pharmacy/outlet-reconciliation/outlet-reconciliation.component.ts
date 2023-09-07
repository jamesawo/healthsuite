import { Component, OnDestroy, OnInit } from '@angular/core';
import { PharmacyLocationTypeEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-outlet-reconciliation',
    templateUrl: './outlet-reconciliation.component.html',
    styleUrls: ['./outlet-reconciliation.component.css'],
})
export class OutletReconciliationComponent implements OnInit, OnDestroy {
    public locationType = PharmacyLocationTypeEnum.OUTLET;
    constructor() {}

    ngOnInit(): void {}

    ngOnDestroy() {}
}

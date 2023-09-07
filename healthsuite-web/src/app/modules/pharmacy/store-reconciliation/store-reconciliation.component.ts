import { Component, OnInit } from '@angular/core';
import { PharmacyLocationTypeEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-store-reconciliation',
    templateUrl: './store-reconciliation.component.html',
    styleUrls: ['./store-reconciliation.component.css'],
})
export class StoreReconciliationComponent implements OnInit {
    public locationType: PharmacyLocationTypeEnum = PharmacyLocationTypeEnum.STORE;

    constructor() {}

    ngOnInit(): void {}
}

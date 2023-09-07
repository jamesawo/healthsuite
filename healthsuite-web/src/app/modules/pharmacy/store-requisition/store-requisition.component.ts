import { Component, OnInit } from '@angular/core';
import { PharmacyLocationTypeEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-store-requisition',
    templateUrl: './store-requisition.component.html',
    styleUrls: ['./store-requisition.component.css'],
})
export class StoreRequisitionComponent implements OnInit {
    public store: PharmacyLocationTypeEnum = PharmacyLocationTypeEnum.STORE;

    constructor() {}

    ngOnInit(): void {}
}

import { Component, OnInit } from '@angular/core';
import { PharmacyLocationTypeEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-outlet-requisition',
    templateUrl: './outlet-requisition.component.html',
    styleUrls: ['./outlet-requisition.component.css'],
})
export class OutletRequisitionComponent implements OnInit {
    public outlet: PharmacyLocationTypeEnum = PharmacyLocationTypeEnum.OUTLET;

    constructor() {}

    ngOnInit(): void {}
}

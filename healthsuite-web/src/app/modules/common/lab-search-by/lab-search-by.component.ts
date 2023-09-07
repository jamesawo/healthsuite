import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {LabSearchByEnum, LabSpecimenCollectionPayload} from '@app/shared/_payload/lab/lab.payload';

@Component({
    selector: 'app-lab-search-by',
    templateUrl: './lab-search-by.component.html',
    styles: [],
})
export class LabSearchByComponent implements OnInit {
    @Input('default') searchEnum: LabSearchByEnum;
    @Output('selected')
    public selectEmit: EventEmitter<LabSearchByEnum> = new EventEmitter<LabSearchByEnum>();
    public patient = LabSearchByEnum.PATIENT;
    public receipt = LabSearchByEnum.RECEIPT_NUMBER;
    public invoice = LabSearchByEnum.INVOICE_NUMBER;
    public labNumber = LabSearchByEnum.LAB_NUMBER;

    constructor() {}

    ngOnInit(): void {
        if (this.searchEnum) {
            this.invoice = this.searchEnum;
        }
    }

    onSelected(value: LabSearchByEnum) {
        if (value) {
            this.selectEmit.emit(value);
        }
    }
}

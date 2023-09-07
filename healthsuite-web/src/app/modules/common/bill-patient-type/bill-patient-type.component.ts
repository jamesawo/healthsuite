import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import { BillPatientTypeEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import { PatientCategoryEnum } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-bill-patient-type',
    templateUrl: './bill-patient-type.component.html',
    styleUrls: ['./bill-patient-type.component.css'],
})
export class BillPatientTypeComponent implements OnInit, OnChanges {
    @Output() valueChanged: EventEmitter<BillPatientTypeEnum> =
        new EventEmitter<BillPatientTypeEnum>();

    @Input() props: { type?: PatientCategoryEnum; defaultSelected?: BillPatientTypeEnum };

    public walkInPatient = BillPatientTypeEnum.WALK_IN;
    public registeredPatient = BillPatientTypeEnum.REGISTERED;

    public scheme: PatientCategoryEnum = PatientCategoryEnum.SCHEME;

    constructor() {}

    ngOnInit(): void {}

    ngOnChanges(changes: SimpleChanges) {
    }

    onTypeChange(value: BillPatientTypeEnum) {
        this.valueChanged.emit(value);
    }
}

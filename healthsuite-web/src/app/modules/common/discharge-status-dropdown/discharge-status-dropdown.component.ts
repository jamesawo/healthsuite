import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import { DischargeStatusConst } from '@app/shared/_payload/erm/patient.payload';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-discharge-status-dropdown',
    templateUrl: './discharge-status-dropdown.component.html',
    styleUrls: ['./discharge-status-dropdown.component.css'],
})
export class DischargeStatusDropdownComponent implements OnInit {
    @ViewChild('ngSelectComponent') ngSelect: NgSelectComponent;

    public collection: any[] = DischargeStatusConst;
    @Output() public selected: EventEmitter<any> = new EventEmitter<any>();
    public defaultSelected: any;
    public selDefault: string = DischargeStatusConst[0].value;

    constructor() {}

    ngOnInit(): void {
        this.defaultSelected = this.collection[0];
    }

    public onSelected(value: any) {
        if (value) {
            this.selected.emit(value);
        }
    }

    public onClearField(): void {
        this.ngSelect.clearModel();
    }
}

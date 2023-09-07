import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { drugFrequencyData } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-drug-frequency-dropdown',
    templateUrl: './drug-frequency-dropdown.component.html',
    styleUrls: ['./drug-frequency-dropdown.component.css'],
})
export class DrugFrequencyDropdownComponent implements OnInit {
    public collection: any[] = drugFrequencyData;
    @Output() public selected: EventEmitter<any> = new EventEmitter<any>();
    public defaultSelected: any;
    public selDefault: string = drugFrequencyData[0].value;
    constructor() {}

    ngOnInit(): void {
        this.defaultSelected = this.collection[0];
    }

    public onNgSelected(data: any) {
        if (data) {
            this.defaultSelected = data;
            this.selected.emit(data);
        }
    }

    public onSelected() {
        this.selected.emit(this.selDefault);
    }
}

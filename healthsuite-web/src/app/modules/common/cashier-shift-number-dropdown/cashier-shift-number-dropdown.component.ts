import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {
    CashierShitPayload,
} from '@app/shared/_payload/shift/shit.payload';

@Component({
    selector: 'app-cashier-shift-number-dropdown',
    templateUrl: './cashier-shift-number-dropdown.component.html',
    styleUrls: ['./cashier-shift-number-dropdown.component.css'],
})
export class CashierShiftNumberDropdownComponent implements OnInit, OnChanges {
    @Input('collection')
    public collection: CashierShitPayload[] = [];
    @Output('selected')
    public selected: EventEmitter<CashierShitPayload> = new EventEmitter<CashierShitPayload>();

    constructor() {}

    ngOnInit(): void {}

    ngOnChanges(changes: SimpleChanges) {}

    public onSelect(shift: CashierShitPayload) {
        if (shift) {
            this.selected.emit(shift);
        }
    }
}

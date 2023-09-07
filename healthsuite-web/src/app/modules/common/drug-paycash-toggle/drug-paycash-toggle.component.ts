import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
    selector: 'app-drug-paycash-toggle',
    templateUrl: './drug-paycash-toggle.component.html',
    styleUrls: ['./drug-paycash-toggle.component.css'],
})
export class DrugPaycashToggleComponent implements OnInit {
    @Output() public valueChange: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor() {}

    ngOnInit(): void {}

    public onValueChange(value: boolean) {
        this.valueChange.emit(value);
    }
}

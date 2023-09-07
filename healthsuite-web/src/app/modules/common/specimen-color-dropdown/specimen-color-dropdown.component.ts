import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'app-specimen-color-dropdown',
    templateUrl: './specimen-color-dropdown.component.html',
    styleUrls: ['./specimen-color-dropdown.component.css'],
})
export class SpecimenColorDropdownComponent implements OnInit {
    @Input('default')
    default: string;

    @Output('selected')
    public selected: EventEmitter<string> = new EventEmitter<string>();

    constructor() {}

    ngOnInit(): void {}

    onSelected(value: string) {
        if (value) {
            this.selected.emit(value);
        }
    }
}

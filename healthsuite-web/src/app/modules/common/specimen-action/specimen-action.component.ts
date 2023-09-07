import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
    selector: 'app-specimen-action',
    templateUrl: './specimen-action.component.html',
    styleUrls: ['./specimen-action.component.css'],
})
export class SpecimenActionComponent implements OnInit {
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

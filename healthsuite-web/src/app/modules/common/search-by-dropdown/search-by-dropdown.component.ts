import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'app-search-by-dropdown',
    templateUrl: './search-by-dropdown.component.html',
    styleUrls: ['./search-by-dropdown.component.css'],
})
export class SearchByDropdownComponent implements OnInit, AfterViewInit {
    @Input() public collection: any[];
    @Output() public selected: EventEmitter<any> = new EventEmitter<any>();

    public defaultSelect: any = {};

    constructor() {}

    ngOnInit(): void {
        if (this.collection?.length) {
            this.defaultSelect = this.collection[0].name;
        }
    }

    public ngAfterViewInit(): void {
        if (this.collection?.length) {
            this.defaultSelect = this.collection[0].name;
        }
    }

    public onSearchByChanged(data: any) {
        if (data) {
            this.selected.emit(data);
        }
    }

    public onSetDefault(payload: any) {
        if (payload) {
            this.defaultSelect = payload.name;
        }
    }
}

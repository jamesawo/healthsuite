import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { drugAdministrationRoute } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-drug-administration-route',
    templateUrl: './drug-administration-route.component.html',
    styleUrls: ['./drug-administration-route.component.css'],
})
export class DrugAdministrationRouteComponent implements OnInit {
    public collection: any[] = drugAdministrationRoute;
    @Output() public selected: EventEmitter<any> = new EventEmitter<any>();
    public defaultSelected: any;
    public selDefault: string = drugAdministrationRoute[0].value;

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

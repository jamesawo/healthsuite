import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'app-editable-table',
    templateUrl: './editable-table.component.html',
    styleUrls: ['./editable-table.component.css'],
})
export class EditableTableComponent implements OnInit {
    public p: any;
    @Input() public props: { data: any[]; columns: any[] };
    @Input() public options: { type: any; isEditable: boolean };
    //todo:: use editable table component for service register and drug register tables
    constructor() {}

    ngOnInit(): void {}
}

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-search-diseases',
    templateUrl: './search-diseases.component.html',
    styleUrls: ['./search-diseases.component.css'],
})
export class SearchDiseasesComponent implements OnInit {
    @Output('selected')
    public selected: EventEmitter<{ name: string; code: string; id: number }> = new EventEmitter();

    constructor() {}

    ngOnInit(): void {}

    public onSelected(disease: any) {
        if (disease) {
            this.selected.emit(disease);
        }
    }
}

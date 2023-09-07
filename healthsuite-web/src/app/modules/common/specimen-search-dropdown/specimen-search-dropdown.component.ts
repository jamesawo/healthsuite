import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-specimen-search-dropdown',
    templateUrl: './specimen-search-dropdown.component.html',
    styleUrls: ['./specimen-search-dropdown.component.css'],
})
export class SpecimenSearchDropdownComponent implements OnInit, OnDestroy, OnChanges {
    public specimenList: SharedPayload[] = [];
    @Output('selected') selected: EventEmitter<SharedPayload> = new EventEmitter<SharedPayload>();

    @Input('default')
    default: SharedPayload = {};

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.onGetAllLabSpecimen().subscribe((res) => {
                if (res.data) {
                    this.specimenList = res.data;
                }
            })
        );
    }

    ngOnDestroy() {}

    ngOnChanges(changes: SimpleChanges) {
        this.default = changes.default.currentValue;
        if (changes?.default.currentValue) {
            this.default = this.specimenList.find((value) => value.id === this.default.id);
            this.onSelected(this.default);
        }
    }

    public onSelected(specimen: SharedPayload) {
        if (specimen) {
            this.selected.emit(specimen);
        }
    }
}

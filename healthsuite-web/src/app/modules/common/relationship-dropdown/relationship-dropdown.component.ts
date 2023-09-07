import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-relationship-dropdown',
    templateUrl: './relationship-dropdown.component.html',
    styleUrls: ['./relationship-dropdown.component.css'],
})
export class RelationshipDropdownComponent implements OnInit, OnDestroy {
    public relationships: SharedPayload[] = [];
    @Output('selected') selected: EventEmitter<SharedPayload> = new EventEmitter<SharedPayload>();

    @Input('default')
    public default: SharedPayload;

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {

        this.subscription.add(
            this.seedDataService.getAllRelationship().subscribe((res) => {
                if (res.data) {
                    this.relationships = res.data;
                }
            })
        );
    }

    ngOnDestroy() {}

    public onSelected(relationship: SharedPayload) {
        if (relationship) {
            this.selected.emit(relationship);
        }
    }
}

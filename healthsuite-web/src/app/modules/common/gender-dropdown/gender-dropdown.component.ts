import {Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import { GenderPayload } from '@app/shared/_payload';
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-gender-dropdown',
    templateUrl: './gender-dropdown.component.html',
    styleUrls: ['./gender-dropdown.component.css'],
})
export class GenderDropdownComponent implements OnInit, OnDestroy {
    @ViewChild('ngSelectComponent') ngComponent: NgSelectComponent;
    public genders: GenderPayload[] = [];
    @Output('selected')
    public selected: EventEmitter<GenderPayload> = new EventEmitter<GenderPayload>();

    subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.getAllGender().subscribe(
                (res) => {
                    this.genders = res.data;
                },
                (error1) => {
                    console.log(error1);
                }
            )
        );
    }

    ngOnDestroy() {}

    onGenderSelected(gender: GenderPayload) {
        if (gender) {
            this.selected.emit(gender);
        }
    }

    onClearField() {
        this.ngComponent.clearModel();
    }
}

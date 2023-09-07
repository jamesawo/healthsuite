import { Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { DepartmentPayload } from '@app/modules/settings';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-clinic-dropdown',
    templateUrl: './clinic-dropdown.component.html',
    styleUrls: ['./clinic-dropdown.component.css'],
})
export class ClinicDropdownComponent implements OnInit, OnDestroy {
    @ViewChild('selectComponent')
    public selectComponent: NgSelectComponent;

    @Output() selected: EventEmitter<DepartmentPayload> = new EventEmitter<DepartmentPayload>();
    public clinic: DepartmentPayload[];
    protected subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.seedSubject$.subscribe((data) => {
                this.clinic = data.clinic;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSelect(payload: DepartmentPayload) {
        if (payload) {
            this.selected.emit(payload);
        }
    }

    public onClearField() {
        this.selectComponent.clearModel();
    }
}

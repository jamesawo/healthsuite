import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { DepartmentPayload } from '@app/modules/settings';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';
import { InputComponentChanged, InputComponentPayload } from '@app/shared/_payload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-service-department-dropdown',
    templateUrl: './service-department-dropdown.component.html',
    styleUrls: ['./service-department-dropdown.component.css'],
})
export class ServiceDepartmentDropdownComponent implements OnInit, OnDestroy {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;
    @Input()
    selected: InputComponentPayload;
    @Output()
    changed: EventEmitter<InputComponentChanged> = new EventEmitter<InputComponentChanged>();
    @Output('valueSelected')
    valueSelected: EventEmitter<DepartmentPayload> = new EventEmitter<DepartmentPayload>();
    public default: DepartmentPayload;

    @Input('defaultSelected')
    public defaultSelected: DepartmentPayload;
    public data: DepartmentPayload[];
    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.getAllDepartment().subscribe((value) => {
                this.data = value.data;
                if (!this.selected) {
                    this.default = new DepartmentPayload();
                } else {
                    this.default = value.data.find((c) => c.id === this.selected.value);
                }

                if (this.defaultSelected && this.defaultSelected.id) {
                    this.default = value.data.find((c) => c.id === this.defaultSelected.id);
                }
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSelect(selected: DepartmentPayload) {
        if (selected) {
            const data: InputComponentChanged = {
                serviceColumn: this.selected?.columnName,
                value: selected.id,
                id: this.selected?.id,
            };
            this.default = selected;
            this.changed.emit(data);
            this.valueSelected.emit(selected);
        }
    }

    onClearSelected() {
        this.ngSelectComponent.searchTerm = '';
        this.default = new DepartmentPayload();
        this.ngSelectComponent.clearModel();
    }
}

import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges, ViewChild,
} from '@angular/core';
import { RevenueDepartmentPayload } from '@app/modules/settings';
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { InputComponentChanged, InputComponentPayload } from '@app/shared/_payload';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-revenue-department-dropdown',
    templateUrl: './revenue-department-dropdown.component.html',
    styleUrls: ['./revenue-department-dropdown.component.css'],
})
export class RevenueDepartmentDropdownComponent implements OnInit, OnDestroy, OnChanges {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;
    @Input() selected: InputComponentPayload;
    @Input() selectedId?: number;
    @Output() changed: EventEmitter<InputComponentChanged> = new EventEmitter<
        InputComponentChanged
    >();
    @Output() select: EventEmitter<RevenueDepartmentPayload> = new EventEmitter(); // used in drug register view (add or edit )

    public default: RevenueDepartmentPayload;
    public data: RevenueDepartmentPayload[];
    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.onGetAllRevenueDepartment().subscribe((value) => {
                this.data = value.data;
                if (!this.selected) {
                    this.default = new RevenueDepartmentPayload();
                } else {
                    this.default = value.data.find((value) => value.id === this.selected.value);
                }
            })
        );
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.selectedId) {
            this.default = this.data.find((value) => value.id == this.selectedId);
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSelect(selected: RevenueDepartmentPayload) {
        const data: InputComponentChanged = {
            serviceColumn: this.selected?.columnName,
            value: selected.id,
            id: this.selected?.id,
        };
        this.default = selected;
        this.changed.emit(data);
        if (selected) {
            this.select.emit(selected);
        }
    }

    clearSelected() {
        this.ngSelectComponent.searchTerm = '';
        this.default = new RevenueDepartmentPayload();
        this.ngSelectComponent.clearModel();
    }
}

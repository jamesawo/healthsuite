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
import {
    DrugTableColumnEnum,
    InputComponentChanged,
    InputComponentPayload,
    SharedPayload,
} from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-drug-formulation',
    templateUrl: './drug-formulation.component.html',
    styleUrls: ['./drug-formulation.component.css'],
})
export class DrugFormulationComponent implements OnInit, OnDestroy, OnChanges {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;
    @Output() public selected: EventEmitter<SharedPayload> = new EventEmitter();
    @Input() public defaultValue: number;

    @Input() columnValue?: InputComponentPayload;
    @Output() columnChanged?: EventEmitter<InputComponentChanged> = new EventEmitter();

    public drugFormulations: SharedPayload[] = [];
    public defaultSelected: SharedPayload = { id: undefined, name: undefined };

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.getAllDrugFormulation().subscribe(
                (res) => {
                    this.drugFormulations = res.data;
                    if (this.columnValue) {
                        this.defaultSelected = res.data.find(
                            (data) => data.id == this.columnValue.value
                        );
                    }
                },
                (error) => {
                    console.log(error.error.message);
                }
            )
        );
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.defaultValue) {
            this.defaultSelected = this.drugFormulations.find(
                (value) => value.id == this.defaultValue
            );
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSelectDrugFormulation(value: SharedPayload) {
        if (value) {
            this.selected.emit(value);

            if (this.columnValue) {
                const data: InputComponentChanged = {
                    drugColumn: DrugTableColumnEnum.formulation,
                    value: value.id,
                    id: this.columnValue?.id,
                };
                this.columnChanged.emit(data);
            }
        }
    }

    onClearField() {
        this.ngSelectComponent.searchTerm = '';
        this.ngSelectComponent.clearModel();
    }
}

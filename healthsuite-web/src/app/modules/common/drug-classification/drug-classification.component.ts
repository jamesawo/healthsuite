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
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-drug-classification',
    templateUrl: './drug-classification.component.html',
    styleUrls: ['./drug-classification.component.css'],
})
export class DrugClassificationComponent implements OnInit, OnDestroy, OnChanges {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;

    @Input() public defaultValue?: number;
    @Output() public selected: EventEmitter<SharedPayload> = new EventEmitter<SharedPayload>();

    @Input() columnValue?: InputComponentPayload;
    @Output() columnChanged?: EventEmitter<InputComponentChanged> = new EventEmitter();

    public drugClassifications: SharedPayload[] = [];
    public defaultSelected: SharedPayload = { id: undefined, name: undefined };

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.getAllDrugClassification().subscribe(
                (res) => {
                    this.drugClassifications = res.data;

                    if (this.columnValue) {
                        this.defaultSelected = res.data.find(
                            (data) => data.id == this.columnValue?.value
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
            this.defaultSelected = this.drugClassifications.find(
                (value) => value.id == this.defaultValue
            );
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSelectDrugClassification(payload: SharedPayload): void {
        if (payload) {
            this.selected.emit(payload);
            if (this.columnValue) {
                const data: InputComponentChanged = {
                    drugColumn: DrugTableColumnEnum.classification,
                    value: payload.id,
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

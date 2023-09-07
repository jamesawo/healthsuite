import {
    Component,
    EventEmitter,
    Input,
    OnChanges, OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import {
    LabDepartmentTypeEnum,
    LabParameterSetupItemPayload,
    LabParameterSetupPayload,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { LabParamSetupService } from '@app/shared/_services/lab/lab-param-setup.service';
import {Subscription} from 'rxjs';

@Component({
    selector: 'app-param-rank-setup',
    templateUrl: './param-rank-setup.component.html',
    styleUrls: ['./param-rank-setup.component.css'],
})
export class ParamRankSetupComponent implements OnInit, OnChanges, OnDestroy {
    public items: LabParameterSetupItemPayload[] = [];

    @Output('onSave')
    public onSave: EventEmitter<LabParameterSetupItemPayload[]> = new EventEmitter<
        LabParameterSetupItemPayload[]
    >();
    public parameter: SharedPayload = { id: undefined, name: '' };
    public hierarchy = 1;
    public isReadOnly = true;

    private subscription: Subscription = new Subscription();
    constructor(private labService: LabParamSetupService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.labService.parameterItems.subscribe((res) => {
                if (res.length) {
                    this.items = res;
                    this.onSetHierarchy();
                }
            })
        );
    }

    ngOnChanges(changes: SimpleChanges): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onRemoveItem = (index: number): void => {
        const items = this.labService.parameterItems.value;
        items.splice(index, 1);
        this.labService.parameterItems.next(items);
    };

    public onParameterSelected(sharedPayload: SharedPayload) {
        if (sharedPayload) {
            this.parameter = sharedPayload;
        }
    }

    public onSaveData() {
        this.items = this.labService.parameterItems.value;
        if (this.items.length) {
            this.onSave.emit(this.items);
        }
    }

    public onAddItem() {
        const payload = new LabParameterSetupItemPayload();
        payload.parameterHierarchy = this.hierarchy;
        payload.parameter = this.parameter;
        if (!this.hasDuplicateEntry(payload)) {
            this.items.push(payload);
            this.labService.parameterItems.next(this.items);
        }
        this.onSetHierarchy();
    }

    private onSetHierarchy() {
        this.hierarchy = this.items.length + 1;
    }

    private hasDuplicateEntry(entry: LabParameterSetupItemPayload): boolean {
        const find = this.items.find((value) => value.parameter.id === entry.parameter.id);
        return !!find;
    }
}

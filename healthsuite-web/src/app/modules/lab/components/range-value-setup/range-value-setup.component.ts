import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import {
    LabParamRangeItemPayload,
    LabParamRangePayload,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { LabParamSetupService } from '@app/shared/_services/lab/lab-param-setup.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-range-value-setup',
    templateUrl: './range-value-setup.component.html',
    styleUrls: ['./range-value-setup.component.css'],
})
export class RangeValueSetupComponent implements OnInit, OnDestroy {
    @Input('onRemoveItem')
    public onRemoveItem?: (index: number) => void;

    @Input('paramPayload')
    public rangePayload: LabParamRangePayload;

    @Input('items')
    public items: LabParamRangeItemPayload[];

    @Output('onSave')
    public onSave: EventEmitter<any> = new EventEmitter<any>();

    public inputName = '';
    public inputUnit = '';
    public inputLower = 1;
    public inputHigher = 2;

    private subscription: Subscription = new Subscription();

    constructor(private labParamService: LabParamSetupService) {}

    ngOnInit(): void {
        if (!this.items) {
            this.items = [];
        }

        this.subscription.add(
            this.labParamService.selectedParemeter$.subscribe((value) => {
                if (value) {
                    this.inputName = value.parameter.title;
                }
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSaveData() {
        if (this.items.length) {
            this.onSave.emit(this.items);
        }
    }

    onAddItem() {
        if (this.inputUnit && this.inputLower && this.inputHigher) {
            const newItem = new LabParamRangeItemPayload();
            newItem.unit = this.inputUnit;
            newItem.name = this.inputName;
            newItem.upperLimit = this.inputHigher;
            newItem.lowerLimit = this.inputLower;
            this.items.push(newItem);
        }
    }
}

import {
    AfterViewInit,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    DrugTableColumnEnum,
    InputComponentChanged,
    InputComponentPayload,
    InputComponentUsageEnum,
    ServicesTableColumnEnum,
} from '@app/shared/_payload';

@Component({
    selector: 'app-input-field',
    templateUrl: './input-field.component.html',
    styleUrls: ['./input-field.component.css'],
})
export class InputFieldComponent implements OnInit, AfterViewInit {
    @Input() props: InputComponentPayload;
    @Output() changed: EventEmitter<InputComponentChanged> = new EventEmitter<
        InputComponentChanged
    >();
    @ViewChild('inputElement') inputElement: ElementRef;
    private timeout: any;

    constructor() {}

    ngOnInit(): void {}

    ngAfterViewInit() {
        this.inputElement.nativeElement.focus();
    }

    onValueChanged(event: any) {
        const value = event.target.value;
        const columnName = this.props?.columnName;
        const id = this.props?.id;

        if (value) {
            if (this.props.columnType == InputComponentUsageEnum.DRUG) {
                return this.onDrugColumnValueChanged(columnName, id, value);
            }

            if (this.props.columnType == InputComponentUsageEnum.SERVICE) {
                return this.onServiceColumnValueChanged(columnName, id, value);
            }
            // return this.onInputValueChanged({
            //     column: this.props?.columnName,
            //     id: this.props.id,
            //     value: value,
            // });
        }
        return;
    }

    onInputValueChanged(payload: { column: ServicesTableColumnEnum; id: any; value: any }) {
        const updatedPayload: InputComponentChanged = {
            serviceColumn: payload.column,
            id: payload.id,
            value: payload.value,
        };
        this.changed.emit(updatedPayload);
    }

    protected onServiceColumnValueChanged(
        column: ServicesTableColumnEnum,
        id: any,
        value: any
    ): void {
        const updatedPayload: InputComponentChanged = {
            serviceColumn: column,
            id: id,
            value: value,
        };
        this.changed.emit(updatedPayload);
    }

    protected onDrugColumnValueChanged(column: DrugTableColumnEnum, id: any, value: any): void {
        const updatedPayload: InputComponentChanged = {
            drugColumn: column,
            id: id,
            value: value,
        };
        this.changed.emit(updatedPayload);
    }
}

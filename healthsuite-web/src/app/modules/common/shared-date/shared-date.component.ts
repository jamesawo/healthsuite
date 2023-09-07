import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { BsDatepickerConfig, BsDaterangepickerDirective } from 'ngx-bootstrap/datepicker';
import { DatePayload } from '@app/shared/_payload';
import * as moment from 'moment';

@Component({
    selector: 'app-shared-date',
    templateUrl: './shared-date.component.html',
    styleUrls: ['./shared-date.component.css'],
})
export class SharedDateComponent implements OnInit, OnChanges {
    @ViewChild('dateInputField') public dateField: any;
    @ViewChild('dp', { static: false })
    public datepicker: BsDaterangepickerDirective;
    @Output()
    public selected: EventEmitter<DatePayload> = new EventEmitter<DatePayload>();
    @Input('defaultDate')
    public defaultDate: DatePayload;
    @Input()
    public props: {
        hideCurrentDate?: boolean;
        minDate?: Date;
        setDate?: DatePayload;
        showTime?: boolean;
    };
    public currentDate: Date;
    public datePayload: DatePayload;
    public placeHolder: Date | string;

    bsConfig: Partial<BsDatepickerConfig>;
    mytime: Date;

    constructor() {}

    ngOnInit(): void {
        if (!this.props?.hideCurrentDate) {
            const today = new Date();
            this.setDate({
                year: today.getFullYear(),
                month: today.getMonth(),
                day: today.getDate(),
            });
        }

        if (this.defaultDate) {
            this.setPlaceHolder(this.defaultDate);
        }

        this.datePayload = {
            year: undefined,
            month: undefined,
            day: undefined,
            hour: undefined,
            min: undefined,
        };
        this.setOptions();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.props && this.props.setDate) {
            this.setPlaceHolder(this.props.setDate);
        } else {
            this.placeHolder = 'YYYY-MM-DD';
        }
    }

    public onValueChange(value: Date): void {
        if (value) {
            this.datePayload.year = value.getFullYear();
            this.datePayload.month = value.getMonth() + 1;
            // this.datePayload.month = value.getMonth();
            this.datePayload.day = value.getDate();
            this.datePayload.hour = value.getHours();
            this.datePayload.min = value.getMinutes();
            this.selected.emit(this.datePayload);
        }
    }

    public setTime(date: Date) {
        if (date) {
            this.datePayload.hour = date.getHours();
            this.datePayload.min = date.getMinutes();
            this.selected.emit(this.datePayload);
        }
    }

    public onClearDateField() {
        this.currentDate = null;
        this.datepicker.bsValue = undefined;
    }

    public onSetDateFromDate() {
        this.currentDate = new Date();
    }

    protected setOptions(): void {
        this.bsConfig = Object.assign(
            {},
            {
                containerClass: 'theme-red',
                showTodayButton: true,
                todayPosition: 'center',
                dateInputFormat: 'YYYY-MM-DD',
            }
        );
        this.datepicker?.setConfig();
    }

    private setPlaceHolder(date: DatePayload) {
        if (date) {
            const { year, month, day }: any = date;
            const dateFormat = new Date(year, month - 1, day);
            this.placeHolder = moment(dateFormat).format('YYYY-MM-DD');
        } else {
            this.placeHolder = 'YYYY-MM-DD';
        }
    }

    private setDate(setDate: DatePayload) {
        if (setDate && !this.currentDate) {
            const { year, month, day }: any = setDate;
            this.currentDate = new Date(year, month, day);
        }
    }
}

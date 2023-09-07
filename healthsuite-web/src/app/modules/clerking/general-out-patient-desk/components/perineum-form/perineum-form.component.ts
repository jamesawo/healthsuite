import { Component, Input, OnInit } from '@angular/core';
import {
    PerieneumFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-perineum-form',
    templateUrl: './perineum-form.component.html',
    styleUrls: ['./perineum-form.component.css'],
})
export class PerineumFormComponent implements OnInit {
    @Input('props')
    public props: { data: PerieneumFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

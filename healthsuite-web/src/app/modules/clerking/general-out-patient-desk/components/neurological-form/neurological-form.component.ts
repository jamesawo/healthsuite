import { Component, Input, OnInit } from '@angular/core';
import {
    NeurologicalExaminationPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-neurological-form',
    templateUrl: './neurological-form.component.html',
    styleUrls: ['./neurological-form.component.css'],
})
export class NeurologicalFormComponent implements OnInit {
    @Input('props')
    public props: { data: NeurologicalExaminationPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

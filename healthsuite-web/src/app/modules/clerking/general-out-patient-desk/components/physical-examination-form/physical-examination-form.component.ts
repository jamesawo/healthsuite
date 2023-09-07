import { Component, Input, OnInit } from '@angular/core';
import {
    PhysicalExaminationFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-physical-examination-form',
    templateUrl: './physical-examination-form.component.html',
    styleUrls: ['./physical-examination-form.component.css'],
})
export class PhysicalExaminationFormComponent implements OnInit {
    @Input('props')
    public props: { data: PhysicalExaminationFormPayload };
    public yes: YesNoEnum = YesNoEnum.YES;
    public no: YesNoEnum = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

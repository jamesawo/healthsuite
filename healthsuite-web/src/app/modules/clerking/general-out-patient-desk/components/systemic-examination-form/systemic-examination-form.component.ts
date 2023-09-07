import { Component, Input, OnInit } from '@angular/core';
import {
    SystemicExaminationFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-systemic-examination-form',
    templateUrl: './systemic-examination-form.component.html',
    styleUrls: ['./systemic-examination-form.component.css'],
})
export class SystemicExaminationFormComponent implements OnInit {
    @Input('props')
    public props: { data: SystemicExaminationFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

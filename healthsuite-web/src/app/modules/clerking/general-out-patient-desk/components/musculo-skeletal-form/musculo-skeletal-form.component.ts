import { Component, Input, OnInit } from '@angular/core';
import {
    MusculoSkeletalFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-musculo-skeletal-form',
    templateUrl: './musculo-skeletal-form.component.html',
    styleUrls: ['./musculo-skeletal-form.component.css'],
})
export class MusculoSkeletalFormComponent implements OnInit {
    @Input('props')
    public props: { data: MusculoSkeletalFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

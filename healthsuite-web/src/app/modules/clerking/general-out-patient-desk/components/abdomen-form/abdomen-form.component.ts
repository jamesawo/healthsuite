import { Component, Input, OnInit } from '@angular/core';
import {
    AbdomenFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-abdomen-form',
    templateUrl: './abdomen-form.component.html',
    styleUrls: ['./abdomen-form.component.css'],
})
export class AbdomenFormComponent implements OnInit {
    @Input('props')
    public props: { data: AbdomenFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}
}

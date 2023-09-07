import { Component, Input, OnInit } from '@angular/core';
import {OtherInformationPayload} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-other-information-form',
    templateUrl: './other-information-form.component.html',
    styleUrls: ['./other-information-form.component.css'],
})
export class OtherInformationFormComponent implements OnInit {
    @Input('props')
    public props: { data: OtherInformationPayload };

    constructor() {}

    ngOnInit(): void {}
}

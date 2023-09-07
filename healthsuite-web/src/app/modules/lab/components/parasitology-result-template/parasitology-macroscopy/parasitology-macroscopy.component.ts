import {Component, Input, OnInit} from '@angular/core';
import { LabMacroscopy } from '@app/shared/_payload/lab/lab-result.payload';
import {LabResultPrepOrVerifyEnum} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-macroscopy',
    templateUrl: './parasitology-macroscopy.component.html',
    styleUrls: ['./parasitology-macroscopy.component.css'],
})
export class ParasitologyMacroscopyComponent implements OnInit {

    @Input('macroscopy')
    public macroscopy: LabMacroscopy;

    @Input('prepOrVerifyEnum')
    public prepOrVerifyEnum: LabResultPrepOrVerifyEnum;

    public resultVerify = LabResultPrepOrVerifyEnum.RESULT_VERIFICATION;

    constructor() {}

    ngOnInit(): void {}
}

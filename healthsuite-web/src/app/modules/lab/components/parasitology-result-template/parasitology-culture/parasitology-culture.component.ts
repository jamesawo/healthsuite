import { Component, Input, OnInit } from '@angular/core';
import {
    LabCulture,
    LabCultureLineOrganismData,
} from '@app/shared/_payload/lab/lab-result.payload';
import {LabResultPrepOrVerifyEnum} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-culture',
    templateUrl: './parasitology-culture.component.html',
    styleUrls: ['./parasitology-culture.component.css'],
})
export class ParasitologyCultureComponent implements OnInit {
    @Input('prepOrVerifyEnum')
    public prepOrVerifyEnum: LabResultPrepOrVerifyEnum;

    public resultVerify = LabResultPrepOrVerifyEnum.RESULT_VERIFICATION;
    @Input('culture') culture: LabCulture;
    lineData = LabCultureLineOrganismData;

    constructor() {}

    ngOnInit(): void {
    }

}

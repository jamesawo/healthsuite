import { Component, OnInit } from '@angular/core';
import {LabDepartmentResultType} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-result-prep',
    templateUrl: './parasitology-result-prep.component.html',
    styleUrls: ['./parasitology-result-prep.component.css'],
})
export class ParasitologyResultPrepComponent implements OnInit {
    type =  LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY;
    constructor() {}

    ngOnInit(): void {
    }

}

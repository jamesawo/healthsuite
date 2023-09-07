import { Component, OnInit } from '@angular/core';
import {
    LabDepartmentResultType,
    LabDepartmentTypeEnum,
} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-result-verification',
    templateUrl: './parasitology-result-verification.component.html',
    styleUrls: ['./parasitology-result-verification.component.css'],
})
export class ParasitologyResultVerificationComponent implements OnInit {
    parasitology = LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY;

    constructor() {}

    ngOnInit(): void {}
}

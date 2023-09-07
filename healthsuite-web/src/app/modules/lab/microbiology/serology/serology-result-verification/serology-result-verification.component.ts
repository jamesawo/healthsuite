import { Component, OnInit } from '@angular/core';
import { LabDepartmentResultType } from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-serology-result-verification',
    templateUrl: './serology-result-verification.component.html',
    styleUrls: ['./serology-result-verification.component.css'],
})
export class SerologyResultVerificationComponent implements OnInit {
    serology = LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY;

    constructor() {}

    ngOnInit(): void {}
}

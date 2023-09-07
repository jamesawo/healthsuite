import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { fadeInOnEnterAnimation, fadeOutOnLeaveAnimation } from 'angular-animations';

interface State {
    id: number;
    name: string;
}

const states: State[] = [
    { id: 0, name: 'Add Gender' },
    { id: 1, name: 'Add Marital Status' },
    { id: 2, name: 'Add Relationship' },
    { id: 3, name: 'Add Religion' },
    { id: 4, name: 'Add Surgery' },
    { id: 5, name: 'Add Roles' },
    { id: 6, name: 'Add Scheme' },
    { id: 7, name: 'Add Drug Classification' },
    { id: 8, name: 'Add Drug Formulation' },
    // {id: 9, name: 'Add Mark Up Class'},
    { id: 10, name: 'Add Department' },
    { id: 11, name: 'Add Bed' },
    { id: 12, name: 'Add Revenue Department' },
    { id: 13, name: 'Add Speciality Unit' },
    // { id: 14, name: 'Add Service' },
    { id: 15, name: 'Add Pharmacy Patient Category' },
    { id: 16, name: 'Add Pharmacy Supplier Category' },
    { id: 17, name: 'Add Nursing Note Label' },
    { id: 18, name: 'Add Lab Specimen' },
    { id: 19, name: 'Add Organism' },
    { id: 20, name: 'Add Antibiotics' },
    { id: 21, name: 'Add Bill Waiver Category' },
    { id: 22, name: 'Add Ethnic Group' },
    { id: 23, name: 'Add Nationality' },
    { id: 24, name: 'Add Means of Identification' },
];

@Component({
    selector: 'app-seed-data',
    templateUrl: './seed-data.component.html',
    styleUrls: ['./seed-data.component.css'],
    animations: [fadeOutOnLeaveAnimation(), fadeInOnEnterAnimation()],
})
export class SeedDataComponent implements OnInit {
    //todo:: @viewChild/@viewChildren to display selected content, and remove switch case
    public model: number = undefined;
    public items: State[] = states;

    constructor() {}

    ngOnInit() {}
}

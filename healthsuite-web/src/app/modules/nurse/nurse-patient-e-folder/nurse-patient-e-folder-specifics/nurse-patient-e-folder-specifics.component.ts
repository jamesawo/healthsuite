import { Component, OnInit } from '@angular/core';
import { CardNoteActivityEnum } from '@app/shared/_payload';

@Component({
    selector: 'app-nurse-patient-e-folder-specifics',
    templateUrl: './nurse-patient-e-folder-specifics.component.html',
    styleUrls: ['./nurse-patient-e-folder-specifics.component.css'],
})
export class NursePatientEFolderSpecificsComponent implements OnInit {
    public typeList: string[] = [];
    public specificTypes: string[] = [];
    drugChart = CardNoteActivityEnum.DRUG_CHART;
    nurseNote = CardNoteActivityEnum.NURSE_NOTE;

    constructor() {}

    ngOnInit(): void {
        this.typeList = Object.keys(CardNoteActivityEnum);
    }

    public onSpecificTypeCheck(value: string, event: any) {
        if (value) {
            const checked = event.target.checked;
            if (checked === true) {
                this.specificTypes.push(value);
            } else {
                const index = this.specificTypes.findIndex((v) => v === value);
                this.specificTypes.splice(index, 1);
            }
        }
    }

    public formatItemName(value: string): string {
        let title = value.replace(/_/g, ' ');
        if (value === this.drugChart) {
            title += '<small>(Allergies, Administration)</small>';
        } else  if (value === this.nurseNote) {
            title = 'NOTES <small>(DOCTOR, NURSE)</small>';
        }
        return title;
    }

    public onGetSelectedRecords(): string[] {
        return this.specificTypes;
    }
}

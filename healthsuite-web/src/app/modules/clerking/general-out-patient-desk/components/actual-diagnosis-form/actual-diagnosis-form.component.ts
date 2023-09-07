import { Component, Input, OnInit } from '@angular/core';
import {
    ActualDiagnosisFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-actual-diagnosis-form',
    templateUrl: './actual-diagnosis-form.component.html',
    styleUrls: ['./actual-diagnosis-form.component.css'],
})
export class ActualDiagnosisFormComponent implements OnInit {
    @Input('props')
    public props: { data: ActualDiagnosisFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}

    public isAllCheckBoxChecked(): boolean {
        if (this.props?.data?.diseasesArray?.length) {
            return this.props.data.diseasesArray.every((p) => p.checked);
        } else {
            return false;
        }
    }

    public checkAllCheckBox(ev): void {
        if (this.props?.data?.diseasesArray?.length) {
            this.props.data.diseasesArray.forEach((x) => (x.checked = ev.target.checked));
        }
    }

    public onRemoveAllCheckedBillItems() {
        if (this.props.data.diseasesArray.length) {
            if (this.isAllCheckBoxChecked()) {
                for (let i = 0; i < this.props.data.diseasesArray.length; i++) {
                    this.props.data.diseasesArray.splice(i, 1);
                    this.props.data.diseases.splice(i, 1);
                }
            } else {
                for (let i = 0; i < this.props.data.diseasesArray.length; i++) {
                    if (this.props.data.diseasesArray[i].checked) {
                        this.props.data.diseasesArray.splice(i, 1);
                        this.props.data.diseases.splice(i, 1);
                    }
                }
            }
        }
    }

    public onDiseaseSelected(disease: { id: number; name: string; code: string }) {
        if (disease) {
            const { id, name, code } = disease;
            const newDisease = { id, name, code, checked: false };
            this.props.data.diseasesArray.push(newDisease);
            this.props.data.diseases.push(id);
        }
    }
}

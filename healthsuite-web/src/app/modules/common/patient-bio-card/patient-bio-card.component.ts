import { Component, Input, OnInit } from '@angular/core';
import {PatientCategoryEnum, PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {CommonService} from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-patient-bio-card',
    templateUrl: './patient-bio-card.component.html',
    styleUrls: ['./patient-bio-card.component.css'],
})
export class PatientBioCardComponent implements OnInit {
    @Input() selectedPatient: PatientPayload;
    public scheme: PatientCategoryEnum = PatientCategoryEnum.SCHEME;

    constructor(private commonService: CommonService) {}

    ngOnInit(): void {}

    public renderPatientImage(image: string) {
        return this.commonService.onGetBase64AsImageUrl(image);
    }
}

import { Component, Input, OnInit } from '@angular/core';
import {
    LabMicroscopy, LabMicroscopySfaType,
    LabMicroscopySmearType,
    LabMicroscopyTypeEnum,
    LabMicroscopyWetAmountTypeEnum,
} from '@app/shared/_payload/lab/lab-result.payload';
import {LabResultPrepOrVerifyEnum} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
    selector: 'app-parasitology-microscopy',
    templateUrl: './parasitology-microscopy.component.html',
    styleUrls: ['./parasitology-microscopy.component.css'],
})
export class ParasitologyMicroscopyComponent implements OnInit {
    @Input('microscopy') microscopy: LabMicroscopy;
    @Input('prepOrVerifyEnum')
    public prepOrVerifyEnum: LabResultPrepOrVerifyEnum;

    public resultVerify = LabResultPrepOrVerifyEnum.RESULT_VERIFICATION;

    // microscopy Type
    microscopyTypeEnumNa = LabMicroscopyTypeEnum.NA;
    microscopyTypeEnumSfa = LabMicroscopyTypeEnum.SFA;
    microscopyTypeEnumSmear = LabMicroscopyTypeEnum.SMEAR;
    microscopyTypeEnumUrinalysis = LabMicroscopyTypeEnum.URINALYSIS;
    microscopyTypeEnumWetAmount = LabMicroscopyTypeEnum.WET_AMOUNT;

    // microscopy weAmountType
    weAmountTypeStainned = LabMicroscopyWetAmountTypeEnum.STAINED;
    weAmountTypeUnstained = LabMicroscopyWetAmountTypeEnum.UNSTAINED;

    // microscopy smearType
    smearTypeGram = LabMicroscopySmearType.GRAM;
    smearTypeGiemsa = LabMicroscopySmearType.GIEMSA;
    smearTypeZeihlNeelsen = LabMicroscopySmearType.ZEIHL_NEELSEN;
    smearTypeOthers = LabMicroscopySmearType.OTHERS;

    // microscopy sfaType
    sfaTypeMachine = LabMicroscopySfaType.MACHINE;
    sfaTypeManual = LabMicroscopySfaType.MANUAL;

    constructor() {}

    ngOnInit(): void {
        // this.microscopy.type = this.microscopyTypeEnumNa;
        // this.microscopy.wetAmount = {...this.microscopy.wetAmount, type: this.weAmountTypeStainned};
        // this.microscopy.smear =  {...this.microscopy.smear, type: this.smearTypeGram, gram: {}};
        // this.microscopy.sfa = {...this.microscopy.sfa, sfaType: this.sfaTypeMachine, machine: {}, manual: {}};
    }
}

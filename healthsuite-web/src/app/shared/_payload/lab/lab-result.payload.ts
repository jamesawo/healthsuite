// all lab result template should implemets this interface ILabResultComponent
export interface ILabResultComponent {
    payload: any;

    onLabSaveResultPreparation();
}

export class LabParasitologyTemplatePayload {
    macroscopy: LabMacroscopy;
    microscopy: LabMicroscopy;
    culture: LabCulture;
    newLabNote: string;
    comment: string;

    constructor() {
        this.macroscopy = {};
        this.microscopy = {
            urinalysis: {},
            sfa: {
                sfaType: LabMicroscopySfaType.MACHINE,
                machine: {},
                manual: {},
            },
            wetAmount: {
                type: LabMicroscopyWetAmountTypeEnum.STAINED,
            },
            smear: {
                type: LabMicroscopySmearType.GRAM,
                gram: {},
                others: {},
                zeihlNeelsen: {},
                giemsa: {}
            },
            type: LabMicroscopyTypeEnum.NA,
        };
        this.culture = {};
        this.newLabNote = '';
        this.comment = '';
    }
}

export interface LabMacroscopy {
    appearance?: string;
    colour?: string;
    size?: string;
    consistency?: string;
    plateletCount?: string;
    levelOfParasitemia?: string;
    protein?: string;
}

export enum LabMicroscopyTypeEnum {
    NA = 'NA',
    WET_AMOUNT = 'WET_AMOUNT',
    SMEAR = 'SMEAR',
    SFA = 'SFA',
    URINALYSIS = 'URINALYSIS',
}

export enum LabMicroscopyWetAmountTypeEnum {
    STAINED = 'STAINED',
    UNSTAINED = 'UNSTAINED',
}

export enum LabMicroscopySmearType {
    GRAM = 'GRAM',
    GIEMSA = 'GIEMSA',
    ZEIHL_NEELSEN = 'ZEIHL_NEELSEN',
    OTHERS = 'OTHERS',
}

export enum LabMicroscopySfaType {
    MACHINE = 'MACHINE',
    MANUAL = 'MANUAL',
}

export interface LabMicroscopySmearGram {
    morphology?: string;
    reaction?: string;
    result?: string;
}

export interface LabMicroscopySmearZeihlNeelsen {
    morphology?: string;
    reaction?: string;
    parasite?: string;
    stage?: string;
    count?: string;
}

export interface LabMicroscopySmearOther {
    result?: string;
    morphology?: string;
    reaction?: string;
    parasite?: string;
    stage?: string;
    count?: string;
    wbc?: string;
    rbc?: string;
    cast?: string;
    whiff?: string;
    epithelials?: string;
    fungi?: string;
    bacteria?: string;
    pusCell?: string;
}

export interface LabMicroscopySmearGiemsa {
    morphology?: string;
    reaction?: string;
    parasite?: string;
    stage?: string;
    count?: string;
    wbc?: string;
    rbc?: string;
    cast?: string;
    whiff?: string;
    epithelials?: string;
    fungi?: string;
    bacteria?: string;
    pusCell?: string;
}

export interface LabMicroscopySmear {
    type?: LabMicroscopySmearType;
    gram?: LabMicroscopySmearGram;
    giemsa?: LabMicroscopySmearGiemsa;
    zeihlNeelsen?: LabMicroscopySmearZeihlNeelsen;
    others?: LabMicroscopySmearOther;
}

export interface LabMicroscopyWetAmount {
    type?: LabMicroscopyWetAmountTypeEnum;
    morphology?: string;
    reaction?: string;
    parasite?: string;
    stage?: string;
    count?: string;
    wbc?: string;
    rbc?: string;
    cast?: string;
    whiff?: string;
    epithelials?: string;
    fungi?: string;
    bacteria?: string;
    pusCell?: string;
    crystal?: string;
    yeast?: string;
    pVaginalis?: string;
    others?: string;
}

export interface LabMicroscopySfaManual {
    activelyMotileSpermCount?: string;
    nonMotileSpermCount?: string;
    sluggishlyMotileSpermCount?: string;
    totalSpermCount?: string;
}

export interface LabMicroscopySfaMachine {
    motility?: string;
    totalAllSperm?: string;
    immotility?: string;
    totalMotSperm?: string;
    normMorph?: string;
    totalFuncSperm?: string;
    tsc?: string;
    fsc?: string;
    smi?: string;
    msc?: string;
}

export interface LabMicroscopySfa {
    morphology?: string;
    reaction?: string;
    abstinence?: string;
    toTest?: string;
    volume?: string;
    liquefaction?: string;
    viscosity?: string;
    ph?: string;
    wbc?: string;
    rbc?: string;
    immatureCells?: string;
    antiSpermAb?: string;
    vitality?: string;
    odour?: string;
    appearance?: string;
    wbcCone?: string;
    aggultination?: string;
    aggregation?: string;
    acrosomeT?: string;
    timeOfProduction?: string;
    timeReceived?: string;
    timeOfExamination?: string;
    modeOfProduction?: string;
    sfaType?: LabMicroscopySfaType;
    machine?: LabMicroscopySfaMachine;
    manual?: LabMicroscopySfaManual;
}

export interface LabMicroscopyUrinalysis {
    morphology?: string;
    reaction?: string;
    wbc?: string;
    rbc?: string;
    cast?: string;
    epithelials?: string;
    ph?: string;
    appearance?: string;
    ketone?: string;
    urobilinogen?: string;
    nitrite?: string;
    trichomonasVaginalis?: string;
    blood?: string;
    glucose?: string;
    specificGravity?: string;
    crystal?: string;
    bilirubin?: string;
    ascrobicAcid?: string;
    bacteria?: string;
    protein?: string;
    yeast?: string;
    leucocyte?: string;
    other?: string;
}

export interface LabMicroscopy {
    type?: LabMicroscopyTypeEnum;
    wetAmount?: LabMicroscopyWetAmount;
    smear?: LabMicroscopySmear;
    sfa?: LabMicroscopySfa;
    urinalysis?: LabMicroscopyUrinalysis;
}

export interface LabCultureLineOrganism {
    organism?: string;
    selected?: boolean;
    antibiotics?: { name: string; degree?: string; level?: string }[];
}

export interface LabCulture {
    temperature?: string;
    duration?: string;
    atmosphere?: string;
    plate?: string;
    incubation?: string;
    lineOrganism?: LabCultureLineOrganism[];
}

export const LabCultureLineOrganismData: LabCultureLineOrganism[] = [
    { organism: 'Plasmodium', selected: false, antibiotics: [{ name: 'Arte-quine' }, { name: 'ciprofloxacin' }] },
    { organism: 'Pseudomonas spp.', selected: false, antibiotics: [{ name: 'SPP' }] },
    {
        organism: 'E.coli', selected: false,
        antibiotics: [
            { name: 'Ampiciline' },
            { name: 'Cypro' },
            { name: 'Gentimicin' },
            { name: 'Penecillin' },
        ],
    },
];

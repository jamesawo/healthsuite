import {DatePayload, GenderPayload} from '@app/shared/_payload';
import { YesNoEnum } from '@app/shared/_payload/clerking/outpatient-desk.payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {DepartmentPayload} from '@app/modules/settings';
import {UserPayload} from '@app/modules/settings/_payload/userPayload';

export class NurseAncPayload {}

export enum PregnancyOutcomeEnum {
    DELIVERED = 'DELIVERED',
    MISCARRIAGE = 'MISCARRIAGE',
    STILL_BIRTH = 'STILL_BIRTH',
}


export class ObPreviousMedHisPayload {
    constructor(
        public heartDisease: string = '',
        public chestDisease: string = '',
        public kidneyDisease: string = '',
        public bloodTransfusion: string = '',
        public others: string = ''
    ) {}
}

export class ObFamilyHisPayload {
    heartDisease: string;
    multiplePregnancy: string;
    hypertension: string;
    tuberculosis: string;
    others: string;

    constructor() {
        this.heartDisease = '';
        this.multiplePregnancy = '';
        this.hypertension = '';
        this.tuberculosis = '';
        this.others = '';
    }
}

export class ObPrevPregnancy {
    durationOfPregnancy: number;
    outcome: PregnancyOutcomeEnum;
    sex: GenderPayload;
    dateOfBirth: DatePayload | Date;
    birthWeight: number;
    alive: YesNoEnum;
    comment: string;

    constructor() {
        this.durationOfPregnancy = undefined;
        this.outcome = undefined;
        this.sex = undefined;
        this.dateOfBirth = undefined;
        this.birthWeight = undefined;
        this.alive = undefined;
        this.comment = undefined;
    }
}

export class ObHisOfPresentPregPayload {
    constructor(
        public bleeding: string = '',
        public discharge: string = '',
        public urinarySymptom: string = '',
        public swellingOfAnkles: string = '',
        public otherSymptoms: string = ''
    ) {}
}

export class ObPhysicalExamPayload {
    constructor(
        public generalCondition: string = '',
        public respiratorySystem: string = '',
        public cardiovascularSystem: string = '',
        public breastAndNipples: string = '',
        public abdomen: string = '',
        public vaginalExamination: string = '',
        public otherAbnormalities: string = '',
        public comment: string = '',
        public specialInstructions: string = ''
    ) {}
}

export class ObMeasurement {
    constructor(
        public weight: string = '',
        public height: string = '',
        public bloodPressure: string = '',
        public breastAndNipples: string = '',
        public abdomen: string = '',
        public vaginalExamination: string = '',
        public otherAbnormalities: string = '',
        public comment: string = '',
        public specialInstructions: string = ''
    ) {}
}

export class ObGeneralFormPayload {
    specialPoints: string;
    specialAttention: string;
    lmp: DatePayload | Date;
    edd: string;
    ga: string;

    constructor() {
        this.specialPoints = '';
        this.specialAttention = '';
        this.lmp = undefined;
        this.edd = '';
        this.ga = '';
    }
}

export class ObInstructionPayload {
    promontory: string;
    sacrum: string;
    sidewalls: string;
    iSchialSp: string;
    subPub: string;
    coccyx: string;
    comments: string;
    deliveryInstructions: string;

    constructor() {
        this.promontory = '';
        this.sacrum = '';
        this.sidewalls = '';
        this.iSchialSp = '';
        this.subPub = '';
        this.coccyx = '';
        this.comments = '';
        this.deliveryInstructions = '';
    }
}

export class ObstetricsHistoryPayload {
    patient: PatientPayload;
    generalForm: ObGeneralFormPayload;
    previousMedicalHistory: ObPreviousMedHisPayload;
    familyHistory: ObFamilyHisPayload;
    previousPregnancies: ObPrevPregnancy[];
    historyOfPresentPregnancy: ObHisOfPresentPregPayload;
    physicalExamination: ObPhysicalExamPayload;
    measurement: ObMeasurement;
    deliveryInstruction: ObInstructionPayload;
    location: DepartmentPayload;
    clerkedBy: UserPayload;

    constructor() {
        this.generalForm = new ObGeneralFormPayload();
        this.previousMedicalHistory = new ObPreviousMedHisPayload();
        this.familyHistory = new ObFamilyHisPayload();
        this.previousPregnancies = [];
        this.historyOfPresentPregnancy = new ObHisOfPresentPregPayload();
        this.physicalExamination = new ObPhysicalExamPayload();
        this.measurement = new ObMeasurement();
        this.patient = new PatientPayload();
        this.deliveryInstruction = new ObInstructionPayload();
        this.location = undefined;
        this.clerkedBy = undefined;
    }
}

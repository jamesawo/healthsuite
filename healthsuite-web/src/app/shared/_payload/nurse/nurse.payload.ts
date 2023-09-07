import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { DepartmentPayload } from '@app/modules/settings';
import { DatePayload, SharedPayload } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';

export class NursePayload {}

export class NurseWaitingPayload {
    id: number;
    patientId: number;
    waitingStatus: WaitingStatusEnum;
    patientName: string;
    patientNumber: string;
    patientCategory: string;
    clinicIds: number[];
    type: WaitingTypeEnum;

    constructor() {
        this.id = undefined;
        this.patientId = undefined;
        this.waitingStatus = undefined;
        this.patientName = undefined;
        this.patientNumber = undefined;
        this.patientCategory = undefined;
        this.clinicIds = [];
        this.type = undefined;
    }
}

export class AttendedPayload {
    id: number;
    attendedBy: UserPayload;
    patient: PatientPayload;
    clinic: DepartmentPayload;

    constructor() {
        this.id = undefined;
        this.attendedBy = undefined;
        this.patient = undefined;
        this.clinic = undefined;
    }
}

export enum WaitingTypeEnum {
    ADD = 'ADD',
    REMOVE = 'REMOVE',
}

export enum WaitingViewTypeEnum {
    NURSE = 'NURSE',
    DOCTOR = 'DOCTOR',
}

export class NurseWaitingHistoryPayload {
    id: number;
    status: WaitingStatusEnum;
    patient: PatientPayload;
    clinic: DepartmentPayload;
    attendedBy: UserPayload;

    constructor() {
        this.id = undefined;
        this.status = undefined;
        this.patient = undefined;
        this.clinic = undefined;
        this.attendedBy = undefined;
    }
}

export enum WaitingStatusEnum {
    WAITING = 'WAITING',
    ATTENDED = 'ATTENDED',
}

export enum CardNoteType {
    SPECIFIC = 'SPECIFIC',
    ALL = 'ALL',
}

export class PatientVitalSign {
    id: number;
    patient: PatientPayload;
    capturedBy: UserPayload;
    captureFromLocation: DepartmentPayload;
    weight: number;
    height: number;
    bodyMassIndex: number;
    temperature: number;
    bodySurfaceArea: number;
    respiratoryRate: number;
    pulseRate: number;
    systolicBp: number;
    diastolicBp: number;
    randomBloodSugar: number;
    fastBloodSugar: number;
    oxygenSaturation: number;
    painScore: number;
    urineAnalysis: number;
    commentRemark: string;
    assignTo: UserPayload;
    isNurse: boolean;
    isDoctor: boolean;

    constructor() {
        this.id = undefined;
        this.capturedBy = undefined;
        this.captureFromLocation = undefined;
        this.weight = 0;
        this.height = 0;
        this.bodyMassIndex = undefined;
        this.temperature = undefined;
        this.bodySurfaceArea = undefined;
        this.respiratoryRate = undefined;
        this.pulseRate = undefined;
        this.systolicBp = undefined;
        this.diastolicBp = undefined;
        this.randomBloodSugar = undefined;
        this.fastBloodSugar = undefined;
        this.patient = new PatientPayload();
        this.oxygenSaturation = undefined;
        this.painScore = undefined;
        this.urineAnalysis = undefined;
        this.commentRemark = undefined;
        this.assignTo = undefined;
        this.isNurse = false;
        this.isDoctor = false;
    }
}

export class NurseNotePayload {
    id: number;
    patient: PatientPayload;
    nurse: UserPayload;
    clinic: DepartmentPayload;
    label: SharedPayload;
    note: string;

    constructor() {
        this.id = undefined;
        this.patient = undefined;
        this.nurse = undefined;
        this.clinic = undefined;
        this.label = undefined;
        this.note = undefined;
    }
}

export class AntenatalBookingPayload {
    id: number;
    code: string;
    bookedBy: UserPayload;
    patient: PatientPayload;
    spouseName: string;
    spousePhoneNumber: string;
    spouseOccupation: string;
    spouseEmployer: string;
    caseConsultant: UserPayload;
    specialityUnit: SharedPayload;
    clinic: DepartmentPayload;
    bookedFrom: DepartmentPayload;

    constructor() {
        this.id = undefined;
        this.code = undefined;
        this.bookedBy = undefined;
        this.patient = undefined;
        this.bookedFrom = undefined;
        this.spouseName = undefined;
        this.spousePhoneNumber = undefined;
        this.spouseOccupation = undefined;
        this.spouseEmployer = undefined;
        this.caseConsultant = undefined;
        this.specialityUnit = {
            id: undefined,
            name: undefined,
            code: undefined,
        };
        this.clinic = undefined;
    }
}

export class PatientCardNotePayload {
    patient: PatientPayload;
    recordType: CardNoteType;
    startDate: DatePayload;
    endDate: DatePayload;
    specificTypes: string[];
    user: UserPayload;
    location: DepartmentPayload;

    constructor() {
        this.patient = undefined;
        this.recordType = undefined;
        this.startDate = undefined;
        this.endDate = undefined;
        this.specificTypes = [];
        this.user = undefined;
        this.location = undefined;
    }
}

export enum CardNoteActivityEnum {
    VITAL_SIGN = 'VITAL_SIGN',
    CONSULTATION = 'CONSULTATION',
    OPERATION_NOTE = 'OPERATION_NOTE',
    WARD_TRANSFER = 'WARD_TRANSFER',
    CLINIC_TRANSFER = 'CLINIC_TRANSFER',
    NURSE_NOTE = 'NURSE_NOTE',
    DIAGNOSIS = 'DIAGNOSIS',
    UPLOAD_FILES = 'UPLOAD_FILES',
    ANC_FOLLOW_UP_VISIT = 'ANC_FOLLOW_UP_VISIT',
    DRUG_CHART = 'DRUG_CHART',
    DRUG_REQUEST = 'DRUG_REQUEST',
    LAB_REQUEST = 'LAB_REQUEST',
    RADIOLOGY_REQUEST = 'RADIOLOGY_REQUEST',
    PROCEDURE_REPORT = 'PROCEDURE_REPORT',
    RADIOLOGY_REPORT = 'RADIOLOGY_REPORT',
    LAB_RESULT = 'LAB_RESULT',
    OBSTETRICS_HISTORY = 'OBSTETRICS_HISTORY',
}

export class VitalSignSearchPayload {
    startDate: DatePayload;
    endDate: DatePayload;
    patient: PatientPayload;

    constructor() {
        this.startDate = undefined;
        this.endDate = undefined;
        this.patient = undefined;
    }
}

export class VitalSignSearch {
    hasData = false;
    dateValues: string[] = [];
    timeValues: string[] = [];
    fastBloodSugarValues: number[] = [];
    randomBloodSugarValues: number[] = [];
    painScoreValues: number[] = [];
    oxygenSaturationValues: number[] = [];
    pulseValues: number[] = [];
    respRateValues: number[] = [];
    bpDiastolicValues: number[] = [];
    bpSystolicValues: number[] = [];
    tempValues: number[] = [];
}

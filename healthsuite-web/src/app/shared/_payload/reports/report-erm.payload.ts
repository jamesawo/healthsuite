import { DatePayload } from '@app/shared/_payload';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { WardPayload } from '@app/modules/settings';

export enum AdmissionSessionTypeEnum {
    ALL = 'ALL',
    PREVIOUS = 'PREVIOUS',
    CURRENT = 'CURRENT',
}

export class InterimSearchPayload {
    visitType: AdmissionSessionTypeEnum;
    startDate: DatePayload;
    endDate: DatePayload;
    patient: PatientPayload;
    session: SearchAdmissionPayload;
    admissionCode: string;

    constructor() {
        this.visitType = AdmissionSessionTypeEnum.CURRENT;
        this.startDate = undefined;
        this.endDate = undefined;
        this.patient = undefined;
        this.session = new SearchAdmissionPayload();
        this.admissionCode = undefined;
    }
}

export class SearchAdmissionPayload {
    admittedDate: Date;
    dischargedDate: Date;
    admittedTime: string;
    dischargedTime: string;
    patient: PatientPayload;
    isOnAdmission: boolean;
    admissionNumber: string;
    ward: WardPayload;
    bed: string;
    netAmount: number;

    constructor() {
        this.admittedDate = undefined;
        this.dischargedDate = undefined;
        this.patient = new PatientPayload();
        this.isOnAdmission = false;
        this.admissionNumber = undefined;
        this.ward = undefined;
        this.bed = undefined;
        this.admittedTime = undefined;
        this.dischargedTime = undefined;
        this.netAmount = 0;
    }
}

import {PatientVitalSign, WaitingStatusEnum} from '@app/shared/_payload';
import {PatientCategoryEnum, PatientPayload} from '@app/shared/_payload/erm/patient.payload';

export class DoctorWaitingPayload {
    id: number;
    patientId: number;
    waitingStatus: WaitingStatusEnum;
    patientName: string;
    patientNumber: string;
    patientCategory: PatientCategoryEnum;
    doctorId: number;
    clinicId: number;

    constructor() {
        this.id = undefined;
        this.patientId = undefined;
        this.waitingStatus = undefined;
        this.patientName = undefined;
        this.patientNumber = undefined;
        this.patientCategory = undefined;
        this.doctorId = undefined;
        this.clinicId = undefined;
    }
}

export interface IVitalTabData {
    isTakeVital: boolean;
    patientId: number;
}

export interface IDoctorRequestPayload {
    patientId?: number;
}

export class ClerkingTabsPayload {
    patientVital: PatientVitalSign;
    drugRequest: any;
    labRequest: any;
    radiologyRequest: any;
    procedureRequest: any;
    admissionRequest: any;
    referralRequest: any;
    appointmentRequest: any;

}

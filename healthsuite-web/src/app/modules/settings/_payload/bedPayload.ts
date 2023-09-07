import { DepartmentPayload } from '@app/modules/settings';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DatePayload } from '@app/shared/_payload';

export class BedPayload {
    constructor(
        public id: number = null,
        public isOccupied = false,
        public code = '',
        public currentState = '',
        public numberOfBedsToCreate = 1,
        public isWardAnObject: boolean = true
    ) {}
}

export class WardPayload {
    id?: number;
    totalBedCount?: number;
    occupiedBedCount?: number;
    percentageCount?: number;
    code?: string;
    departmentId?: number;
    beds?: BedPayload[];
    department?: DepartmentPayload;
    title?: string;
    numberOfBedsToUpdate?: number;
    type: WardUpdateTypeEnum;
}

export enum WardUpdateTypeEnum {
    ADD_BED,
    REMOVE_BED,
}

export class AdmissionPayload {
    id: number;
    patient?: PatientPayload;
    patientId?: number;
    ward?: WardPayload;
    wardId?: number;
    bed?: BedPayload;
    bedId?: number;
    admissionDate?: DatePayload;
    dischargedDate?: DatePayload;
    consultant?: UserPayload;
    consultantId?: number;
    admissionCode?: string;
    admissionStatus: PatientAdmissionStatus;
    admittedBy?: UserPayload;
    location?: DepartmentPayload;
    admittedTime?: string;

    constructor() {
        this.id = undefined;
        this.patient = undefined;
        this.patientId = undefined;
        this.ward = undefined;
        this.wardId = undefined;
        this.bed = undefined;
        this.bedId = undefined;
        this.admissionDate = undefined;
        this.dischargedDate = undefined;
        this.consultant = undefined;
        this.consultantId = undefined;
        this.admissionCode = undefined;
        this.admissionStatus = undefined;
        this.admittedBy = undefined;
        this.location = undefined;
        this.admittedTime = '';
    }
}

export enum PatientAdmissionStatus {
    ADMISSION,
    DISCHARGED,
}

import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {UserPayload} from '@app/modules/settings/_payload/userPayload';
import {DepartmentPayload} from '@app/modules/settings';

export class NurseCarePayload {
    patient: PatientPayload;
    constructor() {
        this.patient = new PatientPayload();
    }
}

export class NursePatientFluid {
    id: number;
    inputType: string;
    blood: number;
    tube: number;
    oral: number;
    iv: number;
    totalIntake: number;
    balance: number;
    totalOutput: number;
    urine: number;
    tubeVomit: number;
    drainFaeces: number;
    outputType: string;
    patient: PatientPayload;
    captureBy: UserPayload;
    capturedFrom: DepartmentPayload;
    additionalInformation: string;

    constructor() {
        this.id = undefined;
        this.inputType = '';
        this.blood = 0;
        this.tube = 0;
        this.oral = 0;
        this.iv = 0;
        this.totalIntake = 0;
        this.balance = 0;
        this.totalOutput = 0;
        this.urine = 0;
        this.tubeVomit = 0;
        this.drainFaeces = 0;
        this.outputType = '';
        this.patient = new PatientPayload();
        this.captureBy = undefined;
        this.capturedFrom = undefined;
        this.additionalInformation = '';
    }

}

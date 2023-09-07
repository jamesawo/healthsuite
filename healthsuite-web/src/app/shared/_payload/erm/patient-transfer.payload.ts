import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {AdmissionPayload, BedPayload, DepartmentPayload, WardPayload} from '@app/modules/settings';
import {DatePayload} from '@app/shared/_payload';
import {UserPayload} from '@app/modules/settings/_payload/userPayload';

export class PatientTransferPayload {
    patient: PatientPayload;
    newWard: WardPayload;
    transferDate: DatePayload;
    transferNote: string;
    newBed: BedPayload;
    consultant: UserPayload;
    location: DepartmentPayload;
    user: UserPayload;
    currentAdmission: AdmissionPayload;

    constructor() {
        this.patient = new PatientPayload();
        this.newWard = undefined;
        this.transferDate = undefined;
        this.transferNote = '';
        this.newBed = new BedPayload();
        this.consultant = undefined;
        this.location = undefined;
        this.user = undefined;
    }
}

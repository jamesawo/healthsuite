import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { DrugRegisterPayload } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { DatePayload, ProductServicePayload } from '@app/shared/_payload';

// main clerking  request payload wrapper
export class ClerkDoctorRequestPayload {
    id: number;
    patient: PatientPayload;
    doctor: UserPayload;
    department: DepartmentPayload;
    date: Date;
    datePayload: DatePayload;
    labRequest?: ClerkLabRequestPayload;
    drugRequest?: ClerkDrugRequestPayload;
    radiologyRequest?: ClerkRadiologyRequestPayload;

    constructor(id?: number) {
        this.id = id || undefined;
        this.patient = undefined;
        this.doctor = undefined;
        this.department = undefined;
        this.date = new Date();
        this.datePayload = undefined;
        this.drugRequest = new ClerkDrugRequestPayload();
        this.labRequest = new ClerkLabRequestPayload();
        this.radiologyRequest = new ClerkRadiologyRequestPayload();
    }
}

// drug request payload
export class ClerkDrugRequestPayload {
    date: Date;
    otherInformation: string;
    drugItems: ClerkDrugItemsPayload[];
    useDepartmentFilter: boolean;
    outlet: number;
    excludeZeroQty: boolean;
    physician: UserPayload;
    doctorRequestId: number;
    patientDto: PatientPayload;

    constructor() {
        this.otherInformation = undefined;
        this.drugItems = [];
        this.useDepartmentFilter = false;
        this.outlet = 0;
        this.excludeZeroQty = false;
        this.physician = new UserPayload();
        this.date = undefined;
        this.doctorRequestId = undefined;
        this.patientDto = undefined;
    }
}

// lab request payload
export class ClerkLabRequestPayload {
    id: number;
    otherInformation: string;
    labItems: ClerkLabItemsPayload[];
    physician: UserPayload;
    date: Date;
    doctorRequestId: number;


    constructor() {
        this.id = undefined;
        this.otherInformation = undefined;
        this.labItems = [];
        this.physician = new UserPayload();
        this.date = undefined;
        this.doctorRequestId = undefined;
    }
}

// radiology request payload
export class ClerkRadiologyRequestPayload {
    id: number;
    previousImagingDetails: string;
    previousOperationDetails: string;
    patientCurrentStatus: string;
    otherInformation: string;
    radiologyItems: ClerkRadiologyItemsPayload[];
    physician: UserPayload;
    date: Date;
    doctorRequestId: number;


    constructor() {
        this.id = undefined;
        this.previousImagingDetails = undefined;
        this.previousOperationDetails = undefined;
        this.patientCurrentStatus = undefined;
        this.otherInformation = undefined;
        this.radiologyItems = [];
        this.physician = new UserPayload();
        this.date = undefined;
        this.doctorRequestId = undefined;
    }
}

export class ClerkRadiologyItemsPayload {
    id: number;
    service: ProductServicePayload;
    examinationRequired: string;
    comment: string;

    constructor() {
        this.id = undefined;
        this.service = undefined;
        this.examinationRequired = undefined;
        this.comment = undefined;
    }
}

// drug items in drug request payload
export class ClerkDrugItemsPayload {
    id: number;
    drugRegister: DrugRegisterPayload;
    dosage: number;
    frequency: string;
    days: number;
    adminRoute: string;
    checked: boolean;
    nurseComment: string;
    isAdministered: boolean;
    administeredBy: UserPayload;
    userId: number;
    locationId: number;
    patientId: number;

    constructor() {
        this.id = undefined;
        this.drugRegister = undefined;
        this.dosage = undefined;
        this.frequency = undefined;
        this.days = undefined;
        this.adminRoute = undefined;
        this.checked = false;
        this.nurseComment = undefined;
        this.isAdministered = false;
        this.administeredBy = undefined;
        this.userId = undefined;
        this.locationId = undefined;
        this.patientId = undefined;
    }
}

// lab items in lab request payload
export class ClerkLabItemsPayload {
    id: number;
    service: ProductServicePayload;
    comment: string;
    specimen: string;
    checked: boolean;

    constructor() {
        this.id = undefined;
        this.service = undefined;
        this.comment = undefined;
        this.checked = false;
    }
}

export class ClerkDoctorRequestSearch {
    startDate: DatePayload;
    endDate: DatePayload;
    patientId: number;

    constructor() {
        this.patientId = undefined;
        this.startDate = { year: undefined, month: undefined, day: undefined };
        this.endDate = { year: undefined, month: undefined, day: undefined };
    }
}

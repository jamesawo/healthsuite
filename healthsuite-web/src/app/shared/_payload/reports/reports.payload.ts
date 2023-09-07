import { DepartmentPayload } from '@app/modules/settings';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DatePayload, GenderPayload } from '@app/shared/_payload';
import { PatientPayload, PatientType } from '@app/shared/_payload/erm/patient.payload';

export class ReportsPayload {}

export class PatientRegisterPayload {
    id: number;
    department: DepartmentPayload;
    user: UserPayload;
    startDate: DatePayload;
    endDate: DatePayload;
    patient: PatientPayload;
    page: PagePayload;
    type: PatientType;
    gender: GenderPayload;

    constructor() {
        this.id = undefined;
        this.department = undefined;
        this.user = undefined;
        this.startDate = undefined;
        this.endDate = undefined;
        this.patient = undefined;
        this.page = new PagePayload();
        this.type = undefined;
        this.gender = undefined;
    }
}

export class PagePayload {
    pageSize: number;
    pageNumber: number;
    totalPages: number;
    totalElements: number;

    constructor() {
        this.pageSize = 10;
        this.pageNumber = 1;
        this.totalPages = undefined;
        this.totalElements = undefined;
    }
}

export class PageResultPayload<T> {
    page: PagePayload;
    result: T[];

    constructor() {
        this.page = new PagePayload();
        this.result = [];
    }
}

export enum ChargeSheetCategoryEnum {
    BOTH = 'BOTH',
    SERVICE_DEPARTMENT = 'SERVICE_DEPARTMENT',
    REVENUE_DEPARTMENT = 'REVENUE_DEPARTMENT',
}


import { ProductServicePayload, SharedPayload } from '@app/shared/_payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { ParamRangeEnum } from '@app/shared/_payload/lab/lab.payload';

export class LabSetupPayload {}

export class LabParameterSetupPayload {
    id: number;
    test: ProductServicePayload;
    specimen: SharedPayload;
    specimenColor: string;
    capturedBy: UserPayload;
    capturedFrom: DepartmentPayload;
    parameterSetupItems: LabParameterSetupItemPayload[];
    viewType: ParamRangeEnum;
    isRequirePathologist: boolean;
    isParasitologTest: boolean;
    isHistopathologySFA: boolean;
    isBoneMarrowTest: boolean;
    isSpecialTest: boolean;
    isImmunoTest: boolean;
    departmentTypeEnum: LabDepartmentTypeEnum;

    constructor() {
        this.id = undefined;
        this.test = undefined;
        this.specimen = undefined;
        this.specimenColor = undefined;
        this.isRequirePathologist = false;
        this.isParasitologTest = false;
        this.capturedBy = undefined;
        this.capturedFrom = undefined;
        this.parameterSetupItems = [];
        this.viewType = ParamRangeEnum.PARAM;
        this.isHistopathologySFA = false;
        this.isBoneMarrowTest = false;
        this.isSpecialTest = false;
        this.isImmunoTest = false;
        this.departmentTypeEnum = undefined;
    }
}

export class LabParameterSetupItemPayload {
    id: number;
    parameter: SharedPayload;
    parameterHierarchy: number;

    constructor() {
        this.id = undefined;
        this.parameterHierarchy = 0;
        this.parameter = undefined;
    }
}

export class LabParamRangePayload {
    id: number;
    test: ProductServicePayload;
    labParameterSetupItem: LabParameterSetupItemPayload;
    capturedBy: UserPayload;
    capturedFrom: DepartmentPayload;
    rangeItems: LabParamRangeItemPayload[];

    constructor() {
        this.id = undefined;
        this.test = undefined;
        this.labParameterSetupItem = undefined;
        this.capturedBy = undefined;
        this.capturedFrom = undefined;
        this.rangeItems = [];
    }
}

export interface InputParams {
    inputName: string;
    inputUnit: string;
    inputLower: number;
    inputHigher: number;
}

export class LabParamRangeItemPayload {
    id: number;
    name: string;
    unit: string;
    lowerLimit: number;
    upperLimit: number;

    constructor() {
        this.id = undefined;
        this.name = undefined;
        this.unit = undefined;
        this.lowerLimit = undefined;
        this.upperLimit = undefined;
    }
}

export enum LabDepartmentTypeEnum {
    CHEMICAL = 'CHEMICAL',
    ANATOMICAL = 'ANATOMICAL',
    HAEMATOLOGY = 'HAEMATOLOGY',
    MICROBIOLOGY = 'MICROBIOLOGY',
    GENERAL = 'GENERAL',
}

export enum LabResultPrepOrVerifyEnum {
    RESULT_PREPARATION,
    RESULT_VERIFICATION
}

export enum LabDepartmentResultType {
    MICROBIOLOGY_SEROLOGY,
    MICROBIOLOGY_PARASITOLOGY
}


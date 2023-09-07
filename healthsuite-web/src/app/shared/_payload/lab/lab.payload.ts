import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { PatientBill } from '@app/shared/_payload/bill-payment/bill.payload';
import { DepartmentPayload } from '@app/modules/settings';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { SharedPayload } from '@app/shared/_payload';
import {LabDepartmentTypeEnum, LabResultPrepOrVerifyEnum} from '@app/shared/_payload/lab/lab-setup.payload';
import {LabParasitologyTemplatePayload} from '@app/shared/_payload/lab/lab-result.payload';

export class LabPayload {}

export class LabParameterPayload {
    id: number;
    title: string;
    checked: boolean;

    constructor(title?: string) {
        this.id = undefined;
        this.title = title || undefined;
        this.checked = false;
    }
}

export class LabSpecimenCollectionPayload {
    id: number;
    patient: PatientPayload;
    searchByEnum: LabSearchByEnum;
    newOrEditSampleEnum: NewOrEditSampleEnum;
    labBillTestRequest: LabTestRequestPayload;
    clinicalSummary: string;
    provisionalDiagnosis: string;
    otherInformation: string;
    capturedFrom: DepartmentPayload;
    capturedBy: UserPayload;
    isAcknowledgeSpecimen: boolean;
    labNumber: string;

    constructor() {
        this.id = undefined;
        this.patient = new PatientPayload();
        this.searchByEnum = undefined;
        this.newOrEditSampleEnum = undefined;
        this.labBillTestRequest = new LabTestRequestPayload();
        this.clinicalSummary = undefined;
        this.provisionalDiagnosis = undefined;
        this.otherInformation = undefined;
        this.capturedFrom = undefined;
        this.capturedBy = undefined;
        this.isAcknowledgeSpecimen = false;
        this.labNumber = undefined;
    }
}

export enum NewOrEditSampleEnum {
    NEW = 'NEW',
    EDIT = 'EDIT',
}

export enum LabSearchByEnum {
    INVOICE_NUMBER = 'INVOICE_NUMBER',
    RECEIPT_NUMBER = 'RECEIPT_NUMBER',
    LAB_NUMBER = 'LAB_NUMBER',
    PATIENT = 'PATIENT',
}

export enum ParamRangeEnum {
    PARAM = 'PARAM',
    RANGE = 'RANGE',
}

export class LabTestRequestPayload {
    id: number;
    invoiceNumber: string;
    receiptNumber: string;
    labNumber: string;
    date: string;
    time: string;
    patient: PatientPayload;
    requestLab: any; // replace with requestLab type
    isDoctorRequest: boolean;
    bill: PatientBill = new PatientBill();
    code: string;
    testItems: LabTestRequestItem[];

    constructor() {
        this.id = undefined;
        this.invoiceNumber = undefined;
        this.receiptNumber = undefined;
        this.labNumber = undefined;
        this.date = undefined;
        this.time = undefined;
        this.patient = undefined;
        this.requestLab = undefined;
        this.isDoctorRequest = false;
        this.code = undefined;
        this.testItems = [];
    }
}

export enum BatchOrSingleEnum {
    BATCH = 'BATCH',
    SINGLE = 'SINGLE'
}

export class LabTestRequestItem {
    id: number;
    requestNumber: string;
    testName: string;
    specimenDto: SharedPayload;
    specimenStatus: boolean;
    acknowledgement: string;
    collectedBy?: string;

    constructor() {
        this.id = undefined;
        this.requestNumber = undefined;
        this.testName = undefined;
        this.specimenDto = undefined;
        this.specimenStatus = undefined;
        this.collectedBy = undefined;
    }
}

export class LabTrackerPayload {
    id: number;
    billType: string;
    paymentStatus: string;
    labTestCode: string;
    isSpecimenCollected: boolean;
    specimenCollectedByUser: string;
    specimenCollectedDate: string;

    isSpecimenAck: boolean;
    specimenAckByUser: string;
    specimenAckDate: string;
    specimenAckStatus: string;

    isLabTestPrepared: boolean;
    specimenPrepByUser: string;
    specimenPrepDate: string;

    isLabTestVerified: boolean;
    testVerifiedByUser: string;
    testVerifiedDate: string;
    testVerifiedApproved: string;

    isPathologistVerified: boolean;
    testVerifiedByPathologistUser: string;
    testVerifiedByPathologistDate: string;
    testVerifiedByPathologistApproved: string;

    constructor() {
        this.id = undefined;
        this.billType = undefined;
        this.paymentStatus = undefined;
        this.labTestCode = undefined;
        this.isSpecimenCollected = undefined;
        this.specimenCollectedByUser = undefined;
        this.specimenCollectedDate = undefined;
        this.isSpecimenAck = undefined;
        this.specimenAckByUser = undefined;
        this.specimenAckDate = undefined;
        this.isLabTestPrepared = undefined;
        this.specimenPrepByUser = undefined;
        this.specimenPrepDate = undefined;
        this.isLabTestVerified = undefined;
        this.testVerifiedByUser = undefined;
        this.testVerifiedDate = undefined;
        this.testVerifiedApproved = undefined;
        this.isPathologistVerified = undefined;
        this.testVerifiedByPathologistUser = undefined;
        this.testVerifiedByPathologistDate = undefined;
        this.testVerifiedByPathologistApproved = undefined;
    }
}

export enum SearchOrEnterResultViewEnum {
    SEARCH = 'SEARCH',
    ENTER = 'ENTER',
}

export class LabResultPrepDto {
    testRequestId: number; // main lab testRequest id
    patientDetailDto: PatientPayload;
    requestNumber: string;
    specimen: string;
    requestDate: string;
    requestingDoctor: string;
    labNote: string;
    testParameterList: LabResultTestParamDto[];
    preparedBy: UserPayload;
    preparedFrom: DepartmentPayload;
    approvalLabNote: string;
    pathologistComment: string;
    singleTestRequestItemId: number;
    parasitologyTemplate: LabParasitologyTemplatePayload;
    resultTypeEnum: LabDepartmentTypeEnum;
    verifyOrPrepEnum: LabResultPrepOrVerifyEnum;
    verificationTypeEnum: LabVerificationViewEnum;


    constructor() {
        this.testRequestId = undefined;
        this.patientDetailDto = undefined;
        this.requestNumber = undefined;
        this.specimen = undefined;
        this.requestDate = undefined;
        this.requestingDoctor = undefined;
        this.labNote = undefined;
        this.testParameterList = [];
        this.preparedBy = undefined;
        this.preparedFrom = undefined;
        this.approvalLabNote = '';
        this.pathologistComment = '';
        this.singleTestRequestItemId = null;
        this.parasitologyTemplate = new LabParasitologyTemplatePayload();
        this.resultTypeEnum = undefined;
        this.verifyOrPrepEnum = undefined;
        this.verificationTypeEnum = undefined;
    }
}

export class LabResultTestParamDto {
    testParamId = undefined;
    filmReport = '';
    testParamName = '';
    comment = '';
    testRangeParamList: LabParamRangeSetupItem[] = [];
}

export class LabParamRangeSetupItem {
    id = undefined;
    name = '';
    unit = '';
    lowerLimit = 0;
    upperLimit = 0;
    value = 0;
    hasError = false;
}

export class LabTestApprovePayload {
    testItemId = undefined;
    approvedBy: UserPayload = undefined;
    approvedFrom: DepartmentPayload = undefined;
    labNote: string = undefined;
    pathologistComment: string = undefined;
    viewType: LabVerificationViewEnum = LabVerificationViewEnum.NORMAL;
}

export enum LabVerificationViewEnum {
    NORMAL = 'NORMAL',
    PATHOLOGIST = 'PATHOLOGIST',
    DOWNLOAD = 'DOWNLOAD',
    PATHOLOGIST_VERIFICATION = 'PATHOLOGIST_VERIFICATION',
}

export interface LabTestRequestSearchWrapper {
    testItems: LabTestRequestPayload[];
    sampleCollectionData: LabSpecimenCollectionPayload;
}

import { DatePayload, GenderPayload, SharedPayload } from '@app/shared/_payload';
import { AdmissionPayload, DepartmentPayload } from '@app/modules/settings';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import {
    ClerkDrugRequestPayload,
    ClerkLabRequestPayload,
    ClerkRadiologyRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';

export class PatientPayload {
    patientId?: number;
    receiptNumber?: string;
    patientNumber?: string;
    departmentId?: number;
    department?: DepartmentPayload;
    maritalStatusId?: number;
    genderId?: number;
    religionId?: number;
    ethnicGroupId?: number;
    ethnicGroup?: SharedPayload;
    patientDateOfBirth?: DatePayload;
    patientAge?: string;
    patientOtherName?: string;
    patientLastName?: string;
    patientFirstName?: string;
    patientTypeEnum?: PatientType;
    patientMeansOfIdentification?: PatientMeansOfIdentification;
    patientContactDetail?: PatientContactDetails;
    patientNokDetail?: PatientNokDetails;
    patientCardHolder?: PatientCardHolder;
    patientInsurance?: PatientInsuranceDetails;
    patientTransferDetails?: PatientTransferDetails;
    patientOtherDetails?: PatientOtherDetails;
    patientCategoryEnum?: PatientCategoryEnum;
    patientFullName?: string;
    patientNameAndNumber?: string;
    genderDto?: GenderPayload;
    maritalStatusDto?: GenderPayload;
    passportBase64?: string;
    isOnAdmission: boolean;
    admission: AdmissionPayload;
    cardNumber?: string;
    folderNumber?: string;
    revisitStatus: boolean;
    patientRevisitDto?: PatientRevisitPayload;
    patientSchemeDiscount?: number;
    totalDepositAmount?: number;
    drugRequestList?: ClerkDrugRequestPayload[];
    labRequestList?: ClerkLabRequestPayload[];
    radiologyRequestList?: ClerkRadiologyRequestPayload[];
    registeredBy: UserPayload;
    registeredFrom: DepartmentPayload;
    schemePlan: SchemePlan;

    constructor(id?: number) {
        if (id) {
            this.patientId = id;
        }
        this.patientMeansOfIdentification = {};
        this.patientContactDetail = {};
        this.patientNokDetail = {};
        this.patientCardHolder = {};
        this.patientInsurance = {};
        this.patientTransferDetails = {};
        this.patientOtherDetails = {};
        this.drugRequestList = [];
        this.labRequestList = [];
        this.radiologyRequestList = [];
        this.registeredBy = undefined;
        this.registeredFrom = undefined;
        this.department = undefined;
        this.schemePlan = new SchemePlan();
    }
}

export interface PatientCardHolder {
    id?: number;
    name?: string;
    insuranceNumber?: string;
    cardExpiry?: Date;
    cardHolderType?: CardHolderType;
    placeOfWork?: string;
    department?: string;
    beneficiaryName?: string;
    relationShipWithCardHolder?: SharedPayload;
}

export interface PatientNhisBeneficiary {
    id?: number;
    name?: string;
    relationShipWithCardHolder?: SharedPayload;
}

export interface PatientContactDetails {
    id?: number;
    residentialAddress?: string;
    email?: string;
    phoneNumber?: string;
    nationalityId?: number;
    nationality?: SharedPayload;
}

export interface PatientInsuranceDetails {
    id?: number;
    schemePlanId?: number;
    primaryProvider?: string;
    typeOfCare?: string;
    diagnosis?: string;
    approvalCode?: string;
    approvalStartDate?: DatePayload;
    approvalEndDate?: DatePayload;
    scheme?: SchemeData;
    nhisNumber?: string;
    treatmentType?: TreatmentTypeEnum;
}

export enum TreatmentTypeEnum {
    PRIMARY = 'PRIMARY',
    SECONDARY = 'SECONDARY'
}

export interface PatientMeansOfIdentification {
    id?: number;
    identificationNumber?: string;
    expiryDate?: DatePayload;
    identificationType?: number;
}

export interface PatientNokDetails {
    id?: number;
    name?: string;
    relationshipId?: number;
    phone?: string;
    address?: string;
}

export interface PatientOtherDetails {
    accessToGoodRoad?: boolean;
    id?: number;
    accessToTelephone?: boolean;
    accessToInternet?: boolean;
    accessToElectricity?: boolean;
    accessToCleanWater?: boolean;
    accessToGoodFood?: boolean;
}

export interface PatientTransferDetails {
    id?: number;
    hospitalTransferFrom?: string;
    dateOfTransfer?: DatePayload;
    hospitalAddress?: string;
    authorizedDoctor?: string;
    reasonForTransfer?: string;
}

export interface PatientCategory {
    id?: number;
    title?: string;
    description?: string;
    type?: string;
}

export interface PatientBasic {
    registerType?: PatientType;
    patientCategoryId?: number;
    patientNumber?: string;
    clinicId?: number;
    maritalStatusId?: number;
    genderId?: number;
    religionId?: number;
    ethnicGroupId?: number;
    dateOfBirth?: string;
    age?: number;
}

export enum PatientType {
    OLD = 'OLD',
    NEW = 'NEW',
    BOTH = 'BOTH',
}

export enum CardHolderType {
    HOLDER = 'HOLDER',
    DEPENDANT = 'DEPENDANT',
}

export enum PatientCategoryEnum {
    GENERAL = 'GENERAL',
    SCHEME = 'SCHEME',
}

export class WalkInPatientPayload {
    id: number;
    firstName: string;
    lastName: string;
    otherName: string;
    address: string;
    phone: string;
    age: string;
    genderId: number;

    constructor() {}
}

export class SchemeData {
    id?: number;
    insuranceName?: string;
    phoneNumber?: string;
    postalAddress?: string;
    insuranceCode?: string;
    emailAddress?: string;
    addressLineTitle?: string;
    address1?: string;
    address2?: string;
    address3?: string;
    address4?: string;
    label?: string;
    planType?: string;
    discount?: number;
    organizationType: string;
    schemePlans: SchemePlan[] = [];
}

export class SchemePlan {
    id?: number;
    planType?: string;
    discount = 0;
    percentService?: number;
    percentDrug?: number;
}

export class PatientRevisitPayload {
    id?: number;
    patientDetail?: PatientPayload;
    clinic?: DepartmentPayload;
    revisitBy?: UserPayload;
    revisitDate?: DatePayload;
    revisitFrom?: DepartmentPayload;
    revisitType?: string;
    code?: string;

    constructor() {
        this.id = undefined;
        this.patientDetail = new PatientPayload();
        this.clinic = new DepartmentPayload();
        this.revisitBy = new UserPayload();
        this.revisitFrom = new DepartmentPayload();
        this.code = undefined;
        this.revisitType = undefined;
        this.revisitDate = undefined;
    }
}

export class PatientAccountSummary {
    totalUnPaidBills: number;
    totalUnusedDeposit: number;
    netBillAmount: number;

    constructor() {
        this.totalUnPaidBills = undefined;
        this.totalUnusedDeposit = undefined;
        this.netBillAmount = undefined;
    }
}

export class AppointmentBookingSetup {
    id: number;
    consultantId: number;
    specialityUnitId: number;
    staffLimit: number;
}

export class AppointmentBooking {
    id: number;
    appointmentType: AppointmentTypeEnum;
    patientId: number;
    consultantId: number;
    specialityId: number;
    dateTime: DatePayload;
    clinicId: number;
    bookedById: number;
    locationId: number;
    status: AppointmentStatusEnum;
    remarks: string;
    specialityUnit?: SharedPayload;
    clinic?: DepartmentPayload;
    consultant?: UserPayload;
}

export enum AppointmentTypeEnum {
    NEW = 'new',
    EDIT = 'edit',
}

export enum AppointmentStatusEnum {
    OPEN = 'open',
    CLOSED = 'closed',
    PENDING = 'pending',
}

export class DischargePatientPayload {
    patient: PatientPayload;
    location: DepartmentPayload;
    dischargedBy: UserPayload;
    dischargedDate: DatePayload;
    consultant: UserPayload;
    dischargeStatus: string;
    finalDiagnosis: string;
    otherComment: string;

    constructor() {
        this.patient = new PatientPayload();
        this.location = undefined;
        this.dischargedBy = undefined;
        this.dischargedDate = undefined;
        this.consultant = undefined;
        this.dischargeStatus = undefined;
        this.finalDiagnosis = undefined;
        this.otherComment = undefined;
    }
}


export const DischargeStatusConst: any[] = [
    { id: 1, value: 'RECOVERED', name: 'RECOVERED' },
    { id: 2, value: 'DECEASED', name: 'DECEASED' },
    { id: 3, value: 'TRANSFERRED', name: 'TRANSFERRED' },
    { id: 4, value: 'LAMA', name: 'LAMA' },
    { id: 5, value: 'ABSCONDED', name: 'ABSCONDED' },
];

export class PatientClinicReferralPayload {
    referralId: number;
    referredBy: UserPayload;
    referredToClinic: DepartmentPayload;
    referredFromClinic: DepartmentPayload;
    patient: PatientPayload;
    referredToDoctor: UserPayload;
    referralNotes: string;

    constructor() {
        this.referralId = undefined;
        this.referredBy = undefined;
        this.referredToClinic = undefined;
        this.referredFromClinic = undefined;
        this.patient = undefined;
        this.referredToDoctor = undefined;
        this.referralNotes = '';
    }
}

export enum CategoryViewTypeEnum {
    CURRENT_DETAILS,
    EDIT_SCHEME_DETAILS,
    CHANGE_CATEGORY,
}

export class PatientCategoryUpdatePayload {
    patient: PatientPayload = new PatientPayload();
    viewType: CategoryViewTypeEnum = CategoryViewTypeEnum.CURRENT_DETAILS;
}

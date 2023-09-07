import {
    PatientCategoryEnum,
    PatientPayload,
    WalkInPatientPayload,
} from '@app/shared/_payload/erm/patient.payload';
import { DatePayload, ProductServicePayload, SharedPayload } from '@app/shared/_payload';
import { DepartmentPayload } from '@app/modules/settings';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { PharmacyBillItem } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { ClerkDoctorRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';

export class BillItem {
    productServiceId: number;
    quantity: number;
    price: number;
    grossAmount: number;
    payCash: boolean;
    nhisPrice: number;
    nhisPercent: number;
    netAmount: number;
    tempNetAmount: number;
    productService?: ProductServicePayload;
    checked = false;
    discountAmount?: number;
    waivedAmount?: number;
    isAllocate?: boolean;
    newQuantity?: number;
    description?: string;
    isAdjusted = false;
}

export enum BillPatientTypeEnum {
    WALK_IN = 'WALK_IN',
    REGISTERED = 'REGISTERED',
}

export enum ServiceOrDrugEnum {
    SERVICE,
    DRUG
}

export enum BillSearchBy {
    NEW_BILL = 'NEW_BILL',
    DOCTOR_REQUEST = 'DOCTOR_REQUEST',
    NEW_PRESCRIPTION = 'NEW_PRESCRIPTION',
    DOCTOR_PRESCRIPTION = 'DOCTOR_PRESCRIPTION',
}

export interface IBillTotal {
    grossTotal: number;
    netTotal: number;
    discountTotal: number;
    billWaivedAmount?: number;
    allocatedAmount?: number;
}

export class PatientBill {
    id: number;
    patientId: number;
    billPatientType: BillPatientTypeEnum;
    billSearchType: BillSearchBy;
    billItems: BillItem[];
    billTotal: IBillTotal;
    walkInPatient: WalkInPatientPayload;
    locationId: number;
    costDate: DatePayload;
    costById: number;
    billPatientCategory: PatientCategoryEnum;
    patientDetailDto: PatientPayload;
    invoiceNumber: string;
    locationDto: SharedPayload;
    costByDto: SharedPayload;
    deposit: DepositSumPayload;
    billTypeFor: PaymentTypeForEnum;
    pharmacyBillItems: PharmacyBillItem[];
    isPayCash: boolean;
    isDoctorRequest: boolean;
    doctorRequest: ClerkDoctorRequestPayload;
    comment?: string;
    isLabBill: boolean;

    constructor() {
        this.patientId = 0;
        this.billTotal = {
            grossTotal: 0,
            netTotal: 0,
            discountTotal: 0,
            allocatedAmount: 0,
            billWaivedAmount: 0,
        };
        this.billItems = [];
        this.billSearchType = BillSearchBy.NEW_BILL;
        this.billPatientType = BillPatientTypeEnum.REGISTERED;
        this.patientDetailDto = new PatientPayload();
        this.locationDto = { name: undefined, id: undefined };
        this.costByDto = { name: undefined, id: undefined };
        this.billTypeFor = undefined;
        this.pharmacyBillItems = [];
        this.isPayCash = true;
        this.costById = undefined;
        this.isDoctorRequest = false;
        this.doctorRequest = undefined;
        this.comment = '';
        this.isLabBill = false;
    }
}

export class SearchPatientBillPayload {
    depositAmount: number;
    billDtoList: PatientBill[];
}

export class BillPayment {
    id: number;
    billItemPaymentType: BillPaymentOptionTypeEnum;
    billSearchedBy: BillPaymentSearchByEnum;
    isAllocateToAllBill: boolean;
    patientDetailPayload: PatientPayload;
    patientBill: PatientBill;
    paymentMethod: SharedPayload;
    department: DepartmentPayload;
    user: UserPayload;
    depositPayload: { totalAmount: number; tempAmount?: number };
    walkInPatient: WalkInPatientPayload;
    paymentTypeForEnum: PaymentTypeForEnum;

    constructor() {
        this.id = undefined;
        this.isAllocateToAllBill = false;
        this.billItemPaymentType = BillPaymentOptionTypeEnum.BILLED_ITEM;
        this.billSearchedBy = BillPaymentSearchByEnum.BILL_NUMBER;
        this.patientDetailPayload = new PatientPayload();
        this.patientBill = new PatientBill();
        this.paymentMethod = { id: null, name: null };
        this.depositPayload = { totalAmount: 0, tempAmount: 0, };
        this.paymentTypeForEnum = undefined;
    }
}

export enum PaymentTypeForEnum {
    SERVICE = 'SERVICE',
    DRUG = 'DRUG',
    DEPOSIT = 'DEPOSIT'
}

export interface ITableProps {
    allowEdit: boolean;
    type: BillPaymentOptionTypeEnum;
    view: BillViewTypeEnum;
    billPatientCategory: PatientCategoryEnum;
    isAllocateAll: boolean;
}

export enum BillPaymentOptionTypeEnum {
    BILLED_ITEM = 'BILLED_ITEM',
    UN_BILLED_ITEM = 'UN-BILLED ITEM',
    WALK_IN = 'WALK_IN',
}

export enum BillViewTypeEnum {
    SERVICE_BILL,
    SERVICE_PAYMENT,
    DRUG_BILL,
    DRUG_PAYMENT,
    LAB_BILL,
    RADIOLOGY_BILL,
}

export enum BillPaymentSearchByEnum {
    BILL_NUMBER = 'BILL_NUMBER',
    PATIENT = 'PATIENT',
}

export class DepositPayload {
    patientId: number;
    locationId: number;
    userId: number;
    revenueDepartmentId: number;
    paymentMethod: SharedPayload;
    paymentMethodId: number;
    transactionRefNumber: string;
    depositAmount: number;
    description: string;

    constructor() {
        this.paymentMethod = { id: null, name: null };
    }
}

export class DepositSumPayload {
    id: number;
    totalAmount: number;

    constructor() {
        this.totalAmount = 0;
        this.id = undefined;
    }
}

export enum PaymentMethodEnum {
    CASH = 'CASH',
    POS = 'POS',
    CHEQUE = 'CHEQUE',
    E_PAYMENT = 'E PAYMENT',
    BANK_TRANSFER = 'BANK TRANSFER',
    MOBILE_WALLET = 'MOBILE WALLET',
}

export class ReceiptPayload {
    receiptId: number;
    receiptNumber: string;
    patientDetail: PatientPayload;
    patientBill: PatientBill;
    isUsed: boolean;
    isTouched: boolean;
    transactionDate: DatePayload;
    walkInPatient: WalkInPatientPayload;
    receiptPatientType: string;
    receiptPaymentTypeFor: PaymentTypeForEnum;
    depositAmount: number;
    receiptBillType: string;

    constructor() {
        this.receiptId = undefined;
        this.receiptNumber = undefined;
        this.patientDetail = undefined;
        this.patientBill = undefined;
        this.isUsed = undefined;
        this.isTouched = undefined;
        this.transactionDate = undefined;
        this.walkInPatient = undefined;
        this.receiptPatientType = undefined;
        this.receiptPaymentTypeFor = undefined;
        this.depositAmount = 0;
        this.receiptBillType = undefined;
    }
}

export const searchByPatientOrBillNumber: any[] = [
    { id: 1, name: 'BILL NUMBER', value: BillPaymentSearchByEnum.BILL_NUMBER },
    { id: 2, name: 'PATIENT', value: BillPaymentSearchByEnum.PATIENT },
];


export class BillAdjustmentPayload {
    billSearchedBy: BillPaymentSearchByEnum;
    patient: PatientPayload;
    patientBill: PatientBill;
    patientBillList: PatientBill[] = [];

    constructor() {
        this.billSearchedBy = BillPaymentSearchByEnum.BILL_NUMBER;
        this.patient =  new PatientPayload();
        this.patientBill = new PatientBill();
    }
}

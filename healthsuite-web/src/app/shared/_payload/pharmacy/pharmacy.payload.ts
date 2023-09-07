import { DatePayload, SharedPayload, VendorPayload } from '@app/shared/_payload';
import { DepartmentPayload } from '@app/modules/settings';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { PatientBill } from '@app/shared/_payload/bill-payment/bill.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';

export class PharmacyPayload {}

export const drugFrequencyData: any[] = [
    { id: 1, value: 'Once Daily', name: 'OD' },
    { id: 2, value: 'Twice Daily', name: 'BD' },
    { id: 3, value: 'Thrice Daily', name: 'TD' },
    { id: 4, value: 'Four Times Daily', name: 'QD' },
    { id: 5, value: 'In The Morning', name: 'MANE' },
    { id: 6, value: 'Urgently', name: 'STAT' },
    { id: 7, value: 'At Night', name: 'NOCTE' },
    { id: 8, value: 'As Needed', name: 'PRN' },
];

export const drugAdministrationRoute: any[] = [
    { id: 1, name: 'Oral', value: 'Oral' },
    { id: 2, name: 'I.M', value: 'I.M' },
    { id: 3, name: 'I.V', value: 'I.V' },
    { id: 4, name: 'Intramuscular', value: 'Intramuscular' },
    { id: 5, name: 'Topical', value: 'Topical' },
    { id: 6, name: 'Subcutaneous', value: 'Subcutaneous' },
    { id: 7, name: 'Drop', value: 'Drop' },
    { id: 8, name: 'Rectal', value: 'Rectal' },
    { id: 9, name: 'Vaginal', value: 'Vaginal' },
    { id: 10, name: 'Intrathical', value: 'Intrathical' },
    { id: 11, name: 'Intradermal', value: 'Intradermal' },
    { id: 12, name: 'Sublingual', value: 'Sublingual' },
    { id: 13, name: 'Gutt', value: 'Gutt' },
    { id: 14, name: 'OCC', value: 'OCC' },
    { id: 15, name: 'Susp.', value: 'Susp.' },
    { id: 16, name: 'Eye Drop', value: 'Eye Drop' },
];

export enum DrugSearchTermEnum {
    GENERIC_NAME = 'GENERIC NAME',
    BRAND_NAME = 'BRAND NAME',
    GENERIC_OR_BRAND_NAME = 'GENERIC OR BRAND NAME',
}

export enum PharmacyLocationTypeEnum {
    OUTLET,
    STORE,
}

export enum DrugDispenseSearchByEnum {
    RECEIPT_NUMBER = 'RECEIPT_NUMBER',
    PATIENT_NUMBER = 'PATIENT_NUMBER',
}

export class DrugRegisterPayload {
    id: number;
    revenueDepartmentId: number;
    formulationId: number;
    formulation: SharedPayload;
    classificationId: number;
    classification: SharedPayload;
    genericName: string;
    brandName: string;
    strength: string;
    unitOfIssue: number;
    unitPerPack: number;
    packsPerPackingUnit: number;
    costPrice: number;
    unitCostPrice: number;
    nhisMarkUp: number;
    generalMarkUp: number;
    regularSellingPrice: number;
    nhisSellingPrice: number;
    discountPercent: number;
    searchTitle: string;
    code: string;
    description: string;
    availableQty: number;
    reorderLevel?: number;
    issuingOutletBal?: number;

    constructor() {
        this.id = undefined;
        this.revenueDepartmentId = undefined;
        this.formulationId = undefined;
        this.classificationId = undefined;
        this.genericName = '';
        this.brandName = '';
        this.strength = '';
        this.unitOfIssue = 0;
        this.unitPerPack = 0;
        this.packsPerPackingUnit = 0;
        this.costPrice = 0;
        this.unitCostPrice = 0;
        this.nhisMarkUp = 0;
        this.generalMarkUp = 0;
        this.regularSellingPrice = 0;
        this.nhisSellingPrice = 0;
        this.discountPercent = 0;
        this.searchTitle = '';
        this.code = '';
        this.description = '';
        this.availableQty = 0;
        this.reorderLevel = 0;
        this.issuingOutletBal = 0;
    }
}

export class PharmacyBillItem {
    id: number;
    nhisPrice: number;
    nhisPercent: number;
    dosage: number;
    frequency: string;
    days: number;
    quantity: number;
    price: number;
    netAmount: number;
    tempNetAmount: number;
    grossAmount: number;
    discountAmount: number;
    drugRegister: DrugRegisterPayload;
    checked = false;
    isPayCash = true;
    isAllocate = false;
    waivedAmount: number;
    availableQuantity: number;
    newQuantity?: number;
    description?: string;
    isAdjusted = false;
}

export class OutletReconciliationPayload {
    outlet: DepartmentPayload;
    location: DepartmentPayload;
    items: OutletStockItemPayload[];

    constructor() {
        this.outlet = undefined;
        this.location = undefined;
        this.items = [];
    }
}

export class OutletStockItemPayload {
    drug: DrugRegisterPayload;
    currentBalance: number;
    quantityToReconcile: number;
    isChecked = false;

    constructor() {
        this.drug = undefined;
        this.currentBalance = 0;
        this.quantityToReconcile = 0;
    }
}

export class DrugDispensePayload {
    receiptId: number;
    outlet: DepartmentPayload;
    patientDetailDto: PatientPayload;
    patientBillDto: PatientBill;
    dispensedBy: UserPayload;

    constructor() {
        this.receiptId = undefined;
        this.outlet = undefined;
        this.patientDetailDto = new PatientPayload();
        this.patientBillDto = new PatientBill();
        this.dispensedBy = undefined;
    }
}

export class DrugOrderPayload {
    id: number;
    code: string;
    outlet: DepartmentPayload;
    user: UserPayload;
    vendor: VendorPayload;
    supplyCategory: DrugOrderSupplyCategoryEnum;
    drugOrderItems: DrugOrderItemPayload[];
    sumTotalAmount: number;
    isStore?: boolean;
    fulfilled?: boolean;

    constructor() {
        this.id = undefined;
        this.code = undefined;
        this.outlet = undefined;
        this.user = undefined;
        this.vendor = new VendorPayload();
        this.supplyCategory = undefined;
        this.drugOrderItems = [];
        this.sumTotalAmount = 0;
        this.isStore = false;
        this.fulfilled = false;
    }
}

export class DrugOrderItemPayload {
    id: number;
    drugOrder: DrugOrderPayload;
    quantity: number;
    rate: number;
    totalAmount: number;
    drugRegister: DrugRegisterPayload;
    checked: boolean = false;

    constructor() {
        this.id = undefined;
        this.drugOrder = undefined;
        this.quantity = undefined;
        this.rate = undefined;
        this.totalAmount = undefined;
        this.drugRegister = undefined;
    }
}

export enum DrugOrderSupplyCategoryEnum {
    MOU = 'MOU',
    EMERGENCY = 'EMERGENCY',
    CREDIT = 'CREDIT',
    CASH = 'CASH',
}

export enum IssuanceTypeEnum {
    NEW,
    REQUISITION,
}

export class PharmacyReceivedGoodsPayload {
    id: number;
    drugOrder: DrugOrderPayload;
    receivingDepartment: DepartmentPayload;
    supplyingCompany: VendorPayload;
    receivedBy: string;
    deliveredBy: string;
    invoiceNumber: string;
    invoiceDate: DatePayload;
    purchaseOrderNumber: string;
    deliveryNoteNumber: string;
    outlet: DepartmentPayload;
    user: UserPayload;
    receivedGoodsItemsList: PharmacyReceivedGoodsItemPayload[];
    relatedInformation: string;
    totalAmountSupplied: number;

    constructor() {
        this.id = undefined;
        this.drugOrder = new DrugOrderPayload();
        this.receivingDepartment = new DepartmentPayload();
        this.supplyingCompany = new VendorPayload();
        this.receivedBy = undefined;
        this.deliveredBy = undefined;
        this.invoiceNumber = undefined;
        this.invoiceDate = undefined;
        this.purchaseOrderNumber = undefined;
        this.deliveryNoteNumber = undefined;
        this.outlet = new DepartmentPayload();
        this.user = new UserPayload();
        this.receivedGoodsItemsList = [];
        this.relatedInformation = undefined;
        this.totalAmountSupplied = 0;
    }
}

export class PharmacyReceivedGoodsItemPayload {
    id: number;
    quantityOrdered: number;
    quantityReceived: number;
    quantitySupplied: number;
    rate: number;
    unitOfIssue: number;
    totalCost: number;
    batchNumber: string;
    expiryDate: DatePayload;
    drugRegister: DrugRegisterPayload;
    pharmacyReceivedGoods: PharmacyReceivedGoodsPayload;

    constructor() {
        this.id = undefined;
        this.quantityOrdered = undefined;
        this.quantityReceived = undefined;
        this.quantitySupplied = undefined;
        this.rate = undefined;
        this.unitOfIssue = undefined;
        this.totalCost = undefined;
        this.batchNumber = undefined;
        this.expiryDate = undefined;
        this.drugRegister = undefined;
        this.pharmacyReceivedGoods = undefined;
    }
}

export class DrugRequisitionItemPayload {
    id: number;
    drugRegister: DrugRegisterPayload;
    requestingQuantity: number;
    unitOfIssue: number;
    issuingOutletBalance: number;
    drugRequisition: DrugRequisitionPayload;
    isChecked?: boolean;
    issuingQuantity: number;

    constructor() {
        this.id = undefined;
        this.drugRegister = undefined;
        this.requestingQuantity = undefined;
        this.unitOfIssue = undefined;
        this.issuingOutletBalance = undefined;
        this.drugRequisition = undefined;
        this.isChecked = false;
        this.issuingQuantity = 0;
    }
}

export class DrugRequisitionPayload {
    id: number;
    issuingDepartment: DepartmentPayload;
    receivingDepartment: DepartmentPayload;
    location: DepartmentPayload;
    operatingUser: UserPayload;
    requisitionTypeEnum: PharmacyLocationTypeEnum; //DrugRequisitionTypeEnum
    isFulfilled: boolean;
    requisitionItems: DrugRequisitionItemPayload[];
    code: string;
    datePayload: DatePayload;
    date: Date;
    issuance: DrugIssuancePayload;

    constructor() {
        this.id = undefined;
        this.issuingDepartment = undefined;
        this.receivingDepartment = undefined;
        this.location = undefined;
        this.operatingUser = undefined;
        this.requisitionTypeEnum = undefined;
        this.isFulfilled = undefined;
        this.requisitionItems = [];
        this.code = undefined;
        this.datePayload = undefined;
        this.date = undefined;
        this.issuance = undefined;
    }
}

/*
export class DrugIssuanceItemPayload {
    id: number;
    requisitionNumber: string;
    requisitionOutlet: DepartmentPayload;
    issuanceNumber: string;
    requisitionDate: DatePayload;
    issuedBy: UserPayload;
    issuanceDate: DatePayload;
    status: boolean;

    constructor() {
        this.id = undefined;
        this.requisitionNumber = undefined;
        this.requisitionOutlet = undefined;
        this.issuanceNumber = undefined;
        this.requisitionDate = undefined;
        this.issuedBy = undefined;
        this.issuanceDate = undefined;
        this.status = undefined;
    }
}
*/

export class DrugIssuancePayload {
    id: number;
    type: IssuanceTypeEnum;
    startDate: DatePayload;
    endDate: DatePayload;
    requisition: DrugRequisitionPayload;
    user: UserPayload;
    outlet: DepartmentPayload;
    issuanceNumber: string;
    dateDto: DatePayload;
    date: Date;

    constructor() {
        this.id = undefined;
        this.type = undefined;
        this.startDate = { min: 0, hour: 0, day: 0, month: 0, year: 0 };
        this.endDate = { min: 0, hour: 0, day: 0, month: 0, year: 0 };
        this.user = undefined;
        this.outlet = undefined;
        this.issuanceNumber = undefined;
        this.dateDto = undefined;
        this.date = undefined;
    }
}

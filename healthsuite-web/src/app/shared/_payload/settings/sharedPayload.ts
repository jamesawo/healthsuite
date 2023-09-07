export interface SharedPayload {
    id?: number;
    name?: string;
    code?: string;
    description?: string;
    model?: SharedPayload; //refactor this interface
    discount?: number;
    title?: string;
}

export interface SocketMessagePayload {
    id?: number;
    content?: string;
    title?: string;
    dateTime?: DatePayload;
}

export enum DepartmentCategoryEnum {
    CLINIC = 'Clinic',
    LABORATORY = 'Laboratory',
    PHARMACY = 'Pharmacy',
    RADIOLOGY = 'Radiology',
    WARD = 'Ward',
    CASH = 'Cash',
    BILLING = 'Billing',
    NHIS = 'NHIS',
    RETAINERSHIP = 'Retainership',
    OTHERS = 'Others',
}

export enum SocketClientState {
    ATTEMPTING,
    CONNECTED,
    DISCONNECTED,
}

export enum DateType {
    START = 'START',
    END = 'END',
}

export enum DailyCollectionFilterTypeEnum {
    REVENUE_DEPARTMENT = 'REVENUE_DEPARTMENT',
    SERVICE_DEPARTMENT = 'SERVICE_DEPARTMENT',
    SERVICE_DRUG = 'SERVICE_DRUG', // only useful for switch operator in daily cash collection report view
    SERVICE = 'SERVICE',
    DRUG = 'DRUG',
}

export interface HmisSharedDropDown {
    id: any;
    value: string;
    name: string;
}

export interface InputComponentPayload {
    id: any;
    value: any;
    columnName: any;
    inputType: string;
    columnType: InputComponentUsageEnum;
}

export enum InputComponentUsageEnum {
    SERVICE,
    DRUG,
}

export enum ServicesTableColumnEnum {
    serviceName = 'SERVICE_NAME',
    serviceDepartment = 'SERVICE_DEPARTMENT',
    revenueDepartment = 'REVENUE_DEPARTMENT',
    generalPrice = 'GENERAL_PRICE',
    nhisPrice = 'NHIS_PRICE',
    costPrice = 'COST_PRICE',
    unitPrice = 'UNIT_PRICE',
    usage = 'USAGE',
}

export enum DrugTableColumnEnum {
    generic = 'GENERIC',
    description = 'DESCRIPTION',
    brand = 'BRAND',
    strength = 'STRENGTH',
    costPrice = 'COST_PRICE',
    unitOfIssue = 'UNIT_OF_ISSUE',
    packsPerPackingUnit = 'PACKS_PER_PACKING_UNIT',
    unitCostPrice = 'UNIT_COST_PRICE',
    generalMarkUp = 'GENERAL_MARK_UP',
    regularSellingPrice = 'REGULAR_SELLING_PRICE',
    nhisSellingPrice = 'NHIS_SELLING_PRICE',
    classification = 'CLASSIFICATION',
    formulation = 'FORMULATION',
}

export enum FileTemplateEnum {
    DRUG_UPLOAD_TEMPLATE = 'DrugUploadTemplate.xlsx',
    SERVICE_UPLOAD_TEMPLATE = 'ServiceUploadTemplate.xlsx',
}

export enum ReportFileExtEnum {
    PDF = '.pdf',
    EXCEL = '.xlsx',
    WORD = '.docx',
}

export enum ReportFileNameEnum {
    EMR_PATIENT_REGISTER = 'PatientRegisterReport',
    EMR_PATIENT_DISCHARGE_GATE_PASS = 'PatientDischargeGatePass',
    EMR_PATIENT_INTERIM_INVOICE = 'PatientInterimReport',
    ACC_DAILY_CASH_COLLECTION = 'DailyCashCollectionReport',
    SHIFT_CASHIER_REPORT = 'CashierShiftReport',
    SHIFT_ACKNOWLEDGEMENT_REPORT = 'ShiftAcknowledgementReport',
    SHIFT_COMPILED_REPORT = 'CompiledShiftReport',
    CLERK_E_FOLDER = 'PatientEFolderReport',
    NURSE_PATIENT_FLUID_BALANCE = 'PatientFluidBalanceReport',
    LAB_TEST_RESULT = 'PatientLabTestResult',
    SERVICE_CHARGE_SHEET = 'ServiceChargePriceList',
    SCHEME_CONSUMPTION_REPORT = 'SchemeConsumptopmReport',
}

export enum FileUploadTypeEnum {
    SERVICE = 'SERVICE',
    DRUG = 'DRUG',
}

export interface InputComponentChanged {
    id: number;
    value: any;
    serviceColumn?: ServicesTableColumnEnum;
    drugColumn?: DrugTableColumnEnum;
}

export enum ModalSizeEnum {
    small = 'modal-sm',
    medium = 'modal-md',
    large = 'modal-lg',
}

export interface DatePayload {
    year: string | number;
    month: string | number;
    day: string | number;
    hour?: number;
    min?: number;
}

export interface ValidationMessage {
    status: boolean;
    message: string;
    messages?: string[];
}

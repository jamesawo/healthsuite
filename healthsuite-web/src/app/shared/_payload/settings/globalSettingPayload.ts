export interface IGlobalSetting {
    map: Map<string, string>;
}

export class GlobalSettingPayload {
    // General
    hospitalName: string;
    patientNumberPrefix: string;
    disableBillingInvoicePops: string;
    enableSocialWelfareModule: string;
    generateHospitalNumberForOldPatient: string;
    enforceRevisitBeforeBillingActions: string;
    enforcePharmacyPatientCategory: string;

    // Finance/Account
    enableOutPatientDeposit: string;
    salesReceiptCopyCount: number;
    directorOfFinance: string;
    hideBillDetailsFromReceipt: string;
    allowPriceEditForWalkP: string;
    enableRegistrationValidation: string;
    depositReceiptCount: number;
    alwaysIncludePayNowBills: string;
    depositAccount: any;

    // Pharmacy Settings
    activateStockInventory: string;
    autoUpdateSellingAfterReceiveGoods: string;
    makePharmacyPatientCatCom: string;

    // Clerking Settings
    activateClerking: string;
    consultantEditWindow: number;
    consultationFeeExpiryDays: number;

    // NHIS Settings
    autoApplyApprovalCode: string;

    // Lab Settings
    enforceSpecimenAck: string;
    enableSpecimenAckDuringCollection: string;
    enableNhisServicePrice: string;

    enforceSystemLocation: string;
    addWardCodeToBedCode: string;
    hasData: Boolean;
    bedCodePrefix: string;
    departmentCodePrefix: string;
    patientNumberStartPoint: number;
    patientNumberSuffix: string;
    revenueDepartmentCode: string;
    specialityUnitCode: string;
    wardCodePrefix: string;

    constructor() {
        this.hospitalName = null;
        this.patientNumberPrefix = null;
        this.disableBillingInvoicePops = null;
        this.enableSocialWelfareModule = null;
        this.generateHospitalNumberForOldPatient = null;
        this.enforceRevisitBeforeBillingActions = null;
        this.enableOutPatientDeposit = null;
        this.salesReceiptCopyCount = 0;
        this.directorOfFinance = null;
        this.hideBillDetailsFromReceipt = null;
        this.allowPriceEditForWalkP = null;
        this.enableRegistrationValidation = null;
        this.depositReceiptCount = 0;
        this.alwaysIncludePayNowBills = null;
        this.depositAccount = null;
        this.activateStockInventory = null;
        this.autoUpdateSellingAfterReceiveGoods = null;
        this.makePharmacyPatientCatCom = null;
        this.activateClerking = null;
        this.consultantEditWindow = 0;
        this.consultationFeeExpiryDays = 0;
        this.autoApplyApprovalCode = null;
        this.enforceSpecimenAck = null;
        this.hasData = false;
        this.bedCodePrefix = null;
        this.departmentCodePrefix = null;
        this.patientNumberStartPoint = 0;
        this.patientNumberSuffix = null;
        this.revenueDepartmentCode = null;
        this.specialityUnitCode = null;
        this.wardCodePrefix = null;
        this.enableNhisServicePrice = null;
    }
}

export enum GlobalSettingValueEnum {
    YES = 'yes',
    NO = 'no',
}

export enum GlobalSettingKeysEnum {
    ACTIVATE_STOCK_INVENTORY = 'ActivateStockInventory',
    ACTIVATE_CLERKING = 'ActivateClerking',
    ENFORCE_SYSTEM_LOCATION = 'EnforceSystemLocation',
    ENABLE_SPECIMEN_ACK_DURING_COLLECTION = 'EnableSpecimenAckDuringCollection',
    ENABLE_NHIS_SERVICE_PRICE = 'EnableNhisServicePrice',

}

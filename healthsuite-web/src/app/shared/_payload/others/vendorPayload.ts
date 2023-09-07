export class VendorPayload {
    id: number;
    supplierName: string;
    phoneNumber: string;
    companyRegistration: string;
    officeAddress: string;
    postalAddress: string;
    emailAddress: string;
    websiteUrl: string;
    faxNumber: string;
    isPharmacyVendor: boolean;
    viewType: VendorViewType;

    constructor() {
        this.id = undefined;
        this.supplierName = undefined;
        this.phoneNumber = undefined;
        this.companyRegistration = undefined;
        this.officeAddress = undefined;
        this.postalAddress = undefined;
        this.emailAddress = undefined;
        this.websiteUrl = undefined;
        this.faxNumber = undefined;
        this.isPharmacyVendor = false;
        this.viewType = VendorViewType.NEW_REGISTRATION;
    }
}

export enum VendorViewType {
    NEW_REGISTRATION = 'NEW REGISTRATION',
    EDIT_VENDOR = 'EDIT REGISTRATION',
}

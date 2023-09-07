import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    DatePayload,
    GlobalSettingValueEnum,
    ProductServicePayload,
    ValidationMessage,
} from '@app/shared/_payload';
import {
    BillItem,
    BillPatientTypeEnum,
    BillPaymentOptionTypeEnum,
    BillSearchBy,
    BillViewTypeEnum,
    IBillTotal,
    ITableProps,
    PatientBill,
    PaymentTypeForEnum, ServiceOrDrugEnum,
} from '@app/shared/_payload/bill-payment/bill.payload';
import {PatientCategoryEnum, PatientInsuranceDetails, PatientPayload, TreatmentTypeEnum} from '@app/shared/_payload/erm/patient.payload';
import { environment } from '@environments/environment';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { PaymentService } from '@app/shared/_services/billing-payment/payment.service';
import { WalkInPatientComponent } from '@app/modules/common/walk-in-patient/walk-in-patient.component';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ServiceNameSearchComponent } from '@app/modules/common/service-name-search/service-name-search.component';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import {
    drugFrequencyData,
    DrugRegisterPayload,
    DrugSearchTermEnum,
    PharmacyBillItem,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { GlobalSettingService } from '@app/shared/_services';
import { skipWhile } from 'rxjs/operators';
import {
    ClerkDoctorRequestPayload,
    ClerkDrugRequestPayload,
    ClerkLabRequestPayload,
    ClerkRadiologyRequestPayload,
} from '@app/shared/_payload/clerking/clerk-request.payload';
import { SearchByDropdownComponent } from '@app/modules/common/search-by-dropdown/search-by-dropdown.component';
import { DrugSearchComponent } from '@app/modules/common/drug-search/drug-search.component';

const serviceSearchBy: any[] = [
    { id: 1, name: 'NEW BILL', value: BillSearchBy.NEW_BILL },
    { id: 1, name: `DOCTOR's REQUEST`, value: BillSearchBy.DOCTOR_REQUEST },
];
const drugSearchBy: any[] = [
    { id: 1, name: 'NEW PRESCRIPTION', value: BillSearchBy.NEW_PRESCRIPTION },
    { id: 1, name: `DOCTOR's PRESCRIPTION`, value: BillSearchBy.DOCTOR_PRESCRIPTION },
];

@Component({
    selector: 'app-patient-billing',
    templateUrl: './patient-billing.component.html',
    styleUrls: ['./patient-billing.component.css'],
})
export class PatientBillingComponent implements OnInit, OnDestroy {
    @ViewChild('searchByDropdownComponent')
    public searchByDropdownComponent: SearchByDropdownComponent;
    @ViewChild('drugSearchComponent')
    public drugSearchComponent: DrugSearchComponent;
    @Input() public props: {
        type: PatientCategoryEnum;
        billFor: PaymentTypeForEnum;
        viewType?: BillViewTypeEnum;
        isPharmBill?: boolean;
    };
    public registeredPatientType = BillPatientTypeEnum.REGISTERED;

    public billItems: BillItem[] = [];
    public pharmacyBillItems: PharmacyBillItem[] = [];
    public billPatientTemp: PatientPayload = new PatientPayload();
    public currencySym = environment.currencySign;
    public billSearchByCollection: any[];
    public genericOrBrandName: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;
    public yes: GlobalSettingValueEnum = GlobalSettingValueEnum.YES;
    public payload: PatientBill = new PatientBill();
    public drugBillView: BillViewTypeEnum = BillViewTypeEnum.DRUG_BILL;
    public labBillView = BillViewTypeEnum.LAB_BILL;
    public radiologyView = BillViewTypeEnum.RADIOLOGY_BILL;
    public doctorRequest = BillSearchBy.DOCTOR_REQUEST;
    public doctorPres = BillSearchBy.DOCTOR_PRESCRIPTION;
    public isLoadDrugRequest = false;
    public isLoadLabRequest = false;
    public isLoadRadiologyRequest = false;
    public canShowLabPrescriptionHandler = false;
    public canShowDrugPrescriptionHandler = false;
    public canShowRadiologyPrescriptionHandler = false;
    public drugRequest: ClerkDrugRequestPayload[] = [];
    public labRequest: ClerkLabRequestPayload[] = [];
    public radiologyRequest: ClerkRadiologyRequestPayload[] = [];
    public selectedRadiologyRequest: ClerkRadiologyRequestPayload = undefined;
    public isEnableNhisServicePrice = false;

    public primary = TreatmentTypeEnum.PRIMARY;
    public secondary = TreatmentTypeEnum.SECONDARY;

    @ViewChild('serviceNameSearchComponent') serviceNameSearchComponent: ServiceNameSearchComponent;
    @ViewChild(PatientSearchComponent) patientSearchComponent: PatientSearchComponent;
    @ViewChild(WalkInPatientComponent) walkInPatientComponent: WalkInPatientComponent;
    SCHEME: PatientCategoryEnum = PatientCategoryEnum.SCHEME;

    public schemeDetails: {
        treatmentType: TreatmentTypeEnum,
        approvalCode: string,
        approvalCodeStart: DatePayload,
        nhisNumber: string,
        diagnosis: string,
        approvalCodeEnd: DatePayload,
    } = {
        treatmentType: undefined,
        approvalCode: '',
        approvalCodeStart: undefined,
        nhisNumber: '',
        diagnosis: '',
        approvalCodeEnd: undefined,
    };

    public enforceStockCount;

    public tableProps: ITableProps;
    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private paymentService: PaymentService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private globalSettingService: GlobalSettingService
    ) {}

    ngOnInit(): void {
        // todo refactor this block this.enforceStockCount is redundant
        this.subscription.add(
            this.globalSettingService.settingSubject$
                .pipe(skipWhile((value) => !value))
                .subscribe((setting) => {
                    this.enforceStockCount =
                        setting.activateStockInventory === GlobalSettingValueEnum.YES;
                    this.isEnableNhisServicePrice =
                        setting.enableNhisServicePrice === GlobalSettingValueEnum.YES;
                    if (this.props.isPharmBill === true) {
                        if (
                            this.commonService.isLocationMatch(LocationConstants.PHARMACY_LOCATION)
                        ) {
                            this.initializeObj().then();
                        } else {
                            this.commonService.flagLocationError();
                        }
                    } else {
                        if (this.commonService.isLocationMatch(LocationConstants.CASH_LOCATION)) {
                            this.initializeObj().then();
                        } else {
                            this.commonService.flagLocationError();
                        }
                    }
                })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public isSchemedPatient = (): boolean => {
        return this.payload.patientDetailDto.patientCategoryEnum === PatientCategoryEnum.SCHEME;
    }

    public isForceStockCount = (): boolean => this.enforceStockCount;

    public onCreateBill(): Promise<void> {
        if (this.payload.billPatientType === BillPatientTypeEnum.WALK_IN) {
            this.payload.walkInPatient = this.walkInPatientComponent.onGetWalkInPatientPayload();
        }

        const isValid = this.onValidateBillPayload();
        if (!isValid.status) {
            this.toast.error(isValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }

        this.payload.billItems = this.billItems;
        this.payload.pharmacyBillItems = this.pharmacyBillItems;

        this.spinner.show().then();
        this.subscription.add(
            this.paymentService.onCreateBill(this.payload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    if (result.status === 200) {
                        this.toast.success('Generating Invoice', 'Successful');
                        const file = new Blob([result.body], { type: 'application/pdf' });
                        const fileURL = URL.createObjectURL(file);
                        this.onClearForm();
                        window.open(fileURL);
                        this.initializeObj().then();
                    } else {
                        this.toast.error(
                            'An error Occurred, Contact Support',
                            HmisConstants.ERR_TITLE
                        );
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error('Oops! An error Occurred!', HmisConstants.VALIDATION_ERR);
                    console.log(error.error.message);
                }
            )
        );
    }

    public onBillPatientTypeChange(changedValue: BillPatientTypeEnum): void {
        this.payload.billPatientType = changedValue;
    }

    public canShowLabDoctorRequestHandler() {
        const canShowLabHandler =
            this.props?.viewType === this.labBillView &&
            this.payload.billSearchType === this.doctorRequest;

        this.canShowLabPrescriptionHandler = canShowLabHandler;
        this.isLoadLabRequest = canShowLabHandler;
    }

    public checkIsHasPatient(): boolean {
        if (
            this.payload.billPatientType === BillPatientTypeEnum.REGISTERED &&
            !this.payload.patientId
        ) {
            this.toast.error(HmisConstants.PATIENT_IS_REQUIRED, HmisConstants.VALIDATION_ERR);
            return false;
        }

        if (this.payload.billPatientType === BillPatientTypeEnum.WALK_IN) {
            const isWalkInPatientValid = this.walkInPatientComponent.onValidate();
            if (!isWalkInPatientValid.status) {
                this.toast.error(isWalkInPatientValid.message, HmisConstants.VALIDATION_ERR);
                return false;
            }
        }

        return true;
    }

    public onProductServiceSelected(payload: ProductServicePayload): void {
        setTimeout(() => {
            if (payload && this.checkIsHasPatient() === false) {
                return;
            }

            if (payload?.id) {
                if (
                    this.isEnableNhisServicePrice === true &&
                    this.payload.patientDetailDto.patientCategoryEnum === PatientCategoryEnum.SCHEME
                ) {
                    this.spinner.show().then();
                    this.subscription.add(
                        this.paymentService
                            .onGetNhisServicePrice(this.payload.patientId, payload.id)
                            .subscribe(
                                (res) => {
                                    this.spinner.hide().then();
                                    if (res && res > 0) {
                                        payload.nhisSellingPrice = res;
                                        this.onAfterProductServiceSelected(payload);
                                    } else {
                                        this.onAfterProductServiceSelected(payload);
                                    }
                                },
                                (error) => {
                                    this.spinner.hide().then();
                                }
                            )
                    );
                } else {
                    this.onAfterProductServiceSelected(payload);
                }

            }
        }, 0);
    }

    public onAfterProductServiceSelected(payload: ProductServicePayload) {
        if (payload && this.isDuplicateService(payload) === false) {
            const isScheme: boolean = this.isSchemedPatient();
            const billItem: BillItem = new BillItem();

            this.setServiceBillPrice(billItem, isScheme, payload, 1);
            this.billItems.push(billItem);
            this.onUpdateTotals();
        }
    }

    public onPatientSearchChange(patient: PatientPayload): void {
        this.onClearPayload();
        if (
            this.props?.viewType === this.drugBillView ||
            this.props?.viewType === this.labBillView ||
            this.props?.viewType === this.radiologyView
        ) {
            this.billPatientTemp = patient;
            this.payload.patientId = patient.patientId;
            this.payload.patientDetailDto = patient;
            this.payload.billPatientCategory = patient.patientCategoryEnum;
            this.drugRequest = patient.drugRequestList;
            this.labRequest = patient.labRequestList;
            this.radiologyRequest = patient.radiologyRequestList;
            return;
        } else if (this.props?.type === patient.patientCategoryEnum) {
            this.billPatientTemp = patient;
            this.payload.patientId = patient.patientId;
            this.payload.patientDetailDto = patient;
            this.onSetPatientInsuranceSchemeDetail(patient.patientInsurance);

            return;
        } else {
            this.toast.error(
                `Patient Category Type Violation. <br> Only ${this.props?.type} Patient is allowed.`,
                HmisConstants.VALIDATION_ERR
            );
            this.patientSearchComponent.clearSearchField();
            return;
        }
    }

    public onSearchByChanged(value: BillSearchBy) {
        if (value) {
            this.pharmacyBillItems = [];
            this.billItems = [];

            this.payload.billSearchType = value;
            this.canShowDrugRequestHandler(value);
            this.canShowLabDoctorRequestHandler();
            this.canShowRadiologyRequestHandler(value);
        }
    }

    public canShowDrugRequestHandler(value: BillSearchBy) {
        const isUsePrescription =
            value === BillSearchBy.DOCTOR_PRESCRIPTION &&
            this.props.viewType === BillViewTypeEnum.DRUG_BILL;
        this.isLoadDrugRequest = isUsePrescription;
        this.canShowDrugPrescriptionHandler = isUsePrescription;
    }

    public canShowRadiologyRequestHandler(value: BillSearchBy) {
        const isUse =
            value === BillSearchBy.DOCTOR_REQUEST &&
            this.props.viewType === BillViewTypeEnum.RADIOLOGY_BILL;
        this.isLoadRadiologyRequest = isUse;
        this.canShowRadiologyPrescriptionHandler = isUse;
    }

    public onBillTotalChanged(payload: IBillTotal) {
        this.onUpdateTotals();
    }

    public hasDuplicateDrug(payload: DrugRegisterPayload): boolean {
        return this.commonService.isDrugItemDuplicateInList(payload, this.pharmacyBillItems);
    }

    public onDrugItemSelected(payload: DrugRegisterPayload) {
        setTimeout(() => {
            if (payload && this.checkIsHasPatient() === false) {
                return;
            }
            if (payload && this.hasDuplicateDrug(payload) === false) {
                if (this.enforceStockCount && payload.availableQty === 0) {
                    this.flagItemLowStockCount(payload.description);
                    return;
                }
                const pharmacyBill: PharmacyBillItem = new PharmacyBillItem();
                this.onSetPharmacyBillItem(payload, pharmacyBill);
                this.pharmacyBillItems.push(pharmacyBill);
                this.onUpdateTotals();
            }
        }, 0);
    }

    public flagItemLowStockCount(itemTitle: string) {
        this.toast.error(
            `${itemTitle} Qty Is Too Low OR Unavailable In Store`,
            HmisConstants.ERR_TITLE
        );
    }

    public onCashOrCreditToggleChange(value: boolean) {
        this.payload.isPayCash = value;
    }

    public onSetPharmacyBillItem(
        drugRegister: DrugRegisterPayload,
        pharmacyBill: PharmacyBillItem,
        dosage: number = 1,
        quantity: number = 1,
        days: number = 1,
        frequency: any = drugFrequencyData[0].value
    ): void {
        pharmacyBill.discountAmount = drugRegister.discountPercent;
        pharmacyBill.nhisPrice = drugRegister.nhisSellingPrice ;
        pharmacyBill.isPayCash = this.payload.isPayCash;
        pharmacyBill.nhisPercent = drugRegister.discountPercent +  this.onGetSchemeDiscount(ServiceOrDrugEnum.DRUG);
        pharmacyBill.dosage = dosage;
        pharmacyBill.frequency = frequency;
        pharmacyBill.days = days;
        pharmacyBill.quantity = quantity;
        pharmacyBill.drugRegister = drugRegister;
        pharmacyBill.drugRegister.availableQty = drugRegister.availableQty
            ? drugRegister.availableQty
            : 0;
        pharmacyBill.availableQuantity = drugRegister.availableQty;

        if (this.isSchemedPatient()) {
            pharmacyBill.price = drugRegister.nhisSellingPrice;
            pharmacyBill.grossAmount = drugRegister.nhisSellingPrice;
            /*if (pharmacyBill.discountAmount > 0) {
                pharmacyBill.netAmount = this.getPercentValue(
                    pharmacyBill.nhisPrice,
                    quantity,
                    pharmacyBill.discountAmount
                );
            } else {
                pharmacyBill.netAmount = drugRegister.nhisSellingPrice;
            }*/

            const discountAmount = pharmacyBill.discountAmount + this.onGetSchemeDiscount(ServiceOrDrugEnum.DRUG);
            pharmacyBill.netAmount = this.getPercentValue(pharmacyBill.nhisPrice, quantity, discountAmount);


        } else {
            pharmacyBill.price = drugRegister.regularSellingPrice;
            pharmacyBill.netAmount = drugRegister.regularSellingPrice;
            pharmacyBill.grossAmount = drugRegister.regularSellingPrice;
        }
    }

    public getPercentValue(price: number, quantity: number, discountAmount: number): number {
        return Math.round(price * quantity - (price * discountAmount) / 100) * quantity;
    }

    public onUpdateTotals(): void {
        const billTotal: IBillTotal = { discountTotal: 0, grossTotal: 0, netTotal: 0 };

        if (this.props?.billFor === PaymentTypeForEnum.SERVICE) {
            this.onUpdateServiceTotals(billTotal);
        } else if (this.props?.billFor === PaymentTypeForEnum.DRUG) {
            this.onUpdateDrugTotals(billTotal);
        }

        this.payload.billTotal = billTotal;
    }

    public onUpdateDrugTotals(billTotal: IBillTotal): void {
        if (this.pharmacyBillItems.length) {
            billTotal.grossTotal = this.pharmacyBillItems
                .map((item) => item.grossAmount)
                .reduce((a, b) => {
                    return a + b;
                });
            billTotal.netTotal = this.pharmacyBillItems
                .map((item) => item.netAmount)
                .reduce((a, b) => {
                    return a + b;
                });
            billTotal.discountTotal = billTotal.grossTotal - billTotal.netTotal;
        }
    }

    public onUpdateServiceTotals(billTotal: IBillTotal): void {
        if (this.billItems.length) {
            billTotal.grossTotal = this.billItems
                .map((item) => item.grossAmount)
                .reduce((a, b) => {
                    return a + b;
                });
            billTotal.netTotal = this.billItems
                .map((item) => item.netAmount)
                .reduce((a, b) => {
                    return a + b;
                });
            billTotal.discountTotal = billTotal.grossTotal - billTotal.netTotal;
        }
    }

    // when searching doctor request clear table data
    public onIsSearchingDoctorRequest(val: boolean) {
        if (val === true) {
            this.pharmacyBillItems = [];
            this.billItems = [];
        }
    }

    // when drug prescription is selected, pass the drug items to create bill
    public onDrugPrescriptionSelected(payload: ClerkDrugRequestPayload) {
        this.pharmacyBillItems = [];
        if (payload && payload.drugItems.length > 0) {
            this.onSetDoctorReqOnPayload(payload.doctorRequestId);
            for (const item of payload.drugItems) {
                const pharmacyBill: PharmacyBillItem = new PharmacyBillItem();
                this.onSetPharmacyBillItem(
                    item.drugRegister,
                    pharmacyBill,
                    item.dosage,
                    1,
                    item.days,
                    item.frequency
                );
                this.pharmacyBillItems.push(pharmacyBill);
            }
            this.onUpdateTotals();
        }
    }

    public onSetDoctorReqOnPayload(requestId: number) {
        this.payload.isDoctorRequest = true;
        this.payload.doctorRequest = new ClerkDoctorRequestPayload(requestId);
    }

    public onLabPrescriptionSelected(payload: ClerkLabRequestPayload) {
        this.billItems = [];
        if (payload && payload.labItems.length > 0) {
            this.onSetDoctorReqOnPayload(payload.doctorRequestId);
            for (const item of payload.labItems) {
                const isScheme: boolean = this.isSchemedPatient();
                const billItem: BillItem = new BillItem();
                this.setServiceBillPrice(billItem, isScheme, item.service, 1);
                this.billItems.push(billItem);
            }
            this.onUpdateTotals();
        }
    }

    public onRadiologyPrescriptionSelected(payload: ClerkRadiologyRequestPayload) {
        this.billItems = [];
        if (payload && payload.radiologyItems.length > 0) {
            this.onSetDoctorReqOnPayload(payload.doctorRequestId);
            this.selectedRadiologyRequest = payload;
            for (const item of payload.radiologyItems) {
                const isScheme: boolean = this.isSchemedPatient();
                const billItem: BillItem = new BillItem();
                this.setServiceBillPrice(billItem, isScheme, item.service, 1);
                this.billItems.push(billItem);
            }
            this.onUpdateTotals();
        }
    }

    protected isDuplicateService(product: ProductServicePayload): boolean {
        let flag = false;
        if (this.billItems.length > 0) {
            this.billItems.forEach((item) => {
                if (item.productService.id === product.id) {
                    flag = true;
                }
            });
        }
        return flag;
    }

    protected onValidateBillPayload(): ValidationMessage {
        const res: ValidationMessage = {
            status: true,
            message: '',
        };

        if (
            this.payload.billPatientType === BillPatientTypeEnum.REGISTERED &&
            !this.payload.patientId
        ) {
            res.status = false;
            res.message += `Select Patient Before Creating Bill. <br>`;
        }

        if (this.payload.billPatientType === BillPatientTypeEnum.WALK_IN) {
            const validateWalkIn = this.walkInPatientComponent.onValidate();
            if (!validateWalkIn.status) {
                res.status = false;
                res.message += `Enter WalkIn Patient Data. <br>`;
            }
        }

        if (this.props?.billFor === PaymentTypeForEnum.SERVICE) {
            if (!this.billItems.length) {
                res.status = false;
                res.message += `Enter At Least 1 Product or Service. <br>`;
            }
        } else if (this.props?.billFor === PaymentTypeForEnum.DRUG) {
            if (!this.pharmacyBillItems.length) {
                res.status = false;
                res.message += `Enter At Least 1 Product or Drug. <br>`;
            }
        }

        return res;
    }

    protected setServiceBillPrice(
        billItem: BillItem,
        isScheme: boolean,
        payload: ProductServicePayload,
        quantity: number
    ): void {
        billItem.productServiceId = payload.id;
        billItem.productService = payload;
        billItem.payCash = this.payload.billPatientType === BillPatientTypeEnum.WALK_IN;
        billItem.quantity = quantity;

        if (isScheme) {
            billItem.price = 0.0;
            // nhis percent is a sum of this productOrService discount and patient scheme service percent;
            billItem.nhisPercent = payload.discount + this.onGetSchemeDiscount(ServiceOrDrugEnum.SERVICE);
            billItem.nhisPrice = payload.nhisSellingPrice;
            billItem.grossAmount = payload.nhisSellingPrice * quantity;

            const discount = payload.discount + this.onGetSchemeDiscount(ServiceOrDrugEnum.SERVICE);
            billItem.netAmount = this.getPercentValue(payload.nhisSellingPrice, quantity, discount);

            billItem.discountAmount = billItem.grossAmount - billItem.netAmount;
        } else {
            billItem.price = payload.regularSellingPrice;
            billItem.grossAmount = payload.regularSellingPrice * quantity;
            billItem.nhisPercent = 0.0;
            billItem.netAmount = payload.regularSellingPrice * quantity;
            billItem.nhisPrice = 0.0;
        }
        // return billItem;
    }

    public  onGetSchemeDiscount = (serviceOrDrug: ServiceOrDrugEnum): number => {
        // get discount amount set on the patient nhis scheme plan
        let schemeDiscount = 0;
        const isSchemePatient = this.isSchemedPatient();
        if (isSchemePatient === true) {
            // get drug discount value from patient insurance scheme plan
            const patientInsurance = this.payload.patientDetailDto.patientInsurance;
            const schemePlan = patientInsurance.scheme.schemePlans.find(value => value.id === patientInsurance.schemePlanId);
            schemeDiscount = serviceOrDrug === ServiceOrDrugEnum.DRUG ? schemePlan.percentDrug : schemePlan.percentService;
        }
        return schemeDiscount;
    }

    protected onClearForm(): void {
        this.onClearPayload();
        this.payload = new PatientBill();
        this.payload.patientDetailDto = new PatientPayload();
    }

    protected onClearPayload(): void {
        this.billItems = [];
        this.pharmacyBillItems = [];
        this.billPatientTemp = new PatientPayload();
        if (this.payload.billPatientType === BillPatientTypeEnum.REGISTERED) {
            this.patientSearchComponent.clearSearchField();
        }
        if (this.payload.billTypeFor === PaymentTypeForEnum.SERVICE) {
            this.serviceNameSearchComponent.clearSearchField();
        }
        if (this.payload.billTypeFor === PaymentTypeForEnum.DRUG) {
            this.drugSearchComponent.onClearField();
        }
    }

    private async initializeObj(): Promise<void> {
        this.payload = new PatientBill();
        this.payload.isLabBill = this.props.viewType === BillViewTypeEnum.LAB_BILL;

        if (this.props?.billFor === PaymentTypeForEnum.SERVICE) {
            this.initializeForServiceBill();
        } else if (this.props?.billFor === PaymentTypeForEnum.DRUG) {
            this.initializeForDrugBill();
        }

        this.payload.billPatientType = BillPatientTypeEnum.REGISTERED;
        const cUser = this.commonService.getCurrentUser();
        this.payload.costByDto = cUser;
        this.payload.costById = cUser.id;
        this.payload.costDate = this.commonService.getCurrentDate();
        const cLocation = this.commonService.getCurrentLocation();
        this.payload.locationDto = cLocation;
        this.payload.locationId = cLocation?.id;
    }

    private initializeForServiceBill(): void {
        this.billSearchByCollection = serviceSearchBy;
        this.payload.billSearchType = BillSearchBy.NEW_BILL;
        this.payload.billTypeFor = PaymentTypeForEnum.SERVICE;
        this.payload.billPatientCategory = this.props.type;

        this.canShowRadiologyPrescriptionHandler = false;
        this.canShowLabPrescriptionHandler = false;
        this.labRequest = [];
        this.radiologyRequest = [];
        this.onSetSearchByDropDownDefault(serviceSearchBy[0]);

        this.tableProps = {
            view: BillViewTypeEnum.SERVICE_BILL,
            allowEdit: true,
            isAllocateAll: false,
            billPatientCategory: PatientCategoryEnum.GENERAL,
            type: BillPaymentOptionTypeEnum.UN_BILLED_ITEM,
        };
    }

    private initializeForDrugBill(): void {
        this.billSearchByCollection = drugSearchBy;
        this.payload.billSearchType = BillSearchBy.NEW_PRESCRIPTION;
        this.payload.billTypeFor = PaymentTypeForEnum.DRUG;

        this.canShowDrugPrescriptionHandler = false;
        this.drugRequest = [];
        this.onSetSearchByDropDownDefault(drugSearchBy[0]);

        this.tableProps = {
            // initial tableProps value
            view: BillViewTypeEnum.DRUG_BILL,
            allowEdit: false,
            isAllocateAll: false,
            billPatientCategory: PatientCategoryEnum.GENERAL,
            type: BillPaymentOptionTypeEnum.UN_BILLED_ITEM,
        };
    }

    private onSetSearchByDropDownDefault(value: any) {
        if (value) {
            this.searchByDropdownComponent?.onSetDefault(value);
        }
    }

    private onSetPatientInsuranceSchemeDetail(patientInsurance: PatientInsuranceDetails) {
        if (patientInsurance) {
            this.schemeDetails = {
                treatmentType: patientInsurance.treatmentType,
                approvalCode: patientInsurance.approvalCode,
                approvalCodeStart: patientInsurance.approvalStartDate,
                nhisNumber: patientInsurance.nhisNumber,
                diagnosis: patientInsurance.diagnosis,
                approvalCodeEnd: patientInsurance.approvalEndDate,
            };
        }

    }
}

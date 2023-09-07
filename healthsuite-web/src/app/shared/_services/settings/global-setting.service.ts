import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {GlobalSettingKeysEnum, GlobalSettingPayload, GlobalSettingValueEnum} from '@app/shared/_payload/settings/globalSettingPayload';
import { ResponsePayload } from '@app/shared/_payload';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import {validate} from 'codelyzer/walkerFactory/walkerFn';

@Injectable({
    providedIn: 'root',
})
export class GlobalSettingService {
    apiURL: string = environment.apiEndPoint;
    settingSubject: BehaviorSubject<GlobalSettingPayload>;
    settingSubject$: Observable<GlobalSettingPayload>;
    globalSettingPayload: GlobalSettingPayload = new GlobalSettingPayload();

    constructor(private http: HttpClient) {
        this.settingSubject = new BehaviorSubject<GlobalSettingPayload>(this.globalSettingPayload);
        this.settingSubject$ = this.settingSubject.asObservable();
        if (!this.globalSettingPayload.hasData) {
            this.onPrepareGlobalSetting();
        }
    }

    get isEnforceLocationValue(): boolean {
        return this.settingSubject?.value?.enforceSystemLocation?.toLowerCase() === GlobalSettingValueEnum.YES.toLowerCase();
    }

    get isEnforceLabSpecimenAck(): boolean {
        return this.settingSubject?.value?.enforceSpecimenAck?.toLowerCase() === GlobalSettingValueEnum.YES.toLowerCase();
    }

    get isEnableSpecimenAckDuringCollection(): boolean {
        return this.settingSubject?.value?.enableSpecimenAckDuringCollection?.toLowerCase() === GlobalSettingValueEnum.YES.toLowerCase();
    }

    /*------------------------------ SETTINGS ----------------------------*/
    public saveSettings(settingsPayload: Map<string, string>) {
        const payload = { map: {} };
        settingsPayload.forEach((val: string, key: string) => {
            payload.map[key] = val;
        });
        return this.http.post<ResponsePayload<any>>(`${this.apiURL}/settings/update`, payload);
    }

    public getSettingValueByKey(key: string) {
        return this.http.get<ResponsePayload<{ value: string; key: string }>>(
            `${this.apiURL}/settings/find-by-key?key=${key}`,
            {
                observe: 'response',
            }
        );
    }

    public getSettingsMap() {
        return this.http.get<ResponsePayload<any>>(`${this.apiURL}/settings/settings-map`);
    }

    public onPrepareGlobalSetting(data?: Map<string, string>): void {
        this.globalSettingPayload.hasData = true;
        this.getSettingsMap()
            .toPromise()
            .then((r) => {
                this.globalSettingPayload.activateStockInventory = r.data.ActivateStockInventory;
                this.globalSettingPayload.activateClerking = r.data.ActivateClerking;
                this.globalSettingPayload.addWardCodeToBedCode = r.data.AddWardCodeToBedCode;
                this.globalSettingPayload.allowPriceEditForWalkP = r.data.AllowPriceEditForWalkP;
                this.globalSettingPayload.alwaysIncludePayNowBills =
                    r.data.AlwaysIncludePayNowBills;
                this.globalSettingPayload.autoApplyApprovalCode = r.data.AutoApplyApprovalCode;
                this.globalSettingPayload.enableNhisServicePrice = r.data.EnableNhisServicePrice;
                this.globalSettingPayload.autoUpdateSellingAfterReceiveGoods =
                    r.data.AutoUpdateSellingAfterReceiveGoods;
                this.globalSettingPayload.bedCodePrefix = r.data.BedCodePrefix;
                this.globalSettingPayload.consultationFeeExpiryDays =
                    r.data.ConsultationFeeExpiration;
                this.globalSettingPayload.consultantEditWindow = r.data.ConsultationFeeExpiration;
                this.globalSettingPayload.departmentCodePrefix = r.data.DepartmentCodePrefix;
                this.globalSettingPayload.depositAccount = r.data.DepositAccount;
                this.globalSettingPayload.depositReceiptCount = r.data.DepositReceiptCount;
                this.globalSettingPayload.directorOfFinance = r.data.DirectorOfFinance;
                this.globalSettingPayload.disableBillingInvoicePops =
                    r.data.DisableBillingInvoicePops;
                this.globalSettingPayload.enableOutPatientDeposit = r.data.EnableOutPatientDeposit;
                this.globalSettingPayload.enableRegistrationValidation =
                    r.data.EnableRegistrationValidation;
                this.globalSettingPayload.enableSocialWelfareModule =
                    r.data.EnableSocialWelfareModule;
                this.globalSettingPayload.enforcePharmacyPatientCategory =
                    r.data.EnforcePharmacyPatientCategory;
                this.globalSettingPayload.enforceRevisitBeforeBillingActions =
                    r.data.EnforceRevisitBeforeBillingActions;
                this.globalSettingPayload.enforceSpecimenAck = r.data.EnforceSpecimenAck;
                this.globalSettingPayload.enableSpecimenAckDuringCollection = r.data.EnableSpecimenAckDuringCollection;
                this.globalSettingPayload.generateHospitalNumberForOldPatient =
                    r.data.GenerateHospitalNumberForOldPatient;
                this.globalSettingPayload.hideBillDetailsFromReceipt =
                    r.data.HideBillDetailsFromReceipt;
                this.globalSettingPayload.hospitalName = r.data.HospitalName;
                this.globalSettingPayload.patientNumberPrefix = r.data.PatientNumberPrefix;
                this.globalSettingPayload.patientNumberStartPoint = r.data.PatientNumberStartPoint;
                this.globalSettingPayload.patientNumberSuffix = r.data.PatientNumberSuffix;
                this.globalSettingPayload.revenueDepartmentCode = r.data.RevenueDepartmentCode;
                this.globalSettingPayload.salesReceiptCopyCount = r.data.SalesReceiptCopyCount;
                this.globalSettingPayload.specialityUnitCode = r.data.SpecialityUnitCode;
                this.globalSettingPayload.wardCodePrefix = r.data.WardCodePrefix;
                this.globalSettingPayload.enforceSystemLocation = r.data.EnforceSystemLocation;
                this.settingSubject.next(this.globalSettingPayload);
            });
        return;
    }

}

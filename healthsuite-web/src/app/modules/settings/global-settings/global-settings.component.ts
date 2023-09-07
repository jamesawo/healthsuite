import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { IGlobalSetting } from '@app/shared/_payload/settings/globalSettingPayload';
import { GlobalSettingService } from '@app/shared/_services/settings/global-setting.service';
import { Subscription } from 'rxjs';
import { NotificationService } from '@app/shared/_services';

@Component({
    selector: 'app-global-settings',
    templateUrl: './global-settings.component.html',
    styleUrls: ['./global-settings.component.css'],
})
export class GlobalSettingsComponent implements OnInit, OnDestroy, AfterViewInit {
    globalSettingForm: FormGroup;
    settingsMap = new Map<string, any>();
    settingsPayload: IGlobalSetting;
    defaultValues;
    subscription: Subscription;
    enableSpecAck: string;
    disableSpecimenAck = false;


    constructor(
        private globalSettingService: GlobalSettingService,
        private notification: NotificationService,
        private cdr: ChangeDetectorRef
    ) {
        this.settingsPayload = {
            map: this.settingsMap,
        };

        this.globalSettingForm = new FormGroup({
            genHospitalName: new FormControl(),
            genPatientNumberPrefix: new FormControl(),
            genDisableBillingInvoicePops: new FormControl(),
            generateHospitalNumberForOldPatient: new FormControl(),
            enforceRevisitBeforeBillingActions: new FormControl(),
            genEnableSocialWelfareModule: new FormControl(),
            genEnableAutoRevisit: new FormControl(),

            accEnableOutPatientDeposit: new FormControl(),
            accSalesReceiptCopyCount: new FormControl(),
            accDirectorOfFinance: new FormControl(),
            accHideBillDetailsFromReceipt: new FormControl(),
            accAllowPriceEditForWalkP: new FormControl(),
            accEnableRegistrationValidation: new FormControl(),
            accDepositReceiptCount: new FormControl(),
            accAlwaysIncludePayNowBills: new FormControl(),
            accDepositAccount: new FormControl(),

            phActivateStockInventory: new FormControl(),
            phAutoUpdateSellingAfterReceiveGoods: new FormControl(),
            phEnforcePharmacyPatientCatOnBill: new FormControl(),
            clerkActivateClerking: new FormControl(),
            clerkConsultantEditWindow: new FormControl(),
            clerkConsultationFeeExpiration: new FormControl(),
            nhisAutoApplyApprovalCode: new FormControl(),
            enableNhisServicePrice: new FormControl(),

            labEnforceSpecimenAck: new FormControl(),
            labEnableSpecimenAckDuringCollection: new FormControl(),

            confDepartmentCodePrefix: new FormControl(),
            confEnforceSystemLocation: new FormControl(), //yes or no
            confAddWardCodeToBedCode: new FormControl(), //yes or no
            confWardCodePrefix: new FormControl(),
            confBedCodePrefix: new FormControl(),
            confRevenueDepartmentCode: new FormControl(),
            confSpecialityUnitCode: new FormControl(),

            notEnableNotificationCore: new FormControl(),
            notNotificationAccessLevel: new FormControl(),
            notEnableEmailNotification: new FormControl(),
            notEnableSmsNotification: new FormControl(),
            notEnableWebNotification: new FormControl(),
        });
    }

    ngOnInit() {
        setTimeout(() => this.notification.useSpinner('show'), 0);

        this.subscription = this.globalSettingService.getSettingsMap().subscribe((result) => {
            if (result.httpStatusCode === 200) {
                this.defaultValues = result.data;
                this.initializeForm(this.defaultValues);
            }
        });
    }

    ngAfterViewInit(): void {
        setTimeout(() => this.notification.useSpinner('hide'), 0);
    }

    initializeForm(defaultValue) {
        this.globalSettingForm.patchValue({
            genHospitalName: defaultValue.HospitalName,
            genPatientNumberPrefix: defaultValue.PatientNumberPrefix,
            genDisableBillingInvoicePops: defaultValue.DisableBillingInvoicePops,
            generateHospitalNumberForOldPatient: defaultValue.GenerateHospitalNumberForOldPatient,
            enforceRevisitBeforeBillingActions: defaultValue.EnforceRevisitBeforeBillingActions,
            genEnableSocialWelfareModule: defaultValue.EnableSocialWelfareModule,
            genEnableAutoRevisit: defaultValue.EnableAutoRevisit,

            accEnableOutPatientDeposit: defaultValue.EnableOutPatientDeposit,
            accSalesReceiptCopyCount: defaultValue.SalesReceiptCopyCount,
            accDirectorOfFinance: defaultValue.DirectorOfFinance,
            accHideBillDetailsFromReceipt: defaultValue.HideBillDetailsFromReceipt,
            accAllowPriceEditForWalkP: defaultValue.AllowPriceEditForWalkP,
            accEnableRegistrationValidation: defaultValue.EnableRegistrationValidation,
            accDepositReceiptCount: defaultValue.DepositReceiptCount,
            accAlwaysIncludePayNowBills: defaultValue.AlwaysIncludePayNowBills,
            accDepositAccount: defaultValue.DepositAccount,

            phActivateStockInventory: defaultValue.ActivateStockInventory,
            phAutoUpdateSellingAfterReceiveGoods: defaultValue.AutoUpdateSellingAfterReceiveGoods,
            phEnforcePharmacyPatientCatOnBill: defaultValue.EnforcePharmacyPatientCategory,

            clerkActivateClerking: defaultValue.ActivateClerking,
            clerkConsultantEditWindow: defaultValue.ConsultantEditWindow,
            clerkConsultationFeeExpiration: defaultValue.ConsultationFeeExpiration,

            nhisAutoApplyApprovalCode: defaultValue.AutoApplyApprovalCode,
            enableNhisServicePrice: defaultValue.EnableNhisServicePrice,

            labEnforceSpecimenAck: defaultValue.EnforceSpecimenAck,
            labEnableSpecimenAckDuringCollection: defaultValue.EnableSpecimenAckDuringCollection,

            confDepartmentCodePrefix: defaultValue.DepartmentCodePrefix,
            confEnforceSystemLocation: defaultValue.EnforceSystemLocation, //yes or no
            confAddWardCodeToBedCode: defaultValue.AddWardCodeToBedCode, //yes or no
            confWardCodePrefix: defaultValue.WardCodePrefix,
            confBedCodePrefix: defaultValue.BedCodePrefix,
            confRevenueDepartmentCode: defaultValue.RevenueDepartmentCode,
            confSpecialityUnitCode: defaultValue.SpecialityUnitCode,

            notEnableNotificationCore: defaultValue.EnableNotificationCore,
            notNotificationAccessLevel: defaultValue.SetNotificationAccessLevel,
            notEnableEmailNotification: defaultValue.EnableEmailNotification,
            notEnableSmsNotification: defaultValue.EnableSmsNotification,
            notEnableWebNotification: defaultValue.EnableWebNotification,
        });
    }

    get formControls() {
        return this.globalSettingForm.controls;
    }

    saveSettings() {
        if (this.globalSettingForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');
        this.settingsMap = this.mapFormToPayload(this.globalSettingForm);

        this.subscription = this.globalSettingService
            .saveSettings(this.settingsMap)
            .subscribe((result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Global Setting Saved',
                    });
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({ type: 'error', message: result.message });
                }
            });
    }

    mapFormToPayload(form: FormGroup): Map<string, any> {
        this.settingsMap.set('HospitalName', form.get('genHospitalName').value);
        this.settingsMap.set('PatientNumberPrefix', form.get('genPatientNumberPrefix').value);
        this.settingsMap.set(
            'DisableBillingInvoicePops',
            form.get('genDisableBillingInvoicePops').value
        );
        this.settingsMap.set(
            'GenerateHospitalNumberForOldPatient',
            form.get('generateHospitalNumberForOldPatient').value
        );
        this.settingsMap.set('EnableAutoRevisit', form.get('genEnableAutoRevisit').value);
        this.settingsMap.set(
            'EnforceRevisitBeforeBillingActions',
            form.get('enforceRevisitBeforeBillingActions').value
        );
        this.settingsMap.set(
            'EnableSocialWelfareModule',
            form.get('genEnableSocialWelfareModule').value
        );

        this.settingsMap.set(
            'EnableOutPatientDeposit',
            form.get('accEnableOutPatientDeposit').value
        );
        this.settingsMap.set('SalesReceiptCopyCount', form.get('accSalesReceiptCopyCount').value);
        this.settingsMap.set('DirectorOfFinance', form.get('accDirectorOfFinance').value);
        this.settingsMap.set(
            'HideBillDetailsFromReceipt',
            form.get('accHideBillDetailsFromReceipt').value
        );
        this.settingsMap.set('AllowPriceEditForWalkP', form.get('accAllowPriceEditForWalkP').value);
        this.settingsMap.set(
            'EnableRegistrationValidation',
            form.get('accEnableRegistrationValidation').value
        );
        this.settingsMap.set('DepositReceiptCount', form.get('accDepositReceiptCount').value);
        this.settingsMap.set(
            'AlwaysIncludePayNowBills',
            form.get('accAlwaysIncludePayNowBills').value
        );
        this.settingsMap.set('DepositAccount', form.get('accDepositAccount').value);

        this.settingsMap.set('ActivateStockInventory', form.get('phActivateStockInventory').value);
        this.settingsMap.set(
            'AutoUpdateSellingAfterReceiveGoods',
            form.get('phAutoUpdateSellingAfterReceiveGoods').value
        );
        this.settingsMap.set(
            'EnforcePharmacyPatientCategory',
            form.get('phEnforcePharmacyPatientCatOnBill').value
        );

        this.settingsMap.set('ActivateClerking', form.get('clerkActivateClerking').value);
        this.settingsMap.set('ConsultantEditWindow', form.get('clerkConsultantEditWindow').value);
        this.settingsMap.set(
            'ConsultationFeeExpiration',
            form.get('clerkConsultationFeeExpiration').value
        );

        this.settingsMap.set('AutoApplyApprovalCode', form.get('nhisAutoApplyApprovalCode').value);
        this.settingsMap.set('EnableNhisServicePrice', form.get('enableNhisServicePrice').value);
        this.settingsMap.set('EnforceSpecimenAck', form.get('labEnforceSpecimenAck').value);
        this.settingsMap.set('EnableSpecimenAckDuringCollection', form.get('labEnableSpecimenAckDuringCollection').value);

        this.settingsMap.set('DepartmentCodePrefix', form.get('confDepartmentCodePrefix').value);
        this.settingsMap.set('AddWardCodeToBedCode', form.get('confAddWardCodeToBedCode').value);
        this.settingsMap.set('EnforceSystemLocation', form.get('confEnforceSystemLocation').value);
        this.settingsMap.set('WardCodePrefix', form.get('confWardCodePrefix').value);
        this.settingsMap.set('BedCodePrefix', form.get('confBedCodePrefix').value);
        this.settingsMap.set('RevenueDepartmentCode', form.get('confRevenueDepartmentCode').value);
        this.settingsMap.set('SpecialityUnitCode', form.get('confSpecialityUnitCode').value);

        // notification
        this.settingsMap.set('EnableNotificationCore', form.get('notEnableNotificationCore').value);
        this.settingsMap.set(
            'SetNotificationAccessLevel',
            form.get('notNotificationAccessLevel').value
        );
        this.settingsMap.set(
            'EnableEmailNotification',
            form.get('notEnableEmailNotification').value
        );
        this.settingsMap.set('EnableSmsNotification', form.get('notEnableSmsNotification').value);
        this.settingsMap.set('EnableWebNotification', form.get('notEnableWebNotification').value);

        return this.settingsMap;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onEnforceSpecimenAckChange(value: string) {
        if (value === 'no') {
            this.disableSpecimenAck = true;
            this.globalSettingForm.get('labEnableSpecimenAckDuringCollection').setValue('no');
        } else  {
            this.disableSpecimenAck = false;
        }
    }


    // todo::add new settings key/value pair for
    // DepartmentCodePrefix: string,
    // AddWardCodeToBedCode: bool,
    // WardCodePrefix: string
    // BedCodePrefix: string
    // RevenueDepartmentCode: string
    // SpecialityUnitCode: string

}

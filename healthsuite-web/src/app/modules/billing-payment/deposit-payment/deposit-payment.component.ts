import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { DepositPayload, PaymentMethodEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { SeedDataService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { NgForm } from '@angular/forms';
import { PaymentMethodComponent } from '@app/modules/common/payment-method/payment-method.component';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PaymentService } from '@app/shared/_services/billing-payment/payment.service';
import { SharedPayload, ValidationMessage } from '@app/shared/_payload';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';

@Component({
    selector: 'app-deposit-payment',
    templateUrl: './deposit-payment.component.html',
    styleUrls: ['./deposit-payment.component.css'],
})
export class DepositPaymentComponent implements OnInit, OnDestroy {
    public posPaymentMethod = PaymentMethodEnum.POS;
    public depositPayload: DepositPayload = new DepositPayload();
    public patientPayload: PatientPayload = new PatientPayload();
    public locationName: string;
    public depositRev: string;
    public isDepositRevenueLoaded = false;
    @ViewChild('depositForm')
    public depositForm: NgForm;
    @ViewChild('paymentMethodComponent')
    public paymentMethodComponent: PaymentMethodComponent;
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;

    private subscription: Subscription = new Subscription();
    constructor(
        private commonService: CommonService,
        private seedDataService: SeedDataService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private paymentService: PaymentService
    ) {}

    ngOnInit(): void {
        if (this.commonService.isLocationMatch(LocationConstants.CASH_LOCATION)) {
            this.onInitializePayload();
            this.subscription.add(
                this.seedDataService.onGetDepositRevenueDepartment().subscribe((res) => {
                    if (res.status === 200) {
                        this.isDepositRevenueLoaded = true;
                        if (res.body.data) {
                            this.depositRev = res.body.data.name;
                            this.depositPayload.revenueDepartmentId = res.body.data.id;
                        }
                    }
                })
            );
        } else {
            this.commonService.flagLocationError();
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(payload: PatientPayload) {
        if (payload) {
            if (!payload.isOnAdmission) {
                this.patientSearchComponent.clearSearchField();
                this.toast.error('Patient Is Not On Admission', HmisConstants.VALIDATION_ERR, {});
                return;
            }
            this.patientPayload = payload;
            this.depositPayload.patientId = payload.patientId;
        }
    }

    public onPaymentMethodSelected(payload: SharedPayload) {
        if (payload) {
            this.depositPayload.paymentMethod = payload;
            this.depositPayload.paymentMethodId = payload.id;
        }
    }

    public numberMatcher(event: KeyboardEvent) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }

    public onSubmitDeposit() {
        const isFormValid = this.isFormValid();
        if (!isFormValid.status) {
            this.toast.error(isFormValid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.paymentService.onCreateDeposit(this.depositPayload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.status === 200) {
                        this.toast.success(
                            'Deposit Submitted Successfully',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        const file = new Blob([res.body], { type: 'application/pdf' });
                        const fileURL = URL.createObjectURL(file);
                        window.open(fileURL);
                        this.onClearField();
                        this.commonService.lookUpCashierShift();
                    }
                },
                (error) => {
                    console.log(error);
                    this.spinner.hide().then();
                    this.toast.error('Refresh The Page & Try Again.', HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    private isFormValid(): ValidationMessage {
        const validatePaymentMethod = this.paymentMethodComponent.onValidateInput();
        const response: ValidationMessage = { message: '', status: true };
        if (!this.depositRev) {
            response.status = false;
            response.message += 'No Revenue Department Is Attached To Handle Deposit Payment<br>';
        }

        if (validatePaymentMethod.hasError) {
            response.status = false;
            response.message += 'Payment Method Is Invalid <br>';
        }
        if (this.depositForm.control.get('depositDescription').invalid) {
            response.status = false;
            response.message += 'Payment Description Is Invalid <br>';
        }
        if (this.depositForm.control.get('depositAmount').invalid) {
            response.status = false;
            response.message += 'Payment Amount Is Invalid <br>';
        }

        if (!this.depositPayload.patientId) {
            response.status = false;
            response.message += 'Select Patient <br>';
        }
        if (!this.depositPayload.locationId) {
            response.status = false;
            response.message += 'Set System Location <br>';
        }
        if (!this.depositPayload.userId) {
            response.status = false;
            response.message += 'Cashier Record Is Invalid <br>';
        }
        if (this.depositPayload.paymentMethod.name === this.posPaymentMethod) {
            if (!this.depositPayload.transactionRefNumber) {
                this.depositForm.control
                    .get('depositTransactionRef')
                    .setErrors({ incorrect: true });
                response.status = false;
                response.message += 'Transaction Ref Is Invalid<br>';
            }
        }
        return response;
    }

    private onClearField() {
        this.patientSearchComponent.clearSearchField();
        this.paymentMethodComponent.clearSearchFiled();
        this.depositForm.resetForm();
        this.patientPayload = new PatientPayload();
        this.depositPayload = new DepositPayload();
        this.onInitializePayload();
    }

    private onInitializePayload() {
        const location = this.commonService.getCurrentLocation();
        this.depositPayload.userId = this.commonService.getCurrentUser().id;
        this.depositPayload.locationId = location?.id;
        this.locationName = location?.name;
    }
}

import {
    Component,
    ElementRef,
    OnDestroy,
    OnInit,
    QueryList,
    Renderer2,
    ViewChild,
    ViewChildren,
} from '@angular/core';
import Stepper from 'bs-stepper';
import { Subscription } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { fadeInOnEnterAnimation, fadeOutOnLeaveAnimation } from 'angular-animations';
import { EmrService, GlobalSettingService } from '@app/shared/_services';
import { GlobalSettingPayload, GlobalSettingValueEnum } from '@app/shared/_payload';
import {
    PatientCategoryEnum,
    PatientPayload,
    PatientType,
} from '@app/shared/_payload/erm/patient.payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ClipboardService } from 'ngx-clipboard';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { HttpErrorResponse } from '@angular/common/http';
import Swal from 'sweetalert2';
import { WebcamImage } from 'ngx-webcam';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientFacialCaptureComponent } from '@app/modules/emr/patient-registration/patient-facial-capture/patient-facial-capture.component';
import { PatientBasicDetailsComponent } from '@app/modules/emr/patient-registration/patient-basic-details/patient-basic-details.component';
import { PatientCardHolderDetailsComponent } from '@app/modules/emr/patient-registration/patient-card-holder-details/patient-card-holder-details.component';
import { PatientContactDetailsComponent } from '@app/modules/emr/patient-registration/patient-contact-details/patient-contact-details.component';
import { PatientInsuranceDetailsComponent } from '@app/modules/emr/patient-registration/patient-insurance-details/patient-insurance-details.component';
import { PatientMeansOfIdentificationComponent } from '@app/modules/emr/patient-registration/patient-means-of-identification/patient-means-of-identification.component';
import { PatientNokDetailsComponent } from '@app/modules/emr/patient-registration/patient-nok-details/patient-nok-details.component';
import { PatientTransferDetailsComponent } from '@app/modules/emr/patient-registration/patient-transfer-details/patient-transfer-details.component';

@Component({
    selector: 'app-patient-registration',
    templateUrl: './patient-registration.component.html',
    styleUrls: ['./patient-registration.component.css'],
    animations: [fadeInOnEnterAnimation(), fadeOutOnLeaveAnimation()],
})
export class PatientRegistrationComponent implements OnInit, OnDestroy {
    private stepper: Stepper;
    private subscription: Subscription = new Subscription();
    public patient: PatientPayload;
    public globalSetting: GlobalSettingPayload;
    public stepPosition = 1;
    public scheme = PatientCategoryEnum.SCHEME;
    public validationMessage = '';
    public webcamImage: WebcamImage = null;

    @ViewChildren('stepperGroup') public stepperGroup: QueryList<any>;
    @ViewChild('captureComponent') public captureComponent: PatientFacialCaptureComponent;
    @ViewChild('basicDetailsComponent') public basicDetailsComponent: PatientBasicDetailsComponent;
    @ViewChild('cardHolderDetailsComponent')
    public cardHolderDetailsComponent: PatientCardHolderDetailsComponent;
    @ViewChild('contactDetailsComponent')
    public contactDetailsComponent: PatientContactDetailsComponent;
    @ViewChild('insuranceDetailsComponent')
    public insuranceDetailsComponent: PatientInsuranceDetailsComponent;
    @ViewChild('meansOfIdentificationComponent')
    public meansOfIdentificationComponent: PatientMeansOfIdentificationComponent;
    @ViewChild('nokDetailsComponent') public nokComponent: PatientNokDetailsComponent;
    @ViewChild('transferDetailsComponent')
    public transferDetailsComponent: PatientTransferDetailsComponent;

    constructor(
        private formBuilder: FormBuilder,
        private globalSettingService: GlobalSettingService,
        private renderer: Renderer2,
        private elem: ElementRef,
        private emrService: EmrService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private clipboardService: ClipboardService,
        private commonService: CommonService
    ) {}

    ngOnInit() {
        // initialize stepper
        this.stepper = new Stepper(document.querySelector('#stepper1'), {
            linear: false,
            animation: true,
        });

        this.subscription.add(
            this.globalSettingService.settingSubject$.subscribe((data) => {
                this.globalSetting = data;
            })
        );

        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patient = payload;
                this.patient.patientTypeEnum = PatientType.NEW;
            })
        );

        this.onSetProp();
    }

    ngOnDestroy() {
        this.emrService.patientSubject.next(new PatientPayload());
        this.subscription.unsubscribe();
    }

    // stepper next controls
    next() {
        if (this.patient.patientCategoryEnum === PatientCategoryEnum.GENERAL) {
            this.stepPosition = 3;
            this.stepper.to(3);
            return;
        } else {
            this.stepPosition += 1;
            this.stepper.next();
        }
    }

    previous() {
        if (this.patient.patientCategoryEnum === PatientCategoryEnum.GENERAL) {
            this.stepPosition = 1;
            this.stepper.to(1);
            return;
        } else {
            this.stepPosition -= 1;
            this.stepper.previous();
        }
    }

    async onRegisterPatient() {
        if (!this.onValidatePayload(this.patient)) {
            this.toast.error(this.validationMessage, 'Form Error', { enableHtml: true });
            this.validationMessage = '';
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onRegisterPatient(this.patient).subscribe(
                (data) => {
                    this.spinner.hide().then();
                    if (data.status === 200) {
                        const patientNumber = data.body;
                        // Sweet alert
                        Swal.fire({
                            title: `Registration Successful`,
                            html: `Registered Patient Number: <strong> ${patientNumber.patientNumber}</strong>`,
                            icon: 'success',
                            allowOutsideClick: false,
                            confirmButtonColor: '#24ac09',
                            confirmButtonText: `<i class="fa fa-thumbs-up"></i> OK`,
                        }).then((r) => {
                            if (r.isConfirmed) {
                                this.emrService.patientSubject.next(new PatientPayload());
                                this.onResetPayload();
                                // this.clipboardService.copyFromContent(patientNumber.patientNumber);
                                this.stepper.reset();
                            } else {
                            }
                        });
                    }
                },
                (error: HttpErrorResponse) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_SERVER_ERROR);
                }
            )
        );
    }

    onSetProp() {
        this.patient.registeredBy = this.commonService.getCurrentUser();
        this.patient.registeredFrom = this.commonService.getCurrentLocation();
    }

    onResetPayload() {
        this.captureComponent.onRestImage();
        this.basicDetailsComponent.onResetForm();
        this.meansOfIdentificationComponent.onResetForm();
        this.contactDetailsComponent.onResetForm();
        this.transferDetailsComponent.onResetForm();
    }

    onValidatePayload(payload: PatientPayload): boolean {
        // validate patient category
        let isValid = true;
        let message = '';
        if (!payload.patientCategoryEnum) {
            message = 'Please select patient category <br>';
            this.validationMessage += message;
            isValid = false;
        }

        // if global setting require receipt then validate receipt
        if (this.globalSetting.enableRegistrationValidation === GlobalSettingValueEnum.YES) {
            if (!payload.receiptNumber) {
                message = 'Provide a receipt <br>';
                this.validationMessage += message;
                isValid = false;
            }
        }

        // validate patient number if user should generate patient number
        if (payload.patientTypeEnum === PatientType.OLD) {
            if (
                this.globalSetting.generateHospitalNumberForOldPatient ===
                    GlobalSettingValueEnum.NO &&
                !payload.patientNumber
            ) {
                message = 'Provide old patient number <br>';
                this.validationMessage += message;
                isValid = false;
            }
        }

        // validate required form fields based on patient category
        if (!payload.patientFirstName) {
            this.validationMessage += 'Enter patient first name <br>';
            isValid = false;
        }
        if (!payload.patientLastName) {
            this.validationMessage += 'Enter patient last name <br>';
            isValid = false;
        }
        if (!payload.patientOtherName) {
            this.validationMessage += 'Enter patient other name <br>';
            isValid = false;
        }

        // religion, ethnic group and date of birth
        if (!payload.ethnicGroupId) {
            this.validationMessage += 'Select patient ethnic group <br>';
            isValid = false;
        }

        if (
            !payload.patientDateOfBirth?.year &&
            !payload.patientDateOfBirth?.month &&
            !payload.patientDateOfBirth?.day
        ) {
            this.validationMessage += 'Choose patient date of birth <br>';
            isValid = false;
        }

        if (!payload.religionId) {
            this.validationMessage += 'Select patient religion <br>';
            isValid = false;
        }

        if (!payload.maritalStatusId) {
            this.validationMessage += 'Select patient marital status <br>';
            isValid = false;
        }

        if (!payload.genderId) {
            this.validationMessage += 'Select patient gender <br>';
            isValid = false;
        }

        // if patient is SCHEME
        if (payload.patientCategoryEnum === PatientCategoryEnum.SCHEME) {
            // check scheme details
            if (!payload.patientInsurance?.scheme?.id) {
                this.validationMessage += 'Provide insurance details correctly.<br>';
                isValid = false;
            }
        }

        // if patient is GENERAL
        if (payload.patientCategoryEnum === PatientCategoryEnum.GENERAL) {
            // check general details;
            /*
            if (!payload.patientNokDetail) {
                this.validationMessage += 'Enter next of kin details <br>';
                isValid = false;
            }
             */
        }

        if (!payload.registeredFrom?.id) {
            // this.validationMessage += 'Registered From Is Required. Check Location <br>';
            // isValid = false;
            payload.registeredFrom = this.commonService.getCurrentLocation();
        }

        if (!payload.registeredBy?.id) {
            // this.validationMessage += 'Registered By Is Required, Try Re-Login <br>';
            // isValid = false;
            payload.registeredBy = this.commonService.getCurrentUser();
        }

        return isValid;
    }

    public handleImage(webcamImage: WebcamImage) {
        this.webcamImage = webcamImage;
        this.patient.passportBase64 = webcamImage.imageAsBase64;
    }
}

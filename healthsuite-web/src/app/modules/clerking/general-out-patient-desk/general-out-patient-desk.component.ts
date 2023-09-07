import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    NurseWaitingPayload,
    SharedPayload,
    ValidationMessage,
    WaitingViewTypeEnum,
} from '@app/shared/_payload';
import Stepper from 'bs-stepper';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from '@app/shared/_services/common/common.service';
import { EmrService } from '@app/shared/_services';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import {
    ClerkingTemplateSearchPayload,
    ClinicDeskEnum,
    OutPatientDeskPayload,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';
import { ClerkConsultationService } from '@app/shared/_services/clerking/clerk-consultation.service';
import Swal from 'sweetalert2';
import { DoctorWaitingListService } from '@app/shared/_services/clerking/doctor-waiting-list.service';
import { bounceAnimation, rubberBandAnimation } from 'angular-animations';
import { SpecialitySearchComponent } from '@app/modules/common/speciality-search/speciality-search.component';
import { ClerkingTemplateService } from '@app/shared/_services/clerking/clerking-template.service';
import { ClinicAssessmentFormComponent } from '@app/modules/clerking/general-out-patient-desk/components/clinic-assessment-form/clinic-assessment-form.component';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { ClerkingTemplateSearchComponent } from '@app/modules/common/clerking-template-search/clerking-template-search.component';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';

@Component({
    selector: 'app-general-out-patient-desk',
    templateUrl: './general-out-patient-desk.component.html',
    styleUrls: ['./general-out-patient-desk.component.css'],
    animations: [rubberBandAnimation(), bounceAnimation({ duration: 1000 })],
})
export class GeneralOutPatientDeskComponent implements OnInit, OnDestroy {
    @ViewChild('specialitySearchComponent')
    public specialitySearchComponent: SpecialitySearchComponent;
    @ViewChild('clinicAssessmentFormComponent')
    public clinicAssessmentFormComponent: ClinicAssessmentFormComponent;
    @ViewChild('patientSearchComponent')
    public patientSearchComponent: PatientSearchComponent;
    @ViewChild('clerkingTemplateSearchComponent')
    public clerkingTemplateSearchComponent: ClerkingTemplateSearchComponent;
    public activeView = 'waiting';
    public payload: OutPatientDeskPayload;
    public doctor = WaitingViewTypeEnum.DOCTOR;
    public waitingCount = 0;
    public attendedWaitingCount = 0;
    public stepPosition = 1;
    public isUseSavedTemplate = false;
    public isSavingTemplate = false;
    public isInvalidTemplateName = false;
    public outPatientDesk = ClinicDeskEnum.GENERAL_OUTPATIENT_DESK;

    private stepper: Stepper;
    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private emrService: EmrService,
        private generalDeskService: ClerkConsultationService,
        private docWaitingList: DoctorWaitingListService,
        private templateService: ClerkingTemplateService,
        private clerkRequestService: ClerkRequestService,
    ) {}

    ngOnInit(): void {
        const isMatch = this.commonService.isLocationMatch(LocationConstants.CLINIC_LOCATION);
        if (isMatch) {
            this.onInitializeComponent();
        } else {
            this.commonService.flagLocationError();
        }
        this.stepper = new Stepper(document.querySelector('#bsStepper'), {
            linear: false,
            animation: true,
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onUpdateCount = (value: number) => {
        setTimeout(() => {
            this.waitingCount = value;
        }, 0);
    };

    public onUpdateAttendedWaitingCount = (value: number) => {
        setTimeout(() => {
            this.attendedWaitingCount = value;
        }, 0);
    };

    public onChangeActiveView(viewType: 'waiting' | 'attended') {
        if (viewType) {
            this.activeView = viewType;
        }
    }

    public onPatientSelected(waitingPayload: NurseWaitingPayload) {
        // todo:: remove duplicate (use shared service)
        if (waitingPayload?.patientId) {
            this.spinner.show().then();
            this.subscription.add(
                this.emrService.onFindPatientById(waitingPayload.patientId).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res.body) {
                            this.payload.patient = res.body;
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(
                            'Something Went Wrong, Refresh And Try Again',
                            HmisConstants.ERR_TITLE
                        );
                    }
                )
            );
        }
    }

    public onPatientSearchSelected(patientPayload: PatientPayload) {
        if (patientPayload) {
            this.payload.patient = patientPayload;
        }
    }

    public onNext() {
        // this.stepPosition = 3;
        // this.stepper.to(3);
        this.stepPosition += 1;
        this.stepper.next();
        this.commonService.onScroll(360, 100);
    }

    public onPrevious() {
        this.stepPosition -= 1;
        this.stepper.previous();
        this.commonService.onScroll(360, 100);
    }

    public onSpecialitySelected(speciality: SharedPayload) {
        if (speciality) {
            this.payload.specialityUnit = speciality;
        }
    }

    public onSubmit() {
        Swal.fire({
            showCancelButton: true,
            confirmButtonText: 'SAVE TEMPLATE & END PATIENT SESSION',
            confirmButtonColor: '#128111',
            cancelButtonText: 'GO BACK',
            cancelButtonColor: '#dc3545',
            showDenyButton: true,
            denyButtonColor: '#5b9cb6',
            denyButtonText: 'SAVE AS TEMPLATE ONLY',
            allowOutsideClick: false,
            allowEscapeKey: false,
        }).then((result) => {
            if (result.isConfirmed) {
                this.onSaveAndEndSession().then();
            } else if (result.isDenied) {
                this.onSaveTemplateOnly();
            }
        });
    }

    public onTypingTemplateName(event: any) {
        this.isInvalidTemplateName = false;
    }

    public onSaveTemplateOnly() {
        this.spinner.show().then();

        if (!this.payload.templateName || this.payload.templateName === '') {
            this.isInvalidTemplateName = true;
            this.toast.error('Template Name is Required', HmisConstants.VALIDATION_ERR);
            this.spinner.hide().then();
            return;
        }

        this.isInvalidTemplateName = false;

        this.isSavingTemplate = true;
        this.subscription.add(
            this.generalDeskService.onSaveTemplateOnly(this.payload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    this.isSavingTemplate = false;
                    if (result === true) {
                        this.toast.success('Success.', 'Template Saved Successfully');
                    } else {
                        this.toast.error(HmisConstants.ERR_TITLE, 'Could Not Save Template.');
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.isSavingTemplate = false;
                    this.toast.error('Failed To Save Template.', HmisConstants.LAST_ACTION_FAILED);
                }
            )
        );
    }

    public async onSaveAndEndSession() {
        this.payload.doctorRequest = this.clerkRequestService.doctorRequestS.value;
        this.spinner.show().then();
        const valid = this.onValidateEntry();
        if (valid.status === false) {
            this.spinner.hide().then();
            this.toast.error(valid.message, HmisConstants.VALIDATION_ERR);
            return;
        }
        this.subscription.add(
            this.generalDeskService.onSaveGeneralOutPatientDesk(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res?.body?.data) {
                        this.toast.success(
                            'Session Saved Successfully.',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.docWaitingList.onRemoveFromWaitingList(
                            this.payload.patient.patientId,
                            this.payload.capturedBy.id
                        );
                        this.onInitializeComponent();
                        this.onClearPayload();
                    } else {
                        this.toast.error(HmisConstants.ERR_SERVER_ERROR, HmisConstants.ERR_TITLE);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.ERR_SERVER_ERROR, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onToggleHasInformantDetails(event: any) {
        this.payload.hasInformantDetails = event.target.checked;
    }

    public onToggleUseSavedTemplate(event: any) {
        this.isUseSavedTemplate = event.target.checked;
    }

    public onToggleSaveAsTemplate(event: any) {
        this.payload.isSaveAsTemplate = event.target.checked;
    }

    public onSavedTemplateSelected(template: ClerkingTemplateSearchPayload) {
        if (template) {
            if (template.deskEnum !== this.outPatientDesk) {
                this.toast.error(
                    `Template format is for ${template.deskTitle}`,
                    'Incompatible Template Format Selected'
                );
                return;
            }
            this.spinner.show().then();
            const tempPatient = this.payload.patient;
            this.subscription.add(
                this.templateService.onFindOutPatientDeskTemplate(template.id).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res) {
                            this.payload = res;
                            this.specialitySearchComponent.onSetDefault(res.specialityUnit);
                            this.payload.id = undefined;
                            this.payload.patient = tempPatient;
                            this.onSetCaptureDetail();
                        }
                    },
                    (error) => {
                        this.spinner.hide().then();
                        this.toast.error(HmisConstants.TEMPLATE_FAILED, HmisConstants.ERR_TITLE);
                    }
                )
            );
        }
    }

    public onReset() {
        this.commonService
            .askAreYouSure('ARE YOU SURE YOU WANT TO CLEAR THE FORM?', 'CLEAR THE FORM ?', 'info')
            .then((result) => {
                if (result.isConfirmed === true) {
                    this.onClearPayload();
                }
            });
    }

    private onClearPayload() {
        this.spinner.show().then();
        this.payload = new OutPatientDeskPayload();
        this.specialitySearchComponent.onClearField();
        this.patientSearchComponent.clearSearchField();
        this.payload.patient = new PatientPayload();
        this.clerkingTemplateSearchComponent.onClear();
        this.spinner.hide().then();
    }

    private onResetStepper() {
        this.stepper.reset();
    }

    private onInitializeComponent() {
        this.payload = new OutPatientDeskPayload();
        this.onSetCaptureDetail();
    }

    private onSetCaptureDetail() {
        this.payload.capturedBy = this.commonService.getCurrentUser();
        this.payload.captureFromLocation = this.commonService.getCurrentLocation();
    }

    private onValidateEntry(): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!this.payload.patient.patientId) {
            res.message += 'Patient is Required <br>';
            res.status = false;
        }
        if (!this.payload.clinicalAssessment.provisionalDiagnosis) {
            res.message += 'Provisional Diagnosis is Required <br>';
            res.status = false;
            this.stepPosition = 5;
            this.stepper.to(5);
        }

        return res;
    }
}

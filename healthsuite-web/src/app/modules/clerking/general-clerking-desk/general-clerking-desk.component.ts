import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { SpecialitySearchComponent } from '@app/modules/common/speciality-search/speciality-search.component';
import {
    ClinicAssessmentFormComponent
} from '@app/modules/clerking/general-out-patient-desk/components/clinic-assessment-form/clinic-assessment-form.component';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { ClerkingTemplateSearchComponent } from '@app/modules/common/clerking-template-search/clerking-template-search.component';
import {
    ClerkingTemplateSearchPayload,
    ClinicDeskEnum,
    GeneralClerkDeskPayload,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';
import {NurseWaitingPayload, SharedPayload, WaitingViewTypeEnum} from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { EmrService } from '@app/shared/_services';
import { ClerkConsultationService } from '@app/shared/_services/clerking/clerk-consultation.service';
import { DoctorWaitingListService } from '@app/shared/_services/clerking/doctor-waiting-list.service';
import { ClerkingTemplateService } from '@app/shared/_services/clerking/clerking-template.service';
import { ClerkRequestService } from '@app/shared/_services/clerking/clerk-request.service';
import { LocationConstants } from '@app/shared/_models/constant/locationConstants';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import {PatientAttendedListComponent} from '@app/modules/common/patient-attended-list/patient-attended-list.component';
import {UserSearchComponent} from '@app/modules/common/user-search/user-search.component';

@Component({
    selector: 'app-general-clerking-desk',
    templateUrl: './general-clerking-desk.component.html',
    styleUrls: ['./general-clerking-desk.component.css'],
})
export class GeneralClerkingDeskComponent implements OnInit, OnDestroy {
    @ViewChild('specialitySearchComponent') public specialitySearchComponent: SpecialitySearchComponent;
    @ViewChild('userSearchComponent') public userSearchComponent: UserSearchComponent;
    @ViewChild('clinicAssessmentFormComponent') public clinicAssessmentFormComponent: ClinicAssessmentFormComponent;
    @ViewChild('patientSearchComponent') public patientSearchComponent: PatientSearchComponent;
    @ViewChild('clerkingTemplateSearchComponent') public clerkingTemplateSearchComponent: ClerkingTemplateSearchComponent;
    @ViewChild('attendedListComponent') public doctorAttendedListComponent: PatientAttendedListComponent;
    public activeView = 'waiting';
    public payload: GeneralClerkDeskPayload = new GeneralClerkDeskPayload();
    public doctor = WaitingViewTypeEnum.DOCTOR;
    public waitingCount = 0;
    public attendedWaitingCount = 0;
    public stepPosition = 1;
    public isUseSavedTemplate = false;
    public isInvalidTemplateName = false;
    public generalDesk = ClinicDeskEnum.GENERAL_CLERKING_DESK;

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
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onUpdateCount = (value: number) => {
        setTimeout(() => {
            this.waitingCount = value;
        }, 0);
    }

    public onUpdateAttendedWaitingCount = (value: number) => {
        setTimeout(() => {
            this.attendedWaitingCount = value;
        }, 0);
    }

    public onChangeActiveView(viewType: 'waiting' | 'attended') {
        if (viewType) {
            this.activeView = viewType;
        }
    }

    public onPatientSelected(waitingPayload: NurseWaitingPayload) {
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

    public onConsultantSelected(userPayload: UserPayload) {
        if (userPayload) {
            if (this.isPatientSelected() === false) {
                return;
            }
            this.payload.consultant = userPayload;
        }
    }

    public onSpecialitySelected(sharedPayload: SharedPayload) {
        if (sharedPayload) {
            if (this.isPatientSelected() === false) {
                return;
            }
            this.payload.specialityUnit = sharedPayload;
        }
    }

    public onToggleHasInformantDetails(event: any) {
        this.payload.hasInformantDetail = event.target.checked;
    }

    public onToggleUseSavedTemplate(event: any) {
        this.isUseSavedTemplate = event.target.checked;
    }

    public onToggleSaveAsTemplate(event: any) {
        this.payload.isSaveAsTemplate = event.target.checked;
    }

    public onToggleFollowUpNote(event: any) {
        this.payload.isUseFollowUpNote = event.target.checked;
    }

    public onSavedTemplateSelected(template: ClerkingTemplateSearchPayload) {
        if (template) {
            if (this.isPatientSelected() === false) { return; }
            if (template.deskEnum !== this.generalDesk) {
                this.toast.error(
                    `Template format is for ${template.deskTitle}`,
                    'Incompatible Template Format Selected'
                );
                return;
            }
            this.spinner.show().then();
            const tempPatient = this.payload.patient;
            this.subscription.add(
                this.templateService.onFindGeneralClerkTemplate(template.id).subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        if (res) {
                            this.payload = res;
                            // this.specialitySearchComponent.onSetDefault(res.specialityUnit);
                            this.payload.id = undefined;
                            this.payload.patient = tempPatient;
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

    private onInitializeComponent() {
        this.payload = new GeneralClerkDeskPayload();
        this.payload.isUseFollowUpNote = false;
        this.payload.isSaveAsTemplate = false;
        this.payload.clerkedBy = this.commonService.getCurrentUser();
        this.payload.location = this.commonService.getCurrentLocation();
    }

    public onSubmit() {
        this.payload.doctorRequest = this.clerkRequestService.doctorRequestS.value;
        if (this.isPatientSelected() === false) {
            return;
        }
        if (!this.payload.provisionalDiagnosis) {
            this.toast.error('Provisional Diagnosis is required', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.spinner.show().then();
        this.subscription.add(
            this.generalDeskService.onSaveGeneralClerking(this.payload).subscribe( res => {
                this.spinner.hide().then();
                if (res?.message) {
                    this.docWaitingList.onRemoveFromWaitingList(
                        this.payload.patient.patientId,
                        this.payload.clerkedBy.id
                    );
                    this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                    this.onReset();
                }

            }, error => {
                this.spinner.hide().then();
                this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                console.log(error);
            })
        );

    }
    public onReset() {
        this.onInitializeComponent();
        this.specialitySearchComponent?.onClearField();
        this.userSearchComponent.onClearField();
        this.clerkingTemplateSearchComponent?.onClear();
    }

    private isPatientSelected = (): boolean => {
        const isPatient = !!this.payload.patient.patientId;
        if (isPatient === false) {
            this.toast.error('Select Patient First', HmisConstants.VALIDATION_ERR);
        }
        return isPatient;
    }

}

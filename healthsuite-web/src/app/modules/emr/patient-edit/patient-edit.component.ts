import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { EmrService } from '@app/shared/_services';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import Swal from 'sweetalert2';
import { WebcamImage } from 'ngx-webcam';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import { PatientFacialCaptureComponent } from '@app/modules/emr/patient-registration/patient-facial-capture/patient-facial-capture.component';
import { PatientContactDetailsComponent } from '@app/modules/emr/patient-registration/patient-contact-details/patient-contact-details.component';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { NationalityPayload } from '@app/modules/settings/_payload/nationality.payload';

@Component({
    selector: 'app-patient-edit',
    templateUrl: './patient-edit.component.html',
    styleUrls: ['./patient-edit.component.css'],
})
export class PatientEditComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    @ViewChild('captureComponent') captureComponent: PatientFacialCaptureComponent;
    @ViewChild('contactDetailsComponent') contactDetailsComponent: PatientContactDetailsComponent;
    public selectedPatient: PatientPayload;
    public webcamImage: WebcamImage = null;
    public defaultNationality: NationalityPayload = new NationalityPayload();
    public defaultChildrenLga: NationalityPayload [] = [];

    private subscription: Subscription = new Subscription();
    constructor(
        private emrService: EmrService,
        private toastService: ToastrService,
        private spinnerService: NgxSpinnerService,
        private commonService: CommonService
    ) {}

    ngOnInit() {
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.selectedPatient = payload;
                this.selectedPatient.registeredBy = this.commonService.getCurrentUser();
                this.selectedPatient.registeredFrom = this.commonService.getCurrentLocation();
            })
        );
    }

    ngOnDestroy() {
        this.emrService.patientSubject.next(new PatientPayload());
        this.subscription.unsubscribe();
    }

    public onEditPatientDetails() {
        this.spinnerService.show().then();
        if (!this.selectedPatient.patientNumber) {
            this.toastService.error('Select Patient Before Editing', 'Patient Edit Error');
            this.spinnerService.hide().then();
            return;
        }
        this.emrService.onUpdatePatient(this.selectedPatient).subscribe(
            (value) => {
                this.spinnerService.hide().then();
                if (value.status === 200) {
                    Swal.fire({
                        title: `Patient Edit Successful`,
                        icon: 'success',
                        allowOutsideClick: false,
                        confirmButtonColor: '#24ac09',
                        confirmButtonText: `<i class="fa fa-thumbs-up"></i> OK`,
                    }).then((r) => {
                        this.onResetForm();
                    });
                }
            },
            (error) => {
                this.spinnerService.hide().then();
                this.toastService.error(error.error.message, error.error.error);
            }
        );
    }

    public handleImage(webcamImage: WebcamImage) {
        this.webcamImage = webcamImage;
        this.selectedPatient.passportBase64 = webcamImage.imageAsBase64;
    }

    public onSearchPatientSelected(payload: PatientPayload) {
        if (payload) {
            this.emrService.patientSubject.next(payload);
            if (payload?.passportBase64) {
                this.onSetPatientCapturedImage(payload.passportBase64);
            }
            this.onFindPatientNationality(payload.patientContactDetail.nationality.id);
        }
    }

    public onFindPatientNationality(id: number) {
        this.spinnerService.show().then();
        this.subscription.add(
            this.emrService.getNationalityByLgaId(id).subscribe(
                (res) => {
                    this.spinnerService.hide().then();
                    this.defaultNationality = { id: res.id, name: res.name };
                    this.defaultChildrenLga = res.childrenDto;
                },
                (error) => {
                    this.spinnerService.hide().then();
                    this.toastService.error(
                        error.error.message,
                        HmisConstants.INTERNAL_SERVER_ERROR
                    );
                }
            )
        );
    }

    public onSetPatientCapturedImage(image: string) {
        if (image) {
            this.captureComponent.onSetDefaultOrPreviousImage(image);
        }
    }

    private onResetForm() {
        this.emrService.patientSubject.next(new PatientPayload());
        this.patientSearchComponent.clearSearchField();
        this.captureComponent.onRestImage();
        this.contactDetailsComponent.onResetForm();
    }
}

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {PatientTransferPayload} from '@app/shared/_payload/erm/patient-transfer.payload';
import {DatePayload, ValidationMessage} from '@app/shared/_payload';
import {UserPayload} from '@app/modules/settings/_payload/userPayload';
import {BedPayload, WardPayload} from '@app/modules/settings';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {CommonService} from '@app/shared/_services/common/common.service';
import {Subscription} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {NgxSpinnerService} from 'ngx-spinner';
import {AdmissionService} from '@app/shared/_services/emr/admission.service';
import {NgSelectComponent} from '@ng-select/ng-select';
import {WardSearchComponent} from '@app/modules/common/ward-search/ward-search.component';
import {UserSearchComponent} from '@app/modules/common/user-search/user-search.component';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Component({
  selector: 'app-shared-ward-transfer',
  templateUrl: './shared-ward-transfer.component.html',
  styleUrls: ['./shared-ward-transfer.component.css']
})
export class SharedWardTransferComponent implements OnInit, OnDestroy {
  data: { patientPayload: PatientPayload };

  @ViewChild('bedDropDownComponent') bedDropDownComponent: NgSelectComponent;
  @ViewChild('wardSearchComponent') wardSearchComponent: WardSearchComponent;
  @ViewChild('userSearchComponent') userSearchComponent: UserSearchComponent;
  @ViewChild('dateComponent') dateComponent: SharedDateComponent;

  @Input('payload') payload: PatientTransferPayload;
  @Output('submitted') submitted: EventEmitter<boolean> = new EventEmitter<boolean>();
  public bedsList: BedPayload[] = [];

  private subscription: Subscription = new Subscription();

  constructor(
      private commonService: CommonService,
      private toast: ToastrService,
      private spinner: NgxSpinnerService,
      private admissionService: AdmissionService
  ) { }

  ngOnInit(): void {
    if (!this.payload && this.data.patientPayload) {
      this.payload = new PatientTransferPayload();
      this.payload.user = this.commonService.getCurrentUser();
      this.payload.location = this.commonService.getCurrentLocation();
      this.payload.patient = this.data.patientPayload;
      this.payload.currentAdmission = this.data.patientPayload.admission;
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getCurrentAdmissionDate(): Date {
    if (this.payload?.currentAdmission) {
      return this.commonService.transformToDate(this.payload.currentAdmission.admissionDate);
    }
    return null;
  }

  onDateSelected(date: DatePayload) {
    if (date) {
      this.payload.transferDate = date;
    }
  }

  onConsultantSelected(user: UserPayload) {
    if (user) {
      this.payload.consultant = user;
    }
  }

  onWardSelected(ward: WardPayload) {
    if (ward) {
      this.bedsList = [];
      if (ward?.beds?.length > 0) {
        this.bedsList = ward.beds.filter((value) => value.isOccupied === false);
      }
      this.payload.newWard = ward;
    }
  }

  onBedSelected(bed: BedPayload) {
    if (bed) {
      this.payload.newBed = bed;
    }
  }

  onSubmit() {
    const checker = this.onValidateBeforeSave();
    if (checker.status === false) {
      this.toast.error(checker.message, HmisConstants.VALIDATION_ERR);
      return;
    }

    this.spinner.show().then();
    this.subscription.add(
        this.admissionService.onTransferPatientWard(this.payload).subscribe(
            (res) => {
              this.spinner.hide().then();
              if (res.message) {
                this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                this.onClearForm();
                this.submitted.emit(true);
              }
            },
            (error) => {
              this.spinner.hide().then();
              this.toast.error(error.error.message, HmisConstants.INTERNAL_SERVER_ERROR);
            }
        )
    );
  }

  onClearForm() {
    this.wardSearchComponent.onClearPayload();
    this.userSearchComponent.onClearField();
    this.dateComponent.onClearDateField();
    this.bedDropDownComponent.clearModel();
  }


  onValidateBeforeSave(): ValidationMessage {
    const res: ValidationMessage = { message: '', status: true };
    if (!this.payload?.patient) {
      res.status = false;
      res.message += 'Patient is required';
    }
    if (!this.payload?.transferNote) {
      res.status = false;
      res.message += 'Transfer Note is required';
    }
    if (!this.payload?.newWard) {
      res.status = false;
      res.message += 'Patient New Ward is required';
    }
    if (!this.payload.newBed?.id) {
      res.status = false;
      res.message += 'Bed is required';
    }
    return res;
  }
}

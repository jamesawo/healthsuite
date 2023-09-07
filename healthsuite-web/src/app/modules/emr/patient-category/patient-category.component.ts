import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
    CardHolderType,
    CategoryViewTypeEnum,
    PatientCategoryEnum,
    PatientCategoryUpdatePayload,
    PatientPayload,
    SchemeData,
    SchemePlan,
} from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { EmrService } from '@app/shared/_services';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { CommonService } from '@app/shared/_services/common/common.service';
import {PatientSearchComponent} from '@app/modules/common/patient-search/patient-search.component';

@Component({
    selector: 'app-patient-category',
    templateUrl: './patient-category.component.html',
    styleUrls: ['./patient-category.component.css'],
})
export class PatientCategoryComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent')
    public patientSearchComponent: PatientSearchComponent;


    public payload: PatientCategoryUpdatePayload = new PatientCategoryUpdatePayload();
    public currentDetails = CategoryViewTypeEnum.CURRENT_DETAILS;
    public changeCategory = CategoryViewTypeEnum.CHANGE_CATEGORY;
    public editSchemeDetails = CategoryViewTypeEnum.EDIT_SCHEME_DETAILS;
    public scheme = PatientCategoryEnum.SCHEME;
    public isUpdateToScheme = false;

    private subscription: Subscription = new Subscription();

    constructor(
        private emrService: EmrService,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService
    ) {}

    ngOnInit() {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onViewTypeChange(value: CategoryViewTypeEnum) {
        this.payload.viewType = value;
    }

    public onPatientSelected(patientPayload: PatientPayload) {
        this.onResetView();
        if (patientPayload) {
            this.payload.patient = patientPayload;
        }
    }

    public onUpdatePatientSchemeData() {
        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onUpdatePatientSchemeDetails(this.payload.patient).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res.message) {
                        this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                        this.onResetView();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                }
            )
        );
    }

    public onCheckBoxClick(value: any) {
        const isChecked = value.target.checked;
        const patientCategoryEnum = this.payload.patient.patientCategoryEnum;

        if (patientCategoryEnum === PatientCategoryEnum.GENERAL) {
            this.payload.patient.schemePlan = new SchemePlan();
            this.payload.patient.patientInsurance = { scheme: new SchemeData() };
            this.payload.patient.patientCardHolder = { cardHolderType: CardHolderType.HOLDER };
        }

        this.isUpdateToScheme =
            isChecked === true && patientCategoryEnum === PatientCategoryEnum.GENERAL;
    }

    public onChangePatientCategory() {
        const newCategory =
            this.payload.patient.patientCategoryEnum === PatientCategoryEnum.SCHEME
                ? PatientCategoryEnum.GENERAL
                : PatientCategoryEnum.SCHEME;
        const payload: any = {
            patient: this.payload.patient,
            newCategory,
            user: this.commonService.getCurrentUser(),
            location: this.commonService.getCurrentLocation(),
        };

        this.spinner.show().then();
        this.subscription.add(
            this.emrService.onChangePatientCategory(payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.toast.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                        this.onResetView();
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.FAILED_RESPONSE, HmisConstants.ERROR);
                    console.log(error);
                }
            )
        );
    }

    public onResetView() {
        this.payload.patient = new PatientPayload();
        this.onViewTypeChange(CategoryViewTypeEnum.CURRENT_DETAILS);
        this.patientSearchComponent.clearSearchField();
    }
}

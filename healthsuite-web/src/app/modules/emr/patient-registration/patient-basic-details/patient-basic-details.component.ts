import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { DepartmentPayload } from '@app/modules/settings';
import { Subscription } from 'rxjs';
import { EmrService, GlobalSettingService, SeedDataService } from '@app/shared/_services';
import {
    DatePayload,
    GenderPayload,
    GlobalSettingPayload,
    GlobalSettingValueEnum,
    HmisSharedDropDown,
    SharedPayload,
} from '@app/shared/_payload';
import {PatientCategoryEnum, PatientPayload, PatientType} from '@app/shared/_payload/erm/patient.payload';
import * as moment from 'moment';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';
import {ClinicDropdownComponent} from '@app/modules/common/clinic-dropdown/clinic-dropdown.component';
import {NgSelectComponent} from '@ng-select/ng-select';
import {DrugDispenseSearchByEnum} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import {PaymentTypeForEnum, ReceiptPayload} from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
    selector: 'app-patient-basic-details',
    templateUrl: './patient-basic-details.component.html',
    styleUrls: ['./patient-basic-details.component.css'],
})
export class PatientBasicDetailsComponent implements OnInit, OnDestroy {
    @ViewChild('dateOfBirthComponent') dateComponent: SharedDateComponent;
    @ViewChild('clinicDropdownComponent') clinicDropDown: ClinicDropdownComponent;
    @ViewChild('ngPatientCategoryDropdown') ngPatientCategoryDropdown: NgSelectComponent;

    @Input()
    public props: { isRegister: boolean };

    public maritalStatus: SharedPayload[];
    public gender: GenderPayload[];
    public religion: SharedPayload[];
    public ethnicGroup: SharedPayload[];
    public globalSetting: GlobalSettingPayload;
    public patientCategoryEnumList: HmisSharedDropDown[];
    public new: PatientType = PatientType.NEW;
    public old: PatientType = PatientType.OLD;
    public age = 'Auto Calculated';
    public patientPayload: PatientPayload;
    public yes = GlobalSettingValueEnum.YES;
    public tempDateOfBirth: DatePayload;
    protected subscription: Subscription = new Subscription();
    public searchBySelected = DrugDispenseSearchByEnum.RECEIPT_NUMBER;
    public serviceReceipt: PaymentTypeForEnum = PaymentTypeForEnum.SERVICE;


    constructor(
        private seedDataService: SeedDataService,
        private globalSettingService: GlobalSettingService,
        private emrService: EmrService
    ) {}

    ngOnInit(): void {
        this.patientPayload = new PatientPayload();
        if (!this.props) {
            this.props = { isRegister: true };
        }
        this.patientCategoryEnumList = this.emrService.patientCategoryEnumToList();
        this.subscription.add(
            this.seedDataService.seedSubject$.subscribe((data) => {
                this.maritalStatus = data.maritalS;
                this.gender = data.gender;
                this.religion = data.religion;
                this.ethnicGroup = data.ethnicG;
            })
        );

        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patientPayload = payload;
                this.tempDateOfBirth = payload.patientDateOfBirth;
            })
        );

        this.subscription.add(
            this.globalSettingService.settingSubject$.subscribe((setting) => {
                this.globalSetting = setting;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    protected getAgeFromDateOfBirth(dateOfBirth?: DatePayload): string {
        const { year, month, day }: any = dateOfBirth;
        const starts = moment(new Date(year, month, day));
        const ends = moment();
        const duration = moment.duration(ends.diff(starts));
        const years =
            duration.years() > 1 ? `${duration.years()} years,` : `${duration.years()} year,`;
        const months =
            duration.months() > 1 ? `${duration.months()} months,` : `${duration.months()} month,`;
        const days = duration.days() > 1 ? `${duration.days()} days` : `${duration.days()} day`;

        const age = `${years} ${months} ${days}`;
        this.patientPayload.patientAge = age;
        return age;
    }

    onSubmit() {
        // this.patientCategory = [...this.patientCategory, new PharmacyPatientCategoryPayload()];
    }

    public onSelectPatientCategory(value: any) {
        this.patientPayload.patientCategoryEnum = value;
    }

    public onDateSelected(datePayload: DatePayload) {
        this.patientPayload.patientDateOfBirth = datePayload;
        this.getAgeFromDateOfBirth(datePayload);
    }

    public onReceiptSelected(receiptPayload: ReceiptPayload) {
        console.log(receiptPayload);
        if (receiptPayload && receiptPayload.receiptNumber) {
            this.patientPayload.receiptNumber = receiptPayload.receiptNumber;
        }
    }

    public onClinicSelected(value: DepartmentPayload) {
        if (value) {
            this.patientPayload.departmentId = value.id;
        }
    }

    onResetForm() {
        this.patientCategoryEnumList = this.emrService.patientCategoryEnumToList();
        this.patientPayload.patientCategoryEnum = PatientCategoryEnum.GENERAL;
        this.patientPayload.patientTypeEnum = this.new;
        this.ngPatientCategoryDropdown.clearModel();
        this.clinicDropDown.onClearField();
        this.dateComponent.onClearDateField();
    }
}

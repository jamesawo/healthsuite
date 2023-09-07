import { NgModule } from '@angular/core';
import { UnderConstructionComponent } from '@app/modules/common/under-contsruction/under-construction.component';
import { PatientSearchComponent } from './patient-search/patient-search.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ServiceNameSearchComponent } from './service-name-search/service-name-search.component';
import { ServiceUsageDropDownComponent } from '@app/modules/common/service-usage-dropdown/service-usage-drop-down.component';
import { ServiceDepartmentDropdownComponent } from './service-department-dropdown/service-department-dropdown.component';
import { RevenueDepartmentDropdownComponent } from './revenue-department-dropdown/revenue-department-dropdown.component';
import { InputFieldComponent } from './input-field/input-field.component';
import { ModalPopupDirective } from '@app/shared/_directives/modal/modal-popup.directive';
import { FileSizePipe, MomentDatePipe } from '@app/shared/_pipes';
import { BillPatientTypeComponent } from './bill-patient-type/bill-patient-type.component';
import { BillSearchTypeComponent } from './bill-search-type/bill-search-type.component';
import { PatientBioCardComponent } from './patient-bio-card/patient-bio-card.component';
import { WalkInPatientComponent } from './walk-in-patient/walk-in-patient.component';
import { SchemeSearchComponent } from './scheme-search/scheme-search.component';
import { WardSearchComponent } from './ward-search/ward-search.component';
import { ConsultantSearchComponent } from './consultant-search/consultant-search.component';
import { SharedDateComponent } from './shared-date/shared-date.component';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { ClinicDropdownComponent } from './clinic-dropdown/clinic-dropdown.component';
import { UserSearchComponent } from './user-search/user-search.component';
import { SpecialitySearchComponent } from './speciality-search/speciality-search.component';
import { TimepickerModule } from 'ngx-bootstrap/timepicker';
import { BillDataSearchComponent } from './bill-data-search/bill-data-search.component';
import { PaymentMethodComponent } from './payment-method/payment-method.component';
import { BillTableDataComponent } from './bill-table-data/bill-table-data.component';
import { SupplierSearchComponent } from './supplier-search/supplier-search.component';
import { DrugSearchComponent } from './drug-search/drug-search.component';
import { EditableTableComponent } from './editable-table/editable-table.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { DrugClassificationComponent } from './drug-classification/drug-classification.component';
import { DrugFormulationComponent } from './drug-formulation/drug-formulation.component';
import { DrugFrequencyDropdownComponent } from './drug-frequency-dropdown/drug-frequency-dropdown.component';
import { DrugPaycashToggleComponent } from './drug-paycash-toggle/drug-paycash-toggle.component';
import { SearchByDropdownComponent } from './search-by-dropdown/search-by-dropdown.component';
import { PaymentReceiptSearchComponent } from './payment-receipt-search/payment-receipt-search.component';
import { DrugOrderSearchComponent } from './drug-order-search/drug-order-search.component';
import { PharmacyLocationSearchComponent } from './pharmacy-location-search/pharmacy-location-search.component';
import { PatientWaitingListComponent } from '@app/modules/common/patient-waiting-list/patient-waiting-list.component';
import { NurseLabelModalComponent } from './nurse-label-modal/nurse-label-modal.component';
import { PatientAttendedListComponent } from './patient-attended-list/patient-attended-list.component';
import { NgbPopoverModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { RelationshipDropdownComponent } from './relationship-dropdown/relationship-dropdown.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { ClerkingTemplateSearchComponent } from './clerking-template-search/clerking-template-search.component';
import { SpecimenSearchDropdownComponent } from './specimen-search-dropdown/specimen-search-dropdown.component';
import { LabBillPrescriptionHandlerComponent } from './lab-bill-prescription-handler/lab-bill-prescription-handler.component';
import { RadiologyBillExtrasComponent } from './radiology-bill-extras/radiology-bill-extras.component';
import { DrugSearchByDepartmentComponent } from './drug-search-by-department/drug-search-by-department.component';
import { DrugAdministrationRouteComponent } from './drug-administration-route/drug-administration-route.component';
import { DrugPrescriptionHandlerComponent } from './drug-prescription-handler/drug-prescription-handler.component';
import { RadiologyPrescriptionHandlerComponent } from './radiology-prescription-handler/radiology-prescription-handler.component';
import { SpecimenActionComponent } from './specimen-action/specimen-action.component';
import { LabSearchByComponent } from './lab-search-by/lab-search-by.component';
import { LabSpecimenColorComponent } from './lab-specimen-color/lab-specimen-color.component';
import { GenderDropdownComponent } from './gender-dropdown/gender-dropdown.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { CashierShiftNumberSearchComponent } from './cashier-shift-number-search/cashier-shift-number-search.component';
import { CashierShiftNumberDropdownComponent } from './cashier-shift-number-dropdown/cashier-shift-number-dropdown.component';
import { AdmissionSessionDropdownComponent } from './admission-session-dropdown/admission-session-dropdown.component';
import { DischargeStatusDropdownComponent } from './discharge-status-dropdown/discharge-status-dropdown.component';
import { RoleDropdownComponent } from './role-dropdown/role-dropdown.component';
import { SearchLabParamComponent } from './search-lab-param/search-lab-param.component';
import { SpecimenColorDropdownComponent } from './specimen-color-dropdown/specimen-color-dropdown.component';
import { SchemeTreatmentTypeComponent } from './scheme-treatment-type/scheme-treatment-type.component';

@NgModule({
    declarations: [
        UnderConstructionComponent,
        PatientSearchComponent,
        ServiceNameSearchComponent,
        ServiceUsageDropDownComponent,
        ServiceDepartmentDropdownComponent,
        RevenueDepartmentDropdownComponent,
        InputFieldComponent,
        ModalPopupDirective,
        FileSizePipe,
        BillPatientTypeComponent,
        BillSearchTypeComponent,
        PatientBioCardComponent,
        WalkInPatientComponent,
        SchemeSearchComponent,
        WardSearchComponent,
        ConsultantSearchComponent,
        SharedDateComponent,
        ClinicDropdownComponent,
        UserSearchComponent,
        SpecialitySearchComponent,
        MomentDatePipe,
        BillDataSearchComponent,
        PaymentMethodComponent,
        BillTableDataComponent,
        SupplierSearchComponent,
        DrugSearchComponent,
        EditableTableComponent,
        DrugClassificationComponent,
        DrugFormulationComponent,
        DrugFrequencyDropdownComponent,
        DrugPaycashToggleComponent,
        SearchByDropdownComponent,
        PaymentReceiptSearchComponent,
        DrugOrderSearchComponent,
        PharmacyLocationSearchComponent,
        PatientWaitingListComponent,
        NurseLabelModalComponent,
        PatientAttendedListComponent,
        RelationshipDropdownComponent,
        ClerkingTemplateSearchComponent,
        SpecimenSearchDropdownComponent,
        LabBillPrescriptionHandlerComponent,
        RadiologyBillExtrasComponent,
        DrugSearchByDepartmentComponent,
        DrugAdministrationRouteComponent,
        DrugPrescriptionHandlerComponent,
        RadiologyPrescriptionHandlerComponent,
        SpecimenActionComponent,
        LabSearchByComponent,
        LabSpecimenColorComponent,
        GenderDropdownComponent,
        ChangePasswordComponent,
        CashierShiftNumberSearchComponent,
        CashierShiftNumberDropdownComponent,
        AdmissionSessionDropdownComponent,
        DischargeStatusDropdownComponent,
        RoleDropdownComponent,
        SearchLabParamComponent,
        SpecimenColorDropdownComponent,
        SchemeTreatmentTypeComponent,
    ],
    imports: [
        NgSelectModule,
        FormsModule,
        CommonModule,
        BsDatepickerModule,
        TimepickerModule,
        NgxPaginationModule,
        NgbTooltipModule,
        NgbPopoverModule,
        NgxSpinnerModule,
    ],
    exports: [
        UnderConstructionComponent,
        PatientSearchComponent,
        ServiceNameSearchComponent,
        ServiceUsageDropDownComponent,
        RevenueDepartmentDropdownComponent,
        ServiceDepartmentDropdownComponent,
        InputFieldComponent,
        ModalPopupDirective,
        FileSizePipe,
        BillPatientTypeComponent,
        BillSearchTypeComponent,
        PatientBioCardComponent,
        WalkInPatientComponent,
        SchemeSearchComponent,
        WardSearchComponent,
        ConsultantSearchComponent,
        SharedDateComponent,
        ClinicDropdownComponent,
        UserSearchComponent,
        SpecialitySearchComponent,
        MomentDatePipe,
        BillDataSearchComponent,
        PaymentMethodComponent,
        BillTableDataComponent,
        SupplierSearchComponent,
        DrugSearchComponent,
        EditableTableComponent,
        DrugFormulationComponent,
        DrugClassificationComponent,
        DrugPaycashToggleComponent,
        SearchByDropdownComponent,
        PaymentReceiptSearchComponent,
        DrugOrderSearchComponent,
        PharmacyLocationSearchComponent,
        PatientWaitingListComponent,
        PatientAttendedListComponent,
        RelationshipDropdownComponent,
        ClerkingTemplateSearchComponent,
        SpecimenSearchDropdownComponent,
        LabBillPrescriptionHandlerComponent,
        RadiologyBillExtrasComponent,
        DrugSearchByDepartmentComponent,
        DrugFrequencyDropdownComponent,
        DrugAdministrationRouteComponent,
        DrugPrescriptionHandlerComponent,
        RadiologyPrescriptionHandlerComponent,
        SpecimenActionComponent,
        LabSearchByComponent,
        GenderDropdownComponent,
        CashierShiftNumberSearchComponent,
        CashierShiftNumberDropdownComponent,
        AdmissionSessionDropdownComponent,
        DischargeStatusDropdownComponent,
        RoleDropdownComponent,
        SearchLabParamComponent,
        SpecimenColorDropdownComponent,
        SchemeTreatmentTypeComponent,
    ],
})
export class HmisCommonModule {}

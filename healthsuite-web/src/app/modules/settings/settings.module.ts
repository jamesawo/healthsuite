import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserManagerComponent } from './user-manager/user-manager.component';
import { SettingsRoutingModule } from '@app/modules/settings/settings-routing.module';
import { RoleManagerComponent } from './role-manager/role-manager.component';
import { SeedDataComponent } from './seed-data/seed-data.component';
import { GlobalSettingsComponent } from './global-settings/global-settings.component';
import { BatchUploadComponent } from './batch-upload/batch-upload.component';
import { LocationSettingsComponent } from './location-settings/location-settings.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
    NgbDatepickerModule,
    NgbPaginationModule,
    NgbPopoverModule,
    NgbTypeaheadModule,
} from '@ng-bootstrap/ng-bootstrap';
import { SeedGenderComponent } from './seed-data/seed-gender/seed-gender.component';
import { SeedMaritalStatusComponent } from './seed-data/seed-marital-status/seed-marital-status.component';
import { SeedRelationshipComponent } from './seed-data/seed-relationship/seed-relationship.component';
import { SeedReligionComponent } from './seed-data/seed-religion/seed-religion.component';
import { SeedSurgeryComponent } from './seed-data/seed-surgery/seed-surgery.component';
import { SeedRolesComponent } from './seed-data/seed-roles/seed-roles.component';
import { SeedSchemeComponent } from './seed-data/seed-scheme/seed-scheme.component';
import { SeedDrugClassificationComponent } from './seed-data/seed-drug-classification/seed-drug-classification.component';
import { SeedDrugFormulationComponent } from './seed-data/seed-drug-formulation/seed-drug-formulation.component';
import { SeedMarkUpComponent } from './seed-data/seed-mark-up/seed-mark-up.component';
import { SeedDepartmentComponent } from './seed-data/seed-department/seed-department.component';
import { SeedBedComponent } from './seed-data/seed-bed/seed-bed.component';
import { SeedRevenueDepartmentComponent } from './seed-data/seed-revenue-department/seed-revenue-department.component';
import { SeedSpecialityUnitComponent } from './seed-data/seed-speciality-unit/seed-speciality-unit.component';
import { SeedServicesComponent } from './seed-data/seed-services/seed-services.component';
import { SeedPharmacyPatientCategoryComponent } from './seed-data/seed-pharmacy-patient-category/seed-pharmacy-patient-category.component';
import { SeedNursingNoteLabelComponent } from './seed-data/seed-nursing-note-label/seed-nursing-note-label.component';
import { SeedLabSpecimenComponent } from './seed-data/seed-lab-specimen/seed-lab-specimen.component';
import { SeedOrganismComponent } from './seed-data/seed-organism/seed-organism.component';
import { SeedAntibioticsComponent } from './seed-data/seed-antibiotics/seed-antibiotics.component';
import { SeedBillWaiverCategoryComponent } from './seed-data/seed-bill-waiver-category/seed-bill-waiver-category.component';
import { SeedPharmacySupplierCategoryComponent } from '@app/modules/settings/seed-data/seed-pharmacy-supplier-category/seed-pharmacy-supplier-category.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { RightSurveillanceComponent } from './right-surveillance/right-surveillance.component';
import { RetSchemeSuspensionComponent } from './ret-scheme-suspension/ret-scheme-suspension.component';
import { NhisSchemeSuspensionComponent } from './nhis-scheme-suspension/nhis-scheme-suspension.component';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { SeedEthnicGroupComponent } from './seed-data/seed-ethnic-group/seed-ethnic-group.component';
import { SeedNationalityComponent } from './seed-data/seed-nationality/seed-nationality.component';
import { SeedMeansOfIdentificationComponent } from './seed-data/seed-means-of-identification/seed-means-of-identification.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgxPaginationModule } from 'ngx-pagination';
import { PopoverModule } from 'ngx-bootstrap/popover';
import { UserUpdateModalComponent } from './user-manager/user-update-modal/user-update-modal.component';

@NgModule({
    declarations: [
        UserManagerComponent,
        RoleManagerComponent,
        SeedDataComponent,
        GlobalSettingsComponent,
        BatchUploadComponent,
        LocationSettingsComponent,
        PasswordResetComponent,
        SeedGenderComponent,
        SeedMaritalStatusComponent,
        SeedRelationshipComponent,
        SeedReligionComponent,
        SeedSurgeryComponent,
        SeedRolesComponent,
        SeedSchemeComponent,
        SeedDrugClassificationComponent,
        SeedDrugFormulationComponent,
        SeedMarkUpComponent,
        SeedDepartmentComponent,
        SeedBedComponent,
        SeedRevenueDepartmentComponent,
        SeedSpecialityUnitComponent,
        SeedServicesComponent,
        SeedPharmacyPatientCategoryComponent,
        SeedPharmacySupplierCategoryComponent,
        SeedNursingNoteLabelComponent,
        SeedLabSpecimenComponent,
        SeedOrganismComponent,
        SeedAntibioticsComponent,
        SeedBillWaiverCategoryComponent,
        RightSurveillanceComponent,
        RetSchemeSuspensionComponent,
        NhisSchemeSuspensionComponent,
        SeedEthnicGroupComponent,
        SeedNationalityComponent,
        SeedMeansOfIdentificationComponent,
        UserUpdateModalComponent,
    ],
    imports: [
        CommonModule,
        SettingsRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgbTypeaheadModule,
        NgbPopoverModule,
        NgbDatepickerModule,
        NgbPaginationModule,
        NgMultiSelectDropDownModule,
        HmisCommonModule,
        NgSelectModule,
        NgxPaginationModule,
        PopoverModule,
    ],
    exports: [
        UserManagerComponent,
        RoleManagerComponent,
        SeedDataComponent,
        GlobalSettingsComponent,
        BatchUploadComponent,
        LocationSettingsComponent,
        PasswordResetComponent,
        SeedGenderComponent,
        SeedMaritalStatusComponent,
        SeedRelationshipComponent,
        SeedReligionComponent,
        SeedSurgeryComponent,
        SeedRolesComponent,
        SeedSchemeComponent,
        SeedDrugClassificationComponent,
        SeedDrugFormulationComponent,
        SeedMarkUpComponent,
        SeedDepartmentComponent,
        SeedBedComponent,
        SeedRevenueDepartmentComponent,
        SeedSpecialityUnitComponent,
        SeedServicesComponent,
        SeedPharmacyPatientCategoryComponent,
        SeedPharmacySupplierCategoryComponent,
        SeedNursingNoteLabelComponent,
        SeedLabSpecimenComponent,
        SeedOrganismComponent,
        SeedAntibioticsComponent,
        SeedBillWaiverCategoryComponent,
        RightSurveillanceComponent,
    ],
})
export class SettingsModule {}

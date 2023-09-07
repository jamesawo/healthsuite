import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import {
    DepartmentCategoryEnum,
    GenderPayload,
    ResponsePayload,
    SharedPayload,
} from '@app/shared/_payload';
import {
    BedPayload,
    DepartmentPayload,
    RevenueDepartmentPayload,
    WardPayload,
} from '@app/modules/settings';
import { PharmacyPatientCategoryPayload } from '@app/modules/settings/_payload/pharmacyPatientCategoryPayload';
import { AntibioticsPayload } from '@app/modules/settings/_payload/antibioticsPayload';
import { NationalityPayload } from '@app/modules/settings/_payload/nationality.payload';
import { BehaviorSubject, Observable } from 'rxjs';
import { SeedSubjectPayload } from '@app/shared/_payload/settings/seed-subject.payload';
import { SchemeData } from '@app/shared/_payload/erm/patient.payload';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {AppStorageService} from '@app/shared/_services/common/app-storage.service';

@Injectable({
    providedIn: 'root',
})
export class SeedDataService {
    url: string = environment.apiEndPoint + '/seed';
    seedSubject: BehaviorSubject<SeedSubjectPayload>;
    seedSubject$: Observable<SeedSubjectPayload>;
    seedData: SeedSubjectPayload = new SeedSubjectPayload();

    defaultDepartment: DepartmentPayload;

    constructor(private http: HttpClient, private appStorage: AppStorageService) {
        this.seedSubject = new BehaviorSubject(this.seedData);
        this.seedSubject$ = this.seedSubject.asObservable();
        if (!this.seedData.hasData) {
            this.onAfterLoginInitializeData();
        }
    }

    get defaultLocation() {
        return this.seedSubject.value;
    }

    /*------------------------------ SEED DATA ----------------------------*/

    // SEED GENDER
    createGender(payload: GenderPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/gender`, payload);
    }
    getAllGender() {
        return this.http.get<ResponsePayload<GenderPayload[]>>(`${this.url}/gender/all`);
    }

    onUpdateGender(payload: GenderPayload) {
        return this.http.patch(`${this.url}/gender/update`, payload);
    }
    /*------------------------------ SEED Marital Status ----------------------------*/

    // Marital Status
    createMaritalStatus(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/maritalStatus`, payload);
    }
    getAllMaritalStatus() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/maritalStatus/all`);
    }

    // Seed Relationship
    getAllRelationship() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/relationship/all`);
    }
    createRelationship(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/relationship`, payload);
    }

    // Seed Religion
    getAllReligion() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/religion/all`);
    }

    createReligion(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/religion`, payload);
    }

    // Seed Surgery
    getAllSurgery() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/surgery/all`);
    }
    createSurgery(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/surgery`, payload);
    }

    // Seed Role
    getAllRole() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/role/all`);
    }
    createRole(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/role/create`, payload);
    }
    updateRole(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/role/update`, payload);
    }

    // Seed Drug Classification
    getAllDrugClassification() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/drugClassification/all`);
    }
    createDrugClassification(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/drugClassification`, payload);
    }

    // Seed Drug Formulation
    getAllDrugFormulation() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/drugFormulation/all`);
    }
    createDrugFormulation(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/drugFormulation`, payload);
    }

    // Seed Department
    getOneDepartment(departmentId: number) {
        return this.http.get<DepartmentPayload>(`${this.url}/get-one-department`);
    }

    // Seed Department
    getOneDepartmentByCode(depCode: string) {
        return this.http.get<DepartmentPayload>(`${this.url}/get-one-department-by-code/${depCode}`);
    }

    // Seed => Get Department By Name
    getOneDepartmentByName(depName: string) {
        return this.http.get<DepartmentPayload>(`${this.url}/get-one-department-by-name/${depName}`);
    }


    getAllDepartmentCategories() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/departmentCategories/all/`);
    }

    getAllDepartment() {
        return this.http.get<ResponsePayload<DepartmentPayload[]>>(`${this.url}/department/all`);
    }

    getDepartmentsByCategoryName(departmentCategoryName: string) {
        return this.http.get<DepartmentPayload[]>(
            `${this.url}/department-by-category-name/${departmentCategoryName}`
        );
    }

    createDepartment(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/department`, payload);
    }

    onUploadDepartmentFile(file: File) {
        const formData: FormData = new FormData();
        formData.append('file', file);
        return this.http.post<ResponsePayload<any>>(`${this.url}/department/batchUpload`, file, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
    }

    // Seed Bed
    getAllWard() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/ward/all`);
    }

    getWardsAndBeds() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/ward/wards-and-bed-count`);
    }

    onAddBedsToWard(payload: WardPayload) {
        return this.http.post<ResponsePayload<WardPayload>>(
            `${this.url}/bed/add-beds-to-ward`,
            payload
        );
    }

    onUpdateWardBedCount(payload: WardPayload) {
        return this.http.post<ResponsePayload<boolean>>(
            `${this.url}/ward/update-bed-count`,
            payload,
            {
                observe: 'response',
            }
        );
    }

    // Seed Revenue Department Type
    onGetAllRevenueDepartmentTypes() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/revenueDepartmentType/all`);
    }

    // Seed Revenue Department
    onGetAllRevenueDepartment() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/revenueDepartment/all`);
    }

    onCreateRevenueDepartment(payload: RevenueDepartmentPayload) {
        return this.http.post<ResponsePayload<any>>(
            `${this.url}/revenueDepartment/create`,
            payload
        );
    }

    onGetDepositRevenueDepartment() {
        return this.http.get<ResponsePayload<RevenueDepartmentPayload>>(
            `${this.url}/revenue-department/deposit-revenue-department`,
            {
                observe: 'response',
            }
        );
    }

    // Seed Speciality Unit
    onCreateSpecialityUnit(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/specialityUnit/create`, payload);
    }

    onGetAllSpecialityUnit() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/specialityUnit/all`);
    }

    onGetAllPaymentMethod() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/paymentMethod/all`);
    }

    onGetAllPharmacyPatientCategoryTypes() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/pharmacyPatientCategoryTypes/all`);
    }

    onGetAllPharmacyPatientCategory() {
        return this.http.get<ResponsePayload<PharmacyPatientCategoryPayload[]>>(
            `${this.url}/pharmacyPatientCategory/all`
        );
    }

    onCreatePharmacyPatientCategory(payload: PharmacyPatientCategoryPayload) {
        return this.http.post<ResponsePayload<any>>(
            `${this.url}/pharmacyPatientCategory/create`,
            payload
        );
    }

    // Seed Pharmacy Supplier Category
    onGetAllPharmacySupplierCategory() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/pharmacySupplierCategory/all`);
    }

    onCreatePharmacySupplierCategory(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(
            `${this.url}/pharmacySupplierCategory/create`,
            payload
        );
    }

    // Seed Nursing Note Label
    onGetAllNursingNoteLabel() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/nursingNoteLabel/all`);
    }
    onCreateNursingNoteLabel(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/nursingNoteLabel/create`, payload);
    }

    // Seed Lab Specimen
    onCreateLabSpecimen(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/labSpecimen/create`, payload);
    }

    onGetAllLabSpecimen() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/labSpecimen/all`);
    }

    // Seed Organism
    onGetAllOrganism() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/organism/all`);
    }

    onCreateOrganism(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/organism/create`, payload);
    }

    // Seed Antibiotics
    onGetAllAntibiotics() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/antibiotics/all`);
    }

    onCreateAntibiotics(payload: AntibioticsPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/antibiotics/create`, payload);
    }

    // Seed Bill Waiver
    onGetAllBillWaiver() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/billWaiver/all`);
    }

    onCreateBillWaiver(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/billWaiver/create`, payload);
    }

    // ethnic group
    onGetAllEthnicGroup() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/ethnic-group/all`);
    }

    onCreateEthnicGroup(payload: SharedPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/ethnic-group/create`, payload, {
            observe: 'response',
        });
    }

    // nationality
    onCreateNationality(payload: NationalityPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.url}/nationality/create`, payload);
    }

    onGetNationalityParentOnly() {
        return this.http.get<ResponsePayload<any>>(`${this.url}/nationality/all`);
    }

    onGetNationalityWithChildren() {
        return this.http.get<ResponsePayload<NationalityPayload[]>>(
            `${this.url}/nationality/parent-with-children`
        );
    }

    // means of identification
    onGetMeansOfIdentification() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(
            `${this.url}/means-of-identification/all`
        );
    }

    onCreateMeansOfIdentification(payload: SharedPayload) {
        return this.http.post<ResponsePayload<SharedPayload>>(
            `${this.url}/means-of-identification/create`,
            payload
        );
    }

    // scheme data
    onGetAllScheme() {
        return this.http.get<ResponsePayload<SharedPayload[]>>(`${this.url}/scheme/all`);
    }

    onCreateScheme(payload: SchemeData) {
        return this.http.post<ResponsePayload<SchemeData>>(`${this.url}/scheme/create`, payload);
    }

    public onAfterLoginInitializeData(): void {
        this.seedData.hasData = true;
        // get patient category
        this.onGetAllPharmacyPatientCategory()
            .toPromise()
            .then((r) => {
                this.seedData.pCategory = r.data;
                this.seedSubject.next(this.seedData);
            });

        this.getDepartmentsByCategoryName(DepartmentCategoryEnum.CLINIC)
            .toPromise()
            .then((value) => {
                this.seedData.clinic = value;
                this.onUpdateSeedDataSubject();
            });
        // marital status
        this.getAllMaritalStatus()
            .toPromise()
            .then((value) => {
                this.seedData.maritalS = value.data;
                this.onUpdateSeedDataSubject();
            });
        // gender
        this.getAllGender()
            .toPromise()
            .then((value) => {
                this.seedData.gender = value.data;
                this.onUpdateSeedDataSubject();
            });
        // religion
        this.getAllReligion()
            .toPromise()
            .then((value) => {
                this.seedData.religion = value.data;
                this.onUpdateSeedDataSubject();
            });
        // ethnic group
        this.onGetAllEthnicGroup()
            .toPromise()
            .then((value) => {
                this.seedData.ethnicG = value.data;
                this.onUpdateSeedDataSubject();
            });

        // means of identification
        this.onGetMeansOfIdentification()
            .toPromise()
            .then((value) => {
                this.seedData.meansOfIdentification = value.data;
                this.onUpdateSeedDataSubject();
            });

        // nationality
        this.onGetNationalityWithChildren()
            .toPromise()
            .then((value) => {
                this.seedData.nationality = value.data;
                this.onUpdateSeedDataSubject();
            });

        // nok relationship
        this.getAllRelationship()
            .toPromise()
            .then((value) => {
                this.seedData.nokRelationship = value.data;
                this.onUpdateSeedDataSubject();
            });
        // scheme data
        this.onGetAllScheme()
            .toPromise()
            .then((value) => {
                this.seedData.schemeData = value.data;
                this.onUpdateSeedDataSubject();
            });

        this.getOneDepartmentByName(HmisConstants.DEFAULT_DEPARTMENT_NAME).toPromise().then(
            value => {
                this.seedData.defaultLocation = value;
                this.defaultDepartment = value;
                this.appStorage.setStore(HmisConstants.DEFAULT_LOCATION_KEY, value);
                this.onUpdateSeedDataSubject();
            }
        );
    }

    private onUpdateSeedDataSubject() {
        this.seedSubject.next(this.seedData);
    }
}

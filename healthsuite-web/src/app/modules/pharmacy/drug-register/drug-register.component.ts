import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import {
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import {
    DrugTableColumnEnum,
    FileTemplateEnum,
    FileUploadTypeEnum,
    InputComponentChanged,
    InputComponentUsageEnum,
    ModalSizeEnum,
    SharedPayload,
    ValidationMessage,
} from '@app/shared/_payload';
import { ModalPopupService, SeedDataService } from '@app/shared/_services';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { NgxSpinnerService } from 'ngx-spinner';
import { FileUploadService } from '@app/shared/_services/others/file-upload.service';
import { saveAs } from 'file-saver';
import { DrugRegisterService } from '@app/shared/_services/pharmacy/drug-register.service';
import { RevenueDepartmentPayload } from '@app/modules/settings';
import { UploadFileComponent } from '@app/modules/others/upload-file/upload-file.component';
import { DrugFormulationComponent } from '@app/modules/common/drug-formulation/drug-formulation.component';
import { DrugClassificationComponent } from '@app/modules/common/drug-classification/drug-classification.component';
import { RevenueDepartmentDropdownComponent } from '@app/modules/common/revenue-department-dropdown/revenue-department-dropdown.component';
import { DrugSearchComponent } from '@app/modules/common/drug-search/drug-search.component';

const searchC: SharedPayload[] = [
    { id: 1, name: 'BRAND NAME' },
    { id: 2, name: 'GENERIC NAME' },
    // { id: 3, name: 'GENERIC OR BRAND' },
];

@Component({
    selector: 'app-drug-register',
    templateUrl: './drug-register.component.html',
    styleUrls: ['./drug-register.component.css'],
})
export class DrugRegisterComponent implements OnInit, OnDestroy {
    @ViewChild('drugFormulationComponent') drugFormulationComponent: DrugFormulationComponent;
    @ViewChild('drugClassificationComponent')
    drugClassificationComponent: DrugClassificationComponent;
    @ViewChild('revenueDepartmentDropdownComponent')
    revenueDepartmentDropdownComponent: RevenueDepartmentDropdownComponent;
    @ViewChild('drugSearchComponent') drugSearchComponent: DrugSearchComponent;
    public p = 1;
    public collection: DrugRegisterPayload[] = [];
    public payload: DrugRegisterPayload = new DrugRegisterPayload();
    public searchByBrandName: DrugSearchTermEnum = DrugSearchTermEnum.BRAND_NAME;
    public searchByGenericName: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_NAME;
    public searchByGenericOrBrandName: DrugSearchTermEnum =
        DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;
    public filters = searchC;
    public selectedFilter: SharedPayload = searchC[0];

    public activeView: 'search' | 'create' = 'search';
    public editing: { id: number; column: DrugTableColumnEnum; showSpinner?: boolean } = {
        id: undefined,
        column: undefined,
    };

    public generic = DrugTableColumnEnum.generic;
    public brand = DrugTableColumnEnum.brand;
    public strength = DrugTableColumnEnum.strength;
    public costPrice = DrugTableColumnEnum.costPrice;
    public unitOfIssue = DrugTableColumnEnum.unitOfIssue;
    public packsPerPackingUnit = DrugTableColumnEnum.packsPerPackingUnit;
    public unitCostPrice = DrugTableColumnEnum.unitCostPrice;
    public generalMarkUp = DrugTableColumnEnum.generalMarkUp;
    public regularSellingPrice = DrugTableColumnEnum.regularSellingPrice;
    public nhisSellingPrice = DrugTableColumnEnum.nhisSellingPrice;
    public classification = DrugTableColumnEnum.classification;
    public formulation = DrugTableColumnEnum.formulation;

    public isSubmit = false;
    public isCreate = true;
    public drugColumnType: InputComponentUsageEnum = InputComponentUsageEnum.DRUG;
    public searchTerm: { searchValue: string; searchType: DrugSearchTermEnum } = {
        searchValue: undefined,
        searchType: DrugSearchTermEnum.BRAND_NAME,
    };

    private subscription: Subscription = new Subscription();

    constructor(
        private seedDataService: SeedDataService,
        private toast: ToastrService,
        private modalService: ModalPopupService,
        private spinnerService: NgxSpinnerService,
        private fileService: FileUploadService,
        private drugRegisterService: DrugRegisterService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onRegisterDrug(): void {
        this.isSubmit = true;

        const valid = this.onValidatePayload(this.payload);
        if (valid.status === false) {
            this.toast.error(valid.message, HmisConstants.VALIDATION_ERR, {
                enableHtml: true,
                disableTimeOut: true,
            });
            this.isSubmit = false;
            return;
        }
        this.spinnerService.show().then();
        this.subscription.add(
            this.drugRegisterService.onCreateDrugRegister(this.payload).subscribe(
                (res) => {
                    this.spinnerService.hide().then();
                    this.isSubmit = false;
                    if (res.body) {
                        this.toast.clear();
                        this.toast.success(
                            'Drug Registered Successfully',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onClearForm();
                    }
                },
                (error) => {
                    this.isSubmit = false;
                    this.spinnerService.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onUpdateDrug(): void {
        const valid = this.onValidatePayload(this.payload);
        if (this.payload.id) {
            if (valid.status === false) {
                this.toast.error(valid.message, HmisConstants.VALIDATION_ERR, {
                    enableHtml: true,
                    disableTimeOut: true,
                });
                return;
            }
        } else {
            this.toast.error('Select A Drug First', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinnerService.show().then();
        this.subscription.add(
            this.drugRegisterService.onUpdateDrug(this.payload).subscribe(
                (res) => {
                    this.spinnerService.hide().then();
                    this.isSubmit = false;
                    if (res.body) {
                        this.toast.clear();
                        this.toast.success(
                            'Drug Updated Successfully',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onClearForm(true);
                    }
                },
                (error) => {
                    this.isSubmit = false;
                    this.spinnerService.hide().then();
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onCreateViewType(value: 'create' | 'edit'): void {
        this.payload = new DrugRegisterPayload();
        this.isCreate = value && value === 'create';
    }

    public onBtnSearchClicked(view: 'search' | 'create'): void {
        this.resetCollection();
        this.activeView = view;
        this.payload = new DrugRegisterPayload();

        if (view === 'search') {
            if (this.searchTerm.searchType && this.searchTerm.searchValue) {
                this.spinnerService.show().then();
                this.subscription.add(
                    this.drugRegisterService.onSearchDrugs(this.searchTerm).subscribe(
                        (result) => {
                            this.spinnerService.hide().then();
                            if (result.body) {
                                this.collection = result.body;
                                return;
                            }
                        },
                        (error) => {
                            this.spinnerService.hide().then();
                            this.toast.error(HmisConstants.ERR_TITLE, 'Try Again');
                        }
                    )
                );
            }
        }
    }

    public onColumnClicked(id: any, column: DrugTableColumnEnum): void {
        this.editing = {
            column,
            id,
        };
    }

    public onColumnChanged(payload: InputComponentChanged): void {
        if (payload) {
            const { id, value, drugColumn } = payload;
            this.onUpdateColumnData(id, value, drugColumn);
            const toUpdate: { column: any; id: any; value: any } = {
                column: drugColumn,
                value,
                id,
            };

            this.subscription.add(
                this.drugRegisterService.onUpdateColumn(toUpdate).subscribe(
                    (result) => {},
                    (error) => {
                        console.log(error);
                    }
                )
            );
            this.editing = undefined;
            this.editing = undefined;
        }
    }

    public onUpdateColumnData(id: number, value: any, column: DrugTableColumnEnum): void {
        const drugPayload = this.collection.find((item) => item.id === id);
        switch (column) {
            case DrugTableColumnEnum.brand:
                drugPayload.brandName = value;
                break;
            case DrugTableColumnEnum.generic:
                drugPayload.genericName = value;
                break;
            case DrugTableColumnEnum.costPrice:
                drugPayload.costPrice = value;
                break;
            case DrugTableColumnEnum.generalMarkUp:
                drugPayload.generalMarkUp = value;
                break;
            case DrugTableColumnEnum.nhisSellingPrice:
                drugPayload.nhisSellingPrice = value;
                break;
            case DrugTableColumnEnum.packsPerPackingUnit:
                drugPayload.packsPerPackingUnit = value;
                break;
            case DrugTableColumnEnum.regularSellingPrice:
                drugPayload.regularSellingPrice = value;
                break;
            case DrugTableColumnEnum.strength:
                drugPayload.strength = value;
                break;
            case DrugTableColumnEnum.unitCostPrice:
                drugPayload.unitCostPrice = value;
                break;
            case DrugTableColumnEnum.unitOfIssue:
                drugPayload.unitOfIssue = value;
                break;
            case DrugTableColumnEnum.classification:
                drugPayload.classificationId = value;
                break;
            case DrugTableColumnEnum.formulation:
                drugPayload.formulationId = value;
                break;
            default:
                console.log('no column match to update');
        }
    }

    public onSearchByDropDownChange(value: SharedPayload): void {
        this.selectedFilter = value;
        this.resetCollection();
        if (value.name === searchC[0].name) {
            this.searchTerm.searchType = DrugSearchTermEnum.BRAND_NAME;
        } else if (value.name === searchC[1].name) {
            this.searchTerm.searchType = DrugSearchTermEnum.GENERIC_NAME;
        }
    }

    public onSearchDrugSelected(
        payload: DrugRegisterPayload,
        type?: 'search' | 'edit',
        searchBy?: DrugSearchTermEnum
    ): void {
        this.resetCollection();
        if (searchBy && payload) {
            if (searchBy === DrugSearchTermEnum.BRAND_NAME) {
                this.searchTerm = {
                    searchType: DrugSearchTermEnum.BRAND_NAME,
                    searchValue: payload?.brandName,
                };
            } else if (searchBy === DrugSearchTermEnum.GENERIC_NAME) {
                this.searchTerm = {
                    searchType: DrugSearchTermEnum.GENERIC_NAME,
                    searchValue: this.payload?.genericName,
                };
            }
        }

        if (type && type === 'edit') {
            // used in drug editing
            this.payload = payload;
        }
    }

    public onSelectDrugFormulation(formulation: SharedPayload): void {
        if (formulation) {
            this.payload.formulationId = formulation.id;
        }
    }

    public onSelectDrugClassification(classification: SharedPayload): void {
        if (classification) {
            this.payload.classificationId = classification.id;
        }
    }

    public openModal() {
        this.modalService.openModalWithComponent(
            UploadFileComponent,
            {
                data: { uploadTypeEnum: FileUploadTypeEnum.DRUG },
                title: 'Drug Batch Upload',
            },
            ModalSizeEnum.large
        );
    }

    public onDownloadTemplate(): void {
        this.subscription.add(
            this.fileService
                .onDownloadFile(FileTemplateEnum.DRUG_UPLOAD_TEMPLATE)
                .subscribe((blob) => {
                    saveAs(blob, FileTemplateEnum.DRUG_UPLOAD_TEMPLATE);
                })
        );
    }

    public resetCollection(): void {
        this.collection = [];
    }

    protected onValidatePayload(payload: DrugRegisterPayload): ValidationMessage {
        const res: ValidationMessage = { message: '', status: true };
        if (!payload?.genericName) {
            res.message += `Generic Name is Required<br>`;
            res.status = false;
        }
        if (!payload?.brandName) {
            res.message += `Brand Name is Required <br>`;
            res.status = false;
        }
        if (!payload?.strength) {
            res.message += `Strength is Required <br>`;
            res.status = false;
        }
        if (!payload?.formulationId) {
            res.message += `Drug Formulation is Required <br>`;
            res.status = false;
        }
        if (!payload?.classificationId) {
            res.message += `Drug Classification is Required <br>`;
            res.status = false;
        }
        if (!payload.nhisMarkUp) {
            res.message += `Nhis MarkUp is Required <br>`;
            res.status = false;
        }
        if (!payload.generalMarkUp) {
            res.message += `General MarkUp is Required <br>`;
            res.status = false;
        }

        return res;
    }

    public onRevenueDepartmentSelected(data: RevenueDepartmentPayload): void {
        if (data) {
            this.payload.revenueDepartmentId = data.id;
        }
    }

    private onClearForm(isEdit?: boolean): void {
        this.drugFormulationComponent.onClearField();
        this.drugClassificationComponent.onClearField();
        this.revenueDepartmentDropdownComponent.clearSelected();
        if (isEdit === true) {
            this.drugSearchComponent.onClearField();
        }
        this.payload = new DrugRegisterPayload();
    }
}

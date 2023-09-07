import {
    Component,
    ElementRef, OnChanges,
    OnDestroy,
    OnInit,
    QueryList, SimpleChanges,
    ViewChild,
    ViewChildren,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { ServiceRegisterService } from '@app/shared/_services/others/service-register.service';
import {
    FileTemplateEnum,
    FileUploadTypeEnum,
    GlobalSettingKeysEnum,
    GlobalSettingValueEnum,
    HmisSharedDropDown,
    InputComponentChanged,
    InputComponentUsageEnum,
    ModalSizeEnum,
    ProductServicePayload,
    ServicesTableColumnEnum,
} from '@app/shared/_payload';
import { NgxSpinnerService } from 'ngx-spinner';
import {
    fadeOutOnLeaveAnimation,
    slideInLeftOnEnterAnimation,
    slideOutRightOnLeaveAnimation,
} from 'angular-animations';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { GlobalSettingService, ModalPopupService } from '@app/shared/_services';
import { UploadFileComponent } from '../upload-file/upload-file.component';
import { saveAs } from 'file-saver';
import { FileUploadService } from '@app/shared/_services/others/file-upload.service';
import { RevenueDepartmentDropdownComponent } from '@app/modules/common/revenue-department-dropdown/revenue-department-dropdown.component';
import { ServiceDepartmentDropdownComponent } from '@app/modules/common/service-department-dropdown/service-department-dropdown.component';
import { ServiceUsageDropDownComponent } from '@app/modules/common/service-usage-dropdown/service-usage-drop-down.component';
import { SchemeData } from '@app/shared/_payload/erm/patient.payload';
import {SchemeSearchComponent} from '@app/modules/common/scheme-search/scheme-search.component';

const search = [
    { id: '1', value: 'SERVICE_NAME', name: 'SERVICE NAME' },
    { id: '2', value: 'SERVICE_DEPARTMENT', name: 'SERVICE DEPARTMENT' },
    { id: '3', value: 'REVENUE_DEPARTMENT', name: 'REVENUE DEPARTMENT' },
];

@Component({
    selector: 'app-service-register',
    templateUrl: './service-register.component.html',
    styleUrls: ['./service-register.component.css'],
    animations: [
        slideOutRightOnLeaveAnimation(),
        slideInLeftOnEnterAnimation(),
        fadeOutOnLeaveAnimation(),
    ],
})
export class ServiceRegisterComponent implements OnInit, OnDestroy, OnChanges {
    @ViewChild('revenueDepartmentDropdownComponent')
    revenueDepartmentDropdown: RevenueDepartmentDropdownComponent;
    @ViewChild('serviceDepartmentDropdownComponent')
    serviceDepartmentDropdown: ServiceDepartmentDropdownComponent;
    @ViewChild('serviceUsageDropDownComponent')
    serviceUsageDropDownComponent: ServiceUsageDropDownComponent;
    @ViewChild('schemeSearchComponent')
    schemeSearchComponent: SchemeSearchComponent;

    p = 1;
    collection: ProductServicePayload[] = [];
    @ViewChildren('generalPriceInputs') generalPriceInputs: QueryList<ElementRef>;
    public filters: HmisSharedDropDown[] = search;
    public filterTitle = '';
    public filterDefaultSelected: HmisSharedDropDown;
    public filterPayload: { searchBy: string; valueId: number } = {
        searchBy: undefined,
        valueId: undefined,
    };
    public resultList: ProductServicePayload[] = [];
    public editing: { id: number; column: ServicesTableColumnEnum; showSpinner?: boolean };
    public serviceName = ServicesTableColumnEnum.serviceName;
    public serviceDepartment = ServicesTableColumnEnum.serviceDepartment;
    public revenueDepartment = ServicesTableColumnEnum.revenueDepartment;
    public generalPrice = ServicesTableColumnEnum.generalPrice;
    public nhisPrice = ServicesTableColumnEnum.nhisPrice;
    public costPrice = ServicesTableColumnEnum.costPrice;
    public unitPrice = ServicesTableColumnEnum.unitPrice;
    public usage = ServicesTableColumnEnum.usage;
    public props: { activeView: string } = { activeView: 'search' };
    public payload: ProductServicePayload = new ProductServicePayload();
    public serviceType: InputComponentUsageEnum = InputComponentUsageEnum.SERVICE;
    // public isEnableNhisPrice = false;

    private subscription: Subscription = new Subscription();

    constructor(
        private service: ServiceRegisterService,
        private modalService: ModalPopupService,
        private fileService: FileUploadService,
        private spinnerService: NgxSpinnerService,
        private toastService: ToastrService,
        private globalSettingService: GlobalSettingService
    ) {}

    ngOnInit(): void {
        this.filterDefaultSelected = this.filters[0];
        // this.isLoadSetting();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
    }

    /*
    public isLoadSetting() {
        setTimeout( () => {
            this.spinnerService.show().then();
            this.subscription.add(
                this.globalSettingService
                    .getSettingValueByKey(GlobalSettingKeysEnum.ENABLE_NHIS_SERVICE_PRICE)
                    .subscribe(
                        (value) => {
                            this.isEnableNhisPrice =
                                value.body.data.value === GlobalSettingValueEnum.YES.toLowerCase();
                            this.spinnerService.hide().then();
                        },
                        (error) => {
                            this.spinnerService.hide().then();
                            this.isEnableNhisPrice = false;
                        }
                    )
            );
        }, 0 );
    }
     */

    public onFilterChange(selected: HmisSharedDropDown) {
        this.filterTitle = selected.name;
    }

    public onValueSelected(value: any) {
        if (value) {
            this.filterPayload.valueId = value.id;
            this.filterPayload.searchBy = this.filterDefaultSelected.value;
        }
    }

    public onSearchProductService() {
        this.props.activeView = 'search';

        if (!this.filterPayload.searchBy && !this.filterPayload.valueId) {
            return;
        }
        this.spinnerService.show().then();
        this.subscription.add(
            this.service.onSearch(this.filterPayload).subscribe(
                (value) => {
                    if (value.status === 200) {
                        this.spinnerService.hide().then();
                        this.collection = value.body;
                    }
                },
                (error) => {
                    this.spinnerService.hide().then();
                }
            )
        );
    }

    public onApplyDiscount(event) {
        if (!this.payload.discount) {
            event.preventDefault();
            this.payload.applyDiscount = false;
            this.toastService.error('Add Discount Before Applying', 'Validation Error');
            return;
        }
        this.payload.applyDiscount = event.target.checked;
    }

    public onColumnClicked(id: any, column: ServicesTableColumnEnum) {
        this.editing = {
            column,
            id,
        };
    }

    public onColumnChanged(payload: InputComponentChanged) {
        const { id, value, serviceColumn } = payload;
        this.updateColumnData(id, value, serviceColumn);

        const changedData: { column: any; id: any; value: any } = {
            column: serviceColumn,
            id,
            value,
        };
        this.subscription.add(this.service.onUpdateColumn(changedData).subscribe((result) => {}));
        this.editing = undefined;
    }

    public onSchemeSelected(schemeData: SchemeData) {
        if (schemeData && schemeData.id) {
            this.payload.schemeId = schemeData.id;
        }
    }

    protected updateColumnData(
        id: any,
        value: any,
        column: ServicesTableColumnEnum
    ): ProductServicePayload {
        const servicePayload = this.collection.find((item) => item.id === id);
        switch (column) {
            case ServicesTableColumnEnum.costPrice:
                servicePayload.costPrice = value;
                break;
            case ServicesTableColumnEnum.generalPrice:
                servicePayload.regularSellingPrice = value;
                break;
            case ServicesTableColumnEnum.nhisPrice:
                servicePayload.nhisSellingPrice = value;
                break;
            case ServicesTableColumnEnum.unitPrice:
                servicePayload.unitCost = value;
                break;
            case ServicesTableColumnEnum.serviceName:
                servicePayload.name = value;
                break;
            case ServicesTableColumnEnum.revenueDepartment:
                servicePayload.revenueDepartmentId = value;
                break;
            case ServicesTableColumnEnum.serviceDepartment:
                servicePayload.departmentId = value;
                break;
            case ServicesTableColumnEnum.usage:
                servicePayload.usedFor = value;
                break;
            default:
                console.log('no service column match to update');
        }
        return servicePayload;
    }

    public onChangeView() {
        this.props.activeView = 'create';
    }

    public onCreateService() {
        // validate payload
        const validation = this.isValid(this.payload);
        if (!validation.isValid) {
            this.toastService.error(validation.message, 'Some Fields Are Invalid', {
                enableHtml: true,
            });
            return;
        }
        this.spinnerService.show().then();
        this.subscription.add(
            this.service.onCreateService(this.payload).subscribe(
                (result) => {
                    if (result.status === 200) {
                        this.spinnerService.hide().then();
                        this.toastService.success(
                            'Added Successfully',
                            HmisConstants.SUCCESS_RESPONSE
                        );
                        this.onClearForm();
                    }
                },
                (error) => {
                    console.log(error);
                    this.spinnerService.hide().then();
                    this.toastService.error('Failed To Add Service', HmisConstants.ERR_TITLE);
                }
            )
        );
    }

    public onDropdownSelected(value: any, type: ServicesTableColumnEnum) {
        if (type === this.revenueDepartment) {
            this.payload.revenueDepartmentId = value;
        } else if (type === this.serviceDepartment) {
            this.payload.departmentId = value;
        } else if (type === this.usage) {
            this.payload.usedFor = value;
        }
    }

    protected isValid(payload: ProductServicePayload): { isValid: boolean; message: string } {
        const message: { isValid: boolean; message: string } = { isValid: true, message: '' };
        if (!payload.name) {
            message.isValid = false;
            message.message += 'Provide Service Name <br>';
        }
        if (!payload.departmentId) {
            message.isValid = false;
            message.message += 'Select Service Department <br>';
        }
        if (!payload.revenueDepartmentId) {
            message.isValid = false;
            message.message += 'Select Revenue Department <br>';
        }
        if (!payload.costPrice) {
            message.isValid = false;
            message.message += 'Provide Cost Price <br>';
        }
        if (!payload.unitCost) {
            message.isValid = false;
            message.message += 'Provide Unit Cost <br>';
        }
        if (!payload.regularSellingPrice) {
            message.isValid = false;
            message.message += 'Provide Regular Price <br>';
        }
        if (!payload.nhisSellingPrice) {
            message.isValid = false;
            message.message += 'Provide Nhis Price <br>';
        }

        if (!payload.usedFor) {
            message.isValid = false;
            message.message += 'Provide Usage <br>';
        }
        return message;
    }

    public openModal() {
        this.modalService.openModalWithComponent(
            UploadFileComponent,
            {
                data: { uploadTypeEnum: FileUploadTypeEnum.SERVICE },
                title: 'Service Batch Upload',
            },
            ModalSizeEnum.large
        );
    }

    public onDownloadTemplate(): void {
        this.subscription.add(
            this.fileService
                .onDownloadFile(FileTemplateEnum.SERVICE_UPLOAD_TEMPLATE)
                .subscribe((blob) => {
                    saveAs(blob, FileTemplateEnum.SERVICE_UPLOAD_TEMPLATE);
                })
        );
    }

    public onClearForm() {
        this.payload = new ProductServicePayload();
        this.serviceDepartmentDropdown.onClearSelected();
        this.revenueDepartmentDropdown.clearSelected();
        this.serviceUsageDropDownComponent.onClearSelected();
        this.schemeSearchComponent.onClearSelected();
    }
}

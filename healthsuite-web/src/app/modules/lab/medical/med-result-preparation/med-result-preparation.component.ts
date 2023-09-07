import {
    Component,
    ComponentFactoryResolver,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    BatchOrSingleEnum,
    LabResultPrepDto,
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestRequestItem,
    NewOrEditSampleEnum,
    SearchOrEnterResultViewEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';
import { Subscription } from 'rxjs';
import { LabResultService } from '@app/shared/_services/lab/lab-result.service';
import {
    LabDepartmentResultType,
    LabResultPrepOrVerifyEnum,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { ModalPopupService } from '@app/shared/_services';
import { LabResultDirective } from '@app/shared/_directives/lab/lab-result.directive';
import {ILabResultComponent, LabParasitologyTemplatePayload} from '@app/shared/_payload/lab/lab-result.payload';
import { LabResultPreparationSharedComponent } from '@app/modules/lab/components/lab-result-preparation-shared/lab-result-preparation-shared.component';
import { ParasitologyResultTemplateComponent } from '@app/modules/lab/components/parasitology-result-template/parasitology-result-template.component';

@Component({
    selector: 'app-med-result-preparation',
    templateUrl: './med-result-preparation.component.html',
    styleUrls: ['./med-result-preparation.component.css'],
})
export class MedResultPreparationComponent implements OnInit, OnDestroy {
    @Input('labResultType')
    public labResultType: LabDepartmentResultType;

    public invoiceNumber = LabSearchByEnum.INVOICE_NUMBER;
    public patient = LabSearchByEnum.PATIENT;
    public payload: LabSpecimenCollectionPayload;
    public specimenPayload: LabSpecimenCollectionPayload = new LabSpecimenCollectionPayload();
    public searchView = SearchOrEnterResultViewEnum.SEARCH;
    public enterView = SearchOrEnterResultViewEnum.ENTER;
    public selectedView = SearchOrEnterResultViewEnum.SEARCH;
    public labResultPrepDto: LabResultPrepDto = new LabResultPrepDto();

    @ViewChild(LabResultDirective, { static: true }) adHost!: LabResultDirective;

    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService,
        private labResultService: LabResultService,
        private modalService: ModalPopupService,
        private componentFactoryResolver: ComponentFactoryResolver
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
        this.labResultService.listen$.subscribe((res) => {
            this.onListen(res);
        });

        this.labResultService.labResultSubject$.subscribe((result) => {
            this.onlabResultChange(result);
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onViewChange(view: SearchOrEnterResultViewEnum) {
        this.onClearViewContainer();
        if (view) {
            this.selectedView = view;
        }
    }

    public onInitPayload() {
        this.payload = new LabSpecimenCollectionPayload();
        const currentUser = this.commonService.getCurrentUser();
        const currentLocation = this.commonService.getCurrentLocation();
        this.payload.capturedBy = currentUser;
        this.payload.capturedFrom = currentLocation;
        this.payload.newOrEditSampleEnum = NewOrEditSampleEnum.NEW;
        this.payload.searchByEnum = LabSearchByEnum.INVOICE_NUMBER;

        this.labResultPrepDto.preparedBy = currentUser;
        this.labResultPrepDto.preparedFrom = currentLocation;
    }

    public onDecideResultComponent() {
        if (
            this.labResultType &&
            this.labResultType === LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY
        ) {
            this.labResultPrepDto.verifyOrPrepEnum = LabResultPrepOrVerifyEnum.RESULT_PREPARATION;
            this.labResultPrepDto.parasitologyTemplate = new LabParasitologyTemplatePayload();
            this.onLoadResultComponent(ParasitologyResultTemplateComponent, this.labResultPrepDto);
            return;
        } else {
            this.onLoadResultComponent(LabResultPreparationSharedComponent, this.labResultPrepDto);
            return;
        }
    }

    public onAddResultInBatch() {
        this.onSearchResultBeforePreparation(
            this.payload.labBillTestRequest.testItems[0],
            BatchOrSingleEnum.BATCH
        );
    }

    public onSearchResultBeforePreparation(
        item: LabTestRequestItem,
        batchOrSingleEnum = BatchOrSingleEnum.SINGLE
    ) {
        // this.labResultPrepDto.singleTestRequestItemId = item.id;
        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onGetLabTestForResult(item.id, batchOrSingleEnum).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    this.labResultPrepDto = res;
                    this.labResultPrepDto.singleTestRequestItemId = item.id;
                    this.onDecideResultComponent();
                },
                (error) => {
                    this.spinner.hide().then();
                    const message = error.error.message;
                    const split = message.split('"');
                    this.toast.error(
                        split[1] ?? HmisConstants.ERROR,
                        HmisConstants.FAILED_RESPONSE
                    );
                    console.log(error);
                }
            )
        );
    }

    private onLoadResultComponent(component: any, data: LabResultPrepDto) {
        this.onViewChange(SearchOrEnterResultViewEnum.ENTER);
        const componentFactory =
            this.componentFactoryResolver.resolveComponentFactory<ILabResultComponent>(component);
        const viewContainerRef = this.onClearViewContainer();
        const componentRef =
            viewContainerRef.createComponent<ILabResultComponent>(componentFactory);
        componentRef.instance.payload = data;
    }

    private onClearViewContainer() {
        const viewContainerRef = this.adHost.viewContainerRef;
        viewContainerRef.clear();
        return viewContainerRef;
    }

    private onListen(res: boolean): void {
        if (res === true) {
            this.onInitPayload();
            this.onViewChange(SearchOrEnterResultViewEnum.SEARCH);
        }
    }

    private onlabResultChange(resultPrepDto: LabResultPrepDto): void {
        if (resultPrepDto) {
            this.labResultPrepDto = resultPrepDto;
        }
    }
}

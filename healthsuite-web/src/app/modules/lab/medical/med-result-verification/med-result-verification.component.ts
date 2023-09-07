import {
    Component,
    ComponentFactoryResolver,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    LabResultPrepDto,
    LabSearchByEnum,
    LabSpecimenCollectionPayload,
    LabTestApprovePayload,
    LabTestRequestItem,
    LabVerificationViewEnum,
    NewOrEditSampleEnum,
    SearchOrEnterResultViewEnum,
} from '@app/shared/_payload/lab/lab.payload';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonService } from '@app/shared/_services/common/common.service';
import { LabSpecimenService } from '@app/shared/_services/lab/lab-specimen.service';
import { LabResultService } from '@app/shared/_services/lab/lab-result.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { saveAs } from 'file-saver';
import { ReportFileNameEnum } from '@app/shared/_payload';
import { LabResultDirective } from '@app/shared/_directives/lab/lab-result.directive';
import { ILabResultComponent } from '@app/shared/_payload/lab/lab-result.payload';
import {
    LabDepartmentResultType,
    LabResultPrepOrVerifyEnum,
} from '@app/shared/_payload/lab/lab-setup.payload';
import { LabResultVerificationSharedComponent } from '@app/modules/lab/components/lab-result-verification-shared/lab-result-verification-shared.component';
import { ParasitologyResultTemplateComponent } from '@app/modules/lab/components/parasitology-result-template/parasitology-result-template.component';

@Component({
    selector: 'app-med-result-verification',
    templateUrl: './med-result-verification.component.html',
    styleUrls: ['./med-result-verification.component.css'],
})
export class MedResultVerificationComponent implements OnInit, OnDestroy {
    @Input('props')
    public props: { viewType: LabVerificationViewEnum };
    public invoiceNumber = LabSearchByEnum.INVOICE_NUMBER;
    public patient = LabSearchByEnum.PATIENT;
    public payload: LabSpecimenCollectionPayload;
    public specimenPayload: LabSpecimenCollectionPayload = new LabSpecimenCollectionPayload();
    public searchView = SearchOrEnterResultViewEnum.SEARCH;
    public enterView = SearchOrEnterResultViewEnum.ENTER;
    public selectedView = SearchOrEnterResultViewEnum.SEARCH;
    public labResultPrepDto: LabResultPrepDto = new LabResultPrepDto();

    @ViewChild(LabResultDirective, { static: true }) adHost!: LabResultDirective;
    @Input('departmentViewType') viewType: LabDepartmentResultType;

    private resultId = undefined;
    private subscription: Subscription = new Subscription();

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private commonService: CommonService,
        private labService: LabSpecimenService,
        private labResultService: LabResultService,
        private componentFactoryResolver: ComponentFactoryResolver
    ) {}

    ngOnInit(): void {
        this.onInitPayload();
        this.labResultService.listen$.subscribe((res) => {
            this.onListen(res);
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

        if (!this.props?.viewType) {
            this.props = { viewType: LabVerificationViewEnum.NORMAL };
        }

        this.labResultPrepDto.preparedBy = currentUser;
        this.labResultPrepDto.preparedFrom = currentLocation;
        this.labResultPrepDto.verificationTypeEnum = this.props.viewType;
    }

    public onViewResult(item: LabTestRequestItem) {
        // check if request is to download result or use specific lab result template
        if (this.props?.viewType === LabVerificationViewEnum.DOWNLOAD) {
            const payload = new LabTestApprovePayload();
            payload.testItemId = item.id;
            this.onDownloadLabResult(payload);
            return;
        }
        this.onDecideLabResultTemplate(item);
    }

    public onDownloadLabResult(payload: LabTestApprovePayload) {
        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onDownloadLabTestResult(payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    const file = new Blob([res.body], { type: 'application/pdf' });
                    saveAs(file, ReportFileNameEnum.LAB_TEST_RESULT);
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(HmisConstants.INTERNAL_SERVER_ERROR);
                    console.log(error);
                }
            )
        );
    }

    public onDecideLabResultTemplate(item: LabTestRequestItem): void {
        this.resultId = item.id;
        this.spinner.show().then();
        this.subscription.add(
            this.labResultService.onViewLabTestResult(item.id).subscribe(
                (res) => {
                    res.singleTestRequestItemId = item.id;
                    res.verificationTypeEnum = this.props.viewType;
                    if (this.viewType === LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY) {
                        res.verifyOrPrepEnum = LabResultPrepOrVerifyEnum.RESULT_VERIFICATION;
                        this.onLoadResultComponent(ParasitologyResultTemplateComponent, res);
                    } else {
                        this.onLoadResultComponent(LabResultVerificationSharedComponent, res);
                    }
                    this.spinner.hide().then();
                    this.selectedView = this.enterView;
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

    private onListen(res: boolean): void {
        if (res === true) {
            this.onInitPayload();
            this.onViewChange(SearchOrEnterResultViewEnum.SEARCH);
        }
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
}

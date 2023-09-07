import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {SchemeData} from '@app/shared/_payload/erm/patient.payload';
import {Subscription} from 'rxjs';
import {GlobalSettingKeysEnum, GlobalSettingValueEnum, ProductServicePayload} from '@app/shared/_payload';
import {NgxSpinnerService} from 'ngx-spinner';
import {ToastrService} from 'ngx-toastr';
import {GlobalSettingService} from '@app/shared/_services';
import {SchemeSearchComponent} from '@app/modules/common/scheme-search/scheme-search.component';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {ServiceRegisterService} from '@app/shared/_services/others/service-register.service';
import {ServiceNameSearchComponent} from '@app/modules/common/service-name-search/service-name-search.component';

@Component({
    selector: 'app-scheme-service-price',
    templateUrl: './scheme-service-price.component.html',
    styleUrls: ['./scheme-service-price.component.css'],
})
export class SchemeServicePriceComponent implements OnInit, OnDestroy {

    @ViewChild('schemeSearchComponent')
    public schemeSearchComponent: SchemeSearchComponent;

    @ViewChild('serviceNameSearchComponent')
    public serviceNameSearchComponent: ServiceNameSearchComponent;

    public isEnableNhisPrice = false;
    public payload: {schemeId: number, serviceId: number, price: number} =
        {schemeId: undefined, serviceId: undefined, price: undefined};

    private subscription: Subscription = new Subscription();
    constructor(
        private spinnerService: NgxSpinnerService,
        private toastService: ToastrService,
        private globalSettingService: GlobalSettingService,
        private service: ServiceRegisterService,

    ) {}

    ngOnInit(): void {
        this.isLoadSetting();
    }

    ngOnDestroy() {
      this.subscription.unsubscribe();
    }

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

    public onSchemeSelected(schemeData: SchemeData) {
        if (schemeData && schemeData.id) {
            this.payload.schemeId = schemeData.id;
        }
    }

    public onServiceSelected(service: ProductServicePayload) {
        if (service && service.id) {
            this.payload.serviceId = service.id;
        }
    }

    public onSavePrice() {
        if (!this.isEnableNhisPrice) {
            this.toastService.error(HmisConstants.FEATURE_NOT_ENABLED, HmisConstants.VALIDATION_ERR);
            return;
        }

        if (!this.payload.schemeId) {
            this.toastService.error('Select Scheme', HmisConstants.VALIDATION_ERR);
            return;
        }
        if (!this.payload.serviceId) {
            this.toastService.error('Select Service', HmisConstants.VALIDATION_ERR);
            return;
        }
        if (!this.payload.price) {
            this.toastService.show('Enter Price', HmisConstants.VALIDATION_ERR);
            return;
        }

        this.spinnerService.show().then();
        this.subscription.add(
            this.service.onSaveSchemeServicePrice(this.payload).subscribe(res => {
                if (res.message) {
                    this.spinnerService.hide().then();
                    this.toastService.success(res.message, HmisConstants.SUCCESS_RESPONSE);
                    this.onClearForm();
                }
            }, error => {
                this.spinnerService.hide().then();
                this.toastService.error(error.error.message, HmisConstants.FAILED_RESPONSE);
            })
        );

        // todo:: add feature for uploading scheme service price via excel

    }

    public onClearForm() {
        this.schemeSearchComponent.onClearSelected();
        this.serviceNameSearchComponent.clearSearchField();
        this.payload = {serviceId: undefined, price: undefined, schemeId: undefined};
    }

}

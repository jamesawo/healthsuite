import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import {
    DrugRegisterPayload,
    DrugSearchTermEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { CommonService } from '@app/shared/_services/common/common.service';
import { GlobalSettingKeysEnum, GlobalSettingValueEnum } from '@app/shared/_payload';
import { GlobalSettingService } from '@app/shared/_services';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-drug-search',
    templateUrl: './drug-search.component.html',
    styleUrls: ['./drug-search.component.css'],
})
export class DrugSearchComponent implements OnInit, OnDestroy, OnChanges {
    baseUrl = environment.apiEndPoint + '/drug-register';
    @Input() props: {
        searchTerm: DrugSearchTermEnum;
        showLabel?: boolean;
        loadIssOutletStockCount?: boolean;
        issuingOutletId?: number;
        showIssOutletStockCount?: boolean;
    }; // loadAvaStock is not in use (using global setting enforce stock & inventory
    @Output() selected: EventEmitter<DrugRegisterPayload> = new EventEmitter();
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;

    public genericName: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_NAME;
    public branName: DrugSearchTermEnum = DrugSearchTermEnum.BRAND_NAME;
    public genericOrBrandName: DrugSearchTermEnum = DrugSearchTermEnum.GENERIC_OR_BRAND_NAME;

    public url = ``;
    public responsePayload: Observable<DrugRegisterPayload[]>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: DrugRegisterPayload;
    public minLengthTerm = 1;
    public useStock: boolean;

    private urlConstruct = '';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    constructor(
        private sharedSearchService: SharedSearchService,
        private commonService: CommonService,
        private globalSettingService: GlobalSettingService
    ) {}

    ngOnInit(): void {
        this.subscription.add(
            this.globalSettingService
                .getSettingValueByKey(GlobalSettingKeysEnum.ACTIVATE_STOCK_INVENTORY)
                .subscribe(
                    (res) => {
                        const isActive: any = res.body.data.value === GlobalSettingValueEnum.YES;
                        this.useStock = isActive;
                        this.initializeObj(isActive);
                    },
                    (error) => {
                        this.initializeObj(false);
                    }
                )
        );
    }

    ngOnChanges(changes: SimpleChanges) {
        this.initializeObj(this.useStock);
    }

    public onClearField() {
        this.selectComponent.searchTerm = '';
        this.selectComponent.clearModel();
    }

    public handleClearClick() {
        this.selectComponent.searchTerm = '';
    }

    trackByFn(item: any) {
        return item.id;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onSelect(selected: DrugRegisterPayload) {
        this.selected.emit(selected);
    }

    public onGetLocationId(): number {
        const currentLocation = this.commonService.getCurrentLocation();
        return currentLocation?.id;
    }

    public initializeObj(activeStock: boolean) {
        this.urlConstruct = ``;
        this.url = ``;

        if (this.props && this.props.searchTerm) {
            const outlet = this.onGetLocationId();

            this.urlConstruct +=
                activeStock === true ? `&loadAvailableStock=true` : `&loadAvailableStock=false`;

            this.urlConstruct += `&outlet=${outlet}`;
            // load issuing outlet stock count
            this.urlConstruct +=
                this.props?.loadIssOutletStockCount === true
                    ? `&loadIssOutletStockCount=true`
                    : `&loadIssOutletStockCount=false`;
            // issuing outlet id
            this.urlConstruct += this.props?.issuingOutletId
                ? `&issuingOutletId=${this.props?.issuingOutletId}`
                : `&issuingOutletId=0`;

            if (this.props.searchTerm === DrugSearchTermEnum.BRAND_NAME) {
                this.url = `${this.baseUrl}/search-by-brand-name`;
            } else if (this.props.searchTerm === DrugSearchTermEnum.GENERIC_NAME) {
                this.url = `${this.baseUrl}/search-by-generic-name`;
            } else if (this.props.searchTerm === DrugSearchTermEnum.GENERIC_OR_BRAND_NAME) {
                this.url = `${this.baseUrl}/search-by-brand-generic-name`;
            }
        } else {
            this.url = `${this.baseUrl}/search-by-brand-generic-name`;
        }
        this.subscription.add(
            this.loading.asObservable().subscribe((value) => {
                this.isLoading = value;
            })
        );

        this.responsePayload = this.sharedSearchService.loadData(
            this.url,
            this.minLengthTerm,
            this.searchInput$,
            this.loading,
            this.urlConstruct
        );
    }
}

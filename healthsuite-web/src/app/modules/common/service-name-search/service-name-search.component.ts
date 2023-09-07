import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { ProductServicePayload } from '@app/shared/_payload';
import { NgSelectComponent } from '@ng-select/ng-select';
import { BillViewTypeEnum } from '@app/shared/_payload/bill-payment/bill.payload';

@Component({
    selector: 'app-service-name-search',
    templateUrl: './service-name-search.component.html',
    styleUrls: ['./service-name-search.component.css'],
})
export class ServiceNameSearchComponent implements OnInit, OnDestroy {
    @ViewChild('searchService') searchServiceNameComponent: NgSelectComponent;
    @Output() selected: EventEmitter<ProductServicePayload> =
        new EventEmitter<ProductServicePayload>();
    @Input() public props: { showLabel: boolean; viewType: BillViewTypeEnum };
    public url: string = environment.apiEndPoint + '/service-register/search-product-services';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;
    public urlConstruct = '';

    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();


    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        if (this.props?.viewType) {
            this.urlConstruct += `&viewType=${this.props.viewType}`;
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

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    trackByFn(item: any) {
        return item.id;
    }

    public onSelect(selected: ProductServicePayload): void {
        this.selected.emit(selected);
    }

    public clearSearchField(): void {
        this.searchServiceNameComponent.searchTerm = '';
        this.searchServiceNameComponent.clearModel();
    }
}

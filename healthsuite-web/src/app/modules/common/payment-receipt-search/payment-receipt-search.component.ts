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
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { environment } from '@environments/environment';
import { NgSelectComponent } from '@ng-select/ng-select';
import { HttpClient } from '@angular/common/http';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { PaymentTypeForEnum, ReceiptPayload } from '@app/shared/_payload/bill-payment/bill.payload';
import { DrugDispenseSearchByEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import {DatePayload} from '@app/shared/_payload';
import {CommonService} from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-payment-receipt-search',
    templateUrl: './payment-receipt-search.component.html',
    styleUrls: ['./payment-receipt-search.component.css'],
})
export class PaymentReceiptSearchComponent implements OnInit, OnDestroy, OnChanges {
    @Output() selected: EventEmitter<ReceiptPayload> = new EventEmitter();
    @Input() props: {
        showLabel: boolean;
        searchBy: DrugDispenseSearchByEnum;
        loadPatientDetail: boolean;
        loadPatientBill: boolean;
        filterFor: PaymentTypeForEnum;
    };
    public responsePayload: Observable<ReceiptPayload[]>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;
    private urlConstruct = '';

    private url: string = environment.apiEndPoint + '/receipt/search-patient-receipt';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    @ViewChild(NgSelectComponent) ngSelectComponent: NgSelectComponent;

    constructor(private http: HttpClient, private sharedSearchService: SharedSearchService,
                private commonService: CommonService
    ) {}

    ngOnInit() {
        const searchBy = this.props?.searchBy;
        const filterFor = this.props?.filterFor;

        this.urlConstruct += searchBy ? `&searchBy=${searchBy}` : `&searchBy=RECEIPT_NUMBER`;
        this.urlConstruct +=
            this.props?.loadPatientDetail === true
                ? `&loadPatientDetail=true`
                : `&loadPatientDetail=false`;
        this.urlConstruct +=
            this.props?.loadPatientBill === true
                ? `&loadPatientBill=true`
                : `&loadPatientBill=false`;

        this.urlConstruct += filterFor ? `&filterFor=${filterFor}` : `&filterFor=`;

        this.responsePayload = this.sharedSearchService.loadData(
            this.url,
            this.minLengthTerm,
            this.searchInput$,
            this.loading,
            this.urlConstruct
        );

        this.subscription.add(
            this.loading.asObservable().subscribe((value) => {
                this.isLoading = value;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {}

    trackByFn(item: any) {
        return item.id;
    }

    public clearSearchField() {
        this.ngSelectComponent.clearModel();
    }

    public onSelectReceipt(value: ReceiptPayload) {
        if (value) {
            this.selected.emit(value);
        }
    }

    public transformDate(transactionDate: DatePayload) {
        return this.commonService.transformToDate(transactionDate);
    }
}

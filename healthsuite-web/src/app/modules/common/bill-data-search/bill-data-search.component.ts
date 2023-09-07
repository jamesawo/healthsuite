import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { environment } from '@environments/environment';
import { NgSelectComponent } from '@ng-select/ng-select';
import { EmrService } from '@app/shared/_services';
import { HttpClient } from '@angular/common/http';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import {
    BillPaymentSearchByEnum,
    PatientBill,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-bill-data-search',
    templateUrl: './bill-data-search.component.html',
    styleUrls: ['./bill-data-search.component.css'],
})
export class BillDataSearchComponent implements OnInit, OnDestroy {
    @Output() selected: EventEmitter<PatientBill> = new EventEmitter();
    public responsePayload: Observable<PatientBill[]>; // response observable based on users search term

    @Input() props: { loadDeposit: boolean };
    public isLoading = false;
    public searchInput$ = new Subject<string>(); //users search input value
    public selectedPayload: any; //selected option
    public minLengthTerm = 1;

    private urlConstruct: string = '';
    private url: string = environment.apiEndPoint + '/billing/search-patient-bill';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    @ViewChild(NgSelectComponent) ngSelectComponent: NgSelectComponent;

    constructor(
        private emrService: EmrService,
        private http: HttpClient,
        private sharedSearchService: SharedSearchService,
        private commonService: CommonService
    ) {}

    ngOnInit() {
        const searchEnum = this.commonService.stripAndJoin(BillPaymentSearchByEnum.BILL_NUMBER);
        this.urlConstruct += `&searchBy=${searchEnum}`;
        this.urlConstruct +=
            this.props.loadDeposit == true ? `&loadDeposit=true` : `&loadDeposit=false`;

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

    trackByFn(item: any) {
        return item.id;
    }

    public clearSearchField() {
        this.ngSelectComponent.clearModel();
    }

    public onSelectServiceBill(value: PatientBill) {
        if (value) {
            this.selected.emit(value);
        }
    }
}

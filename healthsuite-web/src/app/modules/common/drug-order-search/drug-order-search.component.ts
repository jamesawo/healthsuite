import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    BillPaymentSearchByEnum,
    PatientBill,
} from '@app/shared/_payload/bill-payment/bill.payload';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { environment } from '@environments/environment';
import { NgSelectComponent } from '@ng-select/ng-select';
import { EmrService } from '@app/shared/_services';
import { HttpClient } from '@angular/common/http';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { CommonService } from '@app/shared/_services/common/common.service';
import { DrugOrderPayload } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-drug-order-search',
    templateUrl: './drug-order-search.component.html',
    styleUrls: ['./drug-order-search.component.css'],
})
export class DrugOrderSearchComponent implements OnInit, OnDestroy {
    @Output() selected: EventEmitter<DrugOrderPayload> = new EventEmitter();
    public responsePayload: Observable<DrugOrderPayload[]>; // response observable based on users search term
    public isLoading = false;
    public searchInput$ = new Subject<string>(); //users search input value
    public selectedPayload: any; //selected option
    public minLengthTerm = 1;

    private url: string = environment.apiEndPoint + '/drug-order/search';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    @ViewChild(NgSelectComponent) ngSelectComponent: NgSelectComponent;

    constructor(
        private emrService: EmrService,
        private http: HttpClient,
        private sharedSearchService: SharedSearchService
    ) {}

    ngOnInit() {
        this.responsePayload = this.sharedSearchService.loadData(
            this.url,
            this.minLengthTerm,
            this.searchInput$,
            this.loading
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

    public onSelectDrugOrder(value: DrugOrderPayload) {
        if (value) {
            this.selected.emit(value);
        }
    }
}

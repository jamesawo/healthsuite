import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { VendorPayload } from '@app/shared/_payload';
import { environment } from '@environments/environment';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';

@Component({
    selector: 'app-supplier-search',
    templateUrl: './supplier-search.component.html',
    styleUrls: ['./supplier-search.component.css'],
})
export class SupplierSearchComponent implements OnInit, OnChanges {
    @Input('props')
    public props?: { showLabel?: boolean; defaultSelected?: VendorPayload }; //todo:: add props to filter only pharmacy supplier

    @Output() selected: EventEmitter<VendorPayload> = new EventEmitter();
    public url: string = environment.apiEndPoint + '/vendor-management/search-vendor';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;
    private urlConstruct: string = '';

    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        this.onSetDefault();

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
    ngOnChanges(changes: SimpleChanges) {
        this.onSetDefault();
    }

    public onSetDefault() {
        if (this.props?.defaultSelected) {
            this.selectedPayload = this.props.defaultSelected;
        }
    }

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: VendorPayload) {
        this.selected.emit(selected);
    }
}

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
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { DrugRegisterPayload } from '@app/shared/_payload/pharmacy/pharmacy.payload';

@Component({
    selector: 'app-drug-search-by-department',
    templateUrl: './drug-search-by-department.component.html',
    styleUrls: ['./drug-search-by-department.component.css'],
})
export class DrugSearchByDepartmentComponent implements OnInit, OnDestroy, OnChanges {
    @Input() public props: { outletId: number; excludeZeroQuantity: boolean };
    @Output() selected: EventEmitter<DrugRegisterPayload> = new EventEmitter<DrugRegisterPayload>();
    public url: string = environment.apiEndPoint + '/drug-register/search-by-department-filter';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: DrugRegisterPayload;
    public minLengthTerm = 1;

    private urlConstruct = '';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        this.onInitProps();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.onInitProps();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onInitProps() {
        this.onClearProps();
        this.urlConstruct += `&outlet=${this.props.outletId}`;
        this.urlConstruct +=
            this.props.excludeZeroQuantity === true ? `&excludeZero=true` : `&excludeZero=false`;

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

    trackByFn(item: any) {
        return item.id;
    }

    onClearProps() {
        this.urlConstruct = '';
    }

    onSelect(selected: DrugRegisterPayload) {
        this.selected.emit(selected);
    }

    onSetProps(payload: { depId?: number; excludeZero?: boolean }) {
        const { depId, excludeZero } = payload;
        if (depId) {
            this.props.outletId = depId;
        }

        if (excludeZero) {
            this.props.excludeZeroQuantity = excludeZero;
        }
    }
}

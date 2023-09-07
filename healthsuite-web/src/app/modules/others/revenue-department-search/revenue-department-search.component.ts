import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import { BehaviorSubject, concat, Observable, of, Subject, Subscription } from 'rxjs';
import { RevenueDepartmentPayload } from '@app/modules/settings';
import { environment } from '@environments/environment';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';

@Component({
    selector: 'app-revenue-department-search',
    templateUrl: './revenue-department-search.component.html',
    styleUrls: ['./revenue-department-search.component.css'],
})
export class RevenueDepartmentSearchComponent implements OnInit, OnDestroy {
    @Input('props')
    public props: {showLabel: boolean};
    @Output() selected: EventEmitter<RevenueDepartmentPayload> = new EventEmitter<
        RevenueDepartmentPayload
    >();
    public url: string = environment.apiEndPoint + '/seed-data-search/search-revenue-department';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;

    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.loading.asObservable().subscribe((value) => {
                this.isLoading = value;
            })
        );

        this.responsePayload = this.sharedSearchService.loadData(
            this.url,
            this.minLengthTerm,
            this.searchInput$,
            this.loading
        );
    }

    trackByFn(item: any) {
        return item.id;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSelect(selected: RevenueDepartmentPayload) {
        this.selected.emit(selected);
    }
}

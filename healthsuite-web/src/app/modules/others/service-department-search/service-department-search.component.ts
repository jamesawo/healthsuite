import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { DepartmentPayload, RevenueDepartmentPayload } from '@app/modules/settings';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { DepartmentCategoryEnum } from '@app/shared/_payload';

@Component({
    selector: 'app-service-department-search',
    templateUrl: './service-department-search.component.html',
    styleUrls: ['./service-department-search.component.css'],
})
export class ServiceDepartmentSearchComponent implements OnInit {
    @Input('props') props?: {
        showLabel?: boolean;
        loadByCategory?: boolean;
        category?: DepartmentCategoryEnum;
    };
    @Output() selected: EventEmitter<DepartmentPayload> = new EventEmitter<DepartmentPayload>();
    public url: string = environment.apiEndPoint + '/seed-data-search/search-service-department';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;
    public urlConstruct = '';

    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    public departmentPayload: DepartmentPayload = new DepartmentPayload();
    private subscription: Subscription = new Subscription();

    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        if (this.props?.loadByCategory === true) {
            this.urlConstruct += `&searchByCategory=true`;
        } else {
            this.urlConstruct += `&searchByCategory=false`;
        }

        if (this.props?.category) {
            this.urlConstruct += `&categoryEnum=${this.props.category}`;
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

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: DepartmentPayload) {
        this.selected.emit(selected);
    }
}

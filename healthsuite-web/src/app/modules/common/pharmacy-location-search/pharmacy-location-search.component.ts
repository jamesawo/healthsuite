import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { PharmacyLocationTypeEnum } from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { DepartmentPayload } from '@app/modules/settings';
import { environment } from '@environments/environment';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-pharmacy-location-search',
    templateUrl: './pharmacy-location-search.component.html',
    styleUrls: ['./pharmacy-location-search.component.css'],
})
export class PharmacyLocationSearchComponent implements OnInit, OnDestroy {
    @Input() public props: { showLabel?: boolean; loadType?: PharmacyLocationTypeEnum };
    @Output() selected: EventEmitter<DepartmentPayload> = new EventEmitter<DepartmentPayload>();
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;

    public url: string = environment.apiEndPoint + '/seed-data-search/search-service-department';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: DepartmentPayload;
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

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    trackByFn(item: any) {
        return item.id;
    }

    public onSelect(selected: DepartmentPayload) {
        this.selected.emit(selected);
    }

    public onClear() {
        this.selectComponent.clearModel();
    }
}

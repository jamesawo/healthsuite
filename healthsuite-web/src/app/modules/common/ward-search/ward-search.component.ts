import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { WardPayload } from '@app/modules/settings';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-ward-search',
    templateUrl: './ward-search.component.html',
    styleUrls: ['./ward-search.component.css'],
})
export class WardSearchComponent implements OnInit {
    @ViewChild('ngSelectComponent') ngComponent: NgSelectComponent;
    @Output() selected: EventEmitter<WardPayload> = new EventEmitter<WardPayload>();
    @Input() public props: { showLabel: boolean };
    public url: string = environment.apiEndPoint + '/seed/search-ward';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: WardPayload;
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

    onSelect(selected: WardPayload) {
        this.selected.emit(selected);
    }

    onClearPayload() {
        this.ngComponent.clearModel();
    }
}

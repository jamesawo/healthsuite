import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { SharedPayload } from '@app/shared/_payload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-speciality-search',
    templateUrl: './speciality-search.component.html',
    styleUrls: ['./speciality-search.component.css'],
})
export class SpecialitySearchComponent implements OnInit {
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;
    @Input() public props: {
        showLabel?: boolean;
    };
    @Output() selected: EventEmitter<SharedPayload> = new EventEmitter<SharedPayload>();
    public url: string = environment.apiEndPoint + '/seed/search-speciality';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any = {};
    public minLengthTerm = 1;
    private urlConstruct: string = '';

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
            this.loading,
            this.urlConstruct
        );
    }

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: SharedPayload) {
        this.selected.emit(selected);
    }

    onClearField() {
        this.selectComponent.clearModel();
    }

    onSetDefault(payload: any) {
        if (payload) {
            this.selectedPayload = payload;
        }
    }
}

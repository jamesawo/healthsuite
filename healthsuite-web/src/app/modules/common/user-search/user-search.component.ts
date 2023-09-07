import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-user-search',
    templateUrl: './user-search.component.html',
    styleUrls: ['./user-search.component.css'],
})
export class UserSearchComponent implements OnInit {
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;
    @Output() selected: EventEmitter<UserPayload> = new EventEmitter<UserPayload>();
    @Input() public props: {
        showLabel?: boolean;
        isConsultant?: boolean;
        showDisabledUser?: boolean;
    };
    public url: string = environment.apiEndPoint + '/user-manager/search-user';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    public selectedPayload: any;
    public minLengthTerm = 1;
    private urlConstruct = '';

    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    constructor(private sharedSearchService: SharedSearchService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.loading.asObservable().subscribe((value) => {
                this.isLoading = value;
            })
        );
        this.urlConstruct +=
            this.props?.isConsultant === true ? `&isConsultant=true` : `&isConsultant=false`;

        this.urlConstruct +=
            this.props?.showDisabledUser === true
                ? `&showDisabledUser=true`
                : `&showDisabledUser=false`;

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

    onSelect(selected: UserPayload) {
        this.selected.emit(selected);
    }

    onClearField() {
        this.selectComponent.clearModel();
    }
}

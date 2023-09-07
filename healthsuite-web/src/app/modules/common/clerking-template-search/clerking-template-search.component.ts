import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import {
    ClerkingTemplateSearchPayload,
    ClinicDeskEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-clerking-template-search',
    templateUrl: './clerking-template-search.component.html',
    styleUrls: ['./clerking-template-search.component.css'],
})
export class ClerkingTemplateSearchComponent implements OnInit, OnDestroy {
    @ViewChild('ngSelectComponent')
    public ngSelectComponent: NgSelectComponent;
    @Input() public props: { desk: ClinicDeskEnum; doctor: number };
    @Output() selected: EventEmitter<ClerkingTemplateSearchPayload> =
        new EventEmitter<ClerkingTemplateSearchPayload>();
    public url: string = environment.apiEndPoint + '/doctor-template/search';
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
        this.urlConstruct += `&doctor=${this.props.doctor}`;
        this.urlConstruct += `&desk=${this.props.desk}`;

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

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: ClerkingTemplateSearchPayload) {
        this.selected.emit(selected);
    }

    onClear() {
        this.ngSelectComponent.clearModel();
    }
}

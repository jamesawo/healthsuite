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
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { SchemeData } from '@app/shared/_payload/erm/patient.payload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-scheme-search',
    templateUrl: './scheme-search.component.html',
    styleUrls: ['./scheme-search.component.css'],
})
export class SchemeSearchComponent implements OnInit, OnDestroy {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;
    @Input() public props: { showLabel: boolean };
    @Output() selected: EventEmitter<SchemeData> = new EventEmitter<SchemeData>();
    public url: string = environment.apiEndPoint + '/service-register/search-scheme-data';
    public responsePayload: Observable<any>;
    public isLoading = false;
    public searchInput$ = new Subject<string>();
    @Input('default')
    public selectedPayload: SchemeData;
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

        if (this.selectedPayload && this.selectedPayload.id) {
            this.onSelect(this.selectedPayload);
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: SchemeData) {
        this.selected.emit(selected);
    }

    onClearSelected() {
        this.ngSelectComponent.searchTerm = '';
        this.ngSelectComponent.clearModel();
        this.selectedPayload = new SchemeData();
    }
}

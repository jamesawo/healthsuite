import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { NgSelectComponent } from '@ng-select/ng-select';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import {CashierShiftSearchPayload} from '@app/shared/_payload/shift/shit.payload';

@Component({
    selector: 'app-cashier-shift-number-search',
    templateUrl: './cashier-shift-number-search.component.html',
    styleUrls: ['./cashier-shift-number-search.component.css'],
})
export class CashierShiftNumberSearchComponent implements OnInit {
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;
    @Output() selected: EventEmitter<CashierShiftSearchPayload> = new EventEmitter<CashierShiftSearchPayload>();
    @Input() public props: {
        showLabel?: boolean;
        hideClosedShift?: boolean;
    };
    public url: string = environment.apiEndPoint + '/manage-shift/search-shift-by-code';
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
        this.urlConstruct += this.props?.hideClosedShift === true ? `&showClosedShift=true` : `&showClosedShift=false`;
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

    onSelect(selected: CashierShiftSearchPayload) {
        this.selected.emit(selected);
    }

    onClearField() {
        this.selectComponent.clearModel();
    }
}

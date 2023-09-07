import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {NgSelectComponent} from '@ng-select/ng-select';
import {SharedPayload} from '@app/shared/_payload';
import {environment} from '@environments/environment';
import {BehaviorSubject, Observable, Subject, Subscription} from 'rxjs';
import {SharedSearchService} from '@app/shared/_services/others/shared-search.service';

@Component({
  selector: 'app-search-lab-param',
  templateUrl: './search-lab-param.component.html',
  styleUrls: ['./search-lab-param.component.css']
})
export class SearchLabParamComponent implements OnInit, OnDestroy {
  @ViewChild('searchService') searchServiceNameComponent: NgSelectComponent;
  @Output() selected: EventEmitter<SharedPayload> = new EventEmitter<SharedPayload>();
  public url: string = environment.apiEndPoint + '/lab-parameter/search-param-name';
  public responsePayload: Observable<any>;
  public isLoading = false;
  public searchInput$ = new Subject<string>();
  public selectedPayload: any;
  public minLengthTerm = 1;
  public urlConstruct = '';

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

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  trackByFn(item: any) {
    return item.id;
  }

  public onSelect(selected: SharedPayload): void {
    this.selected.emit(selected);
  }

  public clearSearchField(): void {
    this.searchServiceNameComponent.searchTerm = '';
    this.searchServiceNameComponent.clearModel();
  }
}

import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { BehaviorSubject, concat, Observable, of, Subject, Subscription, throwError } from 'rxjs';
import { EmrService } from '@app/shared/_services';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-patient-search',
    templateUrl: './patient-search.component.html',
    styleUrls: ['./patient-search.component.css'],
})
export class PatientSearchComponent implements OnInit, OnDestroy, OnChanges {
    @Output() selected: EventEmitter<PatientPayload> = new EventEmitter();
    @Input() props: {
        showLabel: boolean;
        checkAdmission: boolean;
        loadAdmission: boolean;
        loadRevisit?: boolean;
        loadSchemeDiscount?: boolean;
        loadDeposit?: boolean;
        loadDrugRequest?: boolean;
        loadLabRequest?: boolean;
        loadRadiologyRequest?: boolean;
    }; // bool string
    public responsePayload: Observable<PatientPayload[]>; // response observable based on users search term
    public isLoading = false;
    public searchInput$ = new Subject<string>(); // users search input value
    public selectedPayload: any; // selected option
    public minLengthTerm = 1;
    private urlConstruct = '';
    public patientPayload: PatientPayload = new PatientPayload();

    private url: string = environment.apiEndPoint + '/emr/search-patient-details';
    private loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private subscription: Subscription = new Subscription();

    @ViewChild('searchComponent') ngSelectComponent: NgSelectComponent;

    constructor(
        private emrService: EmrService,
        private http: HttpClient,
        private sharedSearchService: SharedSearchService
    ) {}

    ngOnInit() {
        if (!this.props) {
            this.props = {
                showLabel: false,
                checkAdmission: false,
                loadAdmission: false,
                loadRevisit: false,
                loadSchemeDiscount: false,
                loadDeposit: false,
                loadDrugRequest: false,
                loadLabRequest: false,
                loadRadiologyRequest: false,
            };
        }
        this.onInitialize();
    }

    ngOnChanges(changes: SimpleChanges) {
        this.onInitialize();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onInitialize() {
        this.urlConstruct = '';

        this.urlConstruct +=
            this.props?.loadDrugRequest === true
                ? `&loadDrugRequest=true`
                : `&loadDrugRequest=false`;

        this.urlConstruct +=
            this.props?.loadLabRequest === true ? `&loadLabRequest=true` : `&loadLabRequest=false`;

        this.urlConstruct +=
            this.props?.loadRadiologyRequest === true
                ? `&loadRadiologyRequest=true`
                : `&loadRadiologyRequest=false`;

        this.urlConstruct +=
            this.props?.loadDeposit === true ? `&loadDeposit=true` : `&loadDeposit=false`;

        this.urlConstruct +=
            this.props?.checkAdmission === true ? `&checkAdmission=true` : `&checkAdmission=false`;

        this.urlConstruct +=
            this.props.loadAdmission === true ? `&loadAdmission=true` : `&loadAdmission=false`;

        this.urlConstruct +=
            this.props.loadRevisit === true ? `&loadRevisit=true` : `&loadRevisit=false`;

        this.urlConstruct +=
            this.props.loadSchemeDiscount === true
                ? `&loadSchemeDiscount=true`
                : `&loadSchemeDiscount=false`;

        this.responsePayload = this.sharedSearchService.loadData(
            this.url,
            this.minLengthTerm,
            this.searchInput$,
            this.loading,
            this.urlConstruct
        );
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patientPayload = payload;
            })
        );

        this.subscription.add(
            this.loading.asObservable().subscribe((value) => {
                this.isLoading = value;
            })
        );
    }

    trackByFn(item: any) {
        return item.id;
    }

    public clearSearchField() {
        this.ngSelectComponent.searchTerm = '';
        this.ngSelectComponent.clearModel();
    }

    public onSelectPatient(value: PatientPayload) {
        if (value) {
            this.selected.emit(value);
        }
    }
}

import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import { ClerkDrugRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';
import { NgxSpinnerService } from 'ngx-spinner';
import { DoctorRequestService } from '@app/shared/_services/clerking/doctor-request.service';
import { DatePayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { ToastrService } from 'ngx-toastr';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-drug-prescription-handler',
    templateUrl: './drug-prescription-handler.component.html',
    styleUrls: ['./drug-prescription-handler.component.css'],
})
export class DrugPrescriptionHandlerComponent implements OnInit, OnDestroy, OnChanges {
    @Input('props')
    public props: {
        patient: PatientPayload;
        drugRequestList: ClerkDrugRequestPayload[];
    };

    @Output('selected')
    public selected: EventEmitter<ClerkDrugRequestPayload> = new EventEmitter<ClerkDrugRequestPayload>(
        null
    );
    @Output('isSearching')
    public isSearching: EventEmitter<boolean> = new EventEmitter<boolean>(false);

    public startDate: DatePayload = { day: undefined, month: undefined, year: undefined };
    public endDate: DatePayload = { day: undefined, month: undefined, year: undefined };

    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private service: DoctorRequestService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {}

    onViewSelectedItem(item: ClerkDrugRequestPayload, i: number) {
        if (item) {
            this.selected.emit(item);
        }
    }

    onSearchByDateRange() {
        if (!this.props?.patient.patientId) {
            this.toast.error('Select Patient First.', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.onUpdateIsSearching(true);
        this.spinner.show().then();
        this.subscription.add(
            this.service
                .onSearchDrugRequest({
                    patientId: this.props.patient.patientId,
                    startDate: this.startDate,
                    endDate: this.endDate,
                })
                .subscribe(
                    (res) => {
                        this.spinner.hide().then();
                        this.onUpdateIsSearching(true);
                        this.props.drugRequestList = res;

                    },
                    (error) => {
                        this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                        this.onUpdateIsSearching(false);
                        this.spinner.hide().then();
                    }
                )
        );
    }

    onUpdateIsSearching(value: boolean) {
        this.isSearching.emit(value);
    }

    onDateSelected(payload: DatePayload, type: 'start' | 'end') {
        if (payload.day && payload.month && payload.year) {
            if (type === 'start') {
                this.startDate = payload;
            } else {
                this.endDate = payload;
            }
        }
    }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {ObPrevPregnancy, PregnancyOutcomeEnum,} from '@app/shared/_payload/nurse/nurse-anc.payload';
import {YesNoEnum} from '@app/shared/_payload/clerking/outpatient-desk.payload';
import {DatePayload, GenderPayload} from '@app/shared/_payload';
import {SeedDataService} from '@app/shared/_services';
import {Subscription} from 'rxjs';
import {CommonService} from '@app/shared/_services/common/common.service';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {ToastrService} from 'ngx-toastr';

@Component({
    selector: 'app-pregnancy-history-modal',
    templateUrl: './pregnancy-history-modal.component.html',
    styleUrls: ['./pregnancy-history-modal.component.css'],
})
export class PregnancyHistoryModalComponent implements OnInit, OnDestroy {
    public data: { pregnancyHistories: ObPrevPregnancy[] };
    public outcomeList: any[] = [];
    public aliveList: any[] = [];
    public payload: ObPrevPregnancy = new ObPrevPregnancy();
    public delivered = PregnancyOutcomeEnum.DELIVERED;

    private subscription: Subscription = new Subscription();

    constructor(
        private seedService: SeedDataService,
        private commonService: CommonService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {
        this.outcomeList = Object.keys(PregnancyOutcomeEnum).map((value) =>
            value.replace(/_/g, ' ')
        );
        this.aliveList = Object.keys(YesNoEnum);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onAddData() {
        if (!this.payload || !this.payload.outcome) {
            this.toast.error('Select Outcome First', HmisConstants.VALIDATION_ERR);
            return;
        }
        this.data.pregnancyHistories.push(this.payload);
        this.onCloseModal();
    }

    onOutComeChanged(outcome: PregnancyOutcomeEnum) {
        if (outcome) {
            const re: any = outcome.replace(/ /g, '_');
            this.payload.outcome = re;
            // this.payload.outcome = this.commonService.removeUnderscore(outcome);
        }
    }

    onGenderChanged(gender: GenderPayload) {
        if (gender) {
            this.payload.sex = gender;
        }
    }

    onDateSelected(datePayload: DatePayload) {
        if (datePayload) {
            this.payload.dateOfBirth = this.commonService.transformToDate(datePayload);
        }
    }

    onAliveChanged(value: YesNoEnum) {
        if (value) {
            this.payload.alive = value;
        }
    }

    onCloseModal() {
        this.commonService.onCloseModal();
    }
}

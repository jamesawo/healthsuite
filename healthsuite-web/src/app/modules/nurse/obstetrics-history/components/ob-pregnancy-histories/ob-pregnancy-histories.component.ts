import { Component, Input, OnInit } from '@angular/core';
import { ModalPopupService } from '@app/shared/_services';
import {
    PregnancyHistoryModalComponent
} from '@app/modules/nurse/obstetrics-history/components/pregnancy-history-modal/pregnancy-history-modal.component';
import { DatePayload, ModalSizeEnum } from '@app/shared/_payload';
import {ObPrevPregnancy, PregnancyOutcomeEnum} from '@app/shared/_payload/nurse/nurse-anc.payload';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-ob-pregnancy-histories',
    templateUrl: './ob-pregnancy-histories.component.html',
    styleUrls: ['./ob-pregnancy-histories.component.css'],
})
export class ObPregnancyHistoriesComponent implements OnInit {
    @Input('props') props: { payload: ObPrevPregnancy[] };

    constructor(private modalService: ModalPopupService, private commonService: CommonService) {}

    ngOnInit(): void {
        if (!this.props.payload) {
            this.props.payload = [];
        }
    }

    public onAddPregHistory() {
        this.modalService.openModalWithComponent(
            PregnancyHistoryModalComponent,
            {
                data: { pregnancyHistories: this.props.payload },
                title: 'Add Pregnancy History',
            },
            ModalSizeEnum.large
        );
    }

    getDateOfBirth(dateOfBirth: DatePayload ) {
        if (this.commonService.isValidDatePayload(dateOfBirth)) {
            return this.commonService.transformToDate(dateOfBirth);
        }
    }

    onRemoveHistory(index: number) {
        if (this.props.payload.length) {
            this.props.payload.splice(index, 1);
        }
    }

    onGetOutcome(outcome: PregnancyOutcomeEnum) {
        return  this.commonService.removeUnderscore(outcome);
    }
}

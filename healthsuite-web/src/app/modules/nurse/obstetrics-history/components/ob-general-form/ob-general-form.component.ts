import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ObGeneralFormPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';
import {DatePayload} from '@app/shared/_payload';
import {CommonService} from '@app/shared/_services/common/common.service';
import {SharedDateComponent} from '@app/modules/common/shared-date/shared-date.component';

@Component({
    selector: 'app-ob-general-form',
    templateUrl: './ob-general-form.component.html',
    styleUrls: ['./ob-general-form.component.css'],
})
export class ObGeneralFormComponent implements OnInit {
    @ViewChild('sharedDateComponent') sharedDateComponent: SharedDateComponent;
    @Input('prop') props: { payload: ObGeneralFormPayload };

    constructor(private commonService: CommonService) {}

    ngOnInit(): void {
        if (!this.props.payload) {
            this.props.payload = new ObGeneralFormPayload();
        }
    }

    onDateSelected(datePayload: DatePayload) {
        if (datePayload) {
            this.props.payload.lmp = this.commonService.transformToDate(datePayload);
        }
    }

    clearDateField() {
        this.sharedDateComponent.onClearDateField();
    }
}

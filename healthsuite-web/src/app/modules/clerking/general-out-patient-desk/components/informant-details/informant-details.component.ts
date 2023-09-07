import { Component, Input, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { InformantDetailsPayload } from '@app/shared/_payload/clerking/outpatient-desk.payload';
import { fadeInOnEnterAnimation, fadeOutOnLeaveAnimation } from 'angular-animations';
import {CommonService} from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-informant-details',
    templateUrl: './informant-details.component.html',
    styleUrls: ['./informant-details.component.css'],
    animations: [fadeInOnEnterAnimation(), fadeOutOnLeaveAnimation()],
})
export class InformantDetailsComponent implements OnInit {
    @Input('props') public props: { data: InformantDetailsPayload };

    constructor(private commonService: CommonService) {}

    ngOnInit(): void {
        if (!this.props.data){
            this.props.data = new InformantDetailsPayload();
        }
    }

    public onRelationshipSelected(relationship: SharedPayload) {
        if (relationship) {
            this.props.data.informantRelationship = relationship;
        }
    }

    public numberMatch(event) {
        const isMatch = this.commonService.isNumberMatch(event);
        if (!isMatch) {
            event.preventDefault();
        }
    }
}

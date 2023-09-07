import { Component, Input, OnInit } from '@angular/core';
import { ClerkRadiologyRequestPayload } from '@app/shared/_payload/clerking/clerk-request.payload';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';

@Component({
    selector: 'app-radiology-bill-extras',
    templateUrl: './radiology-bill-extras.component.html',
    styleUrls: ['./radiology-bill-extras.component.css'],
})
export class RadiologyBillExtrasComponent implements OnInit {
    @Input('props')
    public props: { radiologyRequest: ClerkRadiologyRequestPayload };

    constructor() {}

    ngOnInit(): void {
        if (!this.props?.radiologyRequest) {
            this.props.radiologyRequest = new ClerkRadiologyRequestPayload();
        }
    }

}

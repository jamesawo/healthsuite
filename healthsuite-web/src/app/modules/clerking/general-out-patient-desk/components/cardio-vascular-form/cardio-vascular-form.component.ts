import { Component, Input, OnInit } from '@angular/core';
import {
    CardioVascularFormPayload,
    YesNoEnum,
} from '@app/shared/_payload/clerking/outpatient-desk.payload';

@Component({
    selector: 'app-cardio-vascular-form',
    templateUrl: './cardio-vascular-form.component.html',
    styleUrls: ['./cardio-vascular-form.component.css'],
})
export class CardioVascularFormComponent implements OnInit {
    @Input('props')
    public props: { data: CardioVascularFormPayload };
    public yes = YesNoEnum.YES;
    public no = YesNoEnum.NO;

    constructor() {}

    ngOnInit(): void {}

    public onHeartSoundsCheck(event: any, type: string) {
        const checkedValue = event.target.checked;
        if (checkedValue === true) {
            this.onAddToHeartBeat(type);
            return;
        } else {
            this.onRemoveFromHeartBeat(type);
            return;
        }
    }

    private onAddToHeartBeat(value: string) {
        const find = this.props.data.heartSound.find((dat) => dat === value);
        if (!find) {
            this.props.data.heartSound.push(value);
        }
    }

    private onRemoveFromHeartBeat(value: string) {
        for (let i = 0; i < this.props.data.heartSound.length; i++) {
            if (this.props.data.heartSound[i] === value) {
                this.props.data.heartSound.splice(i, 1);
            }
        }
    }
}

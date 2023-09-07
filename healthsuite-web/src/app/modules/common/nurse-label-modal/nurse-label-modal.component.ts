import {Component, OnDestroy, OnInit} from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-nurse-label-modal',
    templateUrl: './nurse-label-modal.component.html',
    styleUrls: ['./nurse-label-modal.component.css'],
})
export class NurseLabelModalComponent implements OnInit, OnDestroy {
    public data: { nurseLabelList: SharedPayload[] };
    public label = '';
    public labelError = false;
    public isSubmitting = false;

    private subscription: Subscription = new Subscription();
    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private seedDataService: SeedDataService
    ) {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnInit(): void {}

    public onAddNewLabel() {
        this.labelError = false;
        if (this.label === '') {
            this.labelError = true;
            return;
        }
        this.isSubmitting = true;
        const newLabel: SharedPayload = { name: this.label, id: null };
        this.subscription.add(
            this.seedDataService.onCreateNursingNoteLabel(newLabel).subscribe(
                (result) => {
                    this.isSubmitting = false;
                    if (result.httpStatusCode === 200) {
                        this.toast.success('LABEL SAVED', HmisConstants.LAST_ACTION_SUCCESS);
                        newLabel.id = result.data.id;
                        this.data.nurseLabelList.push(newLabel);
                    }
                },
                (error) => {
                    console.log(error);
                    this.isSubmitting = false;
                    this.toast.error(error.error.message, 'FAILED TO SAVE LABEL');
                }
            )
        );
    }
}

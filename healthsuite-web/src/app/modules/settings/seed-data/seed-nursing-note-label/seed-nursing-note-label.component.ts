import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-nursing-note-label',
    templateUrl: './seed-nursing-note-label.component.html',
    styleUrls: ['./seed-nursing-note-label.component.css'],
})
export class SeedNursingNoteLabelComponent implements OnInit, OnDestroy {
    isSubmitted = false;
    nursingNoteLabelForm: FormGroup;
    nursingNoteLabel: SharedPayload;
    nursingNoteLabelList: SharedPayload[];
    subscription: Subscription;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.nursingNoteLabelForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
        });
        this.subscription = this.service.onGetAllNursingNoteLabel().subscribe((result) => {
            this.nursingNoteLabelList = result.data;
        });
        this.nursingNoteLabel = { id: null, name: '' };
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    get form() {
        return this.nursingNoteLabelForm.controls;
    }

    onCreateNursingNoteLabel() {
        this.isSubmitted = true;
        if (this.nursingNoteLabelForm.invalid) {
            return;
        }
        this.notification.useSpinner('show');
        this.nursingNoteLabel.name = this.nursingNoteLabelForm.get('name').value;

        if (this.isNursingNoteExist(this.nursingNoteLabel)) {
            this.notification.useSpinner('hide');
            this.notification.showToast({
                type: 'error',
                message: 'Name Already Exist.',
            });
            return;
        }

        this.subscription = this.service.onCreateNursingNoteLabel(this.nursingNoteLabel).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'success',
                        message: 'Nursing Note Successfully Created.',
                    });
                    this.onAddNursingNoteToList({ id: result.data.id, name: result.data.name });
                } else {
                    this.notification.useSpinner('hide');
                    this.notification.showToast({
                        type: 'error',
                        message: result.message,
                    });
                }
            },
            (error) => {
                this.notification.useSpinner('hide');
                this.notification.showToast({
                    type: 'error',
                    message: 'Something went wrong, Contact Support.',
                });
            }
        );
    }

    isNursingNoteExist(data: SharedPayload): boolean {
        let flag = false;
        this.nursingNoteLabelList.forEach((entry) => {
            if (entry.name.toLowerCase() === data.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    onAddNursingNoteToList(payload: SharedPayload) {
        this.nursingNoteLabelList.push(payload);
        this.nursingNoteLabelForm.reset();
        this.isSubmitted = false;
    }
}

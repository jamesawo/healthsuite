import { Component, OnDestroy, OnInit } from '@angular/core';
import { SharedPayload } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService, SeedDataService } from '@app/shared/_services';

@Component({
    selector: 'app-seed-scheme',
    templateUrl: './seed-scheme.component.html',
    styleUrls: ['./seed-scheme.component.css'],
})
export class SeedSchemeComponent implements OnInit, OnDestroy {
    public scheme: SharedPayload;
    public schemeList: any[];
    public isSubmitted = false;
    private subscription: Subscription = new Subscription();
    public schemeForm: FormGroup;

    constructor(private service: SeedDataService, private notification: NotificationService) {}

    ngOnInit(): void {
        this.schemeForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
            discount: new FormControl('', []),
        });
        this.subscription.add(
            this.service.onGetAllScheme().subscribe((result) => (this.schemeList = result.data))
        );
        this.scheme = { name: '', id: null, discount: 0 };
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    get form() {
        return this.schemeForm.controls;
    }

    isSchemeExist(scheme: SharedPayload): boolean {
        let flag = false;
        this.schemeList.forEach((schemeInList) => {
            if (schemeInList.name.toLowerCase() === scheme.name.toLowerCase()) {
                flag = true;
            }
        });
        return flag;
    }

    addScheme(scheme: SharedPayload): void {
        this.schemeList.push(scheme);
        this.schemeForm.reset();
        this.isSubmitted = false;
    }

    // onCreateScheme() {
    //     this.isSubmitted = true;
    //     if (this.schemeForm.invalid) {
    //         return;
    //     }
    //     this.scheme.name = this.schemeForm.get('name').value;
    //     this.scheme.discount = this.schemeForm.get('discount').value;
    //
    //     if (this.isSchemeExist(this.scheme)) {
    //         this.notification.showToast({
    //             type: 'error',
    //             message: 'Name Already Exist',
    //         });
    //         return;
    //     }
    //     this.notification.useSpinner('show');
    //     this.subscription = this.service.onCreateScheme(this.scheme).subscribe(
    //         (result) => {
    //             if (result.data) {
    //                 this.addScheme(this.scheme);
    //                 this.notification.useSpinner('hide');
    //                 this.notification.showToast({
    //                     type: 'success',
    //                     message: 'Created Successfully.',
    //                 });
    //             } else {
    //                 this.notification.useSpinner('hide');
    //                 this.notification.showToast({ type: 'error', message: result.message });
    //             }
    //         },
    //         (error) => {
    //             this.notification.useSpinner('hide');
    //             this.notification.showToast({
    //                 type: 'error',
    //                 message: 'Something is not right, Contact Support',
    //             });
    //         }
    //     );
    // }

    onCreateScheme() {}
}

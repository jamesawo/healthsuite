import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { SnackbarService } from 'ngx-snackbar';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { SocketMessagePayload } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    api: string = environment.apiEndPoint + '/notifications';
    notifications: SocketMessagePayload[] = [];
    notificationSubject: BehaviorSubject<SocketMessagePayload[]>;
    notifications$: Observable<SocketMessagePayload[]>;

    constructor(
        private toast: ToastrService,
        private spinner: NgxSpinnerService,
        private snackbarService: SnackbarService,
        private http: HttpClient
    ) {
        this.notificationSubject = new BehaviorSubject<any[]>(this.notifications);
        this.notifications$ = this.notificationSubject.asObservable();
        // if (!this.notifications.length) {
        //     this.onPrepareNotifications();
        // }
    }

    showToast(alert: { type: string | number; message: string }) {
        switch (alert.type) {
            case 'success':
                this.toast.success(alert.message);
                break;
            case 'error':
                this.toast.error(alert.message);
                break;
            case 200:
                this.toast.success(alert.message);
                break;
            case 0:
                this.toast.error(alert.message);
                break;
            case 404:
                this.toast.error(alert.message);
                break;
            case 401:
                this.toast.error(alert.message);
                break;
            case 501:
                this.toast.error(alert.message);
                break;
            case 500:
                this.toast.error(alert.message);
                break;
            case 'warn':
                this.toast.warning(alert.message);
                break;
            case 'clear':
                this.toast.clear();
                break;
            default:
                this.toast.show(alert.message);
                break;
        }
    }

    useSpinner(option) {
        switch (option) {
            case 'show':
                this.spinner.show();
                break;
            case 'hide':
                this.spinner.hide();
                break;
            default:
                console.log('no option passed to spinner: spinner service');
                break;
        }
    }

    snackBar({
        message,
        actionText,
        actionCallback = {},
        displayTime = 8000,
        onClickCallBack = {},
        onAddCallBack = {},
        onRemoveCallBack = {},
    }) {
        this.snackbarService.add({
            msg: `<strong> ${message} </strong>`,
            timeout: displayTime,
            action: {
                text: actionText,
                onClick: (snack) => {},
            },
            onAdd: (snack) => {
                // console.log('added: ' + snack.id);
            },
            onRemove: (snack) => {},
        });
    }

    snackBarClear() {
        this.snackbarService.clear();
    }

    onGetNotifications(module: string) {
        return this.http.get<SocketMessagePayload[]>(`${this.api}/by-module?module=${module}`);
    }

    // private onPrepareNotifications() {
    //     this.onGetNotifications('PHARMACY')
    //         .toPromise()
    //         .then((result) => {
    //             this.notificationSubject.next(result);
    //         });
    // }
}

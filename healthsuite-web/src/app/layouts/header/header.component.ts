import { Component, OnDestroy, OnInit } from '@angular/core';

import { AuthService } from '@app/shared/_services/auth/auth.service';
import { User } from '@app/shared/_models';
import { Subscription } from 'rxjs';
import { LocationService } from '@app/shared/_services/settings/location.service';
import { DepartmentPayload } from '@app/modules/settings';
import { SocketClientService } from '@app/shared/_services/common/socket-client.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {
    DatePayload,
    ModalSizeEnum,
    NurseWaitingPayload,
    SocketMessagePayload,
    WaitingTypeEnum,
} from '@app/shared/_payload';
import {
    GlobalSettingService,
    ModalPopupService,
    NotificationService,
} from '@app/shared/_services';
import { SnackbarService } from 'ngx-snackbar';
import { ModuleNamesEnum, PermissionsEnum } from '@app/shared/_models/menu/permissions.enum';
import { CommonService } from '@app/shared/_services/common/common.service';
import { NurseWaitingListService } from '@app/shared/_services/nurse/nurse-waiting-list.service';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, mergeMap } from 'rxjs/operators';
import { DoctorWaitingListService } from '@app/shared/_services/clerking/doctor-waiting-list.service';
import { environment } from '@environments/environment';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ChangePasswordComponent } from '@app/modules/common/change-password/change-password.component';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
    showUserProfile = false;
    showNotification = false;
    toggleNotification = false;
    userDetails: User;
    location: DepartmentPayload;
    subscription: Subscription = new Subscription();
    notifications: SocketMessagePayload[] = [];
    pageTitle = '';
    imageAvatar: string = environment.clientBaseDir + 'assets/img/avatar.png';

    constructor(
        public authService: AuthService,
        public locationService: LocationService,
        private socketService: SocketClientService,
        private globalSettingService: GlobalSettingService,
        private snackbarService: SnackbarService,
        private notificationService: NotificationService,
        private commonService: CommonService,
        private nurseWaitingService: NurseWaitingListService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private doctorWaitingList: DoctorWaitingListService,
        private shiftService: ShiftManagerService,
        private spinner: NgxSpinnerService,
        private modalService: ModalPopupService
    ) {
        this.getRouteData();
    }

    ngOnInit() {
        this.getRouteData();
        // this.userDetails = this.authService.currentUserValue;
        this.subscription.add(
            this.authService.currentUser.subscribe((user) => {
                if (user) {
                    this.userDetails = user;
                }
            })
        );
        this.subscription = this.locationService.location$.subscribe((data) => {
            this.location = data;
        });

        this.socketService.onAttemptReconnect();

        this.subscription.add(
            this.socketService
                .onMessage(HmisConstants.SOCK_GLOBAL_SETTING_UPDATE)
                .subscribe((result: SocketMessagePayload) => {
                    if (result.content) {
                        this.snackbarService.add({
                            msg: 'System Settings Updated',
                            action: { text: 'Got It!' },
                        });
                        this.globalSettingService.onPrepareGlobalSetting();
                    }
                })
        );

        this.subscription.add(
            this.socketService
                .onMessage(HmisConstants.SOCK_NOTIFICATION_WEB)
                .subscribe((result: SocketMessagePayload) => {
                    if (result.content) {
                        this.snackbarService.add({
                            msg: `ALERT: ${result.title.toUpperCase()}`,
                            action: { text: 'OK!' },
                        });
                    }
                })
        );

        this.subscription.add(
            this.notificationService
                .onGetNotifications(ModuleNamesEnum.PHARMACY.toUpperCase())
                .subscribe((data) => {
                    this.notificationService.notificationSubject.next(data);
                })
        );

        this.subscription.add(
            this.socketService
                .onMessage(HmisConstants.SOCK_NURSE_WAITING_LIST)
                .subscribe((result: SocketMessagePayload) => {
                    if (result.content) {
                        const data = JSON.parse(result.content);
                        const waiting: NurseWaitingPayload = new NurseWaitingPayload();
                        waiting.clinicIds = data.clinicIds;
                        waiting.patientCategory = data.patientCategory;
                        waiting.patientNumber = data.patientNumber;
                        waiting.patientId = data.patientId;
                        waiting.waitingStatus = data.walkInBillType;
                        waiting.patientName = data.patientName;
                        waiting.id = data.id;
                        waiting.type = data.type;
                        if (waiting.type === WaitingTypeEnum.ADD) {
                            this.nurseWaitingService.onAddToWaitingList(waiting);
                        } else if (waiting.type === WaitingTypeEnum.REMOVE) {
                            this.nurseWaitingService.onRemoveFromWaitingList(waiting.patientId);
                        }
                    }
                })
        );

        this.subscription.add(
            this.notificationService.notifications$.subscribe((list) => (this.notifications = list))
        );

        this.subscription.add(
            this.socketService
                .onMessage(HmisConstants.SOCK_DOCTOR_WAITING_LIST)
                .subscribe((result) => {
                    const data = JSON.parse(result.content);
                    this.doctorWaitingList.onAddToWaitingList(data);
                })
        );

        this.canViewNotification();
    }

    getRouteData() {
        this.router.events
            .pipe(
                filter((event) => event instanceof NavigationEnd),
                map(() => this.activatedRoute),
                map((route) => {
                    while (route.firstChild) {
                        route = route.firstChild;
                    }
                    return route;
                }),
                mergeMap((route) => route.data)
            )
            .subscribe((data) => {
                if (data && data.title) {
                    this.pageTitle = data.title;
                } else {
                    this.pageTitle = '';
                }
            });
    }

    onLogout() {
        this.authService.logout();
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    canViewNotification() {
        this.showNotification = this.authService.hasRightAccess(
            PermissionsEnum.Pharmacy_Pharmacy_Notifications,
            ModuleNamesEnum.PHARMACY
        );
    }

    getDate(payload: DatePayload): Date {
        return this.commonService.transformToDate(payload);
    }

    onCloseCashierShift() {
        const user = this.commonService.getCurrentUser().id;
        this.spinner.show().then();
        this.subscription.add(
            this.shiftService.onCloseCashierShift(this.userDetails.shiftNumber, user).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res === true) {
                        this.authService.removeShitNumber();
                        this.snackbarService.add({ msg: 'SUCCESSFULLY CLOSED SHIFT', action: {text: 'OKAY'} });
                    } else {
                        this.snackbarService.add({ msg: 'FAILED TO CLOSE SHIFT', action: {text: 'TRY AGAIN'} });
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.snackbarService.add({ msg: 'FAILED TO CLOSE SHIFT' });
                    console.log(error);
                }
            )
        );
    }

    onChangePassword() {
        this.modalService.openModalWithComponent(
            ChangePasswordComponent,
            {
                data: {},
                title: 'CHANGE PASSWORD',
            },
            ModalSizeEnum.medium
        );
    }
}

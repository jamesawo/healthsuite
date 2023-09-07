import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginPayload, ResponsePayload} from '@app/shared/_payload';
import { MenuBag, MenuItem, User } from '@app/shared/_models';
import { map } from 'rxjs/operators';
import { Modules, Permission } from '@app/shared/_models/shared';
import { appModules } from '@app/shared/_models/menu/appModules';
import { NgxSpinnerService } from 'ngx-spinner';
import { AppStorageService } from '@app/shared/_services/common/app-storage.service';
import { ModuleNamesEnum, PermissionsEnum } from '@app/shared/_models/menu/permissions.enum';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    apiURL: string = environment.apiEndPoint;
    private currentUserSubject: BehaviorSubject<User>;
    public currentUser: Observable<User>;

    private userModulesSubject: BehaviorSubject<MenuBag[]>;
    public userModules$: Observable<MenuBag[]>;

    public selectedMenu: BehaviorSubject<MenuBag>;
    public selectedMenu$: Observable<MenuBag>;

    private appMenu: MenuBag[];
    private emptyMenuBag: MenuBag[];
    private emptyMenu: MenuBag;
    private defaultMenu: MenuBag;

    private childrenPermission: MenuItem[] = [];
    private childMenu: MenuItem;

    constructor(
        private http: HttpClient,
        private router: Router,
        private spinner: NgxSpinnerService,
        private appStorageService: AppStorageService
    ) {
        this.appMenu = appModules;

        this.currentUserSubject = new BehaviorSubject<User>(
            this.appStorageService.getStore('uuid')
        );
        this.currentUser = this.currentUserSubject.asObservable();

        // user menu
        this.userModulesSubject = new BehaviorSubject<MenuBag[]>(
            this.appStorageService.getStore('userModules')
        );
        this.userModules$ = this.userModulesSubject.asObservable();

        // selected menu
        this.selectedMenu = new BehaviorSubject<MenuBag>(null);
        this.selectedMenu$ = this.selectedMenu.asObservable();
    }

    isAuthenticated(): boolean {
        return this.appStorageService.getStore('uuid') != null;
    }

    public get currentUserValue(): User {
        return this.currentUserSubject.value;
    }

    public get currentUserModules(): MenuBag[] {
        return this.userModulesSubject.value;
    }

    public get defaultMenuBag(): MenuBag {
        return this.defaultMenu;
    }

    public set userShiftNumber(shiftCode: string) {
        const user = this.currentUserValue;
        user.shiftNumber = shiftCode;
        this.currentUserSubject.next(user);
    }

    public login(loginPayload: LoginPayload) {
        return this.http.post<ResponsePayload<any>>(`${this.apiURL}/auth/login`, loginPayload).pipe(
            map((user) => {
                if (user.httpStatusCode === 200) {
                    this.appStorageService.setStore('uuid', user.data);
                    this.appStorageService.setStore('token', user.message);
                    this.currentUserSubject.next(user.data);
                    this.filterUserModule(user.data.modules);
                }
                return user;
            })
        );
    }

    public logout(): void {
        this.appStorageService.clearStore('uuid');
        this.appStorageService.clearStore('token');
        this.appStorageService.clearStore('userModules');
        this.appStorageService.clearStore('globalSetting');
        this.appStorageService.clearStore('globalSetting');
        this.appStorageService.clearStore(HmisConstants.DEFAULT_LOCATION_KEY);
        this.currentUserSubject.next(null);
        this.userModulesSubject.next(null);
        this.router.navigateByUrl('/login').then();
    }

    // pass users modules and filter from system modules
    public filterUserModule(uModules: Modules[]): void {
        this.childrenPermission = [];
        this.spinner.show().then();
        this.emptyMenuBag = [];
        uModules.forEach((userModule) => {
            this.emptyMenu = { permissions: [], uuid: '' };
            this.appMenu.forEach((itemBag) => {
                if (itemBag.uuid.toLowerCase() === userModule.name.toLowerCase()) {
                    this.emptyMenu.uuid = itemBag.uuid;
                    this.emptyMenu.title = itemBag.title;
                    this.emptyMenu.icon = itemBag.icon;
                    this.emptyMenu.linkTitle = itemBag.linkTitle;
                    this.emptyMenu.hierarchy = itemBag.hierarchy;
                    userModule.permissions.forEach((itemsPermission) => {
                        itemBag.permissions.forEach((userPermission) => {
                            if (userPermission.hasChild) {
                                // prepare all the sub module that the user has access to into this.childrenPermission
                                // (eg. Report => Emr Report => Registered Patient Report)
                                this.hasChildPermission(
                                    userPermission,
                                    itemsPermission,
                                    this.childrenPermission
                                );
                            } else {
                                if (itemsPermission.name === userPermission.right) {
                                    this.emptyMenu.permissions.push(userPermission);
                                }
                            }
                        });
                    });
                }
            });
            this.emptyMenuBag.push(this.emptyMenu);
        });

        this.emptyMenuBag.sort((a, b) => (a.hierarchy > b.hierarchy ? 1 : -1));

        this.emptyMenuBag.forEach(value => {
            if (value.permissions.length > 0) {
                value.permissions.sort( (a, b) => (a.hierarchy > b.hierarchy ? 1 : -1) );
                value.permissions.forEach(child => {
                    if (child.hasChild && child.children.length > 0) {
                        child.children.sort( (a, b) => (a.hierarchy > b.hierarchy ? 1 : -1) );
                        child.children.forEach(sub => {
                            if (sub.hasChild && sub.children.length) {
                                child.children.sort( (a, b) => (a.hierarchy > b.hierarchy ? 1 : -1) );
                            }
                        });
                    }
                });
            }
        });

        this.appStorageService.setStore('userModules', this.emptyMenuBag);
        this.setDefaultMenu(this.emptyMenuBag[0]);
        this.spinner.hide().then();
        this.userModulesSubject.next(this.emptyMenuBag);

    }

    public hasChildPermission(
        userPermission: MenuItem, // from appModule.ts
        itemsPermission: Permission, // from api call
        childrenPermission: MenuItem[] // from this.childrenPermission
    ) {
        // iterate through the app the module with children (such as report)
        // check if user module (from remote) contains permission for child module.right
        // call addToChildPermission passing the title of the parent module, childPermission,and this.childrenPermission
        const {title, hierarchy, } = userPermission;
        userPermission.children.forEach((childPerm) => {
            // check if user permission (from api) against module in appModules.ts
            if (childPerm.right === itemsPermission.name) {
                const item = this.addToChildPermission(title, hierarchy, childPerm, childrenPermission);
                const find = this.emptyMenu.permissions.find(value => value.title === item.title);
                if (!find) {
                    this.emptyMenu.permissions.push(item);
                }
            }
        });
    }

    public addToChildPermission(
        title: string,
        hierarchy: number,
        childPerm: MenuItem,
        childrenPermission: MenuItem[]
    ): MenuItem {
        // check if parent title is already in the collection (this.childrenPermission)
        // if true (EMR reports) then add the child permission into the parent (RegisteredPatient sub should be added under same EMR Report)
        // other wise add a new sub module into the collection
        let resMenuItem: MenuItem;
        const menuItem = childrenPermission.find((value) => value.title === title);
        // childrenPermission from this.childrenPermission
        if (menuItem) {
            menuItem.hasChild = true;
            menuItem.children.push(childPerm);
            resMenuItem = menuItem;
        } else {
            const newChildItem: MenuItem = { title, hasChild: true, children: [] };
            newChildItem.children.push(childPerm);
            childrenPermission.push(newChildItem);
            resMenuItem = newChildItem;
        }
        resMenuItem.hierarchy = hierarchy;
        return resMenuItem;
    }

    public removeShitNumber() {
        const user = this.currentUserValue;
        user.shiftNumber = undefined;
        this.currentUserSubject.next(user);
        this.appStorageService.clearStore('uuid');
        this.appStorageService.setStore('uuid', user);
    }

    public addShiftNumber(shiftCode: string) {
        const user = this.currentUserValue;
        user.shiftNumber = shiftCode;
        this.currentUserSubject.next(user);
        this.appStorageService.clearStore('uuid');
        this.appStorageService.setStore('uuid', user);
    }

    public hasRightAccess(pName: PermissionsEnum, mName: ModuleNamesEnum): boolean {
        const bag = this.currentUserModules.find((value) => value.uuid === mName);
        if (bag && bag.permissions.length) {
            const item = bag.permissions.find((value) => value.right === pName);
            return item !== null && item !== undefined;
        }
        return false;
    }

    private setDefaultMenu(menu: MenuBag): void {
        this.defaultMenu = menu;
        this.appStorageService.setStore('setDefaultMenu', 'yes');
        this.defaultMenu.isSelected = true;
        this.selectedMenu.next(this.defaultMenu);
    }
}

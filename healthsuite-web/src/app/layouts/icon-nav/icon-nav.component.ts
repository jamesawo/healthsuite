import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { MenuBag } from '@app/shared/_models';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { Subscription } from 'rxjs';
import { GlobalSettingService } from '@app/shared/_services';
import { GlobalSettingKeysEnum, GlobalSettingValueEnum } from '@app/shared/_payload';
import { SnackbarService } from 'ngx-snackbar';

@Component({
    selector: 'app-icon-nav',
    templateUrl: './icon-nav.component.html',
    styleUrls: ['./icon-nav.component.css'],
})
export class IconNavComponent implements OnInit, OnDestroy {
    menuItems: MenuBag[];
    @Output() openMenu = new EventEmitter<boolean>();
    subscription: Subscription = new Subscription();
    isMenuOpen = false;
    isClerking: boolean = undefined;

    constructor(
        private authService: AuthService,
        private globalSettingService: GlobalSettingService,
        private snackbar: SnackbarService
    ) {}

    ngOnInit() {
        this.globalSettingService
            .getSettingValueByKey(GlobalSettingKeysEnum.ACTIVATE_CLERKING)
            .toPromise()
            .then((value) => {
                this.isClerking = value.body.data.value === GlobalSettingValueEnum.YES;
                this.onGetMenu();
            });
    }

    onGetMenu() {
        return this.authService.userModules$.subscribe((menus) => {
            this.menuItems = menus;
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onClickNavIcon(menu: MenuBag) {
        if (!this.canViewClerking(menu.title)) {
            this.snackbar.add({ msg: 'No Module Access Granted', action: { text: 'OK' } });
            return;
        }
        this.isMenuOpen = true;
        this.setNavIconIsActive(menu);
    }

    setNavIconIsActive(menu: MenuBag) {
        menu.isSelected = true;
        this.menuItems.forEach((menuItem) => {
            if (menuItem.hierarchy !== menu.hierarchy) {
                menuItem.isSelected = false;
            }
        });
        this.authService.selectedMenu.next(menu);
    }

    onOpenMenu() {
        this.openMenu.emit(true);
    }

    canViewClerking(title: string): boolean {
        if (title && title.toLowerCase().includes('clerking')) {
            return this.isClerking;
        }
        return true;
    }
}

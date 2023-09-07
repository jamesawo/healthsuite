import { Component, OnDestroy, OnInit } from '@angular/core';

import { NgxSpinnerService } from 'ngx-spinner';
import { MenuBag } from '@app/shared/_models';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { Subscription } from 'rxjs';
import { slideInLeftOnEnterAnimation, slideOutLeftOnLeaveAnimation } from 'angular-animations';

@Component({
    selector: 'app-layouts',
    templateUrl: './layouts.component.html',
    styleUrls: ['./layouts.component.css'],
    animations: [slideInLeftOnEnterAnimation(), slideOutLeftOnLeaveAnimation()],
})
export class LayoutsComponent implements OnInit, OnDestroy {
    displayMenu = false;
    menuLists: MenuBag[];
    menu: MenuBag = { permissions: [], uuid: '' };
    subscription: Subscription = new Subscription();

    constructor(private spinner: NgxSpinnerService, private authService: AuthService) {}

    ngOnInit() {
        this.subscription.add(
            this.authService.userModules$.subscribe((module) => {
                if (module) {
                    this.menuLists = module;
                    this.menu = module[0];
                }
            })
        );
        this.subscription.add(
            this.authService.selectedMenu$.subscribe((menu) => {
                this.displayMenu = menu?.isSelected;
            })
        );
    }

    toggleMenuDisplay(value: boolean) {
        const selectedMenuValue = this.authService.selectedMenu.value;

        if (!this.displayMenu && !selectedMenuValue) {
            this.authService.selectedMenu.next(this.menu);
        }
        if (this.displayMenu !== value) {
            this.displayMenu = value;
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

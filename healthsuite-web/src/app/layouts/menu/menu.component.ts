import {
    AfterContentChecked,
    AfterViewInit,
    Component,
    EventEmitter,
    OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import { MenuBag } from '@app/shared/_models';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { Subscription } from 'rxjs';
import { environment } from '@environments/environment';
import { LocalStorageService } from 'ngx-webstorage';

@Component({
    selector: 'app-menu',
    templateUrl: './menu.component.html',
    styleUrls: ['./menu.component.css'],
})
export class MenuComponent implements OnInit, OnDestroy, AfterViewInit, AfterContentChecked {
    menuItem: MenuBag;
    @Output() onClickCollapseMenu = new EventEmitter<boolean>();
    appName = environment.appName;

    subscription: Subscription = new Subscription();

    constructor(private authService: AuthService, private localStorage: LocalStorageService) {}

    ngOnInit() {
        this.subscription.add(
            this.authService.selectedMenu$.subscribe((menu) => {
                this.menuItem = menu;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngAfterViewInit(): void {}

    ngAfterContentChecked(): void {}
}

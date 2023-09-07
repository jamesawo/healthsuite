import { Component, OnDestroy, OnInit } from '@angular/core';
import { GlobalSettingService, SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import {environment} from '@environments/environment';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css'],
})
export class WelcomeComponent implements OnInit, OnDestroy {
    basePath: string = environment.clientBaseDir;
    imageList = [
        'assets/img/vector/ld_1.svg',
        'assets/img/vector/ld_2.svg',
        'assets/img/vector/ld_5.svg',
        'assets/img/vector/ld_4.svg',
    ];
    defaultImage = '';
    subscription: Subscription = new Subscription();

    /*
        By injecting socketService connection to websocket will be established
     */

    constructor(
        private seedDataService: SeedDataService,
        private globalSettingService: GlobalSettingService
    ) {}

    getImage() {
        this.defaultImage = this.basePath + this.imageList[Math.floor(Math.random() * this.imageList.length)];
    }

    ngOnInit() {
        this.getImage();
        // initialize app data
        this.seedDataService.onAfterLoginInitializeData();
        this.globalSettingService.onPrepareGlobalSetting();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

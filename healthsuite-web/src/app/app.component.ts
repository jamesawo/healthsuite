import { Component, OnDestroy, OnInit } from '@angular/core';

import { environment } from '@environments/environment';
import { SocketClientService } from '@app/shared/_services/common/socket-client.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy {
    title = 'hmis-2-client';
    url = environment.socketEndPoint;

    constructor(private socketClientService: SocketClientService) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.socketClientService.onDisconnect();
    }
}

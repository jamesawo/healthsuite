import { Injectable, OnDestroy } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import { CompatClient, Message, StompHeaders, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import * as SockJS from 'sockjs-client';

import { SocketClientState } from '@app/shared/_payload';
import { environment } from '@environments/environment';
import { catchError, filter, first, switchMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

/*
    connected on header.component.ts init()
    subscribes to message
    cleans / unsubscribes on header.component.ts  onDestroy()
 */

@Injectable({
    providedIn: 'root',
})
export class SocketClientService implements OnDestroy {
    url = environment.socketEndPoint;

    protected client: CompatClient;
    private state: BehaviorSubject<SocketClientState> = new BehaviorSubject<SocketClientState>(
        SocketClientState.ATTEMPTING
    );

    constructor() {
        this.client = Stomp.Stomp.over(new SockJS(this.url));
        this.client.reconnect_delay = 5000;
        this.client.connect({}, () => {
            this.state.next(SocketClientState.CONNECTED);
        });
        this.client.debug = () => {};
    }

    /*
        return the websocket client,
        unsubscribing from this observable will not disconnect connection from web socket
     */
    private connect(): Observable<CompatClient> {
        return new Observable<CompatClient>((observer) => {
            this.state
                .pipe(filter((state) => state === SocketClientState.CONNECTED))
                .subscribe(() => {
                    observer.next(this.client);
                });
        });
    }

    ngOnDestroy() {
        //hook into ng life cycle
        //disconnect the websocket connection
        this.connect()
            .pipe(first())
            .subscribe((client) => client.disconnect(null));
    }

    /*
        By using the switchMap() operator,
        we can switch from the original observable containing the websocket client,
        to an observable containing the messages that are being sent.
     */
    onMessage(topic: string, handler = SocketClientService.jsonHandler): Observable<any> {
        return this.connect().pipe(
            first(),
            switchMap((client) => {
                return new Observable<any>((observer) => {
                    const subscription: StompSubscription = client.subscribe(topic, (message) => {
                        observer.next(handler(message));
                    });
                    return () => {
                        if (client.connected) client.unsubscribe(subscription.id);
                    };
                });
            })
        );
    }

    /*
    first() function to immediately complete/clean up the observable as soon as a client is obtained. After that we simply call the send()
     */
    send(topic: string, payload: any): void {
        this.connect()
            .pipe(first())
            .subscribe((client) => this.client.send(topic, {}, JSON.stringify(payload)));
    }

    //parse body as JSON
    static jsonHandler(message: Message): any {
        return JSON.parse(message.body);
    }

    //text based
    static textHandler(message: Message): string {
        return message.body;
    }

    onPlainMessage(topic: string): Observable<string> {
        return this.onMessage(topic, SocketClientService.textHandler);
    }

    onDisconnect(): void {
        if (this.client.connected) {
            this.state.next(SocketClientState.DISCONNECTED);
            this.client.disconnect();
        }
    }

    onAttemptReconnect() {
        if (this.state.value !== SocketClientState.CONNECTED) {
            this.connect();
        }
    }
}

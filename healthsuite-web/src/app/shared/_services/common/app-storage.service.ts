import { Injectable } from '@angular/core';
import { LocalStorageService } from 'ngx-webstorage';
import { environment } from '@environments/environment';
import * as CryptoJS from 'crypto-js';

@Injectable({
    providedIn: 'root',
})
export class AppStorageService {
    private enc = environment.encSecret;
    constructor(private localStorage: LocalStorageService) {}

    public getStore(key: string, options?: { decrypt?: boolean; secret?: string }): any {
        let toRetrieve: any = this.localStorage.retrieve(key);
        if (options && options.decrypt === true) {
            toRetrieve = this.decrypt(toRetrieve);
        }
        return toRetrieve;
    }

    public setStore(key: string, value: any, options?: { encrypt: true }): void {
        if (options && options.encrypt === true) {
            this.localStorage.store(key, this.encrypt(value));
            return;
        } else {
            this.localStorage.store(key, value);
            return;
        }
    }

    public clearStore(key) {
        this.localStorage.clear(key);
    }

    private encrypt(value: any): any {
        return CryptoJS.AES.encrypt(JSON.stringify(value), this.enc).toString();
    }

    private decrypt(value: any): any {
        try {
            const decrypted = CryptoJS.AES.decrypt(value, this.enc);
            const result = CryptoJS.enc.Utf8.stringify(decrypted);
            return JSON.parse(result);
        } catch (e) {
            return null;
        }
    }
}

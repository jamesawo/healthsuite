import { Injectable } from '@angular/core';
import { LocalStorageService } from 'ngx-webstorage';
import { BehaviorSubject, Observable } from 'rxjs';
import { DepartmentPayload } from '@app/modules/settings';
import { HttpClient } from '@angular/common/http';
import { AppStorageService } from '@app/shared/_services/common/app-storage.service';
import {environment} from '@environments/environment';

@Injectable({
    providedIn: 'root',
})
export class LocationService {
    url: string = environment.apiEndPoint + '/locations';
    public locationSubject: BehaviorSubject<DepartmentPayload>;
    public location$: Observable<DepartmentPayload>;

    // use key location to store locationSettings value in localstorage
    constructor(
        private localStorage: LocalStorageService,
        private http: HttpClient,
        private appStorageService: AppStorageService
    ) {
        const sLocation: DepartmentPayload = this.appStorageService.getStore('location');
        this.locationSubject = new BehaviorSubject<DepartmentPayload>(sLocation);
        this.location$ = this.locationSubject.asObservable();
    }

    public get locationSetting(): DepartmentPayload {
        return this.locationSubject.value;
    }

    public onSetLocation(location: DepartmentPayload): boolean {
        let flag = false;
        if (location.id !== null) {
            this.appStorageService.setStore('locCategory', location.departmentCategory.name, {
                encrypt: true,
            });
            delete location.departmentCategory;

            this.appStorageService.setStore('location', location);
            this.locationSubject.next(location);
            flag = true;
        }
        return flag;
    }

    public onGetLocationCategory() {
        return this.appStorageService.getStore('locCategory', { decrypt: true });
    }

    public onGetDefaultLocation() {
        return this.http.get<any>(`${this.url}/get-default`);
    }
}

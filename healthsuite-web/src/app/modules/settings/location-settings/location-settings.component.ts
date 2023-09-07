import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NotificationService, SeedDataService } from '@app/shared/_services';
import { Observable, Subscription } from 'rxjs';
import { DepartmentPayload } from '@app/modules/settings';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { LocationService } from '@app/shared/_services/settings/location.service';

@Component({
    selector: 'app-location-settings',
    templateUrl: './location-settings.component.html',
    styleUrls: ['./location-settings.component.css'],
})
export class LocationSettingsComponent implements OnInit, OnDestroy {
    subscription: Subscription;
    departments: DepartmentPayload[];
    model: any;
    invalid = false;

    formatter = (department: DepartmentPayload) =>
        `${department.name?.toUpperCase()} ${department.code?.toUpperCase()}`;

    search = (text$: Observable<string>) =>
        text$.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            map((term) => {
                return term.length < 2
                    ? []
                    : this.departments
                          .filter((v) => {
                              return (
                                  v.name.toLowerCase().indexOf(term.toLowerCase()) > -1 ||
                                  v.code.toLowerCase().indexOf(term.toLowerCase()) > -1
                              );
                          })
                          .slice(0, 10);
            })
        );

    constructor(
        private service: LocationService,
        private dataService: SeedDataService,
        private notification: NotificationService
    ) {}

    ngOnInit() {
        this.subscription = this.dataService.getAllDepartment().subscribe((result) => {
            this.departments = result.data;
        });
    }

    onSetLocation() {
        if (this.model === undefined) {
            this.invalid = true;
            return;
        }
        const isSet = this.service.onSetLocation(this.model);
        this.invalid = false;
        if (isSet) {
            this.notification.showToast({
                type: 'success',
                message: 'Set Location Successful',
            });
        } else {
            this.notification.showToast({ type: 'error', message: 'An Error Occurred' });
        }
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}

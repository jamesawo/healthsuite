import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';
import { RolePayload } from '@app/shared/_payload';

@Component({
    selector: 'app-role-dropdown',
    templateUrl: './role-dropdown.component.html',
    styleUrls: ['./role-dropdown.component.css'],
})
export class RoleDropdownComponent implements OnInit, OnDestroy {
    public roles: RolePayload[] = [];
    @Output('selected')
    public selected: EventEmitter<RolePayload> = new EventEmitter<RolePayload>();
    @Input('defaultSelected')
    public defaultSelected: RolePayload;

    private subscriptions = new Subscription();
    constructor(private seedService: SeedDataService) {}

    ngOnInit(): void {
        this.subscriptions.add(
            this.seedService.getAllRole().subscribe((roles) => {
                this.roles = roles.data;
                if (this.defaultSelected && this.defaultSelected.id) {
                    this.defaultSelected = this.roles.find(
                        (value) => value.id === this.defaultSelected.id
                    );
                }
            })
        );
    }

    ngOnDestroy() {
        this.subscriptions.unsubscribe();
    }

    public onRoleSelected(payload: RolePayload) {
        if (payload) {
            this.selected.emit(payload);
        }
    }
}

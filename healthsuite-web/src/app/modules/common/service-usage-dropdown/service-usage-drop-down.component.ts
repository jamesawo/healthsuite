import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {
    HmisSharedDropDown,
    InputComponentChanged,
    InputComponentPayload,
} from '@app/shared/_payload';
import { ServiceRegisterService } from '@app/shared/_services/others/service-register.service';
import {RevenueDepartmentPayload} from '@app/modules/settings';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-service-usage-dropdown',
    templateUrl: './service-usage-drop-down.component.html',
    styleUrls: ['./service-usage-drop-down.component.css'],
})
export class ServiceUsageDropDownComponent implements OnInit {
    @ViewChild('ngSelectComponent') ngSelectComponent: NgSelectComponent;

    @Input() selected: InputComponentPayload;
    @Output() changed: EventEmitter<InputComponentChanged> = new EventEmitter<
        InputComponentChanged
    >();

    public default: HmisSharedDropDown;
    public serviceUsage: HmisSharedDropDown[];

    constructor(private service: ServiceRegisterService) {}

    ngOnInit(): void {
        this.serviceUsage = this.service.serviceUsageEnumToList();
        if (!this.selected) {
            this.default = { id: undefined, value: undefined, name: undefined };
        } else {
            this.default = this.serviceUsage.find((value) => value.id === this.selected.value);
        }
    }

    onSelectUsage(selected: HmisSharedDropDown) {
        const data: InputComponentChanged = {
            serviceColumn: this.selected?.columnName,
            value: selected.id,
            id: this.selected?.id,
        };
        this.default = selected;
        this.changed.emit(data);
    }

    onClearSelected() {
        this.ngSelectComponent.searchTerm = '';
        this.ngSelectComponent.clearModel();
        this.default = { id: undefined, value: undefined, name: undefined };
    }
}

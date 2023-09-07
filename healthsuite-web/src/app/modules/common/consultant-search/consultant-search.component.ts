import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Subscription } from 'rxjs';
import { SharedSearchService } from '@app/shared/_services/others/shared-search.service';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { UserManagerService } from '@app/shared/_services/settings/user-manager.service';

@Component({
    selector: 'app-consultant-search',
    templateUrl: './consultant-search.component.html',
    styleUrls: ['./consultant-search.component.css'],
})
export class ConsultantSearchComponent implements OnInit {
    //todo::refactor component, make component search user that are consultants only
    @Output() selected: EventEmitter<UserPayload> = new EventEmitter<UserPayload>();
    @Input() public props: { showLabel: boolean; hideDisabledUser: boolean };

    public minLengthTerm = 1;
    public collection: UserPayload[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private sharedSearchService: SharedSearchService,
        private userManagerService: UserManagerService
    ) {}

    ngOnInit(): void {
        this.subscription.add(
            this.userManagerService.onGetAllConsultant().subscribe(
                (result) => {
                    if (result.data) {
                        if (this.props?.hideDisabledUser) {
                            this.collection = result.data.filter(
                                (value) => value.accountEnabled === true
                            );
                        } else {
                            this.collection = result.data;
                        }
                    }
                },
                (error) => {
                    console.log(error);
                }
            )
        );
    }

    trackByFn(item: any) {
        return item.id;
    }

    onSelect(selected: UserPayload) {
        this.selected.emit(selected);
    }

    customSearchFn(term: string, item: UserPayload) {
        term = term.toLowerCase();
        return (
            item.lastName.toLowerCase().indexOf(term) > -1 ||
            item.otherNames.toLowerCase().indexOf(term) > -1
        );
    }
}

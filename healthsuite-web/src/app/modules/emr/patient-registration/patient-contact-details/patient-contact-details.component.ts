import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { EmrService, SeedDataService } from '@app/shared/_services';
import { NationalityPayload } from '@app/modules/settings/_payload/nationality.payload';
import { PatientContactDetails, PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
    selector: 'app-patient-contact-details',
    templateUrl: './patient-contact-details.component.html',
    styleUrls: ['./patient-contact-details.component.css'],
})
export class PatientContactDetailsComponent implements OnInit, OnDestroy {
    @ViewChild('nationalityDropDown') nationalityDropDown: NgSelectComponent;
    @ViewChild('stateOfOriginDropDownComponent') stateOfOriginDropDownComponent: NgSelectComponent;
    private subscription: Subscription = new Subscription();
    public nationality: NationalityPayload[];
    @Input('childrenLga')
    public children: NationalityPayload[];
    public parents: NationalityPayload[] = [];
    public patientPayload: PatientPayload;
    @Input('defaultSelected')
    public defaultSelected: NationalityPayload;

    constructor(private seedDataService: SeedDataService, private emrService: EmrService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((value) => {
                this.patientPayload = value;
            })
        );

        this.subscription.add(
            this.seedDataService.onGetNationalityParentOnly().subscribe(
                (result) => {
                    const actualData = result.data;
                    this.parents = [];
                    if (actualData) {
                        this.nationality = actualData;
                        actualData.forEach((value) => {
                            this.parents.push(value);
                            if (value.children.length > 0) {
                                value.hasChildren = true;
                            }
                        });
                    }
                },
                (error) => console.log(error.error.message)
            )
        );

        if (!this.defaultSelected) {
            this.defaultSelected = new NationalityPayload();
        }
        if (!this.children) {
            this.children = [];
        }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onSelectParent(id: number) {
        let selectedChildren = [];
        if (id) {
            this.children = [];
            this.stateOfOriginDropDownComponent.clearModel();
            this.nationality.forEach((child) => {
                if (child.id === id) {
                    selectedChildren = child.children;
                }
            });
            this.children = selectedChildren;
        }
    }

    onResetForm() {
        this.nationalityDropDown.clearModel();
    }
}

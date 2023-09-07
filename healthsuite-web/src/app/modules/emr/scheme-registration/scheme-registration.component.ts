import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { SchemeData} from '@app/shared/_payload/erm/patient.payload';
import { ModalPopupService, SeedDataService } from '@app/shared/_services';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { SchemePlanModalComponent } from '@app/modules/emr/scheme-registration/scheme-plan-modal/scheme-plan-modal.component';
import { ModalSizeEnum, ValidationMessage} from '@app/shared/_payload';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';
import {NgSelectComponent} from '@ng-select/ng-select';

@Component({
    selector: 'app-scheme-registration',
    templateUrl: './scheme-registration.component.html',
    styleUrls: ['./scheme-registration.component.css'],
})
export class SchemeRegistrationComponent implements OnInit, OnDestroy {
    @ViewChild('registerForm') form: any;
    @ViewChild('selectComponent') selectComponent: NgSelectComponent;
    public isSubmitted = false;
    public payload: SchemeData = new SchemeData();
    public isCreate = 'create';
    public isEdit = 'edit';
    public currentView: string;

    private subscription: Subscription = new Subscription();

    constructor(
        private seedDataService: SeedDataService,
        private spinnerService: NgxSpinnerService,
        private toastService: ToastrService,
        private modalPopupService: ModalPopupService
    ) {}

    ngOnInit(): void {
        this.currentView = this.isCreate;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onOrganizationTypeSelected(value: string) {
        if (value) {
            this.payload.organizationType = value;
        }
    }

    public onRemoveItem(i: number) {
        this.payload.schemePlans.splice(i, 1);
    }

    public onOpenPlanModal() {
        this.modalPopupService.openModalWithComponent(
            SchemePlanModalComponent,
            {
                title: 'ADD PLAN',
                data: {
                    schemePlans: this.payload.schemePlans,
                },
            },
            ModalSizeEnum.medium
        );
    }

    public onCreateScheme() {
        const check: ValidationMessage = this.isValid(this.payload);
        if (check.status === false) {
            this.toastService.error(check.message, HmisConstants.VALIDATION_ERR);
            return false;
        }
        this.spinnerService.show().then();
        this.subscription.add(
            this.seedDataService.onCreateScheme(this.payload).subscribe(
                (result) => {
                    if (result) {
                        this.spinnerService.hide().then();
                        this.toastService.success('Scheme Created', 'Successful');
                        this.form.reset();
                        this.payload.schemePlans = [];
                        this.selectComponent.clearModel();
                    }
                },
                (error) => {
                    this.spinnerService.hide().then();
                    this.toastService.error(error.message, 'Scheme Failed');
                }
            )
        );
    }

    public onTypeChange(value: string) {
        this.payload = new SchemeData();
        this.form.reset();
        this.currentView = value;
    }

    public onSchemeSelected(value: SchemeData) {
        this.payload = value;
    }

    protected isValid(payload: SchemeData): ValidationMessage {
        const checker: ValidationMessage =  {message: '', status: true };
        if (!payload.insuranceName) {
            checker.status = false;
            checker.message +=  'Provider Name is required <br>';
        }
        if (!payload.phoneNumber) {
            checker.status = false;
            checker.message +=  'Provider Phone Number is required <br>';
        }

        /*
        if (!payload.planType) {
            checker.status = false;
            checker.message +=  'Insurance Name is required <br>';
        }
        */
        if (!payload.postalAddress) {
            checker.status = false;
            checker.message +=  'Provider postal address is required <br>';
        }

        if (payload.schemePlans.length < 1) {
            checker.status = false;
            checker.message +=  'Enter at least one scheme plan  <br>';
        }

        return checker;
    }

}

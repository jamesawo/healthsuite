import { Component, OnInit } from '@angular/core';
import { SchemePlan } from '@app/shared/_payload/erm/patient.payload';
import {ModalPopupService} from '@app/shared/_services';
import {ToastrService} from 'ngx-toastr';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-scheme-plan-modal',
    templateUrl: './scheme-plan-modal.component.html',
    styleUrls: ['./scheme-plan-modal.component.css'],
})
export class SchemePlanModalComponent implements OnInit {
    data: { schemePlans: SchemePlan[] };

    public schemePlan: SchemePlan = new SchemePlan();

    constructor(private modalService: ModalPopupService,
                private toast: ToastrService) {}

    ngOnInit(): void {
    }

    onSave() {
        const isAdded  = this.onAddPlanToScheme(this.schemePlan);
        if (isAdded === true) {
            this.modalService.onCloseModal();
        }
    }

    private onAddPlanToScheme(plan: SchemePlan): boolean {
        if (plan && plan.planType && plan.percentService && plan.percentDrug) {
            const schemePlan = this.data.schemePlans.find(
                (value) => value.planType === plan.planType
            );

            if (!schemePlan) {
                this.data.schemePlans.push(plan);
                return true;
            }
            this.toast.error('DUPLICATE PLAN NAME', HmisConstants.VALIDATION_ERR);

            return false;
        }
        this.toast.error('Invalid Entry', HmisConstants.VALIDATION_ERR);
        return  false;
    }

}

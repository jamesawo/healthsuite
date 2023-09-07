import { Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { NgSelectComponent } from '@ng-select/ng-select';
import { SeedDataService } from '@app/shared/_services';
import { Subscription } from 'rxjs';
import { SharedPayload } from '@app/shared/_payload';

@Component({
    selector: 'app-payment-method',
    templateUrl: './payment-method.component.html',
    styleUrls: ['./payment-method.component.css'],
})
export class PaymentMethodComponent implements OnInit, OnDestroy {
    public paymentMethodCollection: SharedPayload[];
    public validate: { showError: boolean; hasError: boolean } = {
        showError: false,
        hasError: false,
    };

    @Output('selected') optionEmitter: EventEmitter<SharedPayload> = new EventEmitter();
    @ViewChild(NgSelectComponent) ngSelectComponent: NgSelectComponent;
    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.onGetAllPaymentMethod().subscribe((result) => {
                this.paymentMethodCollection = result.data;
            })
        );
    }
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public onOptionChange(option: SharedPayload, selectComponent: NgSelectComponent) {
        if (option) {
            this.validate = { showError: false, hasError: false };
            this.optionEmitter.emit(option);
        }
    }

    public onValidateInput(): { showError: boolean; hasError: boolean } {
        if (this.ngSelectComponent.hasValue && this.ngSelectComponent.selectedValues.length > 0) {
            this.validate = { showError: false, hasError: false };
        } else {
            this.validate = { showError: true, hasError: true };
        }
        return this.validate;
    }

    public clearSearchFiled() {
        this.ngSelectComponent.clearModel();
    }
}

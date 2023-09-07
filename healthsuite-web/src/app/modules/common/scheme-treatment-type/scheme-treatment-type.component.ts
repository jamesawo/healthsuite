import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PatientInsuranceDetails, TreatmentTypeEnum} from '@app/shared/_payload/erm/patient.payload';

@Component({
  selector: 'app-scheme-treatment-type',
  templateUrl: './scheme-treatment-type.component.html',
  styles: [
  ],
})
export class SchemeTreatmentTypeComponent implements OnInit {
  // @Input('patientInsurance')
  // public patientInsurance: PatientInsuranceDetails;

  @Output('selected')
  public selected: EventEmitter<TreatmentTypeEnum> = new EventEmitter<TreatmentTypeEnum>();

  @Input('default')
  public default: TreatmentTypeEnum = undefined;

  public primary = TreatmentTypeEnum.PRIMARY;
  public secondary = TreatmentTypeEnum.SECONDARY;

  constructor() { }

  ngOnInit(): void {
  }

  onSelected(value: TreatmentTypeEnum) {
    if (value) {
      this.selected.emit(value);
    }
  }
}

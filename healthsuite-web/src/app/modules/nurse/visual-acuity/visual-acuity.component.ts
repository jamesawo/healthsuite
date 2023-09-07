import {Component, OnDestroy, OnInit} from '@angular/core';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {NurseWaitingPayload, WaitingViewTypeEnum} from '@app/shared/_payload';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-visual-acuity',
  templateUrl: './visual-acuity.component.html',
  styleUrls: ['./visual-acuity.component.css']
})
export class VisualAcuityComponent implements OnInit, OnDestroy {
  public payload: any;
  public patient = new PatientPayload();
  public activeView: 'waiting' | 'attended' = 'waiting';
  public waitingCount = 0;
  public attendedCount = 0;
  public nurse = WaitingViewTypeEnum.NURSE;

  private subscription: Subscription = new Subscription();
  constructor() {}

  ngOnInit(): void {}

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  public onPatientSelected(patient: NurseWaitingPayload) {
    if (patient) {
      this.payload.patient = patient;
    }
  }

  public onChangeActiveView(viewType: 'waiting' | 'attended') {
    if (viewType) {
      this.activeView = viewType;
    }
  }

  public onUpdateCount = (value: number) => {
    setTimeout(() => {
      this.waitingCount = value;
    }, 0);
  };

  public onUpdateAttendedCount = (value: number) => {
    setTimeout(() => {
      this.attendedCount = value;
    }, 0);
  };
}

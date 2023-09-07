import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClerkPatientAppointmentComponent } from './clerk-patient-appointment.component';

describe('ClerkPatientAppointmentComponent', () => {
  let component: ClerkPatientAppointmentComponent;
  let fixture: ComponentFixture<ClerkPatientAppointmentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClerkPatientAppointmentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClerkPatientAppointmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
